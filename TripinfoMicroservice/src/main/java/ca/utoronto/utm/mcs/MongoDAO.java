package ca.utoronto.utm.mcs;

import com.mongodb.client.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoDAO {
    private final MongoCollection<Document> trips;

    private final String username = "root";
    private final String password = "123456";
    private final String dbName = "trip";
    private final String collectionName = "trips";

    public MongoDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("MONGODB_ADDR");
        String uriDb = String.format("mongodb://%s:%s@" + addr + ":27017", username, password);

        MongoClient mongoClient = MongoClients.create(uriDb);
        MongoDatabase database = mongoClient.getDatabase(this.dbName);
        this.trips = database.getCollection(this.collectionName);
    }

    public Document insert(String driverUid, String passengerUid, int startTime) {
        Document doc = new Document();

        doc.put("distance", null);
        doc.put("totalCost", null);
        doc.put("startTime", startTime);
        doc.put("endTime", null);
        doc.put("timeElapsed", null);
        doc.put("driver", driverUid);
        doc.put("driverPayout", null);
        doc.put("passenger", passengerUid);
        doc.put("discount", null);
        try {
            this.trips.insertOne(doc);
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return doc;
    }

    public MongoCursor<Document> getTripById(String _id) {
        try {
            return trips.find(new Document("_id", new ObjectId(_id))).iterator();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }

    public void patchTrip(String _id, int distance, int endTime, String timeElapsed, int discount, Double totalCost, Double driverPayout) {
        try {
            Document filter = new Document("_id", new ObjectId(_id));
            trips.updateOne(filter, new Document("$set", new Document("distance", distance)));
            trips.updateOne(filter, new Document("$set", new Document("endTime", endTime)));
            trips.updateOne(filter, new Document("$set", new Document("timeElapsed", timeElapsed)));
            trips.updateOne(filter, new Document("$set", new Document("discount", discount)));
            trips.updateOne(filter, new Document("$set", new Document("totalCost", totalCost)));
            trips.updateOne(filter, new Document("$set", new Document("driverPayout", driverPayout)));
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
    }

    public MongoCursor<Document> getPassengerTrips(String uid) {
        try {
            return trips.find(new Document("passenger", uid)).iterator();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }

    public MongoCursor<Document> getDriverTrips(String uid) {
        try {
            return trips.find(new Document("driver", uid)).iterator();
        } catch (Exception e) {
            System.out.println("Error occurred");
        }
        return null;
    }

    // Debugging method to test if db is properly connected
    public boolean testInsert() {
        Document doc = new Document();
        doc.put("TestInsert", "Insert successful!");

        try {
            this.trips.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.out.println("Error occurred");
            return false;
        }
    }
}
