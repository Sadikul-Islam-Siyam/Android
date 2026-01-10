package com.siyam.travelschedulemanager.data.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.siyam.travelschedulemanager.model.User;
import com.siyam.travelschedulemanager.util.Constants;

import java.util.Calendar;

public class UserRepository {
    private final FirebaseFirestore db;

    public UserRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Create new user document
     */
    public Task<Void> createUser(User user) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(user.getUid())
                .set(user);
    }

    /**
     * Get user by ID
     */
    public Task<DocumentSnapshot> getUser(String uid) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get();
    }

    /**
     * Update user
     */
    public Task<Void> updateUser(String uid, User user) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .set(user);
    }

    /**
     * Update user status
     */
    public Task<Void> updateUserStatus(String uid, String status, String reason) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .update("status", status, "rejectionReason", reason);
    }

    /**
     * Update user role
     */
    public Task<Void> updateUserRole(String uid, String role) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .update("role", role);
    }

    /**
     * Increment failed login attempts
     */
    public Task<Void> incrementFailedAttempts(String uid, int currentAttempts) {
        int newAttempts = currentAttempts + 1;
        
        if (newAttempts >= Constants.MAX_FAILED_ATTEMPTS) {
            // Lock account
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, Constants.LOCK_DURATION_MINUTES);
            Timestamp lockUntil = new Timestamp(calendar.getTime());
            
            return db.collection(Constants.COLLECTION_USERS)
                    .document(uid)
                    .update(
                            "failedAttempts", newAttempts,
                            "status", Constants.STATUS_LOCKED,
                            "lockUntil", lockUntil
                    );
        } else {
            return db.collection(Constants.COLLECTION_USERS)
                    .document(uid)
                    .update("failedAttempts", newAttempts);
        }
    }

    /**
     * Reset failed attempts
     */
    public Task<Void> resetFailedAttempts(String uid) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .update("failedAttempts", 0);
    }

    /**
     * Unlock user account
     */
    public Task<Void> unlockAccount(String uid) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .update(
                        "status", Constants.STATUS_APPROVED,
                        "failedAttempts", 0,
                        "lockUntil", null
                );
    }

    /**
     * Get all pending users
     */
    public Task<QuerySnapshot> getPendingUsers() {
        return db.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("status", Constants.STATUS_PENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
    }

    /**
     * Get all users
     */
    public Task<QuerySnapshot> getAllUsers() {
        return db.collection(Constants.COLLECTION_USERS)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get();
    }

    /**
     * Check if user exists by email
     */
    public Task<QuerySnapshot> getUserByEmail(String email) {
        return db.collection(Constants.COLLECTION_USERS)
                .whereEqualTo("email", email)
                .limit(1)
                .get();
    }

    /**
     * Lock user account
     */
    public Task<Void> lockAccount(String uid, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minutes);
        Timestamp lockUntil = new Timestamp(calendar.getTime());
        
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .update(
                        "status", Constants.STATUS_LOCKED,
                        "lockUntil", lockUntil
                );
    }

    /**
     * Delete user
     */
    public Task<Void> deleteUser(String uid) {
        return db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .delete();
    }
}
