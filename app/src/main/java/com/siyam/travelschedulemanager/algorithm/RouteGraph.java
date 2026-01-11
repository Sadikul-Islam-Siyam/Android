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
            String from = schedule.getStart().toLowerCase();
            String to = schedule.getDestination().toLowerCase();

            if (!adjacencyList.containsKey(from)) {
                adjacencyList.put(from, new ArrayList<>());
            }

            adjacencyList.get(from).add(new Edge(to, schedule));
        }
    }

    public List<List<UnifiedScheduleDTO>> findRoutes(String source, String destination, int maxLegs) {
        List<List<UnifiedScheduleDTO>> allRoutes = new ArrayList<>();
        
        // Try to find direct routes first (1 leg)
        List<List<UnifiedScheduleDTO>> directRoutes = findDirectRoutes(source, destination);
        allRoutes.addAll(directRoutes);
        
        // Find routes with 2 legs if requested
        if (maxLegs >= 2 && directRoutes.isEmpty()) {
            List<List<UnifiedScheduleDTO>> twoLegRoutes = findMultiLegRoutes(source, destination, 2);
            allRoutes.addAll(twoLegRoutes);
        }
        
        // Find routes with 3 legs if requested
        if (maxLegs >= 3 && allRoutes.isEmpty()) {
            List<List<UnifiedScheduleDTO>> threeLegRoutes = findMultiLegRoutes(source, destination, 3);
            allRoutes.addAll(threeLegRoutes);
        }
        
        // Sort routes by total fare
        allRoutes.sort((r1, r2) -> {
            double fare1 = r1.stream().mapToDouble(UnifiedScheduleDTO::getFare).sum();
            double fare2 = r2.stream().mapToDouble(UnifiedScheduleDTO::getFare).sum();
            return Double.compare(fare1, fare2);
        });
        
        return allRoutes;
    }

    private List<List<UnifiedScheduleDTO>> findDirectRoutes(String source, String destination) {
        List<List<UnifiedScheduleDTO>> routes = new ArrayList<>();
        
        source = source.toLowerCase();
        destination = destination.toLowerCase();
        
        if (adjacencyList.containsKey(source)) {
            for (Edge edge : adjacencyList.get(source)) {
                if (edge.destination.equalsIgnoreCase(destination)) {
                    List<UnifiedScheduleDTO> route = new ArrayList<>();
                    route.add(edge.schedule);
                    routes.add(route);
                }
            }
        }
        
        return routes;
    }

    private List<List<UnifiedScheduleDTO>> findMultiLegRoutes(String source, String destination, int legs) {
        List<List<UnifiedScheduleDTO>> routes = new ArrayList<>();
        List<UnifiedScheduleDTO> currentPath = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        
        dfsRoutes(source, destination, legs, currentPath, visited, routes);
        
        // Filter routes with valid connections (30 min buffer)
        return filterValidRoutes(routes);
    }

    private void dfsRoutes(String current, String destination, int remainingLegs,
                          List<UnifiedScheduleDTO> currentPath, Set<String> visited,
                          List<List<UnifiedScheduleDTO>> routes) {
        
        current = current.toLowerCase();
        destination = destination.toLowerCase();
        
        if (remainingLegs == 0) {
            if (current.equalsIgnoreCase(destination)) {
                routes.add(new ArrayList<>(currentPath));
            }
            return;
        }
        
        visited.add(current);
        
        if (adjacencyList.containsKey(current)) {
            for (Edge edge : adjacencyList.get(current)) {
                if (!visited.contains(edge.destination)) {
                    currentPath.add(edge.schedule);
                    dfsRoutes(edge.destination, destination, remainingLegs - 1, currentPath, visited, routes);
                    currentPath.remove(currentPath.size() - 1);
                }
            }
        }
        
        visited.remove(current);
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
            // Parse times (format: HH:mm)
            String[] arrival = first.getArrivalTime().split(":");
            String[] departure = second.getStartTime().split(":");
            
            int arrivalMinutes = Integer.parseInt(arrival[0]) * 60 + Integer.parseInt(arrival[1]);
            int departureMinutes = Integer.parseInt(departure[0]) * 60 + Integer.parseInt(departure[1]);
            
            // Handle overnight scenarios
            if (departureMinutes < arrivalMinutes) {
                departureMinutes += 24 * 60; // Add 24 hours
            }
            
            int bufferMinutes = departureMinutes - arrivalMinutes;
            return bufferMinutes >= 30; // At least 30 minutes buffer
            
        } catch (Exception e) {
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
