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

public class LocationTests {

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
    public void getNearbyDriverPass() throws JSONException, IOException, InterruptedException {

        String uniqueRoad1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueRoad2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueUser1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueUser2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        JSONObject testRoad1 = new JSONObject();
        testRoad1.put("roadName", uniqueRoad1);
        testRoad1.put("hasTraffic", false);

        JSONObject testRoad2 = new JSONObject();
        testRoad2.put("roadName", uniqueRoad2);
        testRoad2.put("hasTraffic", false);

        JSONObject route1 = new JSONObject();
        route1.put("roadName1", uniqueRoad1);
        route1.put("roadName2", uniqueRoad2);
        route1.put("hasTraffic", false);
        route1.put("time", 15);
        sendRequest(url, "/location/hasRoute", "POST", route1.toString());
    
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

        HttpResponse<String> res = sendRequest(url, "/location/nearbyDriver/" + uniqueUser1 + "?radius=50", "GET");
        assertEquals(200, res.statusCode());
    }

    @Test
    public void getNearbyDriverFail() throws JSONException, IOException, InterruptedException {
        HttpResponse<String> res = sendRequest(url, "/location/nearbyDriver/m99hnk9?radius=0", "GET");
        assertEquals(404, res.statusCode());
    
    }

    @Test
    public void getNavigationPass() throws JSONException, IOException, InterruptedException {
        String uniqueRoad1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueRoad2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueUser1 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String uniqueUser2 = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        JSONObject testRoad1 = new JSONObject();
        testRoad1.put("roadName", uniqueRoad1);
        testRoad1.put("hasTraffic", false);
        sendRequest(url, "/location/road", "PUT", testRoad1.toString());

        JSONObject testRoad2 = new JSONObject();
        testRoad2.put("roadName", uniqueRoad2);
        testRoad2.put("hasTraffic", false);
        sendRequest(url, "/location/road", "PUT", testRoad2.toString());

        JSONObject route1 = new JSONObject();
        route1.put("roadName1", uniqueRoad2);
        route1.put("roadName2", uniqueRoad1);
        route1.put("hasTraffic", false);
        route1.put("time", 15);
        sendRequest(url, "/location/hasRoute", "POST", route1.toString());

        JSONObject route2 = new JSONObject();
        route2.put("roadName1", uniqueRoad1);
        route2.put("roadName2", uniqueRoad2);
        route2.put("hasTraffic", false);
        route2.put("time", 10);
        sendRequest(url, "/location/hasRoute", "POST", route2.toString());
    
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

        HttpResponse<String> res = sendRequest(url, "/location/navigation/" + uniqueUser2 + "?passengerUid=" + uniqueUser1, "GET");
        assertEquals(200, res.statusCode());
    }

    @Test
    public void getNavigationFail() throws JSONException, IOException, InterruptedException {
       HttpResponse<String> res = sendRequest(url, "/location/navigation/?passengerUid=1", "GET");
        assertEquals(400, res.statusCode());
    }
}