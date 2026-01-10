package com.siyam.travelschedulemanager.util;

import java.util.Arrays;
import java.util.List;

public class Constants {

    // User Roles
    public static final String ROLE_USER = "USER";
    public static final String ROLE_DEVELOPER = "DEVELOPER";
    public static final String ROLE_MASTER = "MASTER";

    // Account Status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_LOCKED = "LOCKED";

    // Transport Types
    public static final String TRANSPORT_BUS = "BUS";
    public static final String TRANSPORT_TRAIN = "TRAIN";

    // Request Status
    public static final String REQUEST_PENDING = "PENDING";
    public static final String REQUEST_APPROVED = "APPROVED";
    public static final String REQUEST_REJECTED = "REJECTED";

    // Change Types
    public static final String CHANGE_ADD = "ADD";
    public static final String CHANGE_EDIT = "EDIT";
    public static final String CHANGE_DELETE = "DELETE";

    // Firestore Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_PLANS = "plans";
    public static final String COLLECTION_SCHEDULES = "schedules";
    public static final String COLLECTION_ROUTES = "routes";
    public static final String COLLECTION_PENDING_ROUTES = "pendingRoutes";    public static final String COLLECTION_AUDIT_LOGS = "audit_logs";
    
    // Audit Actions
    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_APPROVE = "APPROVE";
    public static final String ACTION_REJECT = "REJECT";
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_LOCK = "LOCK";
    public static final String ACTION_UNLOCK = "UNLOCK";
    public static final String ACTION_ROLE_CHANGE = "ROLE_CHANGE";
    // Preferences Keys
    public static final String PREF_THEME = "theme";
    public static final String PREF_LANGUAGE = "language";
    public static final String PREF_LAST_SYNC = "lastSync";

    // Security
    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final int LOCK_DURATION_MINUTES = 30;
    public static final int MIN_TRANSFER_TIME_MINUTES = 30;

    // Plan Limits
    public static final int MAX_PLAN_LEGS = 3;
    public static final int MIN_PLAN_LEGS = 1;

    // Bangladesh Cities (19 major cities)
    public static final List<String> BANGLADESH_CITIES = Arrays.asList(
            "Dhaka",
            "Chittagong",
            "Khulna",
            "Rajshahi",
            "Sylhet",
            "Barisal",
            "Rangpur",
            "Mymensingh",
            "Comilla",
            "Narayanganj",
            "Gazipur",
            "Jessore",
            "Bogra",
            "Dinajpur",
            "Cox's Bazar",
            "Brahmanbaria",
            "Tangail",
            "Pabna",
            "Faridpur"
    );

    // Days of Week
    public static final List<String> DAYS_OF_WEEK = Arrays.asList(
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday"
    );

    // Intent Keys
    public static final String EXTRA_PLAN_ID = "plan_id";
    public static final String EXTRA_SCHEDULE_ID = "schedule_id";
    public static final String EXTRA_ROUTE_ID = "route_id";
    public static final String EXTRA_USER_ID = "user_id";

    // Notification Channels
    public static final String CHANNEL_ID_GENERAL = "general";
    public static final String CHANNEL_ID_APPROVALS = "approvals";

    // SharedPreferences
    public static final String PREFS_NAME = "TravelSchedulePrefs";
}
