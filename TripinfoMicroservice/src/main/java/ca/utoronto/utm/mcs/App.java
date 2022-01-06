package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException {
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

      // Server contexts
      server.createContext("/trip/request", new Request());
      server.createContext("/trip/confirm", new Confirm());
      server.createContext("/trip", new Trip());
      server.createContext("/trip/passenger", new PassengerTrips());
      server.createContext("/trip/driver", new DriverTrips());
      server.createContext("/trip/driverTime", new DriverTime());

      ExecutorService executor = Executors.newCachedThreadPool();
      server.setExecutor(executor);

      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}
