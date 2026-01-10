package com.siyam.travelschedulemanager.model;

import com.google.firebase.Timestamp;

public class PendingRoute {
    private String id;
    private String changeType; // ADD, EDIT, DELETE
    private Route routeData;
    private String notes;
    private String reason;
    private String submittedBy;
    private String submittedByName;
    private String status; // PENDING, APPROVED, REJECTED
    private String reviewedBy;
    private String reviewFeedback;
    private Timestamp submittedAt;
    private Timestamp reviewedAt;

    // Empty constructor required for Firestore
    public PendingRoute() {
    }

    public PendingRoute(String changeType, Route routeData, String notes, String reason,
                       String submittedBy, String submittedByName) {
        this.changeType = changeType;
        this.routeData = routeData;
        this.notes = notes;
        this.reason = reason;
        this.submittedBy = submittedBy;
        this.submittedByName = submittedByName;
        this.status = "PENDING";
        this.submittedAt = Timestamp.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public Route getRouteData() {
        return routeData;
    }

    public void setRouteData(Route routeData) {
        this.routeData = routeData;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getSubmittedByName() {
        return submittedByName;
    }

    public void setSubmittedByName(String submittedByName) {
        this.submittedByName = submittedByName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewFeedback() {
        return reviewFeedback;
    }

    public void setReviewFeedback(String reviewFeedback) {
        this.reviewFeedback = reviewFeedback;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Timestamp getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(Timestamp reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}
