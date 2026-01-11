package com.siyam.travelschedulemanager.util;

import java.util.Arrays;
import java.util.List;

public class Constants {

    // User Roles (USER for regular users, MASTER for admin)
    public static final String ROLE_USER = "USER";
    public static final String ROLE_MASTER = "MASTER";
    
    // Deprecated role - removed from system
    @Deprecated public static final String ROLE_DEVELOPER = "DEVELOPER";

    // Account Status (simplified - all approved users)
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_LOCKED = "LOCKED";
    
    // Deprecated status - kept for backward compatibility
    @Deprecated public static final String STATUS_PENDING = "PENDING";
    @Deprecated public static final String STATUS_REJECTED = "REJECTED";
    
    // Transport Types
    public static final String TRANSPORT_BUS = "BUS";
    public static final String TRANSPORT_TRAIN = "TRAIN";



    // Firestore Collections (simplified - only user data in Firebase)
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_PLANS = "plans";
    public static final String COLLECTION_AUDIT_LOGS = "audit_logs";
    
    // Deprecated collections - kept for backward compatibility (schedules from REST API now)
    @Deprecated public static final String COLLECTION_SCHEDULES = "schedules";
    @Deprecated public static final String COLLECTION_ROUTES = "routes";
    @Deprecated public static final String COLLECTION_PENDING_ROUTES = "pending_routes";
    
    // Deprecated request status - kept for backward compatibility
    @Deprecated public static final String REQUEST_PENDING = "PENDING";
    @Deprecated public static final String REQUEST_APPROVED = "APPROVED";
    @Deprecated public static final String REQUEST_REJECTED = "REJECTED";
    
    // Audit Actions (simplified)
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    
    // Deprecated actions - kept for backward compatibility
    @Deprecated public static final String ACTION_CREATE = "CREATE";
    @Deprecated public static final String ACTION_UPDATE = "UPDATE";
    @Deprecated public static final String ACTION_DELETE = "DELETE";
    @Deprecated public static final String ACTION_APPROVE = "APPROVE";
    @Deprecated public static final String ACTION_REJECT = "REJECT";
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

    // Bangladesh Districts (all 64 districts)
    public static final List<String> BANGLADESH_CITIES = Arrays.asList(
            "Barguna", "Barishal", "Bhola", "Jhalokati", "Patuakhali", "Pirojpur",
            "Bandarban", "Brahmanbaria", "Chandpur", "Chattogram", "Coxs Bazar", "Cumilla",
            "Feni", "Khagrachari", "Lakshmipur", "Noakhali", "Rangamati",
            "Dhaka", "Faridpur", "Gazipur", "Gopalganj", "Kishoreganj", "Madaripur",
            "Manikganj", "Munshiganj", "Narayanganj", "Narsingdi", "Rajbari",
            "Shariatpur", "Tangail",
            "Bagerhat", "Chuadanga", "Jashore", "Jhenaidah", "Khulna", "Kushtia",
            "Magura", "Meherpur", "Narail", "Satkhira",
            "Jamalpur", "Mymensingh", "Netrokona", "Sherpur",
            "Bogura", "Joypurhat", "Naogaon", "Natore", "Chapai Nawabganj", "Pabna",
            "Rajshahi", "Sirajganj",
            "Dinajpur", "Gaibandha", "Kurigram", "Lalmonirhat", "Nilphamari",
            "Panchagarh", "Rangpur", "Thakurgaon",
            "Habiganj", "Moulvibazar", "Sunamganj", "Sylhet"
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
