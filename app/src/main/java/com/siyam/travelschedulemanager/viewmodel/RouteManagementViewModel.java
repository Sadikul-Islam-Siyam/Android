package com.siyam.travelschedulemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.siyam.travelschedulemanager.data.firebase.RouteManagementRepository;
import com.siyam.travelschedulemanager.model.BusRoute;
import com.siyam.travelschedulemanager.model.PendingRouteChange;
import com.siyam.travelschedulemanager.model.TrainRoute;
import com.siyam.travelschedulemanager.model.UnifiedRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RouteManagementViewModel extends ViewModel {
    private final RouteManagementRepository repository;
    private final FirebaseAuth auth;

    // LiveData for routes
    private final MutableLiveData<List<UnifiedRoute>> allRoutes = new MutableLiveData<>();
    private final MutableLiveData<List<UnifiedRoute>> filteredRoutes = new MutableLiveData<>();
    private final MutableLiveData<List<BusRoute>> busRoutes = new MutableLiveData<>();
    private final MutableLiveData<List<TrainRoute>> trainRoutes = new MutableLiveData<>();

    // LiveData for pending changes
    private final MutableLiveData<List<PendingRouteChange>> pendingChanges = new MutableLiveData<>();

    // LiveData for autocomplete
    private final MutableLiveData<List<String>> autocompleteSuggestions = new MutableLiveData<>();

    // LiveData for selected route (for editing)
    private final MutableLiveData<BusRoute> selectedBusRoute = new MutableLiveData<>();
    private final MutableLiveData<TrainRoute> selectedTrainRoute = new MutableLiveData<>();

    // UI state
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    // Filter state
    private String currentFilter = "ALL"; // ALL, BUS, TRAIN
    private String currentSearchQuery = "";

    public RouteManagementViewModel() {
        repository = new RouteManagementRepository();
        auth = FirebaseAuth.getInstance();
    }

    // ==================== GETTERS ====================

    public LiveData<List<UnifiedRoute>> getAllRoutes() { return allRoutes; }
    public LiveData<List<UnifiedRoute>> getFilteredRoutes() { return filteredRoutes; }
    public LiveData<List<BusRoute>> getBusRoutes() { return busRoutes; }
    public LiveData<List<TrainRoute>> getTrainRoutes() { return trainRoutes; }
    public LiveData<List<PendingRouteChange>> getPendingChanges() { return pendingChanges; }
    public LiveData<List<String>> getAutocompleteSuggestions() { return autocompleteSuggestions; }
    public LiveData<BusRoute> getSelectedBusRoute() { return selectedBusRoute; }
    public LiveData<TrainRoute> getSelectedTrainRoute() { return selectedTrainRoute; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }
    public LiveData<String> getSuccessMessage() { return successMessage; }

    // ==================== LOAD ROUTES ====================

    public void loadAllRoutes() {
        isLoading.setValue(true);
        List<UnifiedRoute> combinedRoutes = new ArrayList<>();

        repository.getAllBusRoutes().addOnCompleteListener(busTask -> {
            if (busTask.isSuccessful() && busTask.getResult() != null) {
                List<BusRoute> buses = new ArrayList<>();
                for (DocumentSnapshot doc : busTask.getResult()) {
                    BusRoute bus = doc.toObject(BusRoute.class);
                    if (bus != null) {
                        bus.setId(doc.getId());
                        buses.add(bus);
                        combinedRoutes.add(UnifiedRoute.fromBusRoute(bus));
                    }
                }
                busRoutes.setValue(buses);
            }

            repository.getAllTrainRoutes().addOnCompleteListener(trainTask -> {
                if (trainTask.isSuccessful() && trainTask.getResult() != null) {
                    List<TrainRoute> trains = new ArrayList<>();
                    for (DocumentSnapshot doc : trainTask.getResult()) {
                        TrainRoute train = doc.toObject(TrainRoute.class);
                        if (train != null) {
                            train.setId(doc.getId());
                            trains.add(train);
                            combinedRoutes.add(UnifiedRoute.fromTrainRoute(train));
                        }
                    }
                    trainRoutes.setValue(trains);
                }

                allRoutes.setValue(combinedRoutes);
                applyFilters();
                isLoading.setValue(false);
            });
        });
    }

    public void loadBusRoute(String routeId) {
        isLoading.setValue(true);
        repository.getBusRoute(routeId).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                BusRoute bus = task.getResult().toObject(BusRoute.class);
                if (bus != null) {
                    bus.setId(task.getResult().getId());
                    selectedBusRoute.setValue(bus);
                }
            } else {
                error.setValue("Failed to load bus route");
            }
        });
    }

    public void loadTrainRoute(String routeId) {
        isLoading.setValue(true);
        repository.getTrainRoute(routeId).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                TrainRoute train = task.getResult().toObject(TrainRoute.class);
                if (train != null) {
                    train.setId(task.getResult().getId());
                    selectedTrainRoute.setValue(train);
                }
            } else {
                error.setValue("Failed to load train route");
            }
        });
    }

    // ==================== FILTER & SEARCH ====================

    public void setFilter(String filter) {
        this.currentFilter = filter;
        applyFilters();
    }

    public void setSearchQuery(String query) {
        this.currentSearchQuery = query;
        applyFilters();
    }

    private void applyFilters() {
        List<UnifiedRoute> routes = allRoutes.getValue();
        if (routes == null) {
            filteredRoutes.setValue(new ArrayList<>());
            return;
        }

        List<UnifiedRoute> result = routes.stream()
                .filter(route -> {
                    // Apply type filter
                    if ("BUS".equals(currentFilter) && !route.isBus()) return false;
                    if ("TRAIN".equals(currentFilter) && !route.isTrain()) return false;
                    return true;
                })
                .filter(route -> {
                    // Apply search query
                    if (currentSearchQuery == null || currentSearchQuery.isEmpty()) return true;
                    String query = currentSearchQuery.toLowerCase(Locale.ROOT);
                    return matchesSearch(route, query);
                })
                .collect(Collectors.toList());

        filteredRoutes.setValue(result);
    }

    private boolean matchesSearch(UnifiedRoute route, String query) {
        if (route.getDisplayName() != null && route.getDisplayName().toLowerCase().contains(query)) return true;
        if (route.getRouteNumber() != null && route.getRouteNumber().toLowerCase().contains(query)) return true;
        if (route.getOrigin() != null && route.getOrigin().toLowerCase().contains(query)) return true;
        if (route.getDestination() != null && route.getDestination().toLowerCase().contains(query)) return true;
        return false;
    }

    // ==================== AUTOCOMPLETE ====================

    public void loadAutocompleteSuggestions(String query) {
        if (query == null || query.length() < 2) {
            autocompleteSuggestions.setValue(new ArrayList<>());
            return;
        }

        repository.getAutocompleteSuggestions(query).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                autocompleteSuggestions.setValue(task.getResult());
            } else {
                autocompleteSuggestions.setValue(new ArrayList<>());
            }
        });
    }

    // ==================== CREATE/EDIT ROUTES (MASTER DIRECT) ====================

    public void createBusRouteDirect(BusRoute route) {
        isLoading.setValue(true);
        route.setStatus("APPROVED");
        route.setCreatedBy(getCurrentUserId());

        repository.createBusRoute(route).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Bus route created successfully");
                loadAllRoutes();
            } else {
                error.setValue("Failed to create bus route: " + getErrorMessage(task.getException()));
            }
        });
    }

    public void createTrainRouteDirect(TrainRoute route) {
        isLoading.setValue(true);
        route.setStatus("APPROVED");
        route.setCreatedBy(getCurrentUserId());

        repository.createTrainRoute(route).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Train route created successfully");
                loadAllRoutes();
            } else {
                error.setValue("Failed to create train route: " + getErrorMessage(task.getException()));
            }
        });
    }

    public void updateBusRouteDirect(String routeId, BusRoute route) {
        isLoading.setValue(true);
        repository.updateBusRoute(routeId, route).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Bus route updated successfully");
                loadAllRoutes();
            } else {
                error.setValue("Failed to update bus route: " + getErrorMessage(task.getException()));
            }
        });
    }

    public void updateTrainRouteDirect(String routeId, TrainRoute route) {
        isLoading.setValue(true);
        repository.updateTrainRoute(routeId, route).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Train route updated successfully");
                loadAllRoutes();
            } else {
                error.setValue("Failed to update train route: " + getErrorMessage(task.getException()));
            }
        });
    }

    public void deleteBusRouteDirect(String routeId) {
        isLoading.setValue(true);
        repository.deleteBusRoute(routeId).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Bus route deleted successfully");
                loadAllRoutes();
            } else {
                error.setValue("Failed to delete bus route: " + getErrorMessage(task.getException()));
            }
        });
    }

    public void deleteTrainRouteDirect(String routeId) {
        isLoading.setValue(true);
        repository.deleteTrainRoute(routeId).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Train route deleted successfully");
                loadAllRoutes();
            } else {
                error.setValue("Failed to delete train route: " + getErrorMessage(task.getException()));
            }
        });
    }

    // ==================== SUBMIT PENDING CHANGES (DEVELOPER) ====================

    public void submitBusRouteForApproval(BusRoute route, String changeType, String messageToMaster) {
        isLoading.setValue(true);

        PendingRouteChange change = new PendingRouteChange();
        change.setChangeType(changeType);
        change.setRouteType("BUS");
        change.setRouteId(route.getId());
        change.setBusRouteData(route);
        change.setMessageToMaster(messageToMaster);
        change.setSubmittedBy(getCurrentUserId());
        change.setSubmittedByName(getCurrentUserName());
        change.setSubmittedByEmail(getCurrentUserEmail());

        repository.submitPendingRouteChange(change).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Bus route submitted for approval");
            } else {
                error.setValue("Failed to submit: " + getErrorMessage(task.getException()));
            }
        });
    }

    public void submitTrainRouteForApproval(TrainRoute route, String changeType, String messageToMaster) {
        isLoading.setValue(true);

        PendingRouteChange change = new PendingRouteChange();
        change.setChangeType(changeType);
        change.setRouteType("TRAIN");
        change.setRouteId(route.getId());
        change.setTrainRouteData(route);
        change.setMessageToMaster(messageToMaster);
        change.setSubmittedBy(getCurrentUserId());
        change.setSubmittedByName(getCurrentUserName());
        change.setSubmittedByEmail(getCurrentUserEmail());

        repository.submitPendingRouteChange(change).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Train route submitted for approval");
            } else {
                error.setValue("Failed to submit: " + getErrorMessage(task.getException()));
            }
        });
    }

    public void submitDeleteRequest(String routeId, String routeType, String messageToMaster) {
        isLoading.setValue(true);

        PendingRouteChange change = new PendingRouteChange();
        change.setChangeType("DELETE");
        change.setRouteType(routeType);
        change.setRouteId(routeId);
        change.setMessageToMaster(messageToMaster);
        change.setSubmittedBy(getCurrentUserId());
        change.setSubmittedByName(getCurrentUserName());
        change.setSubmittedByEmail(getCurrentUserEmail());

        repository.submitPendingRouteChange(change).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Delete request submitted for approval");
            } else {
                error.setValue("Failed to submit delete request: " + getErrorMessage(task.getException()));
            }
        });
    }

    // ==================== PENDING CHANGES MANAGEMENT ====================

    public void loadPendingChanges() {
        isLoading.setValue(true);
        repository.getAllPendingRouteChanges().addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                List<PendingRouteChange> changes = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    PendingRouteChange change = doc.toObject(PendingRouteChange.class);
                    if (change != null) {
                        change.setId(doc.getId());
                        changes.add(change);
                    }
                }
                pendingChanges.setValue(changes);
            } else {
                error.setValue("Failed to load pending changes");
            }
        });
    }

    public void approveRouteChange(PendingRouteChange change, String feedback) {
        isLoading.setValue(true);

        // First apply the change to live data
        repository.applyApprovedChange(change).addOnCompleteListener(applyTask -> {
            if (applyTask.isSuccessful()) {
                // Then update the pending change status
                repository.approveRouteChange(
                        change.getId(),
                        getCurrentUserId(),
                        getCurrentUserName(),
                        feedback
                ).addOnCompleteListener(updateTask -> {
                    isLoading.setValue(false);
                    if (updateTask.isSuccessful()) {
                        successMessage.setValue("Route change approved and applied");
                        loadPendingChanges();
                        loadAllRoutes();
                    } else {
                        error.setValue("Failed to update approval status");
                    }
                });
            } else {
                isLoading.setValue(false);
                error.setValue("Failed to apply route change: " + getErrorMessage(applyTask.getException()));
            }
        });
    }

    public void rejectRouteChange(String changeId, String feedback) {
        isLoading.setValue(true);
        repository.rejectRouteChange(
                changeId,
                getCurrentUserId(),
                getCurrentUserName(),
                feedback
        ).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Route change rejected");
                loadPendingChanges();
            } else {
                error.setValue("Failed to reject route change");
            }
        });
    }

    // ==================== HELPER METHODS ====================

    private String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : "";
    }

    private String getCurrentUserName() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null && user.getDisplayName() != null ? user.getDisplayName() : "Unknown";
    }

    private String getCurrentUserEmail() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null && user.getEmail() != null ? user.getEmail() : "";
    }

    private String getErrorMessage(Exception e) {
        return e != null && e.getMessage() != null ? e.getMessage() : "Unknown error";
    }

    public void clearError() {
        error.setValue(null);
    }

    public void clearSuccessMessage() {
        successMessage.setValue(null);
    }

    public void setSelectedBusRoute(BusRoute route) {
        selectedBusRoute.setValue(route);
    }

    public void setSelectedTrainRoute(TrainRoute route) {
        selectedTrainRoute.setValue(route);
    }

    public void clearSelectedRoutes() {
        selectedBusRoute.setValue(null);
        selectedTrainRoute.setValue(null);
    }
}
