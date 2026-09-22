package service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import model.BusSchedule;

public class AIContextService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public String buildScheduleContext(List<BusSchedule> schedules) {

        if (schedules == null || schedules.isEmpty()) {
            return "No RouteWise bus schedules were found for this search.";
        }

        StringBuilder context = new StringBuilder();

        context.append("CURRENT ROUTEWISE BUS SEARCH RESULTS:\n\n");

        for (BusSchedule schedule : schedules) {

            context.append("Schedule ID: ")
                    .append(schedule.getScheduleId())
                    .append("\n");

            context.append("Route: ")
                    .append(schedule.getSourceCity())
                    .append(" to ")
                    .append(schedule.getDestinationCity())
                    .append("\n");

            context.append("Bus Number: ")
                    .append(schedule.getBusNumber())
                    .append("\n");

            context.append("Operator: ")
                    .append(schedule.getOperatorName())
                    .append("\n");

            context.append("Bus Type: ")
                    .append(schedule.getBusType())
                    .append("\n");

            context.append("Departure: ")
                    .append(schedule.getDepartureTime()
                            .format(DATE_TIME_FORMATTER))
                    .append("\n");

            context.append("Arrival: ")
                    .append(schedule.getArrivalTime()
                            .format(DATE_TIME_FORMATTER))
                    .append("\n");

            context.append("Fare: ₹")
                    .append(schedule.getFare())
                    .append("\n");

            context.append("Total Seats: ")
                    .append(schedule.getTotalSeats())
                    .append("\n");

            context.append("Available Seats: ")
                    .append(schedule.getAvailableSeats())
                    .append("\n");

            context.append("Trip Category: ")
                    .append(schedule.getTripCategory())
                    .append("\n");

            context.append("\n-----------------------------\n\n");
        }

        return context.toString();
    }
}