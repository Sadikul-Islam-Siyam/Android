package com.siyam.travelschedulemanager.data.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.siyam.travelschedulemanager.model.Plan;
import com.siyam.travelschedulemanager.util.Constants;

public class PlanRepository {
    private final FirebaseFirestore db;

    public PlanRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Create new plan
     */
    public Task<Void> createPlan(Plan plan) {
        String planId = db.collection(Constants.COLLECTION_PLANS).document().getId();
        plan.setId(planId);
        return db.collection(Constants.COLLECTION_PLANS)
                .document(planId)
                .set(plan);
    }

    /**
     * Get plan by ID
     */
    public Task<DocumentSnapshot> getPlan(String planId) {
        return db.collection(Constants.COLLECTION_PLANS)
                .document(planId)
                .get();
    }

    /**
     * Update plan
     */
    public Task<Void> updatePlan(String planId, Plan plan) {
        return db.collection(Constants.COLLECTION_PLANS)
                .document(planId)
                .set(plan);
    }

    /**
     * Delete plan
     */
    public Task<Void> deletePlan(String planId) {
        return db.collection(Constants.COLLECTION_PLANS)
                .document(planId)
                .delete();
    }

    /**
     * Get all plans for a user
     */
    public Task<QuerySnapshot> getUserPlans(String userId) {
        return db.collection(Constants.COLLECTION_PLANS)
                .whereEqualTo("userId", userId)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get();
    }

    /**
     * Get all plans (for admin)
     */
    public Task<QuerySnapshot> getAllPlans() {
        return db.collection(Constants.COLLECTION_PLANS)
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get();
    }

    /**
     * Search plans by name
     */
    public Task<QuerySnapshot> searchPlansByName(String userId, String searchTerm) {
        return db.collection(Constants.COLLECTION_PLANS)
                .whereEqualTo("userId", userId)
                .orderBy("name")
                .startAt(searchTerm)
                .endAt(searchTerm + "\uf8ff")
                .get();
    }
}
