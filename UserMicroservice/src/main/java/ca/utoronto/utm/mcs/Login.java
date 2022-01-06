package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

// Logs in the user if an appropriate email and password are provided.
public class Login extends Endpoint {
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // Get information from request body
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String email, password;

        if (body.has("email") && body.has("password")) {
            email = body.getString("email");
            password = body.getString("password");
        } else {
            this.sendStatus(r, 400);
            return;
        }

        // Make sure email, and password are not empty
        if (email.equals("") || password.equals("")) {
            this.sendStatus(r, 400);
            return;
        }

        // Try to log in the user
        ResultSet loginRs;
        boolean loggedIn;
        try {
            loginRs = this.dao.loginUser(email, password);
            loggedIn = loginRs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        if (!loggedIn) {
            this.sendStatus(r, 404);
            return;
        }

        // Return 200 if everything is updated without error
        this.sendStatus(r, 200);
    }
}
