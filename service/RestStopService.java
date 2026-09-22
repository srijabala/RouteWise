package service;

import dao.RestStopDAO;
import java.util.List;
import model.RestStop;

public class RestStopService {

    private final RestStopDAO restStopDAO;

    public RestStopService() {
        restStopDAO = new RestStopDAO();
    }

    public List<RestStop> getRestStopsByRouteId(int routeId) {
        return restStopDAO.getRestStopsByRouteId(routeId);
    }
}