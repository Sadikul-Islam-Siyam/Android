package com.siyam.travelschedulemanager.data.remote;

import android.content.Context;
import android.util.Log;

import com.siyam.travelschedulemanager.util.TokenManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    
    // Base URL Configuration
    // Using ADB reverse for USB connection (works regardless of WiFi network)
    // Run: adb reverse tcp:8080 tcp:8080
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api/";
    
    private static RetrofitClient instance;
    private final ApiService apiService;
    private String baseUrl;
    private TokenManager tokenManager;

    private RetrofitClient() {
        this(DEFAULT_BASE_URL, null);
    }

    private RetrofitClient(String baseUrl, Context context) {
        this.baseUrl = baseUrl;
        if (context != null) {
            this.tokenManager = new TokenManager(context);
        }
        
        // Create HTTP logging interceptor for debugging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> 
            Log.d(TAG, "API: " + message)
        );
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Configure OkHttp client with auth interceptor
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        // Add authentication interceptor if token manager is available
        if (tokenManager != null) {
            okHttpBuilder.addInterceptor(chain -> {
                Request originalRequest = chain.request();
                
                // Skip auth for login, register, and status endpoints
                String path = originalRequest.url().encodedPath();
                if (path.contains("/auth/login") || 
                    path.contains("/auth/register") || 
                    path.contains("/auth/status")) {
                    return chain.proceed(originalRequest);
                }

                // Add authorization header if token exists
                String token = tokenManager.getBearerToken();
                if (token != null) {
                    Request authenticatedRequest = originalRequest.newBuilder()
                            .header("Authorization", token)
                            .build();
                    return chain.proceed(authenticatedRequest);
                }

                return chain.proceed(originalRequest);
            });
        }

        OkHttpClient okHttpClient = okHttpBuilder.build();

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
     * Get singleton instance with context for token management
     * @param context Application context
     */
    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(DEFAULT_BASE_URL, context);
        }
        return instance;
    }

    /**
     * Get singleton instance with custom base URL and context
     * @param baseUrl Custom base URL for the API
     * @param context Application context
     */
    public static synchronized RetrofitClient getInstance(String baseUrl, Context context) {
        if (instance == null || !instance.baseUrl.equals(baseUrl)) {
            instance = new RetrofitClient(baseUrl, context);
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
     * @param context Application context
     */
    public static synchronized void updateBaseUrl(String newBaseUrl, Context context) {
        Log.i(TAG, "Updating base URL to: " + newBaseUrl);
        resetInstance();
        instance = new RetrofitClient(newBaseUrl, context);
    }
}
