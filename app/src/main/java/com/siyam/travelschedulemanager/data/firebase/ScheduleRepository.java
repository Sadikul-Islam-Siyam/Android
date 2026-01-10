package com.siyam.travelschedulemanager.data.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.siyam.travelschedulemanager.model.Schedule;
import com.siyam.travelschedulemanager.util.Constants;

public class ScheduleRepository {
    private final FirebaseFirestore db;

    public ScheduleRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Create new schedule
     */
    public Task<Void> createSchedule(Schedule schedule) {
        String scheduleId = db.collection(Constants.COLLECTION_SCHEDULES).document().getId();
        schedule.setId(scheduleId);
        return db.collection(Constants.COLLECTION_SCHEDULES)
                .document(scheduleId)
                .set(schedule);
    }

    /**
     * Get schedule by ID
     */
    public Task<DocumentSnapshot> getSchedule(String scheduleId) {
        return db.collection(Constants.COLLECTION_SCHEDULES)
                .document(scheduleId)
                .get();
    }

    /**
     * Update schedule
     */
    public Task<Void> updateSchedule(String scheduleId, Schedule schedule) {
        return db.collection(Constants.COLLECTION_SCHEDULES)
                .document(scheduleId)
                .set(schedule);
    }

    /**
     * Delete schedule
     */
    public Task<Void> deleteSchedule(String scheduleId) {
        return db.collection(Constants.COLLECTION_SCHEDULES)
                .document(scheduleId)
                .delete();
    }

    /**
     * Get all schedules
     */
    public Task<QuerySnapshot> getAllSchedules() {
        return db.collection(Constants.COLLECTION_SCHEDULES)
                .orderBy("departureTime")
                .get();
    }

    /**
     * Search schedules by origin and destination
     */
    public Task<QuerySnapshot> searchSchedules(String origin, String destination, String transportType) {
        Query query = db.collection(Constants.COLLECTION_SCHEDULES)
                .whereEqualTo("origin", origin)
                .whereEqualTo("destination", destination);

        if (transportType != null && !transportType.equals("ALL")) {
            query = query.whereEqualTo("transportType", transportType);
        }

        return query.orderBy("departureTime").get();
    }

    /**
     * Get schedules by transport type
     */
    public Task<QuerySnapshot> getSchedulesByType(String transportType) {
        return db.collection(Constants.COLLECTION_SCHEDULES)
                .whereEqualTo("transportType", transportType)
                .orderBy("departureTime")
                .get();
    }

    /**
     * Get schedules from origin
     */
    public Task<QuerySnapshot> getSchedulesFromOrigin(String origin) {
        return db.collection(Constants.COLLECTION_SCHEDULES)
                .whereEqualTo("origin", origin)
                .orderBy("departureTime")
                .get();
    }

    /**
     * Get schedules to destination
     */
    public Task<QuerySnapshot> getSchedulesToDestination(String destination) {
        return db.collection(Constants.COLLECTION_SCHEDULES)
                .whereEqualTo("destination", destination)
                .orderBy("departureTime")
                .get();
    }

    /**
     * Get schedules by operator
     */
    public Task<QuerySnapshot> getSchedulesByOperator(String operatorName) {
        return db.collection(Constants.COLLECTION_SCHEDULES)
                .whereEqualTo("operatorName", operatorName)
                .orderBy("departureTime")
                .get();
    }
}
