package com.siyam.travelschedulemanager.data.firebase;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.siyam.travelschedulemanager.model.BusRoute;
import com.siyam.travelschedulemanager.model.PendingRouteChange;
import com.siyam.travelschedulemanager.model.RouteStop;
import com.siyam.travelschedulemanager.model.TrainRoute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RouteManagementRepository {
    private final FirebaseFirestore db;
    
    // Collection names
    private static final String COLLECTION_BUS_ROUTES = "busRoutes";
    private static final String COLLECTION_TRAIN_ROUTES = "trainRoutes";
    private static final String COLLECTION_PENDING_ROUTE_CHANGES = "pendingRouteChanges";
    private static final String COLLECTION_AUTOCOMPLETE_DATA = "autocompleteData";

    public RouteManagementRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    // ==================== BUS ROUTE OPERATIONS ====================

    public Task<Void> createBusRoute(BusRoute route) {
        String id = db.collection(COLLECTION_BUS_ROUTES).document().getId();
        route.setId(id);
        route.setCreatedAt(Timestamp.now());
        route.setUpdatedAt(Timestamp.now());
        return db.collection(COLLECTION_BUS_ROUTES).document(id).set(route);
    }

    public Task<DocumentSnapshot> getBusRoute(String routeId) {
        return db.collection(COLLECTION_BUS_ROUTES).document(routeId).get();
    }

    public Task<Void> updateBusRoute(String routeId, BusRoute route) {
        route.setUpdatedAt(Timestamp.now());
        return db.collection(COLLECTION_BUS_ROUTES).document(routeId).set(route);
    }

    public Task<Void> deleteBusRoute(String routeId) {
        return db.collection(COLLECTION_BUS_ROUTES).document(routeId).delete();
    }

    public Task<QuerySnapshot> getAllBusRoutes() {
        return db.collection(COLLECTION_BUS_ROUTES)
                .orderBy("routeName")
                .get();
    }

    public Task<QuerySnapshot> getApprovedBusRoutes() {
        return db.collection(COLLECTION_BUS_ROUTES)
                .whereEqualTo("status", "APPROVED")
                .orderBy("routeName")
                .get();
    }

    // ==================== TRAIN ROUTE OPERATIONS ====================

    public Task<Void> createTrainRoute(TrainRoute route) {
        String id = db.collection(COLLECTION_TRAIN_ROUTES).document().getId();
        route.setId(id);
        route.setCreatedAt(Timestamp.now());
        route.setUpdatedAt(Timestamp.now());
        return db.collection(COLLECTION_TRAIN_ROUTES).document(id).set(route);
    }

    public Task<DocumentSnapshot> getTrainRoute(String routeId) {
        return db.collection(COLLECTION_TRAIN_ROUTES).document(routeId).get();
    }

    public Task<Void> updateTrainRoute(String routeId, TrainRoute route) {
        route.setUpdatedAt(Timestamp.now());
        return db.collection(COLLECTION_TRAIN_ROUTES).document(routeId).set(route);
    }

    public Task<Void> deleteTrainRoute(String routeId) {
        return db.collection(COLLECTION_TRAIN_ROUTES).document(routeId).delete();
    }

    public Task<QuerySnapshot> getAllTrainRoutes() {
        return db.collection(COLLECTION_TRAIN_ROUTES)
                .orderBy("trainName")
                .get();
    }

    public Task<QuerySnapshot> getApprovedTrainRoutes() {
        return db.collection(COLLECTION_TRAIN_ROUTES)
                .whereEqualTo("status", "APPROVED")
                .orderBy("trainName")
                .get();
    }

    // ==================== PENDING ROUTE CHANGE OPERATIONS ====================

    public Task<Void> submitPendingRouteChange(PendingRouteChange change) {
        String id = db.collection(COLLECTION_PENDING_ROUTE_CHANGES).document().getId();
        change.setId(id);
        change.setSubmittedAt(Timestamp.now());
        change.setStatus("PENDING");
        return db.collection(COLLECTION_PENDING_ROUTE_CHANGES).document(id).set(change);
    }

    public Task<DocumentSnapshot> getPendingRouteChange(String changeId) {
        return db.collection(COLLECTION_PENDING_ROUTE_CHANGES).document(changeId).get();
    }

    public Task<QuerySnapshot> getAllPendingRouteChanges() {
        return db.collection(COLLECTION_PENDING_ROUTE_CHANGES)
                .whereEqualTo("status", "PENDING")
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getPendingRouteChangesByUser(String userId) {
        return db.collection(COLLECTION_PENDING_ROUTE_CHANGES)
                .whereEqualTo("submittedBy", userId)
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .get();
    }

    public Task<Void> approveRouteChange(String changeId, String reviewedBy, String reviewedByName, String feedback) {
        return db.collection(COLLECTION_PENDING_ROUTE_CHANGES)
                .document(changeId)
                .update(
                        "status", "APPROVED",
                        "reviewedBy", reviewedBy,
                        "reviewedByName", reviewedByName,
                        "reviewFeedback", feedback,
                        "reviewedAt", Timestamp.now()
                );
    }

    public Task<Void> rejectRouteChange(String changeId, String reviewedBy, String reviewedByName, String feedback) {
        return db.collection(COLLECTION_PENDING_ROUTE_CHANGES)
                .document(changeId)
                .update(
                        "status", "REJECTED",
                        "reviewedBy", reviewedBy,
                        "reviewedByName", reviewedByName,
                        "reviewFeedback", feedback,
                        "reviewedAt", Timestamp.now()
                );
    }

    public Task<QuerySnapshot> getRouteChangeHistory() {
        return db.collection(COLLECTION_PENDING_ROUTE_CHANGES)
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .limit(100)
                .get();
    }

    // ==================== AUTOCOMPLETE OPERATIONS ====================

    /**
     * Get autocomplete suggestions from Firebase
     * Searches through station names, district names, route names, train names
     */
    public Task<List<String>> getAutocompleteSuggestions(String query) {
        if (query == null || query.trim().length() < 2) {
            return Tasks.forResult(new ArrayList<>());
        }

        String searchQuery = query.toLowerCase().trim();
        Set<String> suggestions = new HashSet<>();

        // Get bus routes
        Task<QuerySnapshot> busTask = db.collection(COLLECTION_BUS_ROUTES).get();
        
        // Get train routes
        Task<QuerySnapshot> trainTask = db.collection(COLLECTION_TRAIN_ROUTES).get();

        return Tasks.whenAllSuccess(busTask, trainTask).continueWith(task -> {
            List<Object> results = task.getResult();
            
            // Process bus routes
            QuerySnapshot busSnapshot = (QuerySnapshot) results.get(0);
            for (DocumentSnapshot doc : busSnapshot.getDocuments()) {
                BusRoute bus = doc.toObject(BusRoute.class);
                if (bus != null) {
                    addIfMatches(suggestions, bus.getBusName(), searchQuery);
                    addIfMatches(suggestions, bus.getStart(), searchQuery);
                    addIfMatches(suggestions, bus.getDestination(), searchQuery);
                }
            }

            // Process train routes
            QuerySnapshot trainSnapshot = (QuerySnapshot) results.get(1);
            for (DocumentSnapshot doc : trainSnapshot.getDocuments()) {
                TrainRoute train = doc.toObject(TrainRoute.class);
                if (train != null) {
                    addIfMatches(suggestions, train.getTrainName(), searchQuery);
                    addIfMatches(suggestions, train.getTrainNumber(), searchQuery);
                    addIfMatches(suggestions, train.getStart(), searchQuery);
                    addIfMatches(suggestions, train.getDestination(), searchQuery);
                    if (train.getStops() != null) {
                        for (RouteStop stop : train.getStops()) {
                            addIfMatches(suggestions, stop.getStation(), searchQuery);
                        }
                    }
                }
            }

            return new ArrayList<>(suggestions);
        });
    }

    private void addIfMatches(Set<String> suggestions, String value, String query) {
        if (value != null && !value.isEmpty() && value.toLowerCase().contains(query)) {
            suggestions.add(value);
        }
    }

    // ==================== SEARCH OPERATIONS ====================

    /**
     * Search routes by query string
     */
    public Task<QuerySnapshot> searchBusRoutes(String query) {
        // Firebase doesn't support native text search, so we fetch all and filter client-side
        return db.collection(COLLECTION_BUS_ROUTES).get();
    }

    public Task<QuerySnapshot> searchTrainRoutes(String query) {
        return db.collection(COLLECTION_TRAIN_ROUTES).get();
    }

    // ==================== BATCH OPERATIONS ====================

    /**
     * Apply approved route change to the live database
     */
    public Task<Void> applyApprovedChange(PendingRouteChange change) {
        switch (change.getChangeType()) {
            case "ADD":
                if (change.isBusRoute()) {
                    return createBusRoute(change.getBusRouteData());
                } else {
                    return createTrainRoute(change.getTrainRouteData());
                }
            case "EDIT":
                if (change.isBusRoute()) {
                    return updateBusRoute(change.getRouteId(), change.getBusRouteData());
                } else {
                    return updateTrainRoute(change.getRouteId(), change.getTrainRouteData());
                }
            case "DELETE":
                if (change.isBusRoute()) {
                    return deleteBusRoute(change.getRouteId());
                } else {
                    return deleteTrainRoute(change.getRouteId());
                }
            default:
                return Tasks.forException(new Exception("Unknown change type: " + change.getChangeType()));
        }
    }
}
