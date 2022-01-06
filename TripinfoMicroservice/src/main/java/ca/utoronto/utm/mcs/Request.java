package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Request extends Endpoint {
    // Creates a trip request and finds nearby drivers.
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // Get information from request body
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String uid;
        int radius;

        if (body.has("uid") && body.has("radius")) {
            uid = body.getString("uid");
            radius = body.getInt("radius");
        } else {
            this.sendStatus(r, 400, true);
            return;
        }

        // Make sure uid is valid
        if (uid.equals("")) {
            this.sendStatus(r, 400, true);
            return;
        }

        // Use the location microservice to get all the uids of drivers near the user
        List<String> driverUidList = new ArrayList<>();
        try {
            String url = "http://locationmicroservice:8000/location/";
            String endpoint = "nearbyDriver/" + uid + "?radius=" + radius;

            HttpResponse<String> locationRes = this.sendRequest(url, endpoint, "GET");
            JSONObject locationResBody = new JSONObject(locationRes.body());
            JSONObject locationResData = new JSONObject(locationResBody.getString("data"));

            // Go through the list of drivers and get their uids
            Iterator<String> driverUids = locationResData.keys();

            while (driverUids.hasNext()) {
                String driverUid = driverUids.next();
                driverUidList.add(driverUid);
            }

            // Prepare our response object and send it
            JSONObject res = new JSONObject();
            int status = driverUidList.isEmpty() ? 404: 200;
            res.put("data", driverUidList);

            sendResponse(r, res, status);
            return;

        } catch (Exception e) {
            this.sendStatus(r, 500, true);
            return;
        }
    }
}
