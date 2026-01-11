package com.siyam.travelschedulemanager.data.remote.dto;

import java.util.List;

/**
 * Register response DTO
 */
public class RegisterResponse {
    private boolean success;
    private String message;
    private String status;
    private List<String> errors;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
