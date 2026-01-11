package com.siyam.travelschedulemanager.data.remote.dto;

/**
 * Profile response DTO
 */
public class ProfileResponse {
    private boolean success;
    private UserDTO user;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
