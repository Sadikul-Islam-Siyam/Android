package com.siyam.travelschedulemanager.model;

import com.google.firebase.Timestamp;

public class User {
    private String uid;
    private String username;
    private String email;
    private String role; // USER, DEVELOPER, MASTER
    private String status; // PENDING, APPROVED, REJECTED, LOCKED
    private int failedAttempts;
    private Timestamp lockUntil;
    private Timestamp createdAt;
    private String rejectionReason;

    // Empty constructor required for Firestore
    public User() {
    }

    public User(String uid, String username, String email, String role, String status) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
        this.failedAttempts = 0;
        this.createdAt = Timestamp.now();
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }

    public void setFailedAttempts(int failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public Timestamp getLockUntil() {
        return lockUntil;
    }

    public void setLockUntil(Timestamp lockUntil) {
        this.lockUntil = lockUntil;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
