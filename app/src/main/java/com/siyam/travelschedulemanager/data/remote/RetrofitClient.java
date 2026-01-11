package com.siyam.travelschedulemanager.data.remote;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * Singleton Retrofit Client for REST API communication
 * Manages HTTP client configuration and API service instance
 */
public class RetrofitClient {
    private static final String TAG = "RetrofitClient";
    
    // TODO: User must configure this with their server URL
    // For local testing: "http://192.168.1.X:8080/api/" (replace X with your local IP)
    // For production: "https://your-server.com/api/"
    private static final String DEFAULT_BASE_URL = "http://10.0.2.2:8080/api/"; // Android emulator localhost
    
    private static RetrofitClient instance;
    private final ApiService apiService;
    private String baseUrl;

    private RetrofitClient() {
        this(DEFAULT_BASE_URL);
    }

    private RetrofitClient(String baseUrl) {
        this.baseUrl = baseUrl;
        
        // Create HTTP logging interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> 
            Log.d(TAG, "API: " + message)
        );
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Configure OkHttp client
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        // Build Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        
        Log.i(TAG, "RetrofitClient initialized with base URL: " + this.baseUrl);
    }

    /**
     * Get singleton instance with default base URL
     */
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    /**
     * Get singleton instance with custom base URL
     * @param baseUrl Custom base URL for the API
     */
    public static synchronized RetrofitClient getInstance(String baseUrl) {
        if (instance == null || !instance.baseUrl.equals(baseUrl)) {
            instance = new RetrofitClient(baseUrl);
        }
        return instance;
    }

    /**
     * Reset instance (useful for changing server URL at runtime)
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    /**
     * Get the API service interface
     */
    public ApiService getApiService() {
        return apiService;
    }

    /**
     * Get current base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Update base URL and recreate instance
     * @param newBaseUrl New base URL
     */
    public static synchronized void updateBaseUrl(String newBaseUrl) {
        Log.i(TAG, "Updating base URL to: " + newBaseUrl);
        resetInstance();
        instance = new RetrofitClient(newBaseUrl);
    }
}
