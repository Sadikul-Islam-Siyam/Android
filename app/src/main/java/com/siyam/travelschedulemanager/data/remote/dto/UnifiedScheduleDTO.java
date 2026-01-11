package com.siyam.travelschedulemanager.data.remote.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Unified Data Transfer Object for REST API responses
 * Combines both bus and train schedules with a type indicator
 * Matches the desktop app's UnifiedScheduleDTO format
 */
public class UnifiedScheduleDTO {
    @SerializedName("type")
    private String type;        // "bus" or "train"
    
    @SerializedName("name")
    private String name;        // busName or trainName
    
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
    private String duration;    // Format: "4:00h"
    
    @SerializedName("offDay")
    private String offDay;      // Only for trains, "None" or day name

    public UnifiedScheduleDTO() {}

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

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

    // Helper method to check if it's a bus
    public boolean isBus() {
        return "bus".equalsIgnoreCase(type);
    }

    // Helper method to check if it's a train
    public boolean isTrain() {
        return "train".equalsIgnoreCase(type);
    }

    @Override
    public String toString() {
        return "UnifiedScheduleDTO{" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", start='" + start + '\'' +
                ", destination='" + destination + '\'' +
                ", fare=" + fare +
                '}';
    }
}
