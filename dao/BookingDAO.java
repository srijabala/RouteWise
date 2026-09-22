package dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Booking;
import model.Seat;

public class BookingDAO {

    private PaymentDAO paymentDAO;

    public BookingDAO() {
        paymentDAO = new PaymentDAO();
    }

    public boolean createBooking(
            int userId,
            int scheduleId,
            List<Seat> selectedSeats,
            double totalAmount,
            String paymentMethod,
            int devotionalPackageId,
            int seniorCitizenCount,
            int wheelchairCount) {

        Connection connection = null;

        try {

            connection = DatabaseConnection.getConnection();

            connection.setAutoCommit(false);

            /*
             * STEP 1:
             * Lock the schedule row.
             */
            String lockScheduleSql = """
                    SELECT schedule_id
                    FROM bus_schedules
                    WHERE schedule_id = ?
                    FOR UPDATE
                    """;

            try (PreparedStatement statement =
                    connection.prepareStatement(lockScheduleSql)) {

                statement.setInt(1, scheduleId);

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }
                }
            }

            /*
             * STEP 2:
             * Check accessibility capacity.
             */
            String accessibilitySql = """
                    SELECT
                        COALESCE(SUM(senior_citizen_count), 0)
                            AS senior_count,
                        COALESCE(SUM(wheelchair_count), 0)
                            AS wheelchair_count
                    FROM bookings
                    WHERE schedule_id = ?
                      AND status = 'CONFIRMED'
                    """;

