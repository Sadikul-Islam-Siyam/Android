package com.siyam.travelschedulemanager.model;

import com.google.firebase.Timestamp;

/**
 * Unified Route model that can represent both Bus and Train routes
 * for display in the Route Management list
 */
public class UnifiedRoute {
    private String id;
    private String routeType; // BUS or TRAIN
    private String displayName; // Route name or Train name
    private String routeNumber; // Route number or Train number
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private String duration; // Now stored as string like "4:30h"
    private double fare;
    private String status; // APPROVED, PENDING, DRAFT
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Original route data for editing
    private BusRoute busRoute;
    private TrainRoute trainRoute;

    public UnifiedRoute() {}

    // Create from BusRoute
    public static UnifiedRoute fromBusRoute(BusRoute bus) {
        UnifiedRoute route = new UnifiedRoute();
        route.id = bus.getId();
        route.routeType = "BUS";
        route.displayName = bus.getBusName();
        route.routeNumber = null;
        route.origin = bus.getStart();
        route.destination = bus.getDestination();
        route.departureTime = bus.getStartTime();
        route.arrivalTime = bus.getArrivalTime();
        route.duration = bus.getDuration();
        route.fare = bus.getFare();
        route.status = bus.getStatus();
        route.createdAt = bus.getCreatedAt();
        route.updatedAt = bus.getUpdatedAt();
        route.busRoute = bus;
        return route;
    }

    // Create from TrainRoute
    public static UnifiedRoute fromTrainRoute(TrainRoute train) {
        UnifiedRoute route = new UnifiedRoute();
        route.id = train.getId();
        route.routeType = "TRAIN";
        route.displayName = train.getTrainName();
        route.routeNumber = train.getTrainNumber();
        route.origin = train.getStart();
        route.destination = train.getDestination();
        route.departureTime = train.getStartTime();
        route.arrivalTime = train.getArrivalTime();
        route.duration = train.getDuration();
        route.fare = train.getFare();
        route.status = train.getStatus();
        route.createdAt = train.getCreatedAt();
        route.updatedAt = train.getUpdatedAt();
        route.trainRoute = train;
        return route;
    }

    // Getters
    public String getId() { return id; }
    public String getRouteType() { return routeType; }
    public String getDisplayName() { return displayName; }
    public String getRouteNumber() { return routeNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public String getDuration() { return duration; }
    public double getFare() { return fare; }
    public String getStatus() { return status; }
    public Timestamp getCreatedAt() { return createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public BusRoute getBusRoute() { return busRoute; }
    public TrainRoute getTrainRoute() { return trainRoute; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setRouteType(String routeType) { this.routeType = routeType; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setRouteNumber(String routeNumber) { this.routeNumber = routeNumber; }
    public void setOrigin(String origin) { this.origin = origin; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setFare(double fare) { this.fare = fare; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public void setBusRoute(BusRoute busRoute) { this.busRoute = busRoute; }
    public void setTrainRoute(TrainRoute trainRoute) { this.trainRoute = trainRoute; }

    // Helper methods
    public boolean isBus() {
        return "BUS".equals(routeType);
    }

    public boolean isTrain() {
        return "TRAIN".equals(routeType);
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isDraft() {
        return "DRAFT".equals(status);
    }

    public String getRouteDisplay() {
        return origin + " → " + destination;
    }
}
