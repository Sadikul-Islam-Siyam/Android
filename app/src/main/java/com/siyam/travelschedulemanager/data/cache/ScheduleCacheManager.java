package com.siyam.travelschedulemanager.data.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.siyam.travelschedulemanager.data.remote.dto.BusScheduleDTO;
import com.siyam.travelschedulemanager.data.remote.dto.TrainScheduleDTO;
import com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Cache manager for offline data storage
 * Stores API responses locally for offline access
 */
public class ScheduleCacheManager {
    private static final String TAG = "ScheduleCacheManager";
    private static final String PREF_NAME = "schedule_cache";
    private static final String KEY_BUS_SCHEDULES = "bus_schedules";
    private static final String KEY_TRAIN_SCHEDULES = "train_schedules";
    private static final String KEY_UNIFIED_SCHEDULES = "unified_schedules";
    private static final String KEY_LAST_UPDATE = "last_update";
    private static final String KEY_CACHE_VERSION = "cache_version";
    private static final int CURRENT_CACHE_VERSION = 1;
    
    // Cache validity: 24 hours
    private static final long CACHE_VALIDITY_MS = 24 * 60 * 60 * 1000;
    
    private static ScheduleCacheManager instance;
    private final SharedPreferences preferences;
    private final Gson gson;

    private ScheduleCacheManager(Context context) {
        this.preferences = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        
        // Check cache version and clear if outdated
        checkCacheVersion();
    }

    public static synchronized ScheduleCacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new ScheduleCacheManager(context);
        }
        return instance;
    }

    /**
     * Cache bus schedules
     */
    public void cacheBusSchedules(List<BusScheduleDTO> schedules) {
        try {
            String json = gson.toJson(schedules);
            preferences.edit()
                    .putString(KEY_BUS_SCHEDULES, json)
                    .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                    .apply();
            Log.d(TAG, "Cached " + schedules.size() + " bus schedules");
        } catch (Exception e) {
            Log.e(TAG, "Error caching bus schedules", e);
        }
    }

    /**
     * Cache train schedules
     */
    public void cacheTrainSchedules(List<TrainScheduleDTO> schedules) {
        try {
            String json = gson.toJson(schedules);
            preferences.edit()
                    .putString(KEY_TRAIN_SCHEDULES, json)
                    .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                    .apply();
            Log.d(TAG, "Cached " + schedules.size() + " train schedules");
        } catch (Exception e) {
            Log.e(TAG, "Error caching train schedules", e);
        }
    }

    /**
     * Cache unified schedules
     */
    public void cacheUnifiedSchedules(List<UnifiedScheduleDTO> schedules) {
        try {
            String json = gson.toJson(schedules);
            preferences.edit()
                    .putString(KEY_UNIFIED_SCHEDULES, json)
                    .putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
                    .apply();
            Log.d(TAG, "Cached " + schedules.size() + " unified schedules");
        } catch (Exception e) {
            Log.e(TAG, "Error caching unified schedules", e);
        }
    }

    /**
     * Get cached bus schedules
     */
    public List<BusScheduleDTO> getCachedBusSchedules() {
        try {
            String json = preferences.getString(KEY_BUS_SCHEDULES, null);
            if (json != null) {
                Type listType = new TypeToken<List<BusScheduleDTO>>(){}.getType();
                List<BusScheduleDTO> schedules = gson.fromJson(json, listType);
                Log.d(TAG, "Retrieved " + schedules.size() + " cached bus schedules");
                return schedules;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving cached bus schedules", e);
        }
        return new ArrayList<>();
    }

    /**
     * Get cached train schedules
     */
    public List<TrainScheduleDTO> getCachedTrainSchedules() {
        try {
            String json = preferences.getString(KEY_TRAIN_SCHEDULES, null);
            if (json != null) {
                Type listType = new TypeToken<List<TrainScheduleDTO>>(){}.getType();
                List<TrainScheduleDTO> schedules = gson.fromJson(json, listType);
                Log.d(TAG, "Retrieved " + schedules.size() + " cached train schedules");
                return schedules;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving cached train schedules", e);
        }
        return new ArrayList<>();
    }

    /**
     * Get cached unified schedules
     */
    public List<UnifiedScheduleDTO> getCachedUnifiedSchedules() {
        try {
            String json = preferences.getString(KEY_UNIFIED_SCHEDULES, null);
            if (json != null) {
                Type listType = new TypeToken<List<UnifiedScheduleDTO>>(){}.getType();
                List<UnifiedScheduleDTO> schedules = gson.fromJson(json, listType);
                Log.d(TAG, "Retrieved " + schedules.size() + " cached unified schedules");
                return schedules;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving cached unified schedules", e);
        }
        return new ArrayList<>();
    }

    /**
     * Check if cache is still valid
     */
    public boolean isCacheValid() {
        long lastUpdate = preferences.getLong(KEY_LAST_UPDATE, 0);
        long currentTime = System.currentTimeMillis();
        boolean valid = (currentTime - lastUpdate) < CACHE_VALIDITY_MS;
        Log.d(TAG, "Cache valid: " + valid + " (age: " + (currentTime - lastUpdate) / 1000 / 60 + " minutes)");
        return valid;
    }

    /**
     * Check if cache has data
     */
    public boolean hasCachedData() {
        boolean hasBus = preferences.contains(KEY_BUS_SCHEDULES);
        boolean hasTrain = preferences.contains(KEY_TRAIN_SCHEDULES);
        boolean hasUnified = preferences.contains(KEY_UNIFIED_SCHEDULES);
        return hasBus || hasTrain || hasUnified;
    }

    /**
     * Get last cache update time
     */
    public long getLastUpdateTime() {
        return preferences.getLong(KEY_LAST_UPDATE, 0);
    }

    /**
     * Clear all cached data
     */
    public void clearCache() {
        preferences.edit()
                .remove(KEY_BUS_SCHEDULES)
                .remove(KEY_TRAIN_SCHEDULES)
                .remove(KEY_UNIFIED_SCHEDULES)
                .remove(KEY_LAST_UPDATE)
                .apply();
        Log.i(TAG, "Cache cleared");
    }

    /**
     * Check cache version and clear if outdated
     */
    private void checkCacheVersion() {
        int savedVersion = preferences.getInt(KEY_CACHE_VERSION, 0);
        if (savedVersion != CURRENT_CACHE_VERSION) {
            Log.i(TAG, "Cache version mismatch. Clearing old cache.");
            clearCache();
            preferences.edit()
                    .putInt(KEY_CACHE_VERSION, CURRENT_CACHE_VERSION)
                    .apply();
        }
    }
}
