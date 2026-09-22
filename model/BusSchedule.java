package model;

import java.time.LocalDateTime;

public class BusSchedule {

    private int scheduleId;
    private int routeId;
    private String sourceCity;
    private String destinationCity;
    private String busNumber;
    private String operatorName;
    private String busType;
    private String tripCategory;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double fare;
    private int totalSeats;
    private int availableSeats;

    public BusSchedule() {
    }

    public BusSchedule(
            int scheduleId,
            String sourceCity,
            String destinationCity,
            String busNumber,
            String operatorName,
            String busType,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            double fare,
            int totalSeats) {

        this.scheduleId = scheduleId;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.busNumber = busNumber;
        this.operatorName = operatorName;
        this.busType = busType;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
        this.totalSeats = totalSeats;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public void setSourceCity(String sourceCity) {
        this.sourceCity = sourceCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

public int getAvailableSeats() {
    return availableSeats;
}

public void setAvailableSeats(int availableSeats) {
    this.availableSeats = availableSeats;
}

public String getTripCategory() {
    return tripCategory;
}

public void setTripCategory(String tripCategory) {
    this.tripCategory = tripCategory;
}

public int getRouteId() {
    return routeId;
}

public void setRouteId(int routeId) {
    this.routeId = routeId;
}

}