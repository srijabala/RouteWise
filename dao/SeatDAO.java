package dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Seat;

public class SeatDAO {

    public List<Seat> findSeatsByScheduleId(int scheduleId) {

        List<Seat> seats = new ArrayList<>();

        String sql = """
                SELECT
                    seat_id,
                    schedule_id,
                    seat_number,
                    seat_type,
                    berth_type,
                    status
                FROM seats
                WHERE schedule_id = ?
                ORDER BY seat_id
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, scheduleId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Seat seat = new Seat();

                    seat.setSeatId(
                            resultSet.getInt("seat_id")
                    );

                    seat.setScheduleId(
                            resultSet.getInt("schedule_id")
                    );

                    seat.setSeatNumber(
                            resultSet.getString("seat_number")
                    );

                    seat.setSeatType(
                            resultSet.getString("seat_type")
                    );

                    seat.setBerthType(
                            resultSet.getString("berth_type")
                    );

                    seat.setStatus(
                            resultSet.getString("status")
                    );

                    seats.add(seat);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return seats;


    }

    public List<Integer> findFemaleOccupiedSeatIds(int scheduleId) {

    List<Integer> femaleOccupiedSeatIds = new ArrayList<>();

    String sql = """
            SELECT bs.seat_id
            FROM booking_seats bs
            JOIN bookings b
                ON bs.booking_id = b.booking_id
            JOIN users u
                ON b.user_id = u.user_id
            WHERE b.schedule_id = ?
              AND b.status = 'CONFIRMED'
              AND u.gender = 'FEMALE'
            """;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, scheduleId);

        try (ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                femaleOccupiedSeatIds.add(
                        resultSet.getInt("seat_id")
                );
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return femaleOccupiedSeatIds;
}
}