package com.siyam.travelschedulemanager.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.siyam.travelschedulemanager.data.remote.ApiService;
import com.siyam.travelschedulemanager.data.remote.RetrofitClient;
import com.siyam.travelschedulemanager.data.remote.dto.*;
import com.siyam.travelschedulemanager.util.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for schedule operations using REST API
 */
public class RestScheduleViewModel extends AndroidViewModel {
    private static final String TAG = "RestScheduleViewModel";

    private final ApiService apiService;
    private final NetworkManager networkManager;

    private final MutableLiveData<List<ScheduleDTO>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<List<ScheduleDTO>> allSchedules = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RestScheduleViewModel(@NonNull Application application) {
        super(application);
        this.apiService = RetrofitClient.getInstance(application).getApiService();
        this.networkManager = NetworkManager.getInstance(application);
    }

    // ==================== Search Routes ====================

    public void searchRoutes(String start, String destination) {
        if (!networkManager.isOnline()) {
            errorMessage.setValue("Internet connection required for route search");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        // Desktop API returns List<UnifiedScheduleDTO> directly
        apiService.searchRoutes(start, destination).enqueue(new Callback<List<UnifiedScheduleDTO>>() {
            @Override
            public void onResponse(Call<List<UnifiedScheduleDTO>> call, Response<List<UnifiedScheduleDTO>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<UnifiedScheduleDTO> unified = response.body();
                    List<ScheduleDTO> converted = convertUnifiedToSchedule(unified);
                    searchResults.setValue(converted);
                    Log.d(TAG, "Found " + converted.size() + " routes for " + start + " -> " + destination);
                } else {
                    searchResults.setValue(new ArrayList<>());
                    errorMessage.setValue("No routes found");
                }
            }

            @Override
            public void onFailure(Call<List<UnifiedScheduleDTO>> call, Throwable t) {
                isLoading.setValue(false);
                searchResults.setValue(new ArrayList<>());
                errorMessage.setValue("Search failed: " + t.getMessage());
                Log.e(TAG, "Route search failed", t);
            }
        });
    }

    // ==================== Get All Schedules ====================

    public void loadAllSchedules() {
        if (!networkManager.isOnline()) {
            errorMessage.setValue("Internet connection required");
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        apiService.getAllSchedules().enqueue(new Callback<List<UnifiedScheduleDTO>>() {
            @Override
            public void onResponse(Call<List<UnifiedScheduleDTO>> call, Response<List<UnifiedScheduleDTO>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<UnifiedScheduleDTO> unified = response.body();
                    List<ScheduleDTO> converted = convertUnifiedToSchedule(unified);
                    allSchedules.setValue(converted);
                    Log.d(TAG, "Loaded " + converted.size() + " schedules");
                } else {
                    allSchedules.setValue(new ArrayList<>());
                    errorMessage.setValue("Failed to load schedules");
                }
            }

            @Override
            public void onFailure(Call<List<UnifiedScheduleDTO>> call, Throwable t) {
                isLoading.setValue(false);
                allSchedules.setValue(new ArrayList<>());
                errorMessage.setValue("Failed to load: " + t.getMessage());
                Log.e(TAG, "Failed to load schedules", t);
            }
        });
    }

    // ==================== Helper Methods ====================

    /**
     * Convert UnifiedScheduleDTO to ScheduleDTO format
     */
    private List<ScheduleDTO> convertUnifiedToSchedule(List<UnifiedScheduleDTO> unified) {
        List<ScheduleDTO> result = new ArrayList<>();
        
        for (UnifiedScheduleDTO item : unified) {
            ScheduleDTO schedule = new ScheduleDTO();
            schedule.setId(item.getName()); // Use name as ID
            schedule.setType(item.getType().toUpperCase()); // "bus" or "train"
            schedule.setOrigin(item.getStart());
            schedule.setDestination(item.getDestination());
            schedule.setDepartureTime(item.getStartTime());
            schedule.setArrivalTime(item.getArrivalTime());
            schedule.setFare(item.getFare());
            schedule.setAvailableSeats(0); // Not provided in UnifiedScheduleDTO
            
            if ("bus".equalsIgnoreCase(item.getType())) {
                schedule.setCompanyName(item.getName());
                schedule.setBusType("Standard"); // Default
            } else if ("train".equalsIgnoreCase(item.getType())) {
                schedule.setTrainName(item.getName());
                schedule.setTrainNumber(item.getName());
                schedule.setSeatClass("Second Class"); // Default
            }
            
            result.add(schedule);
        }
        
        return result;
    }

    // ==================== LiveData Getters ====================

    public LiveData<List<ScheduleDTO>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<ScheduleDTO>> getAllSchedules() {
        return allSchedules;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void clearError() {
        errorMessage.setValue(null);
    }
}
