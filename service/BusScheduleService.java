package service;

import dao.BusScheduleDAO;
import java.time.LocalDate;
import java.util.List;
import model.BusSchedule;

public class BusScheduleService {

    private BusScheduleDAO busScheduleDAO;

    public BusScheduleService() {
        busScheduleDAO = new BusScheduleDAO();
    }

    public List<BusSchedule> searchSchedules(
            String sourceCity,
            String destinationCity,
            LocalDate travelDate) {

        return busScheduleDAO.searchSchedules(
                sourceCity,
                destinationCity,
                travelDate
        );
    }

    public List<String> getAvailableTravelDates() {

        return busScheduleDAO.getAvailableTravelDates();
    }

    public List<BusSchedule> getTodaysSchedules() {

        return busScheduleDAO.getTodaysSchedules();
    }
}