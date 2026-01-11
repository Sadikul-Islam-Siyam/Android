package com.siyam.travelschedulemanager.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Data Transfer Object for Bus Schedule from REST API
 * Matches the desktop app's BusScheduleDTO format
 */
public class BusScheduleDTO {
    @SerializedName("busName")
    private String busName;
    
    @SerializedName("start")
    private String start;
    
    @SerializedName("destination")
    private String destination;
    
    @SerializedName("startTime")
    private String startTime;
    
    @SerializedName("arrivalTime")
    private String arrivalTime;
    
    @SerializedName("fare")
    private double fare;
    
    @SerializedName("duration")
    private String duration; // Format: "4:00h"

    public BusScheduleDTO() {}

    public BusScheduleDTO(String busName, String start, String destination, 
                         String startTime, String arrivalTime, double fare, String duration) {
        this.busName = busName;
        this.start = start;
        this.destination = destination;
        this.startTime = startTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.duration = duration;
    }

    // Getters and Setters
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

    @Override
    public String toString() {
        return "BusScheduleDTO{" +
                "busName='" + busName + '\'' +
                ", start='" + start + '\'' +
                ", destination='" + destination + '\'' +
                ", fare=" + fare +
                '}';
    }
}
