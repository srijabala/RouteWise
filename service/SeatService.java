package service;

import dao.SeatDAO;
import java.util.List;
import model.Seat;

public class SeatService {

    private SeatDAO seatDAO;

    public SeatService() {
        seatDAO = new SeatDAO();
    }

    public List<Seat> getSeatsByScheduleId(int scheduleId) {

        return seatDAO.findSeatsByScheduleId(scheduleId);
    }
public List<Integer> getFemaleOccupiedSeatIds(int scheduleId) {

    return seatDAO.findFemaleOccupiedSeatIds(scheduleId);
}


}