package ca.utoronto.utm.mcs;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TripInfoTests {

    final static String url = "http://localhost:8000";

    public static HttpResponse<String> sendRequest(String url, String endpoint, String method) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(""))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public static HttpResponse<String> sendRequest(String url, String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void tripRequestPass() throws JSONException, IOException, InterruptedException {
        String uniqueUser1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueUser2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueRoad1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueRoad2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        JSONObject testRoad1 = new JSONObject();
        testRoad1.put("roadName", uniqueRoad1);
        testRoad1.put("hasTraffic", false);

        JSONObject testRoad2 = new JSONObject();
        testRoad2.put("roadName", uniqueRoad2);
        testRoad2.put("hasTraffic", false);

        sendRequest(url, "/location/road", "PUT", testRoad1.toString());
        sendRequest(url, "/location/road", "PUT", testRoad2.toString());

        JSONObject driver1 = new JSONObject();
        driver1.put("uid", uniqueUser1);
        driver1.put("is_driver", false);

        JSONObject driver1Patch = new JSONObject();
        driver1Patch.put("longitude", 55.00);
        driver1Patch.put("latitude", 58.00);
        driver1Patch.put("street", uniqueRoad1);

        sendRequest(url, "/location/user", "PUT", driver1.toString());
        sendRequest(url, "/location/" + uniqueUser1, "PATCH", driver1Patch.toString());

        JSONObject driver2 = new JSONObject();
        driver2.put("uid", uniqueUser2);
        driver2.put("is_driver", true);

        JSONObject driver2Patch = new JSONObject();
        driver2Patch.put("longitude", 55.02);
        driver2Patch.put("latitude", 57.98);
        driver2Patch.put("street", uniqueRoad2);

        sendRequest(url, "/location/user", "PUT", driver2.toString());
        sendRequest(url, "/location/" + uniqueUser2, "PATCH", driver2Patch.toString());

        JSONObject tripRequest = new JSONObject();
        tripRequest.put("uid", uniqueUser1);
        tripRequest.put("radius", 20);
        HttpResponse<String> res = sendRequest(url, "/trip/request", "POST", tripRequest.toString());
        assertEquals(200, res.statusCode());
    }

    @Test
    public void tripRequestFail() throws JSONException, IOException, InterruptedException {
        JSONObject tripRequest = new JSONObject();
        tripRequest.put("uid", "InvalidRequest");
        HttpResponse<String> res = sendRequest(url, "/trip/request", "POST", tripRequest.toString());
        assertEquals(400, res.statusCode());
    }

    @Test
    public void tripConfirmPass() throws JSONException, IOException, InterruptedException {
        String uniqueUser1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueUser2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueRoad1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueRoad2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        JSONObject driver1 = new JSONObject();
        driver1.put("uid", uniqueUser1);
        driver1.put("is_driver", false);

        JSONObject driver1Patch = new JSONObject();
        driver1Patch.put("longitude", 55.00);
        driver1Patch.put("latitude", 58.00);
        driver1Patch.put("street", uniqueRoad1);

        sendRequest(url, "/location/user", "PUT", driver1.toString());
        sendRequest(url, "/location/" + uniqueUser1, "PATCH", driver1Patch.toString());

        JSONObject driver2 = new JSONObject();
        driver2.put("uid", uniqueUser2);
        driver2.put("is_driver", true);

        JSONObject driver2Patch = new JSONObject();
        driver2Patch.put("longitude", 55.02);
        driver2Patch.put("latitude", 57.98);
        driver2Patch.put("street", uniqueRoad2);

        sendRequest(url, "/location/user", "PUT", driver2.toString());
        sendRequest(url, "/location/" + uniqueUser2, "PATCH", driver2Patch.toString());

        JSONObject tripConfirm = new JSONObject();
        tripConfirm.put("driver", uniqueUser1);
        tripConfirm.put("passenger", uniqueUser2);
        tripConfirm.put("startTime", 1638195330);
        
        HttpResponse<String> res = sendRequest(url, "/trip/confirm", "POST", tripConfirm.toString());
        assertEquals(200, res.statusCode());
    }

    @Test
    public void tripConfirmFail() throws JSONException, IOException, InterruptedException {
        JSONObject tripConfirm = new JSONObject();
        String uniqueUser2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        tripConfirm.put("driver", uniqueUser2);
        HttpResponse<String> res = sendRequest(url, "/trip/confirm", "POST", tripConfirm.toString());
        assertEquals(400, res.statusCode());
    }

    @Test
    public void patchTripPass() throws JSONException, IOException, InterruptedException {
        String uniqueUser1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueUser2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueRoad1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueRoad2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        JSONObject testRoad1 = new JSONObject();
        testRoad1.put("roadName", uniqueRoad1);
        testRoad1.put("hasTraffic", false);

        JSONObject testRoad2 = new JSONObject();
        testRoad2.put("roadName", uniqueRoad2);
        testRoad2.put("hasTraffic", false);

        sendRequest(url, "/location/road", "PUT", testRoad1.toString());
        sendRequest(url, "/location/road", "PUT", testRoad2.toString());

        JSONObject driver1 = new JSONObject();
        driver1.put("uid", uniqueUser1);
        driver1.put("is_driver", false);

        JSONObject driver1Patch = new JSONObject();
        driver1Patch.put("longitude", 55.00);
        driver1Patch.put("latitude", 58.00);
        driver1Patch.put("street", uniqueRoad1);

        sendRequest(url, "/location/user", "PUT", driver1.toString());
        sendRequest(url, "/location/" + uniqueUser1, "PATCH", driver1Patch.toString());

        JSONObject driver2 = new JSONObject();
        driver2.put("uid", uniqueUser2);
        driver2.put("is_driver", true);

        JSONObject driver2Patch = new JSONObject();
        driver2Patch.put("longitude", 55.02);
        driver2Patch.put("latitude", 57.98);
        driver2Patch.put("street", uniqueRoad2);

        sendRequest(url, "/location/user", "PUT", driver2.toString());
        sendRequest(url, "/location/" + uniqueUser2, "PATCH", driver2Patch.toString());

        JSONObject tripPatch = new JSONObject();
        tripPatch.put("distance", 15);
        tripPatch.put("endTime", 1638195345);
        tripPatch.put("timeElapsed", 15);
        tripPatch.put("discount", 0);
        tripPatch.put("totalCost", 100);
        tripPatch.put("driverPayout", 65);

        JSONObject tripConfirm = new JSONObject();
        tripConfirm.put("driver", uniqueUser2);
        tripConfirm.put("passenger", uniqueUser1);
        tripConfirm.put("startTime", 1638195330);
        
        HttpResponse<String> trip = sendRequest(url, "/trip/confirm", "POST", tripConfirm.toString());
        JSONObject tripId = new JSONObject(trip.body());

        String tripid = tripId.getString("data");
        JSONObject tripEd = new JSONObject(tripid);
        String tripfinal = tripEd.getString("_id");
        JSONObject tripND = new JSONObject(tripfinal);
        String tripfinale = tripND.getString("$oid");
        
        HttpResponse<String> res = sendRequest(url, "/trip/" + tripfinale, "PATCH", tripPatch.toString());
        assertEquals(200, res.statusCode());
    }

    @Test
    public void patchTripFail() throws JSONException, IOException, InterruptedException {
        JSONObject tripPatch = new JSONObject();

        HttpResponse<String> res = sendRequest(url, "/trip/" + "invalidReq", "PATCH", tripPatch.toString());
        assertEquals(404, res.statusCode());
    }

     @Test
     public void tripsForPassengerPass() throws JSONException, IOException, InterruptedException {
            String uniqueUser1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String uniqueUser2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String uniqueRoad1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String uniqueRoad2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    
            JSONObject testRoad1 = new JSONObject();
            testRoad1.put("roadName", uniqueRoad1);
            testRoad1.put("hasTraffic", false);
    
            JSONObject testRoad2 = new JSONObject();
            testRoad2.put("roadName", uniqueRoad2);
            testRoad2.put("hasTraffic", false);
    
            sendRequest(url, "/location/road", "PUT", testRoad1.toString());
            sendRequest(url, "/location/road", "PUT", testRoad2.toString());
    
            JSONObject tripRequest = new JSONObject();
            tripRequest.put("uid", uniqueUser1);
            tripRequest.put("radius", 20);
    
            sendRequest(url, "/trip/request", "POST", tripRequest.toString());
    
            JSONObject driver1 = new JSONObject();
            driver1.put("uid", uniqueUser1);
            driver1.put("is_driver", false);
    
            JSONObject driver1Patch = new JSONObject();
            driver1Patch.put("longitude", 55.00);
            driver1Patch.put("latitude", 58.00);
            driver1Patch.put("street", uniqueRoad1);
    
            sendRequest(url, "/location/user", "PUT", driver1.toString());
            sendRequest(url, "/location/" + uniqueUser1, "PATCH", driver1Patch.toString());
    
            JSONObject driver2 = new JSONObject();
            driver2.put("uid", uniqueUser2);
            driver2.put("is_driver", true);
    
            JSONObject driver2Patch = new JSONObject();
            driver2Patch.put("longitude", 55.02);
            driver2Patch.put("latitude", 57.98);
            driver2Patch.put("street", uniqueRoad2);
    
            sendRequest(url, "/location/user", "PUT", driver2.toString());
            sendRequest(url, "/location/" + uniqueUser2, "PATCH", driver2Patch.toString());

            JSONObject tripConfirm = new JSONObject();
            tripConfirm.put("driver", uniqueUser2);
            tripConfirm.put("passenger", uniqueUser1);
            tripConfirm.put("startTime", 1638195330);
            sendRequest(url, "/trip/confirm", "POST", tripConfirm.toString());
            HttpResponse<String> res = sendRequest(url, "/trip/passenger/" + uniqueUser1, "GET");
            assertEquals(200, res.statusCode());
    }

    @Test
    public void tripsForPassengerFail() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> res = sendRequest(url, "/trip/passenger/" + "Volcaronabestmon", "GET");
        assertEquals(404, res.statusCode());
    }

     @Test
     public void tripsForDriverPass() throws JSONException, IOException, InterruptedException {
            String uniqueUser1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String uniqueUser2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String uniqueRoad1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String uniqueRoad2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    
            JSONObject testRoad1 = new JSONObject();
            testRoad1.put("roadName", uniqueRoad1);
            testRoad1.put("hasTraffic", false);
    
            JSONObject testRoad2 = new JSONObject();
            testRoad2.put("roadName", uniqueRoad2);
            testRoad2.put("hasTraffic", false);
    
            sendRequest(url, "/location/road", "PUT", testRoad1.toString());
            sendRequest(url, "/location/road", "PUT", testRoad2.toString());
    
            JSONObject tripRequest = new JSONObject();
            tripRequest.put("uid", uniqueUser1);
            tripRequest.put("radius", 20);
    
            sendRequest(url, "/trip/request", "POST", tripRequest.toString());
    
            JSONObject driver1 = new JSONObject();
            driver1.put("uid", uniqueUser1);
            driver1.put("is_driver", false);
    
            JSONObject driver1Patch = new JSONObject();
            driver1Patch.put("longitude", 55.00);
            driver1Patch.put("latitude", 58.00);
            driver1Patch.put("street", uniqueRoad1);
    
            sendRequest(url, "/location/user", "PUT", driver1.toString());
            sendRequest(url, "/location/" + uniqueUser1, "PATCH", driver1Patch.toString());
    
            JSONObject driver2 = new JSONObject();
            driver2.put("uid", uniqueUser2);
            driver2.put("is_driver", true);
    
            JSONObject driver2Patch = new JSONObject();
            driver2Patch.put("longitude", 55.02);
            driver2Patch.put("latitude", 57.98);
            driver2Patch.put("street", uniqueRoad2);
    
            sendRequest(url, "/location/user", "PUT", driver2.toString());
            sendRequest(url, "/location/" + uniqueUser2, "PATCH", driver2Patch.toString());

            JSONObject tripConfirm = new JSONObject();
            tripConfirm.put("driver", uniqueUser2);
            tripConfirm.put("passenger", uniqueUser1);
            tripConfirm.put("startTime", 1638195330);
            sendRequest(url, "/trip/confirm", "POST", tripConfirm.toString());

        HttpResponse<String> res = sendRequest(url, "/trip/driver/" + uniqueUser2, "GET");
        assertEquals(200, res.statusCode());
    }

    @Test
    public void tripsForDriverFail() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> res = sendRequest(url, "/trip/driver/" + "invalidReq", "GET");
        assertEquals(404, res.statusCode());
    }

    @Test
        public void driverTimePass() throws JSONException, IOException, InterruptedException {
            String uniqueUser1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String uniqueUser2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String uniqueRoad1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            String uniqueRoad2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    
            JSONObject testRoad1 = new JSONObject();
            testRoad1.put("roadName", uniqueRoad1);
            testRoad1.put("hasTraffic", false);
    
            JSONObject testRoad2 = new JSONObject();
            testRoad2.put("roadName", uniqueRoad2);
            testRoad2.put("hasTraffic", false);
    
            sendRequest(url, "/location/road", "PUT", testRoad1.toString());
            sendRequest(url, "/location/road", "PUT", testRoad2.toString());

            JSONObject route1 = new JSONObject();
            route1.put("roadName1", uniqueRoad1);
            route1.put("roadName2", uniqueRoad2);
            route1.put("hasTraffic", false);
            route1.put("time", 15);
            sendRequest(url, "/location/hasRoute", "POST", route1.toString());
    
            JSONObject driver1 = new JSONObject();
            driver1.put("uid", uniqueUser1);
            driver1.put("is_driver", true);
            JSONObject driver1Patch = new JSONObject();
            driver1Patch.put("longitude", 55.00);
            driver1Patch.put("latitude", 58.00);
            driver1Patch.put("street", uniqueRoad1);
            sendRequest(url, "/location/user", "PUT", driver1.toString());
            sendRequest(url, "/location/" + uniqueUser1, "PATCH", driver1Patch.toString());
    
            JSONObject driver2 = new JSONObject();
            driver2.put("uid", uniqueUser2);
            driver2.put("is_driver", true);
            JSONObject driver2Patch = new JSONObject();
            driver2Patch.put("longitude", 55.02);
            driver2Patch.put("latitude", 57.98);
            driver2Patch.put("street", uniqueRoad2);
            sendRequest(url, "/location/user", "PUT", driver2.toString());
            sendRequest(url, "/location/" + uniqueUser2, "PATCH", driver2Patch.toString());

            JSONObject tripConfirm = new JSONObject();
            tripConfirm.put("driver", uniqueUser1);
            tripConfirm.put("passenger", uniqueUser2);
            tripConfirm.put("startTime", 1638195330);
            HttpResponse <String> trip = sendRequest(url, "/trip/confirm", "POST", tripConfirm.toString());

            JSONObject tripId = new JSONObject(trip.body());
            tripId = tripId.getJSONObject("data");
            tripId = tripId.getJSONObject("_id");
            String tripid = tripId.getString("$oid");

            HttpResponse<String> res = sendRequest(url, "/trip/driverTime/" + tripid, "GET");
            assertEquals(200, res.statusCode());
    }

    @Test
        public void driverTimeFail() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> res = sendRequest(url, "/trip/driverTime/", "GET");
        assertEquals(400, res.statusCode());
    }
}

    