            try (PreparedStatement statement =
                    connection.prepareStatement(accessibilitySql)) {

                statement.setInt(1, scheduleId);

                try (ResultSet resultSet = statement.executeQuery()) {

                    if (resultSet.next()) {

                        int existingSeniorCount =
                                resultSet.getInt("senior_count");

                        int existingWheelchairCount =
                                resultSet.getInt("wheelchair_count");

                        if (existingSeniorCount + seniorCitizenCount > 4) {
                            connection.rollback();
                            return false;
                        }

                        if (existingWheelchairCount + wheelchairCount > 2) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
            }

            /*
             * STEP 3:
             * Create booking.
             */
            String bookingSql = """
                    INSERT INTO bookings
                    (
                        user_id,
                        schedule_id,
                        total_seats,
                        total_amount,
                        devotional_package_id,
                        senior_citizen_count,
                        wheelchair_count
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;

            int bookingId;

            try (PreparedStatement statement =
                    connection.prepareStatement(
                            bookingSql,
                            PreparedStatement.RETURN_GENERATED_KEYS)) {

                statement.setInt(1, userId);
                statement.setInt(2, scheduleId);
                statement.setInt(3, selectedSeats.size());
                statement.setDouble(4, totalAmount);

                if (devotionalPackageId == 0) {

                    statement.setNull(
                            5,
                            java.sql.Types.INTEGER
                    );

                } else {

                    statement.setInt(
                            5,
                            devotionalPackageId
                    );
                }

                statement.setInt(6, seniorCitizenCount);
                statement.setInt(7, wheelchairCount);

                statement.executeUpdate();

                try (ResultSet resultSet =
                        statement.getGeneratedKeys()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        return false;
                    }

                    bookingId = resultSet.getInt(1);
                }
            }

            /*
             * STEP 4:
             * Mark seats as BOOKED.
             */
            String updateSeatSql = """
                    UPDATE seats
                    SET status = 'BOOKED'
                    WHERE seat_id = ?
                    """;

            try (PreparedStatement statement =
                    connection.prepareStatement(updateSeatSql)) {

                for (Seat seat : selectedSeats) {

                    statement.setInt(
                            1,
                            seat.getSeatId()
                    );

                    statement.addBatch();
                }

                statement.executeBatch();
            }

            /*
             * STEP 5:
             * Store booking-seat relationships.
             */
            String bookingSeatSql = """
                    INSERT INTO booking_seats
                    (
                        booking_id,
                        seat_id
                    )
                    VALUES (?, ?)
                    """;

            try (PreparedStatement statement =
                    connection.prepareStatement(bookingSeatSql)) {

                for (Seat seat : selectedSeats) {

                    statement.setInt(
                            1,
                            bookingId
                    );

                    statement.setInt(
                            2,
                            seat.getSeatId()
                    );

                    statement.addBatch();
                }

                statement.executeBatch();
            }

            /*
             * STEP 6:
             * Create payment using SAME connection.
             */
            boolean paymentCreated =
                    paymentDAO.createPayment(
                            connection,
                            bookingId,
                            totalAmount,
                            paymentMethod
                    );

            if (!paymentCreated) {

                connection.rollback();
                return false;
            }

            /*
             * STEP 7:
             * Commit everything.
             */
            connection.commit();

            return true;

        } catch (SQLException e) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            e.printStackTrace();

            return false;

        } finally {

            if (connection != null) {

                try {

                    connection.setAutoCommit(true);
                    connection.close();

                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    public List<Booking> findBookingsByUserId(int userId) {

        List<Booking> bookings = new ArrayList<>();

        String sql = """
                SELECT
                    b.booking_id,
                    b.user_id,
                    b.schedule_id,
                    r.source_city,
                    r.destination_city,
                    bs.departure_time,
                    bs.arrival_time,
                    bus.bus_number,
                    bt.type_name,
                    b.booking_date,
                    b.total_seats,
                    b.total_amount,
                    b.status,
                    b.senior_citizen_count,
                    b.wheelchair_count,
                    p.payment_method,
                    p.payment_status
                FROM bookings b
                JOIN bus_schedules bs
                    ON b.schedule_id = bs.schedule_id
                JOIN routes r
                    ON bs.route_id = r.route_id
                JOIN buses bus
                    ON bs.bus_id = bus.bus_id
                JOIN bus_types bt
                    ON bus.bus_type_id = bt.bus_type_id
                LEFT JOIN payments p
                    ON b.booking_id = p.booking_id
                WHERE b.user_id = ?
                ORDER BY b.booking_date DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            try (ResultSet resultSet =
                    statement.executeQuery()) {

                while (resultSet.next()) {

                    Booking booking = new Booking();

                    booking.setBookingId(
                            resultSet.getInt("booking_id")
                    );

                    booking.setUserId(
                            resultSet.getInt("user_id")
                    );

                    booking.setScheduleId(
                            resultSet.getInt("schedule_id")
                    );

                    booking.setSourceCity(
                            resultSet.getString("source_city")
                    );

                    booking.setDestinationCity(
                            resultSet.getString("destination_city")
                    );

                    booking.setDepartureTime(
                            resultSet.getTimestamp("departure_time")
                                    .toLocalDateTime()
                    );

                    booking.setArrivalTime(
                            resultSet.getTimestamp("arrival_time")
                                    .toLocalDateTime()
                    );

                    booking.setBusNumber(
                            resultSet.getString("bus_number")
                    );

                    booking.setBusType(
                            resultSet.getString("type_name")
                    );

                    booking.setBookingDate(
                            resultSet.getTimestamp("booking_date")
                                    .toLocalDateTime()
                    );

                    booking.setTotalSeats(
                            resultSet.getInt("total_seats")
                    );

                    booking.setTotalAmount(
                            resultSet.getDouble("total_amount")
                    );

                    booking.setSeniorCitizenCount(
                            resultSet.getInt(
                                    "senior_citizen_count"
                            )
                    );

                    booking.setWheelchairCount(
                            resultSet.getInt(
                                    "wheelchair_count"
                            )
                    );

                    booking.setStatus(
                            resultSet.getString("status")
                    );

                    booking.setPaymentMethod(
                            resultSet.getString("payment_method")
                    );

                    booking.setPaymentStatus(
                            resultSet.getString("payment_status")
                    );

                    bookings.add(booking);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return bookings;
    }

    public boolean cancelBooking(
            int bookingId,
            int userId) {

        Connection connection = null;

        try {

            connection =
                    DatabaseConnection.getConnection();

            connection.setAutoCommit(false);

            /*
             * STEP 1:
             * Lock booking and verify owner.
             */
            String checkBookingSql = """
                    SELECT booking_id, status
                    FROM bookings
                    WHERE booking_id = ?
                      AND user_id = ?
                    FOR UPDATE
                    """;

            try (PreparedStatement statement =
                    connection.prepareStatement(
                            checkBookingSql)) {

                statement.setInt(1, bookingId);
                statement.setInt(2, userId);

                try (ResultSet resultSet =
                        statement.executeQuery()) {

                    if (!resultSet.next()) {

                        connection.rollback();
                        return false;
                    }

                    String status =
                            resultSet.getString("status");

                    if (!"CONFIRMED".equals(status)) {

                        connection.rollback();
                        return false;
                    }
                }
            }

            /*
             * STEP 2:
             * Release seats.
             */
            String releaseSeatsSql = """
                    UPDATE seats s
                    JOIN booking_seats bs
                        ON s.seat_id = bs.seat_id
                    SET s.status = 'AVAILABLE'
                    WHERE bs.booking_id = ?
                    """;

            try (PreparedStatement statement =
                    connection.prepareStatement(
                            releaseSeatsSql)) {

                statement.setInt(1, bookingId);
                statement.executeUpdate();
            }

            /*
             * STEP 3:
             * Cancel booking.
             */
            String cancelBookingSql = """
                    UPDATE bookings
                    SET status = 'CANCELLED'
                    WHERE booking_id = ?
                    """;

            try (PreparedStatement statement =
                    connection.prepareStatement(
                            cancelBookingSql)) {

                statement.setInt(1, bookingId);
                statement.executeUpdate();
            }

            /*
             * STEP 4:
             * Refund payment.
             */
            String refundPaymentSql = """
                    UPDATE payments
                    SET payment_status = 'REFUNDED'
                    WHERE booking_id = ?
                    """;

            try (PreparedStatement statement =
                    connection.prepareStatement(
                            refundPaymentSql)) {

                statement.setInt(1, bookingId);
                statement.executeUpdate();
            }

            connection.commit();

            return true;

        } catch (SQLException e) {

            if (connection != null) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            e.printStackTrace();

            return false;

        } finally {

            if (connection != null) {

                try {

                    connection.setAutoCommit(true);
                    connection.close();

                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }
    }

    public List<Booking> findAllBookings() {

        List<Booking> bookings = new ArrayList<>();

        String sql = """
                SELECT
                    b.booking_id,
                    b.user_id,
                    b.schedule_id,
                    r.source_city,
                    r.destination_city,
                    bs.departure_time,
                    bs.arrival_time,
                    bus.bus_number,
                    bt.type_name,
                    b.booking_date,
                    b.total_seats,
                    b.total_amount,
                    b.senior_citizen_count,
                    b.wheelchair_count,
                    b.status,
                    p.payment_method,
                    p.payment_status
                FROM bookings b
                JOIN bus_schedules bs
                    ON b.schedule_id = bs.schedule_id
                JOIN routes r
                    ON bs.route_id = r.route_id
                JOIN buses bus
                    ON bs.bus_id = bus.bus_id
                JOIN bus_types bt
                    ON bus.bus_type_id = bt.bus_type_id
                LEFT JOIN payments p
                    ON b.booking_id = p.booking_id
                ORDER BY b.booking_date DESC
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Booking booking = new Booking();

                booking.setBookingId(
                        resultSet.getInt("booking_id")
                );

                booking.setUserId(
                        resultSet.getInt("user_id")
                );

                booking.setScheduleId(
                        resultSet.getInt("schedule_id")
                );

                booking.setSourceCity(
                        resultSet.getString("source_city")
                );

                booking.setDestinationCity(
                        resultSet.getString("destination_city")
                );

                booking.setDepartureTime(
                        resultSet.getTimestamp("departure_time")
                                .toLocalDateTime()
                );

                booking.setArrivalTime(
                        resultSet.getTimestamp("arrival_time")
                                .toLocalDateTime()
                );

                booking.setBusNumber(
                        resultSet.getString("bus_number")
                );

                booking.setBusType(
                        resultSet.getString("type_name")
                );

                booking.setBookingDate(
                        resultSet.getTimestamp("booking_date")
                                .toLocalDateTime()
                );

                booking.setTotalSeats(
                        resultSet.getInt("total_seats")
                );

                booking.setTotalAmount(
                        resultSet.getDouble("total_amount")
                );

                /*
                 * IMPORTANT:
                 * These two lines were missing before.
                 * The SQL SELECT had the columns, but the
                 * values were never copied into Booking.
                 */
                booking.setSeniorCitizenCount(
                        resultSet.getInt(
                                "senior_citizen_count"
                        )
                );

                booking.setWheelchairCount(
                        resultSet.getInt(
                                "wheelchair_count"
                        )
                );

                booking.setStatus(
                        resultSet.getString("status")
                );

                booking.setPaymentMethod(
                        resultSet.getString("payment_method")
                );

                booking.setPaymentStatus(
                        resultSet.getString("payment_status")
                );

                bookings.add(booking);
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return bookings;
    }
}