package ca.utoronto.utm.mcs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException {
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);

      // Server contexts
      server.createContext("/location/user", new User());
      server.createContext("/location/", new Location());
      server.createContext("/location/road", new Road());
      server.createContext("/location/hasRoute", new Route());
      server.createContext("/location/route", new Route());
      server.createContext("/location/nearbyDriver", new Nearby());
      server.createContext("/location/navigation", new Navigation());

      ExecutorService executor = Executors.newCachedThreadPool();
      server.setExecutor(executor);

      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}