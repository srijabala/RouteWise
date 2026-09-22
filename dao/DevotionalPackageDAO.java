package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import config.DatabaseConnection;
import model.DevotionalPackage;

public class DevotionalPackageDAO {

    public DevotionalPackage getPackageByScheduleId(int scheduleId) {

        String sql = """
                SELECT
                    package_id,
                    schedule_id,
                    package_name,
                    description,
                    price_per_person
                FROM devotional_packages
                WHERE schedule_id = ?
                """;

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setInt(1, scheduleId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                DevotionalPackage devotionalPackage =
                        new DevotionalPackage();

                devotionalPackage.setPackageId(
                        resultSet.getInt("package_id")
                );

                devotionalPackage.setScheduleId(
                        resultSet.getInt("schedule_id")
                );

                devotionalPackage.setPackageName(
                        resultSet.getString("package_name")
                );

                devotionalPackage.setDescription(
                        resultSet.getString("description")
                );

                devotionalPackage.setPricePerPerson(
                        resultSet.getDouble("price_per_person")
                );

                return devotionalPackage;
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }
}