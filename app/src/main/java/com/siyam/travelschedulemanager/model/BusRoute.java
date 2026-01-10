package com.siyam.travelschedulemanager.model;

import com.google.firebase.Timestamp;

/**
 * Model class representing a Bus Route.
 * Simplified model matching the JSON structure.
 */
public class BusRoute {
    private String id;
    private String routeType = "BUS";
    private String busName;         // e.g. "Hanif Enterprise (Bus-1)"
    private String start;           // Origin station
    private String destination;     // Destination station
    private String startTime;       // Departure time e.g. "20:30"
    private String arrivalTime;     // Arrival time e.g. "12:00"
    private double fare;            // Total fare
    private String duration;        // Duration e.g. "9:00h"
    private String status;          // APPROVED, PENDING, DRAFT
    private String createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Empty constructor required for Firestore
    public BusRoute() {
        this.routeType = "BUS";
    }

    public BusRoute(String busName, String start, String destination) {
        this.routeType = "BUS";
        this.busName = busName;
        this.start = start;
        this.destination = destination;
        this.status = "DRAFT";
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }

    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }

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

    // For compatibility - getRouteName returns busName
    public String getRouteName() { return busName; }

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
        if (busName != null && !busName.isEmpty()) {
            return busName;
        }
        return start + " → " + destination;
    }
}
