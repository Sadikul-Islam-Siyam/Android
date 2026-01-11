package com.siyam.travelschedulemanager.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Schedule DTO matching desktop API format
 * Combines both bus and train schedule information
 */
public class ScheduleDTO {
    @SerializedName("id")
    private String id;
    
    @SerializedName("type")
    private String type; // "BUS" or "TRAIN"
    
    @SerializedName("origin")
    private String origin;
    
    @SerializedName("destination")
    private String destination;
    
    @SerializedName("departureTime")
    private String departureTime;
    
    @SerializedName("arrivalTime")
    private String arrivalTime;
    
    @SerializedName("fare")
    private double fare;
    
    @SerializedName("availableSeats")
    private int availableSeats;
    
    // Bus specific fields
    @SerializedName("companyName")
    private String companyName;
    
    @SerializedName("busType")
    private String busType;
    
    // Train specific fields
    @SerializedName("trainName")
    private String trainName;
    
    @SerializedName("trainNumber")
    private String trainNumber;
    
    @SerializedName("seatClass")
    private String seatClass;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public String getTrainName() {
        return trainName;
    }

    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    // Helper methods
    public boolean isBus() {
        return "BUS".equalsIgnoreCase(type);
    }

    public boolean isTrain() {
        return "TRAIN".equalsIgnoreCase(type);
    }
}
