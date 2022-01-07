package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Navigation extends Endpoint {

    // Gets a path from the driver to the passenger, if one exists.
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // Break down URL and check for issues
        String[] URLArray = r.getRequestURI().toString().split("/");
        if (URLArray.length != 4 || URLArray[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        // Break down parameters and check for issues
        String[] paramArray = URLArray[3].split("\\?");
        if (paramArray.length != 2 || paramArray[0].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        // Break down passengerUid key/value and check for issues
        String[] passengerUidArray = paramArray[1].split("=");
        if (passengerUidArray.length != 2 || !passengerUidArray[0].equals("passengerUid") || passengerUidArray[1].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        String driverUid = paramArray[0];
        String passengerUid = passengerUidArray[1];

        try {
            // Find the driver's current road
            Result driverRoadResult = this.dao.getUserStreet(driverUid);
            if (!driverRoadResult.hasNext()) {
                this.sendStatus(r, 404, true);
                return;
            }
            Record driverRoadRecord = driverRoadResult.next();
            String driverRoad = driverRoadRecord.get("n.street").asString();

            // Find the passenger's current road
            Result passengerRoadResult = this.dao.getUserStreet(passengerUid);
            if (!passengerRoadResult.hasNext()) {
                this.sendStatus(r, 404, true);
                return;
            }
            Record passengerRoadRecord = passengerRoadResult.next();
            String passengerRoad = passengerRoadRecord.get("n.street").asString();

            // Prepare the data we need for our response
            JSONObject data = new JSONObject();
            int total_time = 0;
            List<JSONObject> routeList = new ArrayList<>();
            JSONObject firstRouteObject = new JSONObject();

            // Grab the route at the start and put it into our list. We keep track of this as the previous street
            Result pathResult = this.dao.getShortestWeightedPath(driverRoad, passengerRoad);
            if (!pathResult.hasNext()) {
                this.sendStatus(r, 404, true);
                return;
            }
            Record previousPathRecord = pathResult.next();
            String previousStreet = previousPathRecord.get("result.name").asString();
            Boolean previousTrafficBoolean = previousPathRecord.get("result.is_traffic").asBoolean();
            firstRouteObject.put("street", previousStreet);
            firstRouteObject.put("time", 0);
            firstRouteObject.put("is_traffic", previousTrafficBoolean);
            routeList.add(firstRouteObject);

            // Loop through our pathResult to get the shortest weighted path from the driver's road to the passenger's road
            while (pathResult.hasNext()) {
                Record pathRecord = pathResult.next();
                String street = pathRecord.get("result.name").asString();
                Boolean trafficBoolean = pathRecord.get("result.is_traffic").asBoolean();

                // Use the previousStreet to calculate the time to the current street
                Result routeTimeResult = this.dao.getRouteTime(previousStreet, street);
                if (!routeTimeResult.hasNext()) {
                    this.sendStatus(r, 404, true);
                    return;
                }
                Record routeTimeRecord = routeTimeResult.next();
                int time = routeTimeRecord.get("r.travel_time").asInt();

                JSONObject routeObject = new JSONObject();
                routeObject.put("street", street);
                routeObject.put("is_traffic", trafficBoolean);
                routeObject.put("time", time);
                routeList.add(routeObject);

                previousStreet = street;
                total_time += time;
            }

            // Send our response
            JSONObject res = new JSONObject();
            data.put("total_time", total_time);
            data.put("route", routeList);
            res.put("data", data);
            sendResponse(r, res, 200);

        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        }
    }
}
