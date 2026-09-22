import java.time.LocalDate;
import java.util.List;
import model.BusSchedule;
import service.BusScheduleService;

public class TestBusSearch {

    public static void main(String[] args) {

        BusScheduleService service = new BusScheduleService();

        List<BusSchedule> schedules =
                service.searchSchedules(
                        "Visakhapatnam",
                        "Vijayawada",
                        LocalDate.of(2026, 9, 20)
                );

        for (BusSchedule schedule : schedules) {

            System.out.println(
                    schedule.getBusNumber()
                    + " | "
                    + schedule.getOperatorName()
                    + " | "
                    + schedule.getBusType()
                    + " | "
                    + schedule.getDepartureTime()
                    + " | ₹"
                    + schedule.getFare()
            );
        }
    }
}