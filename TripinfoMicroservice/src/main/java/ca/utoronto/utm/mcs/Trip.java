package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoCursor;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {
    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        // Break down URL and check for issues
        String[] URLArray = r.getRequestURI().toString().split("/");
        if (URLArray.length != 3 || URLArray[2].isEmpty()) {
            sendStatus(r, 400);
            return;
        }
        String _id = URLArray[2];

        // Make sure the document with that id exists
        MongoCursor<Document> exists = this.dao.getTripById(_id);
        if (exists == null) {
            sendStatus(r, 404);
            return;
        }
        // This if statement probably isn't necessary, but mongodb has weird behaviour when filtering by id
        if (!exists.hasNext()) {
            sendStatus(r, 404);
            return;
        }

        // Get information from request body
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String timeElapsed;
        Double totalCost, driverPayout;
        int distance, endTime, discount;

        if (body.has("distance") && body.has("endTime") && body.has("timeElapsed")
                && body.has("discount") && body.has("totalCost") && body.has("driverPayout")) {
            distance = body.getInt("distance");
            endTime = body.getInt("endTime");
            timeElapsed = body.getString("timeElapsed");
            discount = body.getInt("discount");
            totalCost = body.getDouble("totalCost");
            driverPayout = body.getDouble("driverPayout");
        } else {
            this.sendStatus(r, 400);
            return;
        }

        // Update the document in the db
        try {
            this.dao.patchTrip(_id, distance, endTime, timeElapsed, discount, totalCost, driverPayout);
            sendStatus(r, 200);
            return;
        } catch (Exception e) {
            this.sendStatus(r, 500);
            return;
        }
    }
}
