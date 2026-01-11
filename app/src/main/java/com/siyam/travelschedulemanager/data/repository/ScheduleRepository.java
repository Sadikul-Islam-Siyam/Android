package com.siyam.travelschedulemanager.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.siyam.travelschedulemanager.data.cache.ScheduleCacheManager;
import com.siyam.travelschedulemanager.data.remote.ApiService;
import com.siyam.travelschedulemanager.data.remote.RetrofitClient;
import com.siyam.travelschedulemanager.data.remote.dto.ApiResponseWrapper;
import com.siyam.travelschedulemanager.data.remote.dto.BusScheduleDTO;
import com.siyam.travelschedulemanager.data.remote.dto.TrainScheduleDTO;
import com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO;
import com.siyam.travelschedulemanager.util.NetworkManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for schedule data
 * Implements smart caching strategy for offline/online scenarios:
 * - Online: Fetch from REST API and cache locally
 * - Offline: Use cached data
 * - Automatic fallback to cache on network errors
 */
public class ScheduleRepository {
    private static final String TAG = "ScheduleRepository";
    
    private static ScheduleRepository instance;
    private final ApiService apiService;
    private final ScheduleCacheManager cacheManager;
    private final NetworkManager networkManager;
    private final Context context;

    private ScheduleRepository(Context context) {
        this.context = context.getApplicationContext();
        this.apiService = RetrofitClient.getInstance().getApiService();
        this.cacheManager = ScheduleCacheManager.getInstance(context);
        this.networkManager = NetworkManager.getInstance(context);
    }

