package com.siyam.travelschedulemanager.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.siyam.travelschedulemanager.data.firebase.ScheduleRepository;
import com.siyam.travelschedulemanager.model.Schedule;
import com.siyam.travelschedulemanager.util.RouteAlgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleViewModel extends ViewModel {
    private final ScheduleRepository scheduleRepository;
    private final MutableLiveData<List<Schedule>> schedules = new MutableLiveData<>();
    private final MutableLiveData<List<Schedule>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<List<RouteAlgorithm.RouteResult>> routeResults = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ScheduleViewModel() {
        this.scheduleRepository = new ScheduleRepository();
    }

    public LiveData<List<Schedule>> getSchedules() {
        return schedules;
    }

    public LiveData<List<Schedule>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<RouteAlgorithm.RouteResult>> getRouteResults() {
        return routeResults;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadAllSchedules() {
        scheduleRepository.getAllSchedules()
                .addOnSuccessListener(querySnapshot -> {
                    List<Schedule> scheduleList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Schedule schedule = document.toObject(Schedule.class);
                        scheduleList.add(schedule);
                    }
                    schedules.setValue(scheduleList);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to load schedules: " + e.getMessage());
                });
    }

    public void searchSchedules(String origin, String destination, String transportType) {
        scheduleRepository.searchSchedules(origin, destination, transportType)
                .addOnSuccessListener(querySnapshot -> {
                    List<Schedule> scheduleList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Schedule schedule = document.toObject(Schedule.class);
                        scheduleList.add(schedule);
                    }
                    searchResults.setValue(scheduleList);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to search schedules: " + e.getMessage());
                });
    }

    public void createSchedule(Schedule schedule) {
        scheduleRepository.createSchedule(schedule)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("Schedule created successfully");
                    loadAllSchedules();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to create schedule: " + e.getMessage());
                });
    }

    public void updateSchedule(String scheduleId, Schedule schedule) {
        scheduleRepository.updateSchedule(scheduleId, schedule)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("Schedule updated successfully");
                    loadAllSchedules();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to update schedule: " + e.getMessage());
                });
    }

    public void deleteSchedule(String scheduleId) {
        scheduleRepository.deleteSchedule(scheduleId)
                .addOnSuccessListener(aVoid -> {
                    message.setValue("Schedule deleted successfully");
                    loadAllSchedules();
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to delete schedule: " + e.getMessage());
                });
    }

    public void findOptimalRoutes(String origin, String destination, Date travelDate, int maxLegs) {
        scheduleRepository.getAllSchedules()
                .addOnSuccessListener(querySnapshot -> {
                    List<Schedule> allSchedules = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Schedule schedule = document.toObject(Schedule.class);
                        allSchedules.add(schedule);
                    }

                    List<RouteAlgorithm.RouteResult> results = RouteAlgorithm.findOptimalRoutes(
                            origin, destination, travelDate, allSchedules, maxLegs
                    );
                    routeResults.setValue(results);
                })
                .addOnFailureListener(e -> {
                    error.setValue("Failed to find routes: " + e.getMessage());
                });
    }
}
