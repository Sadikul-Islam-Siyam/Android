package com.siyam.travelschedulemanager.data.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.siyam.travelschedulemanager.model.AuditLog;
import com.siyam.travelschedulemanager.util.Constants;

public class AuditLogRepository {
    private final FirebaseFirestore db;

    public AuditLogRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Task<Void> createAuditLog(String userId, String userName, String userRole, 
                                     String action, String entityType, String entityId, String details) {
        String id = db.collection(Constants.COLLECTION_AUDIT_LOGS).document().getId();
        
        AuditLog auditLog = new AuditLog(
            id,
            userId,
            userName,
            userRole,
            action,
            entityType,
            entityId,
            details,
            Timestamp.now(),
            "N/A" // IP address not available in Android
        );

        return db.collection(Constants.COLLECTION_AUDIT_LOGS)
                .document(id)
                .set(auditLog);
    }

    public Task<QuerySnapshot> getAllAuditLogs() {
        return db.collection(Constants.COLLECTION_AUDIT_LOGS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getAuditLogsByUser(String userId) {
        return db.collection(Constants.COLLECTION_AUDIT_LOGS)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getAuditLogsByAction(String action) {
        return db.collection(Constants.COLLECTION_AUDIT_LOGS)
                .whereEqualTo("action", action)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getAuditLogsByEntity(String entityType) {
        return db.collection(Constants.COLLECTION_AUDIT_LOGS)
                .whereEqualTo("entityType", entityType)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getRecentAuditLogs(int limit) {
        return db.collection(Constants.COLLECTION_AUDIT_LOGS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }
}
