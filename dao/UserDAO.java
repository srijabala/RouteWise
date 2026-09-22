package dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

public class UserDAO {

    public User findByEmail(String email) {

        String sql = """
                SELECT user_id, full_name, email, password_hash,
                       phone, gender, role
                FROM users
                WHERE email = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    User user = new User();

                    user.setUserId(resultSet.getInt("user_id"));
                    user.setFullName(resultSet.getString("full_name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPasswordHash(resultSet.getString("password_hash"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setGender(resultSet.getString("gender"));
                    user.setRole(resultSet.getString("role"));

                    return user;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean registerUser(User user) {

    String sql = """
            INSERT INTO users
            (full_name, email, password_hash, phone, gender, role)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, user.getFullName());
        statement.setString(2, user.getEmail());
        statement.setString(3, user.getPasswordHash());
        statement.setString(4, user.getPhone());
        statement.setString(5, user.getGender());
        statement.setString(6, "CUSTOMER");

        int rowsInserted = statement.executeUpdate();

        return rowsInserted > 0;

    } catch (SQLException e) {

        e.printStackTrace();
        return false;
    }
}
}