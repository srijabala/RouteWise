package model;

public class Seat {

    private int seatId;
    private int scheduleId;
    private String seatNumber;
    private String seatType;
    private String berthType;
    private String status;

    public Seat() {
    }

    public Seat(
            int seatId,
            int scheduleId,
            String seatNumber,
            String seatType,
            String berthType,
            String status) {

        this.seatId = seatId;
        this.scheduleId = scheduleId;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.berthType = berthType;
        this.status = status;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        this.seatType = seatType;
    }

    public String getBerthType() {
        return berthType;
    }

    public void setBerthType(String berthType) {
        this.berthType = berthType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}