package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoCursor;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpResponse;

public class DriverTime extends Endpoint {

    // Calculates the drive time for trip with specified ID
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // Break down URL and check for issues
        String[] URLArray = r.getRequestURI().toString().split("/");
        if (URLArray.length != 4 || URLArray[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }
        String _id = URLArray[3];

        try {
            // Find the document with the given id
            MongoCursor<Document> cursor = this.dao.getTripById(_id);
            if (cursor == null) {
                this.sendStatus(r, 404, true);
                return;
            }
            // This if statement probably isn't necessary, but mongodb has weird behaviour when filtering by id
            if (!cursor.hasNext()) {
                this.sendStatus(r, 404, true);
                return;
            }

            // Get the driver and passenger uids
            Document tripDoc = cursor.next();
            JSONObject tripJson = new JSONObject(tripDoc.toJson());
            String driverUid = tripJson.getString("driver");
            String passengerUid = tripJson.getString("passenger");

            // Use the location microservice to get the relevant information
            String url = "http://locationmicroservice:8000/location/";
            String endpoint = "navigation/" + driverUid + "?passengerUid=" + passengerUid;

            HttpResponse<String> locationRes = this.sendRequest(url, endpoint, "GET");
            if (locationRes.statusCode() != 200) {
                this.sendStatus(r, 404, true);
                return;
            }
            JSONObject locationResBody = new JSONObject(locationRes.body());
            JSONObject locationResData = new JSONObject(locationResBody.getString("data"));
            int estimatedTime = locationResData.getInt("total_time");

            // Prepare our response object and send it
            JSONObject res = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("arrival_time", estimatedTime);
            res.put("data", data);

            this.sendResponse(r, res,200);
            return;

        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
    }
}
