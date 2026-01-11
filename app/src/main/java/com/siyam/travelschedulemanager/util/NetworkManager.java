package com.siyam.travelschedulemanager.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Network connectivity manager
 * Monitors online/offline status and provides connectivity information
 */
public class NetworkManager {
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance;
    
    private final Context context;
    private final MutableLiveData<Boolean> isOnlineLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<NetworkStatus> networkStatusLiveData = new MutableLiveData<>(NetworkStatus.OFFLINE);
    
    private ConnectivityManager connectivityManager;

    private NetworkManager(Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        
        // Initial check
        updateNetworkStatus();
        
        // Register network callback for real-time monitoring
        registerNetworkCallback();
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkManager(context);
        }
        return instance;
    }

    /**
     * Check if device is currently online
     */
    public boolean isOnline() {
        if (connectivityManager == null) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return false;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            return capabilities != null && (
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            );
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    /**
     * Get LiveData for online status
     */
    public LiveData<Boolean> getIsOnlineLiveData() {
        return isOnlineLiveData;
    }

    /**
     * Get LiveData for detailed network status
     */
    public LiveData<NetworkStatus> getNetworkStatusLiveData() {
        return networkStatusLiveData;
    }

    /**
     * Update network status
     */
    private void updateNetworkStatus() {
        boolean online = isOnline();
        isOnlineLiveData.postValue(online);
        
        if (online) {
            networkStatusLiveData.postValue(getDetailedNetworkStatus());
        } else {
            networkStatusLiveData.postValue(NetworkStatus.OFFLINE);
        }
        
        Log.d(TAG, "Network status updated: " + (online ? "ONLINE" : "OFFLINE"));
    }

    /**
     * Get detailed network status
     */
    private NetworkStatus getDetailedNetworkStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            if (network == null) return NetworkStatus.OFFLINE;

            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities == null) return NetworkStatus.OFFLINE;

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return NetworkStatus.WIFI;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return NetworkStatus.MOBILE;
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return NetworkStatus.ETHERNET;
            }
        }
        return NetworkStatus.UNKNOWN;
    }

    /**
     * Register network callback for real-time monitoring
     */
    private void registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    Log.d(TAG, "Network available");
                    updateNetworkStatus();
                }

                @Override
                public void onLost(Network network) {
                    Log.d(TAG, "Network lost");
                    updateNetworkStatus();
                }

                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    Log.d(TAG, "Network capabilities changed");
                    updateNetworkStatus();
                }
            });
        }
    }

    /**
     * Network status enum
     */
    public enum NetworkStatus {
        OFFLINE("Offline - Using cached data"),
        WIFI("Connected via WiFi"),
        MOBILE("Connected via Mobile Data"),
        ETHERNET("Connected via Ethernet"),
        UNKNOWN("Connected");

        private final String message;

        NetworkStatus(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public boolean isOnline() {
            return this != OFFLINE;
        }
    }
}
