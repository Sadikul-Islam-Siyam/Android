package com.siyam.travelschedulemanager.model;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a Train Route.
 * Matches the JSON structure with stops array.
 */
public class TrainRoute {
    private String id;
    private String routeType = "TRAIN";
    private String trainName;
    private String trainNumber;
    private String start;           // First station (derived from stops)
    private String destination;     // Last station (derived from stops)
    private String startTime;       // Departure time from first station
    private String arrivalTime;     // Arrival time at last station
    private double fare;            // Total fare (from last stop's cumulativeFare)
    private String duration;        // Total journey duration e.g. "4:30h"
    private String offDay;          // Single off day e.g. "Monday", "Friday"
    private List<RouteStop> stops;  // List of all stops with details
    private String status;          // APPROVED, PENDING, DRAFT
    private String createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Empty constructor required for Firestore
    public TrainRoute() {
        this.routeType = "TRAIN";
        this.stops = new ArrayList<>();
    }

    public TrainRoute(String trainName, String trainNumber) {
        this.routeType = "TRAIN";
        this.trainName = trainName;
        this.trainNumber = trainNumber;
        this.status = "DRAFT";
        this.stops = new ArrayList<>();
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Helper methods
    public void addStop(RouteStop stop) {
        if (stops == null) {
            stops = new ArrayList<>();
        }
        stops.add(stop);
        updateStartAndDestination();
    }

    public void removeStop(int index) {
        if (stops != null && index >= 0 && index < stops.size()) {
            stops.remove(index);
            updateStartAndDestination();
        }
    }

    private void updateStartAndDestination() {
        if (stops != null && !stops.isEmpty()) {
            RouteStop firstStop = stops.get(0);
            RouteStop lastStop = stops.get(stops.size() - 1);
            this.start = firstStop.getStation();
            this.destination = lastStop.getStation();
            this.startTime = firstStop.getDepartureTime();
            this.arrivalTime = lastStop.getArrivalTime();
            this.fare = lastStop.getCumulativeFare();
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }

    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }

    public String getTrainNumber() { return trainNumber; }
    public void setTrainNumber(String trainNumber) { this.trainNumber = trainNumber; }

    public String getStart() { return start; }
    public void setStart(String start) { this.start = start; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public double getFare() { return fare; }
    public void setFare(double fare) { this.fare = fare; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getOffDay() { return offDay; }
    public void setOffDay(String offDay) { this.offDay = offDay; }

    public List<RouteStop> getStops() { return stops; }
    public void setStops(List<RouteStop> stops) { 
        this.stops = stops;
        updateStartAndDestination();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    // For compatibility - getOrigin returns start
    public String getOrigin() { return start; }

    // For compatibility - getDepartureTime returns startTime  
    public String getDepartureTime() { return startTime; }

    // Helper to get duration in minutes for display
    public int getDurationMinutes() {
        if (duration == null || duration.isEmpty()) return 0;
        try {
            String cleaned = duration.replace("h", "").trim();
            String[] parts = cleaned.split(":");
            if (parts.length == 2) {
                int hours = Integer.parseInt(parts[0]);
                int minutes = Integer.parseInt(parts[1]);
                return hours * 60 + minutes;
            }
        } catch (Exception e) {
            return 0;
        }
        return 0;
    }

    // Helper method to get display name
    public String getDisplayName() {
        if (trainName != null && !trainName.isEmpty()) {
            return trainName;
        }
        return start + " → " + destination;
    }
}

