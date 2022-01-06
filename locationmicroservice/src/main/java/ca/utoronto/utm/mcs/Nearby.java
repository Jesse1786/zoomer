package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import java.io.IOException;

public class Nearby extends Endpoint {

    /**
     * GET /location/nearbyDriver/:uid?radius=
     * Get the drivers that are in a radius that the user defined from the user's current location.
     *
     * @param r
     * @return 200, 400, 404, 500
     */
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

        // Break down radius key/value and check for issues
        String[] radiusArray = paramArray[1].split("=");
        if (radiusArray.length != 2 || !radiusArray[0].equals("radius") || radiusArray[1].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }

        String uid = paramArray[0];
        int radius;

        // Make sure radius is an int
        try {
            radius = Integer.parseInt(radiusArray[1]);
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 400, true);
            return;
        }

        try {
            // Get the user's current longitude and latitude
            Result userLocationResult = this.dao.getUserLocationByUid(uid);
            if (!userLocationResult.hasNext()) {
                this.sendStatus(r, 404, true);
                return;
            }

            Record userLocationRecord = userLocationResult.next();
            Double userLongitude = userLocationRecord.get("n.longitude").asDouble();
            Double userLatitude = userLocationRecord.get("n.latitude").asDouble();

            // Get all the drivers excluding the user
            Result allDriversResult = this.dao.getAllOtherDrivers(uid);
            if (!allDriversResult.hasNext()) {
                this.sendStatus(r, 404, true);
                return;
            }

            // Go through all the drivers to find ones that meet our criteria
            JSONObject drivers = new JSONObject();
            while (allDriversResult.hasNext()) {
                Record driverRecord = allDriversResult.next();
                Double driverLongitude = driverRecord.get("n.longitude").asDouble();
                Double driverLatitude = driverRecord.get("n.latitude").asDouble();
                String driverStreet = driverRecord.get("n.street").asString();
                String driverUid = driverRecord.get("n.uid").asString();

                // Store drivers that are within the specified radius
                if (insideRadius(radius, userLongitude, userLatitude, driverLongitude, driverLatitude)) {
                    JSONObject driverInfo = new JSONObject();
                    driverInfo.put("longitude", driverLongitude);
                    driverInfo.put("latitude", driverLatitude);
                    driverInfo.put("street", driverStreet);
                    drivers.put(driverUid, driverInfo);
                }
            }

            // Check if drivers are available
            if (drivers.length() == 0) {
                this.sendStatus(r, 404, true);
                return;
            }

            // Send our response
            JSONObject res = new JSONObject();
            res.put("data", drivers);
            sendResponse(r, res, 200);

        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        }
    }

    /**
     * Check if the driver is within a specified radius from the user's location.
     *
     * @param radius
     * @param userLongitude
     * @param userLatitude
     * @param driverLongitude
     * @param driverLatitude
     * @return boolean
     */
    public boolean insideRadius(int radius, double userLongitude, double userLatitude, double driverLongitude, double driverLatitude) {
        double distance = Math.sqrt(Math.pow((userLongitude - driverLongitude), 2) + Math.pow((userLatitude - driverLatitude), 2));
        return radius > distance;
    }
}
