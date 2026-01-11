package com.siyam.travelschedulemanager.algorithm;

import com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO;

import java.util.*;

public class RouteGraph {
    private Map<String, List<Edge>> adjacencyList;
    private List<UnifiedScheduleDTO> allSchedules;

    public RouteGraph(List<UnifiedScheduleDTO> schedules) {
        this.allSchedules = schedules;
        this.adjacencyList = new HashMap<>();
        buildGraph();
    }

    private void buildGraph() {
        for (UnifiedScheduleDTO schedule : allSchedules) {
            // Normalize to lowercase for consistent comparison
            String from = schedule.getStart().toLowerCase().trim();
            String to = schedule.getDestination().toLowerCase().trim();

            if (!adjacencyList.containsKey(from)) {
                adjacencyList.put(from, new ArrayList<>());
            }

            adjacencyList.get(from).add(new Edge(to, schedule));
        }
        
        android.util.Log.d("RouteGraph", "Graph built with " + adjacencyList.size() + " cities:");
        for (String city : adjacencyList.keySet()) {
            android.util.Log.d("RouteGraph", "  " + city + ": " + adjacencyList.get(city).size() + " routes");
        }
    }

    public List<List<UnifiedScheduleDTO>> findRoutes(String source, String destination, int maxLegs) {
        List<List<UnifiedScheduleDTO>> allRoutes = new ArrayList<>();
        
        source = source.toLowerCase().trim();
        destination = destination.toLowerCase().trim();
        
        android.util.Log.d("RouteGraph", "Finding routes from '" + source + "' to '" + destination + "' with max " + maxLegs + " legs");
        android.util.Log.d("RouteGraph", "Graph has " + adjacencyList.size() + " cities");
        android.util.Log.d("RouteGraph", "Cities from " + source + ": " + 
            (adjacencyList.containsKey(source) ? adjacencyList.get(source).size() + " connections" : "NONE"));
        
        // Try ALL possible leg counts from 1 to maxLegs
        for (int legs = 1; legs <= maxLegs; legs++) {
            android.util.Log.d("RouteGraph", "Searching for " + legs + "-leg routes...");
            List<List<UnifiedScheduleDTO>> routesWithNLegs = findMultiLegRoutes(source, destination, legs);
            android.util.Log.d("RouteGraph", "Found " + routesWithNLegs.size() + " routes with " + legs + " leg(s)");
            allRoutes.addAll(routesWithNLegs);
        }
        
        android.util.Log.d("RouteGraph", "Total routes before validation: " + allRoutes.size());
        
        // Sort routes by total fare (cheapest first)
        allRoutes.sort((r1, r2) -> {
            double fare1 = r1.stream().mapToDouble(UnifiedScheduleDTO::getFare).sum();
            double fare2 = r2.stream().mapToDouble(UnifiedScheduleDTO::getFare).sum();
            return Double.compare(fare1, fare2);
        });
        
        // Limit to top 10 routes to avoid overwhelming UI
        if (allRoutes.size() > 10) {
            allRoutes = allRoutes.subList(0, 10);
        }
        
        return allRoutes;
    }

    private List<List<UnifiedScheduleDTO>> findMultiLegRoutes(String source, String destination, int legs) {
        List<List<UnifiedScheduleDTO>> routes = new ArrayList<>();
        List<UnifiedScheduleDTO> currentPath = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        
        source = source.toLowerCase().trim();
        destination = destination.toLowerCase().trim();
        
        dfsRoutes(source, destination, legs, currentPath, visited, routes);
        
        android.util.Log.d("RouteGraph", "DFS found " + routes.size() + " raw routes");
        
        // Filter routes with valid connections
        // Make 30-min validation more lenient for multi-leg routes
        List<List<UnifiedScheduleDTO>> validRoutes = new ArrayList<>();
        for (List<UnifiedScheduleDTO> route : routes) {
            // For multi-leg routes, be more lenient with overnight connections
            if (legs == 1 || isValidRoute(route)) {
                validRoutes.add(route);
            } else {
                // Log why route was rejected
                android.util.Log.d("RouteGraph", "Rejected route due to connection time constraints");
            }
        }
        
        android.util.Log.d("RouteGraph", "After validation: " + validRoutes.size() + " valid routes");
        return validRoutes;
    }

