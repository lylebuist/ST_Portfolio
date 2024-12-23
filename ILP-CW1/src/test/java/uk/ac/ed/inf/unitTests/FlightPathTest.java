package uk.ac.ed.inf.unitTests;

import junit.framework.TestCase;
import org.junit.jupiter.api.RepeatedTest;
import uk.ac.ed.inf.data.Flightpath;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.pathFinding.*;
import uk.ac.ed.inf.serializationDeserialization.*;

import static uk.ac.ed.inf.pathFinding.FindPath.findPath;

import java.io.IOException;
import java.util.*;


/*
 * ASSUMPTIONS
 *
 */
public class FlightPathTest extends TestCase {
    public static Random RANDOM = new Random();
    public static LngLatHandler HANDLER = new LngLatHandler();
    public static RestAccess REST_ACCESS = new RestAccess();

    /**
     * Generates random points until it's in a region
     *
     * @param region given region
     * @return random point in that region
     */
    public LngLat createLngLatInRegion(NamedRegion region) {
        LngLat[] vertices = region.vertices();
        Arrays.sort(vertices, Comparator
                .comparing(LngLat::lng)
                .thenComparing(LngLat::lat)
        );
        double lng;
        double lat;
        LngLat pos = new LngLat(0, 0);

        while (!HANDLER.isInRegion(pos, region)) {
            lng = RANDOM.nextDouble(vertices[0].lng(), vertices[3].lng());
            lat = RANDOM.nextDouble(vertices[0].lat(), vertices[3].lat());

            pos = new LngLat(lng, lat);
        }

        return pos;
    }

    @RepeatedTest(50)
    public void testPathFromRandomPositionInNoFlyZone() throws IOException, InterruptedException {
        NamedRegion[] noFlyZones = RestAccess.getNoFlyZones(TestConstants.ENDPOINT);

        LngLat pos = createLngLatInRegion(noFlyZones[RANDOM.nextInt(0, noFlyZones.length - 1)]);
        ArrayList<Flightpath> path = findPath(pos, TestConstants.APPLETON_TOWER, "testOrder", noFlyZones, TestConstants.CENTRAL_AREA);


        System.err.println("From [" + pos.lng() + ", " + pos.lat() + "]");
        if (!path.isEmpty()) {
            System.out.println("\tPath found: ");
            for (Flightpath point : path) {
                System.out.println("[" + point.toLongitude() + ", " + point.toLatitude() + "],");
            }
        }

        assertTrue(path.isEmpty());
    }

    @RepeatedTest(50)
    public void testPathFromNullPosition() throws InterruptedException {
        NamedRegion[] noFlyZones = RestAccess.getNoFlyZones(TestConstants.ENDPOINT);

        ArrayList<Flightpath> path = findPath(null, TestConstants.APPLETON_TOWER, "testOrder", noFlyZones, TestConstants.CENTRAL_AREA);

        if (!path.isEmpty()) {
            System.out.println("\tPath found: ");
            for (Flightpath point : path) {
                System.out.println("[" + point.toLongitude() + ", " + point.toLatitude() + "],");
            }
        }

        assertTrue(path.isEmpty());
    }

    @RepeatedTest(50)
    public void testPathToRandomPositionInNoFlyZone() throws IOException, InterruptedException {
        NamedRegion[] noFlyZones = RestAccess.getNoFlyZones(TestConstants.ENDPOINT);

        LngLat pos = createLngLatInRegion(noFlyZones[RANDOM.nextInt(0, noFlyZones.length - 1)]);
        ArrayList<Flightpath> path = findPath(TestConstants.APPLETON_TOWER, pos, "testOrder", noFlyZones, TestConstants.CENTRAL_AREA);


        System.err.println("From [" + pos.lng() + ", " + pos.lat() + "]");
        if (!path.isEmpty()) {
            System.out.println("\tPath found: ");
            for (Flightpath point : path) {
                System.out.println("[" + point.toLongitude() + ", " + point.toLatitude() + "],");
            }
        }

        assertTrue(path.isEmpty());
    }

    @RepeatedTest(50)
    public void testPathToNullPosition() throws InterruptedException {
        NamedRegion[] noFlyZones = RestAccess.getNoFlyZones(TestConstants.ENDPOINT);

        ArrayList<Flightpath> path = findPath(TestConstants.APPLETON_TOWER, null, "testOrder", noFlyZones, TestConstants.CENTRAL_AREA);

        if (!path.isEmpty()) {
            System.out.println("\tPath found: ");
            for (Flightpath point : path) {
                System.out.println("[" + point.toLongitude() + ", " + point.toLatitude() + "],");
            }
        }

        assertTrue(path.isEmpty());
    }
}