package com.siyam.travelschedulemanager.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.siyam.travelschedulemanager.data.remote.dto.BusScheduleDTO;
import com.siyam.travelschedulemanager.data.remote.dto.TrainScheduleDTO;
import com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO;
import com.siyam.travelschedulemanager.data.repository.ScheduleRepository;
import com.siyam.travelschedulemanager.data.repository.ScheduleRepository.Resource;
import com.siyam.travelschedulemanager.util.NetworkManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for schedule data - integrates with REST API
 * Online: Fetches from server and caches
 * Offline: Uses cached data
 */
public class ScheduleViewModel extends AndroidViewModel {
    private final ScheduleRepository scheduleRepository;
    private final NetworkManager networkManager;
    
    private final MediatorLiveData<Resource<List<BusScheduleDTO>>> busSchedules = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<TrainScheduleDTO>>> trainSchedules = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<UnifiedScheduleDTO>>> searchResults = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> isServerReachable = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public ScheduleViewModel(Application application) {
        super(application);
        this.scheduleRepository = ScheduleRepository.getInstance(application);
        this.networkManager = NetworkManager.getInstance(application);
    }

    // Getters for LiveData
    public LiveData<Resource<List<BusScheduleDTO>>> getBusSchedules() {
        return busSchedules;
    }

    public LiveData<Resource<List<TrainScheduleDTO>>> getTrainSchedules() {
        return trainSchedules;
    }

    public LiveData<Resource<List<UnifiedScheduleDTO>>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Boolean> getIsServerReachable() {
        return isServerReachable;
    }

    public LiveData<Boolean> isOnline() {
        return networkManager.getIsOnlineLiveData();
    }

    public LiveData<NetworkManager.NetworkStatus> getNetworkStatus() {
        return networkManager.getNetworkStatusLiveData();
    }

    public LiveData<String> getMessage() {
        return message;
    }
    
    /**
     * Deprecated - for backward compatibility with old UI
     */
    @Deprecated
    public LiveData<String> getError() {
        return message; // Same as message
    }
    
    /**
     * Deprecated - for backward compatibility, returns empty list
     * Old UI should be updated to use getBusSchedules() or getTrainSchedules()
     */
    @Deprecated
    public LiveData<List<com.siyam.travelschedulemanager.model.Schedule>> getSchedules() {
        MutableLiveData<List<com.siyam.travelschedulemanager.model.Schedule>> emptyList = new MutableLiveData<>();
        emptyList.setValue(new ArrayList<>());
        return emptyList;
    }
    
    /**
     * Deprecated - for backward compatibility
     * Schedules now come from REST API, use loadBusSchedules() or loadTrainSchedules()
     */
    @Deprecated
    public void loadAllSchedules() {
        loadBusSchedules();
        loadTrainSchedules();
    }
    
    /**
     * Deprecated - no longer supported, schedules are read-only from REST API
     */
    @Deprecated
    public void createSchedule(com.siyam.travelschedulemanager.model.Schedule schedule) {
        message.setValue("Schedule management has been moved to desktop app");
    }
    
    /**
     * Deprecated - no longer supported
     */
    @Deprecated
    public void findOptimalRoutes(String origin, String destination, java.util.Date travelDate, int maxLegs) {
        message.setValue("Use searchRoutes() instead");
        searchRoutes(origin, destination);
    }

    /**
     * Load all bus schedules
     * Online: Fetches from API and caches
     * Offline: Returns cached data
     */
    public void loadBusSchedules() {
        LiveData<Resource<List<BusScheduleDTO>>> source = scheduleRepository.getAllBusSchedules();
        busSchedules.addSource(source, resource -> {
            busSchedules.setValue(resource);
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                message.setValue(resource.isFromCache() ? 
                    "Loaded from cache (offline)" : "Loaded from server");
            }
        });
    }

    /**
     * Load all train schedules
     * Online: Fetches from API and caches
     * Offline: Returns cached data
     */
    public void loadTrainSchedules() {
        LiveData<Resource<List<TrainScheduleDTO>>> source = scheduleRepository.getAllTrainSchedules();
        trainSchedules.addSource(source, resource -> {
            trainSchedules.setValue(resource);
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                message.setValue(resource.isFromCache() ? 
                    "Loaded from cache (offline)" : "Loaded from server");
            }
        });
    }

    /**
     * Search for routes between origin and destination
     * Requires online connection
     */
    public void searchRoutes(String start, String destination) {
        Boolean online = networkManager.isOnline();
        if (online == null || !online) {
            searchResults.setValue(Resource.error("Route search requires internet connection", null));
            return;
        }

        LiveData<Resource<List<UnifiedScheduleDTO>>> source = scheduleRepository.searchRoutes(start, destination);
        searchResults.addSource(source, resource -> {
            searchResults.setValue(resource);
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS && resource.getData() != null) {
                message.setValue("Found " + resource.getData().size() + " routes");
            }
        });
    }

    /**
     * Check if the REST API server is reachable
     */
    public void checkServerHealth() {
        LiveData<Resource<Boolean>> source = scheduleRepository.checkServerHealth();
        source.observeForever(resource -> {
            if (resource != null && resource.getStatus() == Resource.Status.SUCCESS) {
                Boolean healthy = resource.getData();
                isServerReachable.setValue(healthy != null && healthy);
                message.setValue((healthy != null && healthy) ? "Server is reachable" : "Server is not responding");
            } else if (resource != null && resource.getStatus() == Resource.Status.ERROR) {
                isServerReachable.setValue(false);
                message.setValue("Server check failed: " + resource.getMessage());
            }
        });
    }

    /**
     * Refresh all cached data (when back online)
     */
    public void refreshAllData() {
        loadBusSchedules();
        loadTrainSchedules();
        checkServerHealth();
    }

    /**
     * Clear all cached schedule data
     */
    public void clearCache() {
        scheduleRepository.clearCache();
        message.setValue("Cache cleared");
    }
}
