package dao;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentDAO {

    public boolean createPayment(
            Connection connection,
            int bookingId,
            double amount,
            String paymentMethod) {

        String transactionReference =
                "RW-" + UUID.randomUUID();

        String sql = """
                INSERT INTO payments
                (
                    booking_id,
                    amount,
                    payment_method,
                    payment_status,
                    transaction_reference,
                    paid_at
                )
                VALUES (?, ?, ?, 'SUCCESS', ?, ?)
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, bookingId);
            statement.setDouble(2, amount);
            statement.setString(3, paymentMethod);
            statement.setString(
                    4,
                    transactionReference
            );
            statement.setObject(
                    5,
                    LocalDateTime.now()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;
        }
    }
}