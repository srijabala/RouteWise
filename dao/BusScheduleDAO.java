package dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.BusSchedule;

public class BusScheduleDAO {

    public List<BusSchedule> searchSchedules(
            String sourceCity,
            String destinationCity,
            LocalDate travelDate) {

        List<BusSchedule> schedules = new ArrayList<>();

          String sql = """
        SELECT
            bs.schedule_id,
            r.source_city,
            r.destination_city,
            b.bus_number,
            b.operator_name,
            bt.type_name,
            bs.departure_time,
            bs.arrival_time,
            bs.fare,
            b.total_seats,
            bs.trip_category,
            
            bs.route_id,
            SUM(CASE
                    WHEN s.status = 'AVAILABLE' THEN 1
                    ELSE 0
                END) AS available_seats
        FROM bus_schedules bs
        JOIN routes r
            ON bs.route_id = r.route_id
        JOIN buses b
            ON bs.bus_id = b.bus_id
        JOIN bus_types bt
            ON b.bus_type_id = bt.bus_type_id
        JOIN seats s
            ON bs.schedule_id = s.schedule_id
        WHERE r.source_city = ?
          AND r.destination_city = ?
          AND DATE(bs.departure_time) = ?
          AND bs.status = 'SCHEDULED'
        GROUP BY
            bs.schedule_id,
            r.source_city,
            r.destination_city,
            b.bus_number,
            b.operator_name,
            bt.type_name,
            bs.departure_time,
            bs.arrival_time,
            bs.fare,
            b.total_seats
        ORDER BY bs.departure_time
        """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, sourceCity);
            statement.setString(2, destinationCity);
            statement.setDate(
                    3,
                    java.sql.Date.valueOf(travelDate)
            );

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    BusSchedule schedule = new BusSchedule();

                    schedule.setScheduleId(
                            resultSet.getInt("schedule_id")
                    );
                   schedule.setRouteId(
               resultSet.getInt("route_id")
                   );
                    schedule.setSourceCity(
                            resultSet.getString("source_city")
                    );

                    schedule.setDestinationCity(
                            resultSet.getString("destination_city")
                    );

                    schedule.setBusNumber(
                            resultSet.getString("bus_number")
                    );

                    schedule.setOperatorName(
                            resultSet.getString("operator_name")
                    );

                    schedule.setBusType(
                            resultSet.getString("type_name")
                    );

                    schedule.setDepartureTime(
                            resultSet
                                    .getTimestamp("departure_time")
                                    .toLocalDateTime()
                    );

                    schedule.setArrivalTime(
                            resultSet
                                    .getTimestamp("arrival_time")
                                    .toLocalDateTime()
                    );

                    schedule.setFare(
                            resultSet.getDouble("fare")
                    );

                  schedule.setTotalSeats(
                  resultSet.getInt("total_seats")
                );

                schedule.setTripCategory(
               resultSet.getString("trip_category")
                 );
                schedule.setAvailableSeats(
                  resultSet.getInt("available_seats")
               );

                

                    schedules.add(schedule);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return schedules;
    }

    public List<BusSchedule> getTodaysSchedules() {

    List<BusSchedule> schedules = new ArrayList<>();

    String sql = """
            SELECT
                bs.schedule_id,
                r.source_city,
                r.destination_city,
                b.bus_number,
                b.operator_name,
                bt.type_name,
                bs.departure_time,
                bs.arrival_time,
                bs.fare,
                b.total_seats,
                bs.trip_category,
                bs.route_id,
                SUM(CASE
                        WHEN s.status = 'AVAILABLE' THEN 1
                        ELSE 0
                    END) AS available_seats
            FROM bus_schedules bs
            JOIN routes r
                ON bs.route_id = r.route_id
            JOIN buses b
                ON bs.bus_id = b.bus_id
            JOIN bus_types bt
                ON b.bus_type_id = bt.bus_type_id
            JOIN seats s
                ON bs.schedule_id = s.schedule_id
            WHERE bs.status = 'SCHEDULED'
              AND DATE(bs.departure_time) = CURDATE()
              AND bs.departure_time >= NOW()
            GROUP BY
                bs.schedule_id,
                r.source_city,
                r.destination_city,
                b.bus_number,
                b.operator_name,
                bt.type_name,
                bs.departure_time,
                bs.arrival_time,
                bs.fare,
                b.total_seats,
                bs.trip_category,
                bs.route_id
            ORDER BY bs.departure_time
            """;

    try (Connection connection =
                 DatabaseConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql);
         ResultSet resultSet =
                 statement.executeQuery()) {

        while (resultSet.next()) {

            BusSchedule schedule = new BusSchedule();

            schedule.setScheduleId(
                    resultSet.getInt("schedule_id")
            );

            schedule.setRouteId(
                    resultSet.getInt("route_id")
            );

            schedule.setSourceCity(
                    resultSet.getString("source_city")
            );

            schedule.setDestinationCity(
                    resultSet.getString("destination_city")
            );

            schedule.setBusNumber(
                    resultSet.getString("bus_number")
            );

            schedule.setOperatorName(
                    resultSet.getString("operator_name")
            );

            schedule.setBusType(
                    resultSet.getString("type_name")
            );

            schedule.setDepartureTime(
                    resultSet.getTimestamp("departure_time")
                            .toLocalDateTime()
            );

            schedule.setArrivalTime(
                    resultSet.getTimestamp("arrival_time")
                            .toLocalDateTime()
            );

            schedule.setFare(
                    resultSet.getDouble("fare")
            );

            schedule.setTotalSeats(
                    resultSet.getInt("total_seats")
            );

            schedule.setTripCategory(
                    resultSet.getString("trip_category")
            );

            schedule.setAvailableSeats(
                    resultSet.getInt("available_seats")
            );

            schedules.add(schedule);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return schedules;
}
public List<String> getAvailableTravelDates() {

    List<String> dates = new ArrayList<>();

    String sql = """
            SELECT DISTINCT DATE(departure_time) AS travel_date
            FROM bus_schedules
            WHERE status = 'SCHEDULED'
              AND departure_time >= NOW()
            ORDER BY DATE(departure_time)
            """;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        while (resultSet.next()) {

            dates.add(
                    resultSet.getDate("travel_date")
                            .toLocalDate()
                            .toString()
            );
        }

    } catch (SQLException e) {

        e.printStackTrace();
    }

    return dates;
}

}