    public static synchronized ScheduleRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ScheduleRepository(context);
        }
        return instance;
    }

    /**
     * Get all bus schedules with smart caching
     * 
     * Strategy:
     * 1. If online: Fetch from API, cache, and return
     * 2. If offline: Return cached data
     * 3. If API fails: Fallback to cache
     */
    public LiveData<Resource<List<BusScheduleDTO>>> getAllBusSchedules() {
        MutableLiveData<Resource<List<BusScheduleDTO>>> result = new MutableLiveData<>();
        
        if (networkManager.isOnline()) {
            // Online: Fetch from API
            result.setValue(Resource.loading(null));
            
            apiService.getAllBusSchedules().enqueue(new Callback<ApiResponseWrapper<BusScheduleDTO>>() {
                @Override
                public void onResponse(Call<ApiResponseWrapper<BusScheduleDTO>> call, Response<ApiResponseWrapper<BusScheduleDTO>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getValue() != null) {
                        List<BusScheduleDTO> schedules = response.body().getValue();
                        // Cache the fresh data
                        cacheManager.cacheBusSchedules(schedules);
                        result.setValue(Resource.success(schedules, false));
                        Log.d(TAG, "Fetched " + schedules.size() + " bus schedules from API (Count: " + response.body().getCount() + ")");
                    } else {
                        // API error, fallback to cache
                        Log.w(TAG, "API error, using cached data");
                        result.setValue(Resource.success(cacheManager.getCachedBusSchedules(), true));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseWrapper<BusScheduleDTO>> call, Throwable t) {
                    Log.e(TAG, "API call failed", t);
                    // Network error, fallback to cache
                    List<BusScheduleDTO> cached = cacheManager.getCachedBusSchedules();
                    if (!cached.isEmpty()) {
                        result.setValue(Resource.success(cached, true));
                    } else {
                        result.setValue(Resource.error("No internet connection and no cached data", null));
                    }
                }
            });
        } else {
            // Offline: Use cache immediately
            Log.d(TAG, "Offline mode, using cached bus schedules");
            List<BusScheduleDTO> cached = cacheManager.getCachedBusSchedules();
            if (!cached.isEmpty()) {
                result.setValue(Resource.success(cached, true));
            } else {
                result.setValue(Resource.error("No internet connection. Please connect to view schedules.", null));
            }
        }
        
        return result;
    }

    /**
     * Get all train schedules with smart caching
     */
    public LiveData<Resource<List<TrainScheduleDTO>>> getAllTrainSchedules() {
        MutableLiveData<Resource<List<TrainScheduleDTO>>> result = new MutableLiveData<>();
        
        if (networkManager.isOnline()) {
            result.setValue(Resource.loading(null));
            
            apiService.getAllTrainSchedules().enqueue(new Callback<ApiResponseWrapper<TrainScheduleDTO>>() {
                @Override
                public void onResponse(Call<ApiResponseWrapper<TrainScheduleDTO>> call, Response<ApiResponseWrapper<TrainScheduleDTO>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getValue() != null) {
                        List<TrainScheduleDTO> schedules = response.body().getValue();
                        cacheManager.cacheTrainSchedules(schedules);
                        result.setValue(Resource.success(schedules, false));
                        Log.d(TAG, "Fetched " + schedules.size() + " train schedules from API (Count: " + response.body().getCount() + ")");
                    } else {
                        Log.w(TAG, "API error, using cached data");
                        result.setValue(Resource.success(cacheManager.getCachedTrainSchedules(), true));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseWrapper<TrainScheduleDTO>> call, Throwable t) {
                    Log.e(TAG, "API call failed", t);
                    List<TrainScheduleDTO> cached = cacheManager.getCachedTrainSchedules();
                    if (!cached.isEmpty()) {
                        result.setValue(Resource.success(cached, true));
                    } else {
                        result.setValue(Resource.error("No internet connection and no cached data", null));
                    }
                }
            });
        } else {
            Log.d(TAG, "Offline mode, using cached train schedules");
            List<TrainScheduleDTO> cached = cacheManager.getCachedTrainSchedules();
            if (!cached.isEmpty()) {
                result.setValue(Resource.success(cached, true));
            } else {
                result.setValue(Resource.error("No internet connection. Please connect to view schedules.", null));
            }
        }
        
        return result;
    }

    /**
     * Get all schedules (unified) with smart caching
     */
    public LiveData<Resource<List<UnifiedScheduleDTO>>> getAllSchedules() {
        MutableLiveData<Resource<List<UnifiedScheduleDTO>>> result = new MutableLiveData<>();
        
        if (networkManager.isOnline()) {
            result.setValue(Resource.loading(null));
            
            apiService.getAllSchedules().enqueue(new Callback<ApiResponseWrapper<UnifiedScheduleDTO>>() {
                @Override
                public void onResponse(Call<ApiResponseWrapper<UnifiedScheduleDTO>> call, Response<ApiResponseWrapper<UnifiedScheduleDTO>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getValue() != null) {
                        List<UnifiedScheduleDTO> schedules = response.body().getValue();
                        cacheManager.cacheUnifiedSchedules(schedules);
                        result.setValue(Resource.success(schedules, false));
                        Log.d(TAG, "Fetched " + schedules.size() + " unified schedules from API");
                    } else {
                        Log.w(TAG, "API error, using cached data");
                        result.setValue(Resource.success(cacheManager.getCachedUnifiedSchedules(), true));
                    }
                }

                @Override
                public void onFailure(Call<ApiResponseWrapper<UnifiedScheduleDTO>> call, Throwable t) {
                    Log.e(TAG, "API call failed", t);
                    List<UnifiedScheduleDTO> cached = cacheManager.getCachedUnifiedSchedules();
                    if (!cached.isEmpty()) {
                        result.setValue(Resource.success(cached, true));
                    } else {
                        result.setValue(Resource.error("No internet connection and no cached data", null));
                    }
                }
            });
        } else {
            Log.d(TAG, "Offline mode, using cached unified schedules");
            List<UnifiedScheduleDTO> cached = cacheManager.getCachedUnifiedSchedules();
            if (!cached.isEmpty()) {
                result.setValue(Resource.success(cached, true));
            } else {
                result.setValue(Resource.error("No internet connection. Please connect to view schedules.", null));
            }
        }
        
        return result;
    }

    /**
     * Search routes (REQUIRES ONLINE)
     * This is real-time search and cannot work offline
     */
    public LiveData<Resource<List<UnifiedScheduleDTO>>> searchRoutes(String start, String destination) {
        MutableLiveData<Resource<List<UnifiedScheduleDTO>>> result = new MutableLiveData<>();
        
        if (!networkManager.isOnline()) {
            // Must be online for search
            result.setValue(Resource.error("Internet connection required for route search", null));
            return result;
        }
        
        result.setValue(Resource.loading(null));
        
        apiService.searchRoutes(start, destination).enqueue(new Callback<ApiResponseWrapper<UnifiedScheduleDTO>>() {
            @Override
            public void onResponse(Call<ApiResponseWrapper<UnifiedScheduleDTO>> call, Response<ApiResponseWrapper<UnifiedScheduleDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UnifiedScheduleDTO> routes = response.body().getValue();
                    result.setValue(Resource.success(routes, false));
                    Log.d(TAG, "Found " + routes.size() + " routes for " + start + " -> " + destination + " (Count: " + response.body().getCount() + ")");
                } else {
                    result.setValue(Resource.error("No routes found or server error", new ArrayList<>()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponseWrapper<UnifiedScheduleDTO>> call, Throwable t) {
                Log.e(TAG, "Route search failed", t);
                result.setValue(Resource.error("Search failed: " + t.getMessage(), null));
            }
        });
        
        return result;
    }

    /**
     * Check API server health
     */
    public LiveData<Resource<Boolean>> checkServerHealth() {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        
        if (!networkManager.isOnline()) {
            result.setValue(Resource.error("No internet connection", false));
            return result;
        }
        
        result.setValue(Resource.loading(false));
        
        apiService.healthCheck().enqueue(new Callback<ApiService.HealthResponse>() {
            @Override
            public void onResponse(Call<ApiService.HealthResponse> call, Response<ApiService.HealthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    result.setValue(Resource.success(true, false));
                    Log.d(TAG, "Server is healthy: " + response.body().getService());
                } else {
                    result.setValue(Resource.error("Server not responding", false));
                }
            }

            @Override
            public void onFailure(Call<ApiService.HealthResponse> call, Throwable t) {
                Log.e(TAG, "Health check failed", t);
                result.setValue(Resource.error("Cannot reach server: " + t.getMessage(), false));
            }
        });
        
        return result;
    }

    /**
     * Clear cache
     */
    public void clearCache() {
        cacheManager.clearCache();
    }

    /**
     * Check if cache has data
     */
    public boolean hasCachedData() {
        return cacheManager.hasCachedData();
    }

    /**
     * Get last cache update time
     */
    public long getLastCacheUpdateTime() {
        return cacheManager.getLastUpdateTime();
    }

    /**
     * Resource wrapper class for handling loading, success, and error states
     */
    public static class Resource<T> {
        public enum Status {
            SUCCESS,
            ERROR,
            LOADING
        }

        private final Status status;
        private final T data;
        private final String message;
        private final boolean fromCache;

        private Resource(Status status, T data, String message, boolean fromCache) {
            this.status = status;
            this.data = data;
            this.message = message;
            this.fromCache = fromCache;
        }

        public static <T> Resource<T> success(T data, boolean fromCache) {
            return new Resource<>(Status.SUCCESS, data, null, fromCache);
        }

        public static <T> Resource<T> error(String message, T data) {
            return new Resource<>(Status.ERROR, data, message, false);
        }

        public static <T> Resource<T> loading(T data) {
            return new Resource<>(Status.LOADING, data, null, false);
        }

        public Status getStatus() { return status; }
        public T getData() { return data; }
        public String getMessage() { return message; }
        public boolean isFromCache() { return fromCache; }
    }
}
