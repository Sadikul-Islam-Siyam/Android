package com.siyam.travelschedulemanager.model;

import com.google.firebase.Timestamp;
import java.util.List;

public class Schedule {
    private String id;
    private String transportType; // BUS or TRAIN
    private String origin;
    private String destination;
    private String departureTime; // Format: HH:mm
    private String arrivalTime;   // Format: HH:mm
    private int duration; // in minutes
    private double fare;
    private String operatorName;
    private String trainNumber; // For trains only
    private int totalSeats;
    private List<String> offDays; // Days when service is not available (e.g., "Friday")
    private Timestamp createdAt;
    private String createdBy;

    // Empty constructor required for Firestore
    public Schedule() {
    }

    public Schedule(String transportType, String origin, String destination, String departureTime,
                   String arrivalTime, int duration, double fare, String operatorName) {
        this.transportType = transportType;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.fare = fare;
        this.operatorName = operatorName;
        this.createdAt = Timestamp.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public List<String> getOffDays() {
        return offDays;
    }

    public void setOffDays(List<String> offDays) {
        this.offDays = offDays;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
