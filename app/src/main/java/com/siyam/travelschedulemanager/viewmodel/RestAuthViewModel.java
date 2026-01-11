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
import com.siyam.travelschedulemanager.util.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for REST API Authentication
 */
public class RestAuthViewModel extends AndroidViewModel {
    private static final String TAG = "RestAuthViewModel";

    private final ApiService apiService;
    private final TokenManager tokenManager;

    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> statusMessage = new MutableLiveData<>();
    private final MutableLiveData<UserDTO> currentUser = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RestAuthViewModel(@NonNull Application application) {
        super(application);
        this.apiService = RetrofitClient.getInstance(application).getApiService();
        this.tokenManager = new TokenManager(application);
    }

    // ==================== Login ====================

    public void login(String username, String password) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        LoginRequest request = new LoginRequest(username, password);
        
        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    
                    if (loginResponse.isSuccess() && loginResponse.getToken() != null) {
                        // Save token and user info
                        tokenManager.saveToken(loginResponse.getToken());
                        
                        UserDTO user = loginResponse.getUser();
                        if (user != null) {
                            tokenManager.saveUserInfo(
                                    user.getId(),
                                    user.getUsername(),
                                    user.getFullName(),
                                    user.getEmail(),
                                    user.getRole()
                            );
                            currentUser.setValue(user);
                        }
                        
                        loginSuccess.setValue(true);
                        Log.i(TAG, "Login successful for: " + username);
                    } else {
                        loginSuccess.setValue(false);
                        errorMessage.setValue(loginResponse.getError() != null ? 
                                loginResponse.getError() : "Login failed");
                    }
                } else {
                    loginSuccess.setValue(false);
                    errorMessage.setValue("Server error: " + response.code());
                    Log.e(TAG, "Login failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                isLoading.setValue(false);
                loginSuccess.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Login network error", t);
            }
        });
    }

    // ==================== Register ====================

    public void register(String username, String email, String password, String fullName, String role) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        RegisterRequest request = new RegisterRequest(username, email, password, fullName, role);
        
        apiService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();
                    
                    if (registerResponse.isSuccess()) {
                        registerSuccess.setValue(true);
                        statusMessage.setValue(registerResponse.getMessage());
                        Log.i(TAG, "Registration successful: " + username + " (Status: " + registerResponse.getStatus() + ")");
                    } else {
                        registerSuccess.setValue(false);
                        if (registerResponse.getErrors() != null && !registerResponse.getErrors().isEmpty()) {
                            errorMessage.setValue(String.join("\n", registerResponse.getErrors()));
                        } else {
                            errorMessage.setValue("Registration failed");
                        }
                    }
                } else {
                    registerSuccess.setValue(false);
                    errorMessage.setValue("Server error: " + response.code());
                    Log.e(TAG, "Registration failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                isLoading.setValue(false);
                registerSuccess.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Registration network error", t);
            }
        });
    }

    // ==================== Check Status ====================

    public void checkAccountStatus(String username) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        apiService.checkStatus(username).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    statusMessage.setValue(response.body().getMessage());
                    Log.i(TAG, "Status: " + response.body().getStatus() + " - " + response.body().getMessage());
                } else {
                    errorMessage.setValue("Failed to check status: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
                Log.e(TAG, "Status check network error", t);
            }
        });
    }

    // ==================== Logout ====================

    public void logout() {
        tokenManager.logout();
        currentUser.setValue(null);
        Log.i(TAG, "User logged out");
    }

    // ==================== LiveData Getters ====================

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public LiveData<String> getStatusMessage() {
        return statusMessage;
    }

    public LiveData<UserDTO> getCurrentUser() {
        return currentUser;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }

    public void clearError() {
        errorMessage.setValue(null);
    }
}
