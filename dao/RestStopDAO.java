package dao;

import config.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.RestStop;

public class RestStopDAO {

    public List<RestStop> getRestStopsByRouteId(int routeId) {

        List<RestStop> restStops = new ArrayList<>();

        String sql = """
                SELECT
                    stop_id,
                    route_id,
                    stop_name,
                    stop_order,
                    food_available,
                    restroom_available,
                    wheelchair_accessible,
                    parking_available,
                    description
                FROM rest_stops
                WHERE route_id = ?
                ORDER BY stop_order
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, routeId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    RestStop restStop = new RestStop();

                    restStop.setStopId(
                            resultSet.getInt("stop_id")
                    );

                    restStop.setRouteId(
                            resultSet.getInt("route_id")
                    );

                    restStop.setStopName(
                            resultSet.getString("stop_name")
                    );

                    restStop.setStopOrder(
                            resultSet.getInt("stop_order")
                    );

                    restStop.setFoodAvailable(
                            resultSet.getBoolean("food_available")
                    );

                    restStop.setRestroomAvailable(
                            resultSet.getBoolean("restroom_available")
                    );

                    restStop.setWheelchairAccessible(
                            resultSet.getBoolean("wheelchair_accessible")
                    );

                    restStop.setParkingAvailable(
                            resultSet.getBoolean("parking_available")
                    );

                    restStop.setDescription(
                            resultSet.getString("description")
                    );

                    restStops.add(restStop);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return restStops;
    }
}