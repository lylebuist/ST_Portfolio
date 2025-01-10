package uk.ac.ed.inf.unitTests;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.data.Delivery;
import uk.ac.ed.inf.data.Flightpath;
import static uk.ac.ed.inf.serializationDeserialization.CreateJson.*;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CreateJsonTest extends TestCase {

    @Test
    public void testCreateJson() {
        File deliveryFile = new File("resultfiles/delivery-2030-01-08.json");
        File droneFile = new File("resultfiles/drone-2030-01-08.geojson");
        File flightpathFile = new File("resultfiles/flightpath-2030-01-08.json");

        LocalDate date = LocalDate.parse("2030-01-08", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        ArrayList<Delivery> deliveries = new ArrayList<>();
        ArrayList<Flightpath> flightpath = new ArrayList<>();
        ArrayList<Flightpath> drone = new ArrayList<>();

        createDeliveriesJson(deliveries, date);
        createFlightpathJson(flightpath, date);
        createDroneGeojson(drone, date);

        assertTrue(deliveryFile.exists());
        assertTrue(droneFile.exists());
        assertTrue(flightpathFile.exists());
    }
}
