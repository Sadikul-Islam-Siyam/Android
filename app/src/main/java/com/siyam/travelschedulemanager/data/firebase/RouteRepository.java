package com.siyam.travelschedulemanager.data.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.siyam.travelschedulemanager.model.PendingRoute;
import com.siyam.travelschedulemanager.model.Route;
import com.siyam.travelschedulemanager.util.Constants;

public class RouteRepository {
    private final FirebaseFirestore db;

    public RouteRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    // ========== ROUTE OPERATIONS ==========

    /**
     * Create new route
     */
    public Task<Void> createRoute(Route route) {
        String routeId = db.collection(Constants.COLLECTION_ROUTES).document().getId();
        route.setId(routeId);
        return db.collection(Constants.COLLECTION_ROUTES)
                .document(routeId)
                .set(route);
    }

    /**
     * Get route by ID
     */
    public Task<DocumentSnapshot> getRoute(String routeId) {
        return db.collection(Constants.COLLECTION_ROUTES)
                .document(routeId)
                .get();
    }

    /**
     * Update route
     */
    public Task<Void> updateRoute(String routeId, Route route) {
        route.setUpdatedAt(Timestamp.now());
        return db.collection(Constants.COLLECTION_ROUTES)
                .document(routeId)
                .set(route);
    }

    /**
     * Delete route
     */
    public Task<Void> deleteRoute(String routeId) {
        return db.collection(Constants.COLLECTION_ROUTES)
                .document(routeId)
                .delete();
    }

    /**
     * Get all active routes
     */
    public Task<QuerySnapshot> getAllActiveRoutes() {
        return db.collection(Constants.COLLECTION_ROUTES)
                .whereEqualTo("status", "ACTIVE")
                .orderBy("origin")
                .get();
    }

    /**
     * Get all routes
     */
    public Task<QuerySnapshot> getAllRoutes() {
        return db.collection(Constants.COLLECTION_ROUTES)
                .orderBy("origin")
                .get();
    }

    // ========== PENDING ROUTE OPERATIONS ==========

    /**
     * Submit pending route request
     */
    public Task<Void> submitPendingRoute(PendingRoute pendingRoute) {
        String id = db.collection(Constants.COLLECTION_PENDING_ROUTES).document().getId();
        pendingRoute.setId(id);
        return db.collection(Constants.COLLECTION_PENDING_ROUTES)
                .document(id)
                .set(pendingRoute);
    }

    /**
     * Get pending route by ID
     */
    public Task<DocumentSnapshot> getPendingRoute(String id) {
        return db.collection(Constants.COLLECTION_PENDING_ROUTES)
                .document(id)
                .get();
    }

    /**
     * Get all pending routes
     */
    public Task<QuerySnapshot> getAllPendingRoutes() {
        return db.collection(Constants.COLLECTION_PENDING_ROUTES)
                .whereEqualTo("status", Constants.REQUEST_PENDING)
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .get();
    }

    /**
     * Get pending routes submitted by user
     */
    public Task<QuerySnapshot> getPendingRoutesByUser(String userId) {
        return db.collection(Constants.COLLECTION_PENDING_ROUTES)
                .whereEqualTo("submittedBy", userId)
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .get();
    }

    /**
     * Approve pending route
     */
    public Task<Void> approvePendingRoute(String id, String reviewedBy, String feedback) {
        return db.collection(Constants.COLLECTION_PENDING_ROUTES)
                .document(id)
                .update(
                        "status", Constants.REQUEST_APPROVED,
                        "reviewedBy", reviewedBy,
                        "reviewFeedback", feedback,
                        "reviewedAt", Timestamp.now()
                );
    }

    /**
     * Reject pending route
     */
    public Task<Void> rejectPendingRoute(String id, String reviewedBy, String feedback) {
        return db.collection(Constants.COLLECTION_PENDING_ROUTES)
                .document(id)
                .update(
                        "status", Constants.REQUEST_REJECTED,
                        "reviewedBy", reviewedBy,
                        "reviewFeedback", feedback,
                        "reviewedAt", Timestamp.now()
                );
    }

    /**
     * Get request history (all reviewed requests)
     */
    public Task<QuerySnapshot> getRequestHistory() {
        return db.collection(Constants.COLLECTION_PENDING_ROUTES)
                .whereIn("status", java.util.Arrays.asList(
                        Constants.REQUEST_APPROVED,
                        Constants.REQUEST_REJECTED
                ))
                .orderBy("reviewedAt", Query.Direction.DESCENDING)
                .get();
    }

    /**
     * Delete pending route
     */
    public Task<Void> deletePendingRoute(String id) {
        return db.collection(Constants.COLLECTION_PENDING_ROUTES)
                .document(id)
                .delete();
    }
}
