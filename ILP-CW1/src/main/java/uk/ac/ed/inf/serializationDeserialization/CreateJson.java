package uk.ac.ed.inf.serializationDeserialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.*;
import uk.ac.ed.inf.data.Delivery;
import uk.ac.ed.inf.data.Flightpath;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class CreateJson {

    /**
     *
     * @param deliveries The list of deliveries to be serialized
     * @param date The date the data was gathered from
     * Serializes the deliveries
     */
    public static void createDeliveriesJson(ArrayList<Delivery> deliveries, LocalDate date){

        // Writes deliveries to a Json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        writer("delivery", gson.toJson(deliveries), date);
    }

    /**
     *
     * @param flightpaths The list of flightpaths to be serialized
     * @param date The date the data was gathered from
     * Serializes the flightpaths
     */
    public static void createFlightpathJson(ArrayList<Flightpath> flightpaths, LocalDate date){

        // Writes flightpaths to a Json
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        writer("flightpath", gson.toJson(flightpaths), date);
    }

    /**
     *
     * @param flightpaths The list of flightpaths to be serialized
     * @param date The date the data was gathered from
     * Serializes the flightpaths in geojson format
     */
    public static void createDroneGeojson(ArrayList<Flightpath> flightpaths, LocalDate date) {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Adds the FeatureCollection to the geogson
        JsonObject geoJson = new JsonObject();
        geoJson.addProperty("type", "FeatureCollection");

        JsonArray features = new JsonArray();

        // Creates the feature
        JsonObject feature = new JsonObject();
        feature.addProperty("type", "Feature");

        // Creates the LineString
        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", "LineString");

        JsonArray coordsArray = new JsonArray();

        // Adds each point in flightpath to a JsonArray
        for (Flightpath flightpath : flightpaths) {

            JsonArray point = new JsonArray();
            point.add(flightpath.fromLongitude());
            point.add(flightpath.fromLatitude());
            coordsArray.add(point);
        }

        // Adds the JsonArray geometry, adding it to the feature
        geometry.add("coordinates", coordsArray);
        feature.add("geometry", geometry);
        feature.add("properties", new JsonObject());

        // Adds the feature to the list of features, then adding that to the geogson
        features.add(feature);
        geoJson.add("features", features);

        writer("drone", gson.toJson(geoJson), date);
    }

    /**
     *
     * @param fileName The name of the file to be created
     * @param file The data to be serialized
     * @param date The date the data was gathered from
     * Writes the json into the correct directory
     */
    public static void writer(String fileName, String file, LocalDate date) {

        String folderName = "resultfiles";
        String fullFileName;

        // Sets the file name according to the data to be serialized
        if (Objects.equals(fileName, "drone")) {
            fullFileName = fileName + "-" + date.toString() + ".geojson";
        } else {
            fullFileName = fileName + "-" + date.toString() + ".json";
        }

        // Adds the file to be written to the correct directory
        try {
            Files.createDirectories(Paths.get(folderName));

            Path filePath = Paths.get(folderName, fullFileName);
            Files.write(filePath, file.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
