package com.siyam.travelschedulemanager.data.remote;

import com.siyam.travelschedulemanager.data.remote.dto.ApiResponseWrapper;
import com.siyam.travelschedulemanager.data.remote.dto.BusScheduleDTO;
import com.siyam.travelschedulemanager.data.remote.dto.TrainScheduleDTO;
import com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API Service Interface
 * Defines all REST API endpoints for the desktop app integration
 * Desktop API returns: { "value": [...], "Count": X }
 */
public interface ApiService {

    /**
     * Get all schedules (bus + train) in unified format
     * GET /api/schedules
     * Response: {"value": [...], "Count": X}
     */
    @GET("schedules")
    Call<ApiResponseWrapper<UnifiedScheduleDTO>> getAllSchedules();

    /**
     * Get all bus schedules
     * GET /api/schedules/bus
     * Response: {"value": [...], "Count": X}
     */
    @GET("schedules/bus")
    Call<ApiResponseWrapper<BusScheduleDTO>> getAllBusSchedules();

    /**
     * Get all train schedules
     * GET /api/schedules/train
     * Response: {"value": [...], "Count": X}
     */
    @GET("schedules/train")
    Call<ApiResponseWrapper<TrainScheduleDTO>> getAllTrainSchedules();

    /**
     * Search routes across both bus and train schedules
     * GET /api/routes?start={start}&destination={destination}
     * Response: {"value": [...], "Count": X}
     * 
     * @param start Origin station/city
     * @param destination Destination station/city
     * @return List of matching routes
     */
    @GET("routes")
    Call<ApiResponseWrapper<UnifiedScheduleDTO>> searchRoutes(
            @Query("start") String start,
            @Query("destination") String destination
    );

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
