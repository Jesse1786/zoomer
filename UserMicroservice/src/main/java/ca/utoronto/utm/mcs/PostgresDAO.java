package ca.utoronto.utm.mcs;

import java.sql.*;
import java.util.Arrays;

import io.github.cdimascio.dotenv.Dotenv;

public class PostgresDAO {
	
	public Connection conn;
    public Statement st;

	public PostgresDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("POSTGRES_ADDR");
        String url = "jdbc:postgresql://" + addr + ":5432/root";
		try {
            Class.forName("org.postgresql.Driver");
			this.conn = DriverManager.getConnection(url, "root", "123456");
            this.st = this.conn.createStatement();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

    // Searches the users for a specific email
	public ResultSet getUsersFromEmail(String email) throws SQLException {
		String query = "SELECT * FROM users WHERE email = '%s'";
        query = String.format(query, email);
        return this.st.executeQuery(query);
	}

    // Searches the users for a specific uid
    public ResultSet getUsersFromUid(int uid) throws SQLException {
        String query = "SELECT * FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    // Gets specific date from a user with a specific uid
    public ResultSet getUserData(int uid) throws SQLException {
        String query = "SELECT prefer_name as name, email, rides, isdriver,availableCoupons, redeemedCoupons FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    // Updates the specified user to have any fields that are passed into the function.
    public void updateUserAttributes(int uid, String email, String password, String prefer_name, Integer rides, Boolean isDriver, Integer[] availableCoupons, Integer[] redeemedCoupons) throws SQLException {

        String query;
        if (email != null) {
            query = "UPDATE users SET email = '%s' WHERE uid = %d";
            query = String.format(query, email, uid);
            this.st.execute(query);
        }
        if (password != null) {
            query = "UPDATE users SET password = '%s' WHERE uid = %d";
            query = String.format(query, password, uid);
            this.st.execute(query);
        }
        if (prefer_name != null) {
            query = "UPDATE users SET prefer_name = '%s' WHERE uid = %d";
            query = String.format(query, prefer_name, uid);
            this.st.execute(query);
        }
        if ((rides != null)) {
            query = "UPDATE users SET rides = %d WHERE uid = %d";
            query = String.format(query, rides, uid);
            this.st.execute(query);
        }
        if (isDriver != null) {
            query = "UPDATE users SET isdriver = %s WHERE uid = %d";
            query = String.format(query, isDriver.toString(), uid);
            this.st.execute(query);
        }
        if (availableCoupons != null) {
            query = ("UPDATE users SET availablecoupons = '%s' WHERE uid = %d");
            query = String.format(query, Arrays.toString(availableCoupons), uid).replace('[', '{').replace(']', '}');
            this.st.execute(query);
        }
        if (redeemedCoupons != null) {
            query = ("UPDATE users SET redeemedcoupons = '%s' WHERE uid = %d");
            query = String.format(query, Arrays.toString(redeemedCoupons), uid).replace('[', '{').replace(']', '}');
            this.st.execute(query);
        }
    }

    // Adds a new user to the table.
    public void createUser(String email, String name, String password) throws SQLException {
        String query = "INSERT INTO users(email, prefer_name, \"password\", rides, availableCoupons, redeemedCoupons) " +
                "VALUES ('%s', '%s', '%s', 0, '{}', '{}');";
        query = String.format(query, email, name, password);
        this.st.execute(query);
    }

    // Returns a user with specified email and password if one exists.
    public ResultSet loginUser(String email, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE email='%s' AND \"password\"='%s'";
        query = String.format(query, email, password);
        return this.st.executeQuery(query);
    }
}
