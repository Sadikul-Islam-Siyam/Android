package com.siyam.travelschedulemanager.data.remote;

import com.siyam.travelschedulemanager.data.remote.dto.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API Service Interface
 * Defines all REST API endpoints for the desktop app integration
 */
public interface ApiService {

    // ==================== Authentication Endpoints ====================
    
    /**
     * Login and get session token
     * POST /api/auth/login
     */
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * Register new account (pending approval)
     * POST /api/auth/register
     */
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    /**
     * Check account approval status
     * GET /api/auth/status/{username}
     */
    @GET("auth/status/{username}")
    Call<StatusResponse> checkStatus(@Path("username") String username);

    /**
     * Get current user's profile
     * GET /api/users/profile
     */
    @GET("users/profile")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token);

    // ==================== Admin Endpoints (Master/Developer Only) ====================

    /**
     * Get all pending user registrations
     * GET /api/admin/pending-users
     */
    @GET("admin/pending-users")
    Call<PendingUsersResponse> getPendingUsers(@Header("Authorization") String token);

    /**
     * Approve a pending user
     * POST /api/admin/approve/{userId}
     */
    @POST("admin/approve/{userId}")
    Call<ApiResponse> approveUser(
            @Header("Authorization") String token,
            @Path("userId") int userId
    );

    /**
     * Reject a pending user
     * POST /api/admin/reject/{userId}
     */
    @POST("admin/reject/{userId}")
    Call<ApiResponse> rejectUser(
            @Header("Authorization") String token,
            @Path("userId") int userId
    );

    // ==================== Schedule Search Endpoints ====================

    /**
     * Search routes (bus + train combined)
     * GET /api/routes?start={origin}&destination={dest}
     */
    @GET("routes")
    Call<List<UnifiedScheduleDTO>> searchRoutes(
            @Query("start") String start,
            @Query("destination") String destination
    );

    /**
     * Get all schedules (bus + train) from desktop app
     * GET /schedules
     */
    @GET("schedules")
    Call<List<UnifiedScheduleDTO>> getAllSchedules();

    /**
     * Get all bus schedules
     * GET /api/schedules/bus
     */
    @GET("schedules/bus")
    Call<ApiResponseWrapper<BusScheduleDTO>> getAllBusSchedules();

    /**
     * Get all train schedules
     * GET /api/schedules/train
     */
    @GET("schedules/train")
    Call<ApiResponseWrapper<TrainScheduleDTO>> getAllTrainSchedules();

    /**
     * Health check endpoint
     * GET /api/health
     */
    @GET("health")
    Call<HealthResponse> healthCheck();

    /**
     * Simple health response class
     */
    class HealthResponse {
        private String status;
        private String timestamp;
        private String service;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public String getService() { return service; }
        public void setService(String service) { this.service = service; }
    }
}
