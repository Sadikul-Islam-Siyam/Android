package com.siyam.travelschedulemanager.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("EEEE", Locale.getDefault());

    /**
     * Calculate duration in minutes between two times
     */
    public static int calculateDuration(String startTime, String endTime) {
        try {
            Date start = TIME_FORMAT.parse(startTime);
            Date end = TIME_FORMAT.parse(endTime);
            
            if (start != null && end != null) {
                long diff = end.getTime() - start.getTime();
                
                // Handle overnight journeys
                if (diff < 0) {
                    diff += 24 * 60 * 60 * 1000; // Add 24 hours
                }
                
                return (int) (diff / (60 * 1000)); // Convert to minutes
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Format duration in minutes to readable string
     */
    public static String formatDuration(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        
        if (hours > 0 && mins > 0) {
            return hours + "h " + mins + "m";
        } else if (hours > 0) {
            return hours + "h";
        } else {
            return mins + "m";
        }
    }

    /**
     * Get day of week from date
     */
    public static String getDayOfWeek(Date date) {
        return DAY_FORMAT.format(date);
    }

    /**
     * Check if date is a specific day
     */
    public static boolean isDayOfWeek(Date date, String dayName) {
        return getDayOfWeek(date).equalsIgnoreCase(dayName);
    }

    /**
     * Format date to readable string
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }

    /**
     * Format date and time to readable string
     */
    public static String formatDateTime(Date date) {
        return DATE_TIME_FORMAT.format(date);
    }

    /**
     * Add minutes to time string
     */
    public static String addMinutesToTime(String timeStr, int minutesToAdd) {
        try {
            Date time = TIME_FORMAT.parse(timeStr);
            if (time != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(time);
                calendar.add(Calendar.MINUTE, minutesToAdd);
                return TIME_FORMAT.format(calendar.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStr;
    }

    /**
     * Check if there's enough transfer time between two legs
     */
    public static boolean isValidTransferTime(String arrivalTime, String departureTime, int minimumMinutes) {
        int gap = calculateDuration(arrivalTime, departureTime);
        return gap >= minimumMinutes;
    }

    /**
     * Parse time string to hour and minute
     */
    public static int[] parseTime(String timeStr) {
        try {
            String[] parts = timeStr.split(":");
            return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        } catch (Exception e) {
            return new int[]{0, 0};
        }
    }

    /**
     * Compare two times
     * Returns: -1 if time1 < time2, 0 if equal, 1 if time1 > time2
     */
    public static int compareTimes(String time1, String time2) {
        try {
            Date t1 = TIME_FORMAT.parse(time1);
            Date t2 = TIME_FORMAT.parse(time2);
            if (t1 != null && t2 != null) {
                return t1.compareTo(t2);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get current time in HH:mm format
     */
    public static String getCurrentTime() {
        return TIME_FORMAT.format(new Date());
    }

    /**
     * Get current date
     */
    public static Date getCurrentDate() {
        return new Date();
    }
}