    private void dfsRoutes(String current, String destination, int remainingLegs,
                          List<UnifiedScheduleDTO> currentPath, Set<String> visited,
                          List<List<UnifiedScheduleDTO>> routes) {
        
        current = current.toLowerCase().trim();
        destination = destination.toLowerCase().trim();
        
        // Base case: reached destination
        if (current.equals(destination) && !currentPath.isEmpty()) {
            routes.add(new ArrayList<>(currentPath));
            android.util.Log.d("RouteGraph", "Found route: " + formatRoute(currentPath));
            return;
        }
        
        // Base case: no more legs allowed
        if (remainingLegs == 0) {
            return;
        }
        
        // Prevent revisiting cities
        visited.add(current);
        
        // Explore all edges from current city
        if (adjacencyList.containsKey(current)) {
            List<Edge> edges = adjacencyList.get(current);
            for (Edge edge : edges) {
                String nextCity = edge.destination.toLowerCase().trim();
                
                // Don't revisit cities (prevents cycles)
                if (!visited.contains(nextCity)) {
                    // Add this leg to the path
                    currentPath.add(edge.schedule);
                    
                    // Recurse to next city
                    dfsRoutes(nextCity, destination, remainingLegs - 1, currentPath, visited, routes);
                    
                    // Backtrack
                    currentPath.remove(currentPath.size() - 1);
                }
            }
        }
        
        // Backtrack: allow this city to be visited in other paths
        visited.remove(current);
    }
    
    private String formatRoute(List<UnifiedScheduleDTO> route) {
        if (route.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < route.size(); i++) {
            UnifiedScheduleDTO leg = route.get(i);
            if (i > 0) sb.append(" → ");
            sb.append(leg.getStart()).append("-").append(leg.getDestination());
        }
        return sb.toString();
    }

    private List<List<UnifiedScheduleDTO>> filterValidRoutes(List<List<UnifiedScheduleDTO>> routes) {
        List<List<UnifiedScheduleDTO>> validRoutes = new ArrayList<>();
        
        for (List<UnifiedScheduleDTO> route : routes) {
            if (isValidRoute(route)) {
                validRoutes.add(route);
            }
        }
        
        return validRoutes;
    }

    private boolean isValidRoute(List<UnifiedScheduleDTO> route) {
        for (int i = 0; i < route.size() - 1; i++) {
            UnifiedScheduleDTO current = route.get(i);
            UnifiedScheduleDTO next = route.get(i + 1);
            
            if (!isValidConnection(current, next)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidConnection(UnifiedScheduleDTO first, UnifiedScheduleDTO second) {
        try {
            // Check if destination of first leg matches origin of second leg
            if (!first.getDestination().equalsIgnoreCase(second.getStart())) {
                android.util.Log.d("RouteGraph", "Invalid connection: " + 
                    first.getDestination() + " != " + second.getStart());
                return false;
            }
            
            // Parse times (format: HH:mm)
            String[] arrival = first.getArrivalTime().split(":");
            String[] departure = second.getStartTime().split(":");
            
            int arrivalMinutes = Integer.parseInt(arrival[0]) * 60 + Integer.parseInt(arrival[1]);
            int departureMinutes = Integer.parseInt(departure[0]) * 60 + Integer.parseInt(departure[1]);
            
            // Handle overnight scenarios (departure next day)
            if (departureMinutes < arrivalMinutes) {
                departureMinutes += 24 * 60; // Add 24 hours
            }
            
            int bufferMinutes = departureMinutes - arrivalMinutes;
            
            // Be more lenient: allow 15 min for quick connections, or long layovers (up to 12 hours)
            boolean isValid = bufferMinutes >= 15 && bufferMinutes <= 720; // 15 min to 12 hours
            
            if (!isValid) {
                android.util.Log.d("RouteGraph", "Invalid buffer: " + bufferMinutes + " minutes between " + 
                    first.getArrivalTime() + " and " + second.getStartTime());
            }
            
            return isValid;
            
        } catch (Exception e) {
            android.util.Log.e("RouteGraph", "Error validating connection", e);
            return false;
        }
    }

    public Set<String> getAllCities() {
        Set<String> cities = new HashSet<>();
        for (UnifiedScheduleDTO schedule : allSchedules) {
            cities.add(schedule.getStart());
            cities.add(schedule.getDestination());
        }
        return cities;
    }

    private static class Edge {
        String destination;
        UnifiedScheduleDTO schedule;

        Edge(String destination, UnifiedScheduleDTO schedule) {
            this.destination = destination;
            this.schedule = schedule;
        }
    }
}
