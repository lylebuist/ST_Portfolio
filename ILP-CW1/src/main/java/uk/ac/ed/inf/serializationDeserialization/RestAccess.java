package uk.ac.ed.inf.serializationDeserialization;

import com.google.gson.*;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Objects;

public class RestAccess {

    /**
     *
     * @param url The url to make the connection with
     * Checks if the connection is alive
     */
    public static boolean appAlive(String url) {
        try {
            // Creates the request to be sent
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/isAlive"))
                    .build();

            // Creates a connection with the url and obtains the response to the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return Objects.equals(response.body(), "true") && response.statusCode() == 200;
        } catch (Exception e) {
            e.getCause();
            return false;
        }
    }

    /**
     *
     * @param url The url to make the connection with
     * @return The restaurants
     * Deserialize the restaurants from the REST service
     */
    public static Restaurant[] getRestaurants(String url) {
        if (appAlive(url)) {
            try {
                // Creates the request to be sent
                HttpClient client = HttpClient.newBuilder().build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url + "/restaurants"))
                        .build();

                // Creates a connection with the url and obtains the response to the request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Converts the response into the desired java object
                return new GsonBuilder().create().fromJson(response.body(), Restaurant[].class);
            } catch (Exception e) {
                e.getCause();
                return null;
            }
        } else {
            System.err.println("API Dead, try again later");
            return null;
        }
    }

    /**
     *
     * @param url The url to make the connection with
     * @return The orders
     * Deserialize the orders from the REST service
     */
    public static Order[] getOrders(String url) {
        if (appAlive(url)) {
            try {
                // Creates the request to be sent
                HttpClient client = HttpClient.newBuilder().build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url + "/orders"))
                        .build();

                // Creates a connection with the url and obtains the response to the request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                LocalDateDeserializer LocalDateDeserializer = new LocalDateDeserializer();

                // Converts the response into the desired java object and in this case deserializing the LocalDate properly
                return new GsonBuilder().registerTypeAdapter(LocalDate.class, LocalDateDeserializer).create().fromJson(response.body(), Order[].class);
            } catch (Exception e) {
                e.getCause();
                return null;
            }
        } else {
            System.err.println("API Dead, try again later");
            return null;
        }
    }

    /**
     *
     * @param url The url to make the connection with
     * @return The no-fly zones
     * Deserializes the no-fly zones from the REST service
     */
    public static NamedRegion[] getNoFlyZones(String url) {
        if (appAlive(url)) {
            try {
                // Creates the request to be sent
                HttpClient client = HttpClient.newBuilder().build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url + "/noFlyZones"))
                        .build();

                // Creates a connection with the url and obtains the response to the request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Converts the response into the desired java object
                return new GsonBuilder().create().fromJson(response.body(), NamedRegion[].class);
            } catch (Exception e) {
                e.getCause();
                return null;
            }
        } else {
            System.err.println("API Dead, try again later");
            return null;
        }
    }

    /**
     *
     * @param url The url to make the connection with
     * @return The central region
     * Deserializes the central region from the REST service
     */
    public static NamedRegion getCentralRegion(String url) {
        if (appAlive(url)) {
            try {
                // Creates the request to be sent
                HttpClient client = HttpClient.newBuilder().build();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url + "/centralArea"))
                        .build();

                // Creates a connection with the url and obtains the response to the request
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Converts the response into the desired java object
                return new GsonBuilder().create().fromJson(response.body(), NamedRegion.class);
            } catch (Exception e) {
                e.getCause();
                return null;
            }
        } else {
            System.err.println("API Dead, try again later");
            return null;
        }
    }
}
