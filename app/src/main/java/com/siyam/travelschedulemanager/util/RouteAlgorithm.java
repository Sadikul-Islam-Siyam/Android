package com.siyam.travelschedulemanager.util;

import com.siyam.travelschedulemanager.model.Schedule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class RouteAlgorithm {

    private static final int MIN_TRANSFER_TIME = 30; // Minimum 30 minutes between legs

    /**
     * Find optimal routes using BFS algorithm
     */
    public static List<RouteResult> findOptimalRoutes(
            String origin,
            String destination,
            Date travelDate,
            List<Schedule> allSchedules,
            int maxLegs) {

        List<RouteResult> results = new ArrayList<>();
        String dayOfWeek = DateUtils.getDayOfWeek(travelDate);

        // Filter schedules available on the travel date
        List<Schedule> availableSchedules = new ArrayList<>();
        for (Schedule schedule : allSchedules) {
            if (schedule.getOffDays() == null || !schedule.getOffDays().contains(dayOfWeek)) {
                availableSchedules.add(schedule);
            }
        }

        // Build adjacency list for graph
        Map<String, List<Schedule>> graph = buildGraph(availableSchedules);

        // BFS to find all possible routes
        Queue<RoutePath> queue = new LinkedList<>();
        queue.add(new RoutePath(origin, new ArrayList<>(), 0, 0, null));

        while (!queue.isEmpty()) {
            RoutePath current = queue.poll();

            // Check if we've reached destination
            if (current.currentCity.equals(destination) && !current.legs.isEmpty()) {
                results.add(new RouteResult(
                        new ArrayList<>(current.legs),
                        current.totalDuration,
                        current.totalFare
                ));
                continue;
            }

            // Don't exceed max legs
            if (current.legs.size() >= maxLegs) {
                continue;
            }

            // Get available schedules from current city
            List<Schedule> nextSchedules = graph.get(current.currentCity);
            if (nextSchedules == null) {
                continue;
            }

            for (Schedule schedule : nextSchedules) {
                // Check transfer time if not first leg
                if (current.lastArrivalTime != null) {
                    if (!DateUtils.isValidTransferTime(
                            current.lastArrivalTime,
                            schedule.getDepartureTime(),
                            MIN_TRANSFER_TIME)) {
                        continue;
                    }
                }

                // Avoid cycles - don't revisit cities
                if (hasVisited(current.legs, schedule.getDestination())) {
                    continue;
                }

                // Create new path
                List<Schedule> newLegs = new ArrayList<>(current.legs);
                newLegs.add(schedule);

                queue.add(new RoutePath(
                        schedule.getDestination(),
                        newLegs,
                        current.totalDuration + schedule.getDuration(),
                        current.totalFare + schedule.getFare(),
                        schedule.getArrivalTime()
                ));
            }
        }

        // Sort by total duration (fastest first)
        Collections.sort(results, new Comparator<RouteResult>() {
            @Override
            public int compare(RouteResult r1, RouteResult r2) {
                return Integer.compare(r1.totalDuration, r2.totalDuration);
            }
        });

        // Return top 5 results
        return results.size() > 5 ? results.subList(0, 5) : results;
    }

    /**
     * Build graph from schedules
     */
    private static Map<String, List<Schedule>> buildGraph(List<Schedule> schedules) {
        Map<String, List<Schedule>> graph = new HashMap<>();

        for (Schedule schedule : schedules) {
            if (!graph.containsKey(schedule.getOrigin())) {
                graph.put(schedule.getOrigin(), new ArrayList<Schedule>());
            }
            graph.get(schedule.getOrigin()).add(schedule);
        }

        return graph;
    }

    /**
     * Check if city has been visited in current path
     */
    private static boolean hasVisited(List<Schedule> legs, String city) {
        for (Schedule leg : legs) {
            if (leg.getOrigin().equals(city) || leg.getDestination().equals(city)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Class to represent a path during search
     */
    private static class RoutePath {
        String currentCity;
        List<Schedule> legs;
        int totalDuration;
        double totalFare;
        String lastArrivalTime;

        RoutePath(String currentCity, List<Schedule> legs, int totalDuration,
                 double totalFare, String lastArrivalTime) {
            this.currentCity = currentCity;
            this.legs = legs;
            this.totalDuration = totalDuration;
            this.totalFare = totalFare;
            this.lastArrivalTime = lastArrivalTime;
        }
    }

    /**
     * Class to represent a route result
     */
    public static class RouteResult {
        public List<Schedule> legs;
        public int totalDuration;
        public double totalFare;

        RouteResult(List<Schedule> legs, int totalDuration, double totalFare) {
            this.legs = legs;
            this.totalDuration = totalDuration;
            this.totalFare = totalFare;
        }

        public int getNumLegs() {
            return legs.size();
        }
    }
}
