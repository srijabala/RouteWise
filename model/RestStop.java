package model;

public class RestStop {

    private int stopId;
    private int routeId;
    private String stopName;
    private int stopOrder;
    private boolean foodAvailable;
    private boolean restroomAvailable;
    private boolean wheelchairAccessible;
    private boolean parkingAvailable;
    private String description;
    private int restDurationMinutes;
    private String foodMenu;

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public int getStopOrder() {
        return stopOrder;
    }

    public void setStopOrder(int stopOrder) {
        this.stopOrder = stopOrder;
    }

    public boolean isFoodAvailable() {
        return foodAvailable;
    }

    public void setFoodAvailable(boolean foodAvailable) {
        this.foodAvailable = foodAvailable;
    }

    public boolean isRestroomAvailable() {
        return restroomAvailable;
    }

    public void setRestroomAvailable(boolean restroomAvailable) {
        this.restroomAvailable = restroomAvailable;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setWheelchairAccessible(boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRestDurationMinutes() {
        return restDurationMinutes;
    }

    public void setRestDurationMinutes(int restDurationMinutes) {
        this.restDurationMinutes = restDurationMinutes;
    }

    public String getFoodMenu() {
        return foodMenu;
    }

    public void setFoodMenu(String foodMenu) {
        this.foodMenu = foodMenu;
    }
}