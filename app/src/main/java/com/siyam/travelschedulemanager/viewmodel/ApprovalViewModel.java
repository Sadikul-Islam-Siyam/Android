package com.siyam.travelschedulemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.siyam.travelschedulemanager.data.firebase.AuditLogRepository;
import com.siyam.travelschedulemanager.data.firebase.RouteRepository;
import com.siyam.travelschedulemanager.data.firebase.UserRepository;
import com.siyam.travelschedulemanager.model.AuditLog;
import com.siyam.travelschedulemanager.model.PendingRoute;
import com.siyam.travelschedulemanager.model.User;
import com.siyam.travelschedulemanager.util.Constants;
import java.util.ArrayList;
import java.util.List;

public class ApprovalViewModel extends ViewModel {
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    
    private final MutableLiveData<List<PendingRoute>> pendingRoutes = new MutableLiveData<>();
    private final MutableLiveData<List<AuditLog>> auditLogs = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ApprovalViewModel() {
        this.routeRepository = new RouteRepository();
        this.userRepository = new UserRepository();
        this.auditLogRepository = new AuditLogRepository();
    }

    public LiveData<List<PendingRoute>> getPendingRoutes() {
        return pendingRoutes;
    }

    public LiveData<List<AuditLog>> getAuditLogs() {
        return auditLogs;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadPendingRoutes() {
        routeRepository.getAllPendingRoutes()
                .addOnSuccessListener(querySnapshot -> {
                    List<PendingRoute> routes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        PendingRoute route = document.toObject(PendingRoute.class);
                        // Filter for PENDING status in code instead of query
                        if (Constants.REQUEST_PENDING.equals(route.getStatus())) {
                            routes.add(route);
                        }
                    }
                    pendingRoutes.setValue(routes);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load pending routes: " + e.getMessage());
                });
    }

    public void approvePendingRoute(String pendingRouteId, String reviewerId, String reviewerName, 
                                   String reviewerRole, String feedback) {
        routeRepository.approvePendingRoute(pendingRouteId, reviewerId, feedback)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("Route approved successfully");
                    // Create audit log
                    auditLogRepository.createAuditLog(
                        reviewerId,
                        reviewerName,
                        reviewerRole,
                        Constants.ACTION_APPROVE,
                        "pending_route",
                        pendingRouteId,
                        "Approved route change: " + feedback
                    );
                    loadPendingRoutes();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to approve route: " + e.getMessage());
                });
    }

    public void rejectPendingRoute(String pendingRouteId, String reviewerId, String reviewerName,
                                  String reviewerRole, String feedback) {
        routeRepository.rejectPendingRoute(pendingRouteId, reviewerId, feedback)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("Route rejected successfully");
                    // Create audit log
                    auditLogRepository.createAuditLog(
                        reviewerId,
                        reviewerName,
                        reviewerRole,
                        Constants.ACTION_REJECT,
                        "pending_route",
                        pendingRouteId,
                        "Rejected route change: " + feedback
                    );
                    loadPendingRoutes();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to reject route: " + e.getMessage());
                });
    }

    public void loadUserPendingRoutes(String userId) {
        routeRepository.getPendingRoutesByUser(userId)
                .addOnSuccessListener(querySnapshot -> {
                    List<PendingRoute> routes = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        PendingRoute route = document.toObject(PendingRoute.class);
                        routes.add(route);
                    }
                    pendingRoutes.setValue(routes);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load your pending routes: " + e.getMessage());
                });
    }

    public void loadAllAuditLogs() {
        auditLogRepository.getAllAuditLogs()
                .addOnSuccessListener(querySnapshot -> {
                    List<AuditLog> logs = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        AuditLog log = document.toObject(AuditLog.class);
                        logs.add(log);
                    }
                    auditLogs.setValue(logs);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load audit logs: " + e.getMessage());
                });
    }

    public void loadRecentAuditLogs(int limit) {
        auditLogRepository.getRecentAuditLogs(limit)
                .addOnSuccessListener(querySnapshot -> {
                    List<AuditLog> logs = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        AuditLog log = document.toObject(AuditLog.class);
                        logs.add(log);
                    }
                    auditLogs.setValue(logs);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load recent logs: " + e.getMessage());
                });
    }
}
