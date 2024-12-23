package uk.ac.ed.inf.unitTests;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.serializationDeserialization.*;
import uk.ac.ed.inf.ilp.data.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RESTAccessTest extends TestCase {
    @Test
    public void testIsAliveNoURL() {
        RestAccess access = new RestAccess();

        boolean isIt = false;
        try {
            isIt = access.appAlive(null);
        } catch (Exception e) {
            fail("An exception " + e + " was thrown");
        }

        assertEquals(isIt, false);
    }

    @Test
    public void testIsAliveMalformedURL() {
        RestAccess access = new RestAccess();

        boolean isIt = false;
        try {
            isIt = access.appAlive("https://www.google.com");
        } catch (Exception e) {
            fail("An exception " + e + " was thrown");
        }

        assertEquals(isIt, false);
    }

    @Test
    public void testIsAlive() {
        RestAccess access = new RestAccess();

        boolean isIt = false;
        try {
            isIt = access.appAlive("https://ilp-rest-2024.azurewebsites.net");
        } catch (Exception e) {
            fail("An exception " + e + " was thrown");
        }
        assertEquals(isIt, true);
    }

    @Test
    public void testGetRestaurants() {
        RestAccess access = new RestAccess();

        Restaurant[] restaurants = new Restaurant[] {};
        try {
            restaurants = access.getRestaurants("https://ilp-rest-2024.azurewebsites.net");
        } catch (Exception e) {
            fail("An exception " + e + " was thrown");
        }

        for (int i = 0; i < restaurants.length; i++) {
            System.out.println(restaurants[i].name());
        }

        List<String> RestNames = Arrays.asList(restaurants).stream().map( restaurant -> restaurant.name() ).collect( Collectors.toList() );
        List<String> ConstantsNames = Arrays.asList(TestConstants.RESTAURANTS).stream().map(restaurant -> restaurant.name() ).collect( Collectors.toList() );

        boolean isEqual = RestNames.equals(ConstantsNames);

        assertEquals(true, isEqual);
    }
}