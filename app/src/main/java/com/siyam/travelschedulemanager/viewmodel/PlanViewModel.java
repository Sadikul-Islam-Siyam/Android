package com.siyam.travelschedulemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.siyam.travelschedulemanager.data.firebase.PlanRepository;
import com.siyam.travelschedulemanager.model.Plan;

import java.util.ArrayList;
import java.util.List;

public class PlanViewModel extends ViewModel {
    private final PlanRepository planRepository;
    private final MutableLiveData<List<Plan>> plans = new MutableLiveData<>();
    private final MutableLiveData<Plan> currentPlan = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public PlanViewModel() {
        this.planRepository = new PlanRepository();
    }

    public LiveData<List<Plan>> getPlans() {
        return plans;
    }

    public LiveData<Plan> getCurrentPlan() {
        return currentPlan;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * Load user plans
     */
    public void loadUserPlans(String userId) {
        planRepository.getUserPlans(userId)
                .addOnSuccessListener(querySnapshot -> {
                    List<Plan> planList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Plan plan = document.toObject(Plan.class);
                        planList.add(plan);
                    }
                    plans.setValue(planList);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load plans: " + e.getMessage());
                });
    }

    /**
     * Create new plan
     */
    public void createPlan(Plan plan) {
        planRepository.createPlan(plan)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("Plan saved successfully");
                    currentPlan.setValue(plan);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to save plan: " + e.getMessage());
                });
    }

    /**
     * Update plan
     */
    public void updatePlan(String planId, Plan plan) {
        planRepository.updatePlan(planId, plan)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("Plan updated successfully");
                    currentPlan.setValue(plan);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to update plan: " + e.getMessage());
                });
    }

    /**
     * Delete plan
     */
    public void deletePlan(String planId) {
        planRepository.deletePlan(planId)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("Plan deleted successfully");
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to delete plan: " + e.getMessage());
                });
    }

    /**
     * Load single plan
     */
    public void loadPlan(String planId) {
        planRepository.getPlan(planId)
                .addOnSuccessListener(documentSnapshot -> {
                    Plan plan = documentSnapshot.toObject(Plan.class);
                    currentPlan.setValue(plan);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load plan: " + e.getMessage());
                });
    }

    /**
     * Search plans by name
     */
    public void searchPlans(String userId, String searchTerm) {
        planRepository.searchPlansByName(userId, searchTerm)
                .addOnSuccessListener(querySnapshot -> {
                    List<Plan> planList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Plan plan = document.toObject(Plan.class);
                        planList.add(plan);
                    }
                    plans.setValue(planList);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to search plans: " + e.getMessage());
                });
    }
}
