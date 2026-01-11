package com.siyam.travelschedulemanager.data.remote.dto;

import java.util.List;

/**
 * Pending users response DTO (for admin endpoints)
 */
public class PendingUsersResponse {
    private boolean success;
    private int count;
    private List<PendingUserDTO> pendingUsers;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<PendingUserDTO> getPendingUsers() {
        return pendingUsers;
    }

    public void setPendingUsers(List<PendingUserDTO> pendingUsers) {
        this.pendingUsers = pendingUsers;
    }
}
