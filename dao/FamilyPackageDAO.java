package dao;

import config.DatabaseConnection;
import model.FamilyPackage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FamilyPackageDAO {

    public List<FamilyPackage> getAllPackages() {

        List<FamilyPackage> packages = new ArrayList<>();

        String sql = """
                SELECT
                    package_id,
                    destination,
                    package_name,
                    description,
                    price_per_person
                FROM family_packages
                ORDER BY destination
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                FamilyPackage familyPackage = new FamilyPackage();

                familyPackage.setPackageId(
                        resultSet.getInt("package_id")
                );

                familyPackage.setDestination(
                        resultSet.getString("destination")
                );

                familyPackage.setPackageName(
                        resultSet.getString("package_name")
                );

                familyPackage.setDescription(
                        resultSet.getString("description")
                );

                familyPackage.setPricePerPerson(
                        resultSet.getDouble("price_per_person")
                );

                packages.add(familyPackage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return packages;
    }
}