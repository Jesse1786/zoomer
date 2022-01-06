package ca.utoronto.utm.mcs;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTests {

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

    @BeforeAll
    public static void setUp() throws IOException, InterruptedException, JSONException {
        // Insert a user into the db for testing purposes
        JSONObject testUser = new JSONObject();
        testUser.put("name", "testUser");
        testUser.put("email", "testUser@gmail.com");
        testUser.put("password", "123456");

        sendRequest(url, "/user/register", "POST", testUser.toString());
    }

    @Test
    public void userLoginPass() throws JSONException, IOException, InterruptedException {
        JSONObject validUser = new JSONObject();
        validUser.put("email", "testUser@gmail.com");
        validUser.put("password", "123456");

        HttpResponse<String> res = sendRequest(url, "/user/login", "POST", validUser.toString());
        assertEquals(200, res.statusCode());
    }

    @Test
    public void userLoginFail() throws JSONException, IOException, InterruptedException {
        JSONObject invalidUser = new JSONObject();
        invalidUser.put("email", "invalidUser@gmail.com");

        HttpResponse<String> res = sendRequest(url, "/user/login", "POST", invalidUser.toString());
        assertEquals(400, res.statusCode());
    }

    @Test
    public void userRegisterPass() throws JSONException, IOException, InterruptedException {
        String uniqueEmail = UUID.randomUUID().toString().replace("-", "").substring(0, 10) + "@gmail.com";
        JSONObject uniqueUser = new JSONObject();
        uniqueUser.put("name", "uniqueUser");
        uniqueUser.put("email", uniqueEmail);
        uniqueUser.put("password", "123456");

        HttpResponse<String> res = sendRequest(url, "/user/register", "POST", uniqueUser.toString());
        assertEquals(200, res.statusCode());
    }

    @Test
    public void userRegisterFail() throws JSONException, IOException, InterruptedException {
        JSONObject invalidUser = new JSONObject();
        invalidUser.put("name", "invalidUser");
        invalidUser.put("email", "invalidUser@gmail.com");

        HttpResponse<String> res = sendRequest(url, "/user/register", "POST", invalidUser.toString());
        assertEquals(400, res.statusCode());
    }
}
