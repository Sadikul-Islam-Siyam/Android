package com.siyam.travelschedulemanager.model;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Plan {
    private String id;
    private String userId;
    private String name;
    private String notes;
    private double totalFare;
    private int totalDuration; // in minutes
    private Timestamp createdDate;
    private List<PlanLeg> legs;

    // Empty constructor required for Firestore
    public Plan() {
        this.legs = new ArrayList<>();
    }

    public Plan(String userId, String name, String notes) {
        this.userId = userId;
        this.name = name;
        this.notes = notes;
        this.totalFare = 0;
        this.totalDuration = 0;
        this.createdDate = Timestamp.now();
        this.legs = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(int totalDuration) {
        this.totalDuration = totalDuration;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public List<PlanLeg> getLegs() {
        return legs;
    }

    public void setLegs(List<PlanLeg> legs) {
        this.legs = legs;
    }

    // Inner class for Plan Leg
    public static class PlanLeg {
        private String scheduleId;
        private String transportType; // BUS or TRAIN
        private String origin;
        private String destination;
        private String departureTime;
        private String arrivalTime;
        private double fare;
        private String operatorName;
        private int legNumber;

        public PlanLeg() {
        }

        public PlanLeg(String scheduleId, String transportType, String origin, String destination,
                      String departureTime, String arrivalTime, double fare, String operatorName, int legNumber) {
            this.scheduleId = scheduleId;
            this.transportType = transportType;
            this.origin = origin;
            this.destination = destination;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.fare = fare;
            this.operatorName = operatorName;
            this.legNumber = legNumber;
        }

        // Getters and Setters
        public String getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(String scheduleId) {
            this.scheduleId = scheduleId;
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

        public int getLegNumber() {
            return legNumber;
        }

        public void setLegNumber(int legNumber) {
            this.legNumber = legNumber;
        }
    }
}
