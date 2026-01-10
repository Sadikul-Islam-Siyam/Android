package com.siyam.travelschedulemanager.util;

import com.google.firebase.firestore.FirebaseFirestore;
import com.siyam.travelschedulemanager.model.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleDataInitializer {

    public static void initializeSampleSchedules() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if schedules already exist
        db.collection(Constants.COLLECTION_SCHEDULES).limit(1).get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        // Add sample schedules
                        List<Schedule> sampleSchedules = createSampleSchedules();
                        for (Schedule schedule : sampleSchedules) {
                            String id = db.collection(Constants.COLLECTION_SCHEDULES).document().getId();
                            schedule.setId(id);
                            db.collection(Constants.COLLECTION_SCHEDULES)
                                    .document(id)
                                    .set(schedule);
                        }
                    }
                });
    }

    private static List<Schedule> createSampleSchedules() {
        List<Schedule> schedules = new ArrayList<>();

        // Bus schedules
        schedules.add(createSchedule(Constants.TRANSPORT_BUS, "Dhaka", "Chittagong",
                "08:00", "14:00", 360, 600, "Green Line Paribahan", null));
        schedules.add(createSchedule(Constants.TRANSPORT_BUS, "Dhaka", "Sylhet",
                "09:00", "15:30", 390, 550, "Shyamoli Paribahan", null));
        schedules.add(createSchedule(Constants.TRANSPORT_BUS, "Dhaka", "Khulna",
                "07:30", "15:00", 450, 650, "Hanif Enterprise", null));
        schedules.add(createSchedule(Constants.TRANSPORT_BUS, "Dhaka", "Rajshahi",
                "08:30", "14:30", 360, 500, "Nabil Paribahan", null));
        schedules.add(createSchedule(Constants.TRANSPORT_BUS, "Chittagong", "Cox's Bazar",
                "06:00", "10:00", 240, 400, "Soudia Coach", null));

        // Train schedules
        schedules.add(createSchedule(Constants.TRANSPORT_TRAIN, "Dhaka", "Chittagong",
                "15:00", "21:00", 360, 450, "Suborno Express", "701"));
        schedules.add(createSchedule(Constants.TRANSPORT_TRAIN, "Dhaka", "Sylhet",
                "22:00", "06:00", 480, 400, "Upaban Express", "711"));
        schedules.add(createSchedule(Constants.TRANSPORT_TRAIN, "Dhaka", "Rajshahi",
                "09:00", "16:00", 420, 350, "Silk City Express", "751"));

        // More schedules for connections
        schedules.add(createSchedule(Constants.TRANSPORT_BUS, "Chittagong", "Dhaka",
                "08:00", "14:00", 360, 600, "Green Line Paribahan", null));
        schedules.add(createSchedule(Constants.TRANSPORT_BUS, "Sylhet", "Dhaka",
                "09:00", "15:30", 390, 550, "Shyamoli Paribahan", null));

        return schedules;
    }

    private static Schedule createSchedule(String type, String origin, String destination,
                                          String depTime, String arrTime, int duration,
                                          double fare, String operator, String trainNum) {
        Schedule schedule = new Schedule(type, origin, destination, depTime, arrTime,
                duration, fare, operator);
        schedule.setTrainNumber(trainNum);
        schedule.setTotalSeats(type.equals(Constants.TRANSPORT_BUS) ? 40 : 100);
        schedule.setOffDays(Arrays.asList());
        return schedule;
    }
}
