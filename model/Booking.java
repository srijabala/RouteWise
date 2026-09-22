package model;

import java.time.LocalDateTime;

public class Booking {

    private int bookingId;
    private int userId;
    private int scheduleId;

    private String sourceCity;
    private String destinationCity;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private String busNumber;
    private String busType;

    private LocalDateTime bookingDate;

    private int totalSeats;
    private double totalAmount;

    private String status;

    private String paymentMethod;
    private String paymentStatus;

    private int devotionalPackageId;
   private String devotionalPackageName;
   private double devotionalPackagePrice;

    private int seniorCitizenCount;
    private int wheelchairCount;

    public Booking() {
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

// for devotional
    public int getDevotionalPackageId() {
    return devotionalPackageId;
}

public void setDevotionalPackageId(int devotionalPackageId) {
    this.devotionalPackageId = devotionalPackageId;
}

public String getDevotionalPackageName() {
    return devotionalPackageName;
}

public void setDevotionalPackageName(String devotionalPackageName) {
    this.devotionalPackageName = devotionalPackageName;
}

public double getDevotionalPackagePrice() {
    return devotionalPackagePrice;
}

public void setDevotionalPackagePrice(double devotionalPackagePrice) {
    this.devotionalPackagePrice = devotionalPackagePrice;
}
public int getSeniorCitizenCount() {
    return seniorCitizenCount;
}

public void setSeniorCitizenCount(int seniorCitizenCount) {
    this.seniorCitizenCount = seniorCitizenCount;
}

public int getWheelchairCount() {
    return wheelchairCount;
}

public void setWheelchairCount(int wheelchairCount) {
    this.wheelchairCount = wheelchairCount;
}

}