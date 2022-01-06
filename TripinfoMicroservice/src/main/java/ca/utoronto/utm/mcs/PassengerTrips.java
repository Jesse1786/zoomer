package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoCursor;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Returns all of the trips made by passenger with said uid, assuming that they exist
public class PassengerTrips extends Endpoint {
    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // Break down URL and check for issues
        String[] URLArray = r.getRequestURI().toString().split("/");
        if (URLArray.length != 4 || URLArray[3].isEmpty()) {
            this.sendStatus(r, 400, true);
            return;
        }
        String passengerUid = URLArray[3];

        try {
            // Prepare our response objects
            JSONObject trips = new JSONObject();
            List<JSONObject> tripList = new ArrayList<>();
            MongoCursor<Document> cursor = this.dao.getPassengerTrips(passengerUid);

            // Error if not found
            if (!cursor.hasNext()) {
                this.sendStatus(r, 404, true);
                return;
            }

            // If passenger exists, get all the passenger's trips
            while (cursor.hasNext()) {
                Document trip = cursor.next();
                trip.remove("passenger");
                trip.remove("driverPayout");
                tripList.add(new JSONObject(trip.toJson()));
            }

            // Send our response
            JSONObject res = new JSONObject();
            trips.put("trips", tripList);
            res.put("data", trips);
            this.sendResponse(r, res, 200);
            return;
        } catch (Exception e) {
            this.sendStatus(r, 500, true);
            return;
        }
    }
}
