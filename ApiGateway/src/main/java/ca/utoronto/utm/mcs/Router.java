package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.net.URI;
import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONException;

public class Router implements HttpHandler {
    public String url;
    public HashMap<Integer, String> errorMap;

    public Router(String url) {
        this.url = url;
        errorMap = new HashMap<>();
        errorMap.put(200, "OK");
        errorMap.put(400, "BAD REQUEST");
        errorMap.put(403, "FORBIDDEN");
        errorMap.put(404, "NOT FOUND");
        errorMap.put(405, "METHOD NOT ALLOWED");
        errorMap.put(500, "INTERNAL SERVER ERROR");
    }

    public void handle(HttpExchange r) {
        try {
            // Forward the request to the appropriate microservice and return the response
            try {
                String endpoint = r.getRequestURI().toString();
                System.out.println("Sending request to: " + url + endpoint);
                HttpResponse<String> res = this.sendRequest(url, endpoint, r.getRequestMethod(), Utils.convert(r.getRequestBody()));
                sendResponse(r, new JSONObject(res.body()), res.statusCode());
                return;
            } catch (Exception e) {
                this.sendStatus(r, 500, true);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeOutputStream(HttpExchange r, String response) throws IOException {
        OutputStream os = r.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public void sendResponse(HttpExchange r, JSONObject obj, int statusCode) throws JSONException, IOException {
        obj.put("status", errorMap.get(statusCode));
        String response = obj.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    public void sendStatus(HttpExchange r, int statusCode) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("status", errorMap.get(statusCode));
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    public void sendStatus(HttpExchange r, int statusCode, boolean hasEmptyData) throws JSONException, IOException {
        JSONObject res = new JSONObject();
        res.put("status", errorMap.get(statusCode));
        res.put("data", new JSONObject());
        String response = res.toString();
        r.sendResponseHeaders(statusCode, response.length());
        this.writeOutputStream(r, response);
    }

    public boolean validateFields(JSONObject JSONRequest, ArrayList<String> stringFields, ArrayList<String> integerFields) {
        try {
            return validateFieldsHelper(JSONRequest,stringFields, String.class) && validateFieldsHelper(JSONRequest, integerFields, Integer.class);
        } catch (JSONException e) {
            System.err.println("Caught Exception: " + e.getMessage());
            return false;
        }
    }

    private boolean validateFieldsHelper(JSONObject JSONRequest, ArrayList<String> fields, Class<?> classOfFields) throws JSONException {
        for (String field : fields) {
            if (!(JSONRequest.has(field) && JSONRequest.get(field).getClass().equals(classOfFields))) {
                return false;
            }
        }
        return true;
    }

    public HttpResponse<String> sendRequest(String url, String endpoint, String method, String reqBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(reqBody))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendRequest(String url, String endpoint, String method) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + endpoint))
                .method(method, HttpRequest.BodyPublishers.ofString(""))
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}

