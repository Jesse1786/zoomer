package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

// Registers a user with all appropriate fields in the database.
public class Register extends Endpoint {
    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // Get information from request body
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String name, email, password;

        if (body.has("name") && body.has("email") && body.has("password")) {
            name = body.getString("name");
            email = body.getString("email");
            password = body.getString("password");
        } else {
            this.sendStatus(r, 400);
            return;
        }

        // Make sure name, email, and password are not empty
        if (name.equals("") || email.equals("") || password.equals("")) {
            this.sendStatus(r, 400);
            return;
        }

        // Check the db to see if email is available
        ResultSet emailRs;
        boolean emailAlreadyUsed;
        try {
            emailRs = this.dao.getUsersFromEmail(email);
            emailAlreadyUsed = emailRs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }

        // Return 400 if email already used
        if (emailAlreadyUsed) {
            this.sendStatus(r, 400);
            return;
        }

        // Make the query to create the user in db
        try {
            this.dao.createUser(email, name, password);
        } catch (SQLException e) {
            e.printStackTrace();
            this.sendStatus(r, 500, true);
            return;
        }

        // Return 200 if everything is updated without error
        this.sendStatus(r, 200);
    }
}
