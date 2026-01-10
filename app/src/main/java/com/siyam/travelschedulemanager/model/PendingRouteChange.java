package com.siyam.travelschedulemanager.model;

import com.google.firebase.Timestamp;

/**
 * Model for pending route changes that require Master approval
 */
public class PendingRouteChange {
    private String id;
    private String changeType; // ADD, EDIT, DELETE
    private String routeType; // BUS, TRAIN
    private String routeId; // Original route ID (for EDIT/DELETE)
    
    // Route data based on type
    private BusRoute busRouteData;
    private TrainRoute trainRouteData;
    
    // Developer message to Master
    private String messageToMaster;
    private String changeDescription;
    
    // Submission info
    private String submittedBy;
    private String submittedByName;
    private String submittedByEmail;
    private Timestamp submittedAt;
    
    // Review info
    private String status; // PENDING, APPROVED, REJECTED
    private String reviewedBy;
    private String reviewedByName;
    private String reviewFeedback;
    private Timestamp reviewedAt;

    public PendingRouteChange() {
        this.status = "PENDING";
        this.submittedAt = Timestamp.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }

    public String getRouteType() { return routeType; }
    public void setRouteType(String routeType) { this.routeType = routeType; }

    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }

    public BusRoute getBusRouteData() { return busRouteData; }
    public void setBusRouteData(BusRoute busRouteData) { this.busRouteData = busRouteData; }

    public TrainRoute getTrainRouteData() { return trainRouteData; }
    public void setTrainRouteData(TrainRoute trainRouteData) { this.trainRouteData = trainRouteData; }

    public String getMessageToMaster() { return messageToMaster; }
    public void setMessageToMaster(String messageToMaster) { this.messageToMaster = messageToMaster; }

    public String getChangeDescription() { return changeDescription; }
    public void setChangeDescription(String changeDescription) { this.changeDescription = changeDescription; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    public String getSubmittedByName() { return submittedByName; }
    public void setSubmittedByName(String submittedByName) { this.submittedByName = submittedByName; }

    public String getSubmittedByEmail() { return submittedByEmail; }
    public void setSubmittedByEmail(String submittedByEmail) { this.submittedByEmail = submittedByEmail; }

    public Timestamp getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Timestamp submittedAt) { this.submittedAt = submittedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public String getReviewedByName() { return reviewedByName; }
    public void setReviewedByName(String reviewedByName) { this.reviewedByName = reviewedByName; }

    public String getReviewFeedback() { return reviewFeedback; }
    public void setReviewFeedback(String reviewFeedback) { this.reviewFeedback = reviewFeedback; }

    public Timestamp getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Timestamp reviewedAt) { this.reviewedAt = reviewedAt; }

    // Helper methods
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    public boolean isRejected() {
        return "REJECTED".equals(status);
    }

    public boolean isBusRoute() {
        return "BUS".equals(routeType);
    }

    public boolean isTrainRoute() {
        return "TRAIN".equals(routeType);
    }

    public String getRouteName() {
        if (isBusRoute() && busRouteData != null) {
            return busRouteData.getRouteName();
        } else if (isTrainRoute() && trainRouteData != null) {
            return trainRouteData.getTrainName();
        }
        return "Unknown Route";
    }

    public String getRouteDisplay() {
        String origin = "";
        String destination = "";
        
        if (isBusRoute() && busRouteData != null) {
            origin = busRouteData.getOrigin();
            destination = busRouteData.getDestination();
        } else if (isTrainRoute() && trainRouteData != null) {
            origin = trainRouteData.getOrigin();
            destination = trainRouteData.getDestination();
        }
        
        return origin + " → " + destination;
    }
}
