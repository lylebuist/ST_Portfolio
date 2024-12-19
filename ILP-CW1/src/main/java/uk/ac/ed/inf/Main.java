package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.ac.ed.inf.data.Delivery;
import uk.ac.ed.inf.data.Flightpath;
import uk.ac.ed.inf.ilp.data.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

import static uk.ac.ed.inf.orderHandling.CreateDeliveries.createDeliveries;
import static uk.ac.ed.inf.pathFinding.FindAllPaths.findAllPaths;
import static uk.ac.ed.inf.orderHandling.ValidateOrders.validateOrders;
import static uk.ac.ed.inf.serializationDeserialization.CreateJson.*;
import static uk.ac.ed.inf.serializationDeserialization.RestAccess.*;

public class Main {

    /**
     *
     * @param args The input arguments
     */
    public static void main(String[] args) {

        LocalDate dateToRunOn = null;
        String url = "";

        try {
            // Collects the args and instantiates them as variables
            dateToRunOn = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            if (!Objects.equals(args[1], "https://ilp-rest.azurewebsites.net")) {
                System.err.println("Invalid url, ending program");
                System.exit(0);
            }
            url = args[1];
        } catch (Exception e) {
            System.err.println("Invalid date, ending program.");
            System.exit(0);
        }


        // Accesses the REST service to collect the information that is needed
        Restaurant[] restaurants = getRestaurants(url);
        Order[] orders = getOrders(dateToRunOn, url);

        NamedRegion[] noFlyZones = getNoFlyZones(url);
        NamedRegion centralRegion = getCentralRegion(url);

        System.out.println("Successfully deserialized all data");

        // Validates all orders, updating their status code
        ArrayList<Order> validatedOrders = validateOrders(orders, restaurants);

        System.out.println("Successfully validated " + validatedOrders.size() + " orders");

        // List of deliveries to be created into a Json
        ArrayList<Delivery> deliveries = createDeliveries(validatedOrders);

        // List of flightpaths to be created into a JSON
        ArrayList<Flightpath> flightpaths = findAllPaths(validatedOrders, restaurants, noFlyZones, centralRegion);

        // Creates all required Json files
        createDeliveriesJson(deliveries, dateToRunOn);
        createFlightpathJson(flightpaths, dateToRunOn);
        createDroneGeojson(flightpaths, dateToRunOn);

        System.out.println("All result files successfully created");
    }
}
