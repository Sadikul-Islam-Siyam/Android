package com.siyam.travelschedulemanager.util;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {

    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validate password (minimum 6 characters)
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    /**
     * Validate username (minimum 3 characters, alphanumeric)
     */
    public static boolean isValidUsername(String username) {
        if (TextUtils.isEmpty(username) || username.length() < 3) {
            return false;
        }
        return username.matches("[a-zA-Z0-9_]+");
    }

    /**
     * Validate fare amount
     */
    public static boolean isValidFare(double fare) {
        return fare > 0 && fare <= 100000;
    }

    /**
     * Validate duration in minutes
     */
    public static boolean isValidDuration(int duration) {
        return duration > 0 && duration <= 1440; // Max 24 hours
    }

    /**
     * Validate time format (HH:mm)
     */
    public static boolean isValidTimeFormat(String time) {
        if (TextUtils.isEmpty(time)) {
            return false;
        }
        return time.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    }

    /**
     * Validate seat count
     */
    public static boolean isValidSeatCount(int seats) {
        return seats > 0 && seats <= 500;
    }

    /**
     * Validate plan name
     */
    public static boolean isValidPlanName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= 3;
    }

    /**
     * Validate city name
     */
    public static boolean isValidCity(String city) {
        return !TextUtils.isEmpty(city) && city.trim().length() >= 3;
    }

    /**
     * Validate operator name
     */
    public static boolean isValidOperatorName(String operatorName) {
        return !TextUtils.isEmpty(operatorName) && operatorName.trim().length() >= 2;
    }

    /**
     * Validate train number
     */
    public static boolean isValidTrainNumber(String trainNumber) {
        if (TextUtils.isEmpty(trainNumber)) {
            return false;
        }
        return trainNumber.matches("[0-9]+");
    }

    /**
     * Validate route change reason
     */
    public static boolean isValidReason(String reason) {
        return !TextUtils.isEmpty(reason) && reason.trim().length() >= 10;
    }

    /**
     * Get error message for invalid email
     */
    public static String getEmailError(String email) {
        if (TextUtils.isEmpty(email)) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        return null;
    }

    /**
     * Get error message for invalid password
     */
    public static String getPasswordError(String password) {
        if (TextUtils.isEmpty(password)) {
            return "Password is required";
        }
        if (password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        return null;
    }

    /**
     * Get error message for invalid username
     */
    public static String getUsernameError(String username) {
        if (TextUtils.isEmpty(username)) {
            return "Username is required";
        }
        if (username.length() < 3) {
            return "Username must be at least 3 characters";
        }
        if (!username.matches("[a-zA-Z0-9_]+")) {
            return "Username can only contain letters, numbers, and underscore";
        }
        return null;
    }
}
