package com.siyam.travelschedulemanager.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Token Manager for handling authentication tokens
 */
public class TokenManager {
    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences prefs;

    public TokenManager(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save authentication token
     */
    public void saveToken(String token) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }

    /**
     * Get authentication token
     */
    public String getToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    /**
     * Get Bearer token for Authorization header
     */
    public String getBearerToken() {
        String token = getToken();
        return token != null ? "Bearer " + token : null;
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    /**
     * Save user information
     */
    public void saveUserInfo(int userId, String username, String fullName, String email, String role) {
        prefs.edit()
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_USERNAME, username)
                .putString(KEY_FULL_NAME, fullName)
                .putString(KEY_EMAIL, email)
                .putString(KEY_USER_ROLE, role)
                .apply();
    }

    /**
     * Get user ID
     */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get username
     */
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    /**
     * Get full name
     */
    public String getFullName() {
        return prefs.getString(KEY_FULL_NAME, null);
    }

    /**
     * Get email
     */
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    /**
     * Get user role
     */
    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, null);
    }

    /**
     * Check if user is master
     */
    public boolean isMaster() {
        return Constants.ROLE_MASTER.equals(getUserRole());
    }

    /**
     * Check if user is developer
     */
    public boolean isDeveloper() {
        return Constants.ROLE_DEVELOPER.equals(getUserRole());
    }

    /**
     * Check if user has admin privileges (master or developer)
     */
    public boolean isAdmin() {
        return isMaster() || isDeveloper();
    }

    /**
     * Clear all authentication data (logout)
     */
    public void clearToken() {
        prefs.edit().clear().apply();
    }

    /**
     * Clear all user data (logout)
     */
    public void logout() {
        clearToken();
    }
}
