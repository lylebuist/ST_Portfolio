package uk.ac.ed.inf.unitTests;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.serializationDeserialization.*;
import uk.ac.ed.inf.ilp.data.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RESTAccessTest extends TestCase {
    public void testIsAliveNoURL() {
        boolean Alive = false;
        try {
            Alive = RestAccess.appAlive(null);
        } catch (Exception e) {
            fail("An exception " + e + " was thrown");
        }

        assertFalse(Alive);
    }

    public void testIsAliveMalformedURL() {
        boolean Alive = false;
        try {
            Alive = RestAccess.appAlive("https://www.google.com");
        } catch (Exception e) {
            fail("An exception " + e + " was thrown");
        }

        assertFalse(Alive);
    }

    public void testIsAlive() {
        boolean Alive = false;
        try {
            Alive = RestAccess.appAlive("https://ilp-rest-2024.azurewebsites.net");
        } catch (Exception e) {
            fail("An exception " + e + " was thrown");
        }
        assertTrue(Alive);
    }

    public void testGetRestaurants() {
        Restaurant[] restaurants = new Restaurant[] {};
        try {
            restaurants = RestAccess.getRestaurants("https://ilp-rest-2024.azurewebsites.net");
        } catch (Exception e) {
            fail("An exception " + e + " was thrown");
        }

        List<String> RestNames = Arrays.stream(restaurants).map(Restaurant::name).toList();
        List<String> ConstantsNames = Arrays.stream(TestConstants.RESTAURANTS).map(Restaurant::name).toList();

        boolean isEqual = RestNames.equals(ConstantsNames);

        assertTrue(isEqual);
    }

    public void testGetOrders() {
        Order[] orders = new Order[] {};

        try {
            orders = RestAccess.getOrders("https://ilp-rest-2024.azurewebsites.net");
        } catch (Exception e) {
            fail("An exception " + e + " was thrown");
        }

        List<String> OrderIds = Arrays.stream(orders).map(Order::getOrderNo).toList();

        boolean isEqual = OrderIds.size() == 600;

        assertTrue(isEqual);
    }

    public void testGetCentralArea() {
        NamedRegion centralArea = RestAccess.getCentralRegion("https://ilp-rest-2024.azurewebsites.net");

        boolean isEqual = true;

        for (int i = 0; i < centralArea.vertices().length; i++) {
            if (!centralArea.vertices()[i].equals(TestConstants.CENTRAL_AREA.vertices()[i])) {
                isEqual = false;
                break;
            }
        }

        assertTrue(isEqual);
    }

    public void testGetNoFlyZone() {
        NamedRegion[] noFlyZones = RestAccess.getNoFlyZones("https://ilp-rest-2024.azurewebsites.net");

        boolean isEqual = true;

        for (int i = 0; i < noFlyZones.length; i++) {
            NamedRegion noFlyZone = noFlyZones[i];
            if (!(Objects.equals(noFlyZone.name(), TestConstants.NO_FLY_ZONES[i].name()))) {
                isEqual = false;
                break;
            }
            for (int j = 0; j < noFlyZone.vertices().length; j++) {
                if (!noFlyZone.vertices()[j].equals(TestConstants.NO_FLY_ZONES[i].vertices()[j])) {
                    isEqual = false;
                    break;
                }
            }
        }

        assertTrue(isEqual);
    }
}