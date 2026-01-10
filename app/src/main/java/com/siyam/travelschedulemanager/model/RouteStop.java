package com.siyam.travelschedulemanager.model;

/**
 * Model class representing a stop in a train or bus route.
 * Contains station name, arrival/departure times, and cumulative fare.
 */
public class RouteStop {
    private String station;
    private String arrivalTime;
    private String departureTime;
    private double cumulativeFare;

    // Empty constructor for Firestore
    public RouteStop() {}

    public RouteStop(String station, String arrivalTime, String departureTime, double cumulativeFare) {
        this.station = station;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.cumulativeFare = cumulativeFare;
    }

    // Getters and Setters
    public String getStation() { return station; }
    public void setStation(String station) { this.station = station; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public double getCumulativeFare() { return cumulativeFare; }
    public void setCumulativeFare(double cumulativeFare) { this.cumulativeFare = cumulativeFare; }

    @Override
    public String toString() {
        return "RouteStop{" +
                "station='" + station + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", cumulativeFare=" + cumulativeFare +
                '}';
    }
}
