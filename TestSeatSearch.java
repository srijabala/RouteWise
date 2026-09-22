import model.Seat;
import service.SeatService;

import java.util.List;

public class TestSeatSearch {

    public static void main(String[] args) {

        SeatService seatService = new SeatService();

        List<Seat> seats =
                seatService.getSeatsByScheduleId(1);

        for (Seat seat : seats) {

            System.out.println(
                    seat.getSeatNumber()
                    + " | "
                    + seat.getSeatType()
                    + " | "
                    + seat.getBerthType()
                    + " | "
                    + seat.getStatus()
            );
        }
    }
}