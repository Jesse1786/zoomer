package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Confirm extends Endpoint {

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // Get information from request body
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String driverUid, passengerUid;
        int startTime;

        if (body.has("driver") && body.has("passenger") && body.has("startTime")) {
            driverUid = body.getString("driver");
            passengerUid = body.getString("passenger");
            startTime = body.getInt("startTime");
        } else {
            this.sendStatus(r, 400, true);
            return;
        }

        // Make sure values are valid
        if (driverUid.equals("") || passengerUid.equals("")) {
            this.sendStatus(r, 400, true);
            return;
        }

        try {
            // Insert into the db
            Document doc = this.dao.insert(driverUid, passengerUid, startTime);

            // Prepare our response object and send it
            JSONObject res = new JSONObject();
            Document idDoc = new Document("_id", doc.getObjectId("_id"));
            res.put("data", new JSONObject(idDoc.toJson()));
            sendResponse(r, res, 200);
            return;

        } catch (Exception e) {
            this.sendStatus(r, 500, true);
            return;
        }
    }
}
