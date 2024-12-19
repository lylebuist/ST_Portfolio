package uk.ac.ed.inf.pathFinding;

import uk.ac.ed.inf.data.Flightpath;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.util.*;

public class FindPath {

    /**
     *
     * @param start The start destination
     * @param to The goal destination
     * @param orderNo The order number of the current order
     * @param noFlyZones The no-fly zones
     * @param centralRegion The central region
     * @return An optimal path from a start destination to a goal destination
     */
    public static ArrayList<Flightpath> findPath(LngLat start, LngLat to, String orderNo, NamedRegion[] noFlyZones, NamedRegion centralRegion) {

        // Creates objects needed for pathfinding
        LngLatHandler lngLatHandler = new LngLatHandler();
        Comparator<Map.Entry<LngLat[], Double[]>> heuristicComparator = new HeuristicComparator();
        ArrayList<Flightpath> flightpaths = new ArrayList<>();
        PriorityQueue<Map.Entry<LngLat[], Double[]>> frontier = new PriorityQueue<>(heuristicComparator);

        // Instantiate needed variable
        LngLat from = start;

        // Sets centralCounter to 1 if the path starts in centralRegion, else 0
        int centralCounter = 0;
        if (lngLatHandler.isInCentralArea(from, centralRegion)) {
            centralCounter = 1;
        }

        while (!lngLatHandler.isCloseTo(from, to)) {

            // Creates a priority queue to pick from all the valid points from the current node
            PriorityQueue<Map.Entry<LngLat[], Double[]>> validPossiblePoints = new PriorityQueue<>(heuristicComparator);

            // The flyZoneCounter will stay at 0 unless any of the valid possible points are in a no-fly zone, then it becomes 1, indicating there is a no-fly zone close
            int flyZoneCounter = 0;

            // Adds all possible, valid points to the frontier
            for (double angle = 0; angle < 360; angle += 22.5) {

                // Finds the next possible points, its distance to the goal, and the already travelled distance
                LngLat possiblePoint = lngLatHandler.nextPosition(from, angle);
                double distanceToEnd = lngLatHandler.distanceTo(possiblePoint, to);
                double distanceSoFar = lngLatHandler.distanceTo(start, possiblePoint);

                // Updates flyZoneCounter
                if (flyZoneCounter == 0) {
                    flyZoneCounter = closeNoFlyZone(possiblePoint, noFlyZones, lngLatHandler);
                }

                // Adds a valid point to the validPossiblePoints if it is not already in the frontier
                if (ValidPoint(lngLatHandler, centralCounter, possiblePoint, noFlyZones, centralRegion, frontier)) {
                    // Note that the weighting in the heuristic is on the distance to the goal
                    Map.Entry<LngLat[], Double[]> possiblePointEntry = new AbstractMap.SimpleEntry<>(new LngLat[]{possiblePoint, from}, new Double[]{distanceSoFar + 1.5*distanceToEnd, angle});
                    validPossiblePoints.add(possiblePointEntry);
                }
            }

            // If there are no close no-fly zones just take the best possible point, else add all possible points to frontier
            if (flyZoneCounter == 0) {
                // Finds the point closest to the goal out of the possible points
                if (!(validPossiblePoints.isEmpty())) {
                    Map.Entry<LngLat[], Double[]> nextPoint = validPossiblePoints.poll();
                    frontier.add(nextPoint);
                }
            } else {
                frontier.addAll(validPossiblePoints);
            }

            // Finds the point closest to the goal
            Map.Entry<LngLat[], Double[]> nextPoint = frontier.poll();

            LngLat nextPointLngLat = nextPoint.getKey()[0];

            // If the path is going to exit centralRegion, set centralCounter to 2, if it is going to enter it, set centralCounter to 3
            if (centralCounter == 1 && !lngLatHandler.isInCentralArea(nextPointLngLat, centralRegion)) {
                centralCounter = 2;
            } else if (centralCounter == 0 && lngLatHandler.isInCentralArea(nextPointLngLat, centralRegion)) {
                centralCounter = 3;
            }

            // Updates flightpaths, visitedPoints, and from
            flightpaths.add(new Flightpath(orderNo, nextPoint.getKey()[1].lng(), nextPoint.getKey()[1].lat(), nextPoint.getValue()[1], nextPointLngLat.lng(), nextPointLngLat.lat()));
            from = nextPointLngLat;
        }
        return reconfigureFlightpaths(flightpaths, start, orderNo);
    }

    /**
     *
     * @param centralCounter The counter determining whether the path has left or entered central
     * @param possiblePoint The point to be validated
     * @param noFlyZones THe no-fly zones
     * @param centralRegion The central region
     * @return Whether the point is valid
     */
    private static boolean ValidPoint (LngLatHandler lngLatHandler, int centralCounter, LngLat possiblePoint, NamedRegion[] noFlyZones, NamedRegion centralRegion, PriorityQueue<Map.Entry<LngLat[], Double[]>> frontier) {
        if (centralCounter == 2) {
            // If the path started in centralRegion and then left, it cannot reenter
            if (lngLatHandler.isInCentralArea(possiblePoint, centralRegion)) {
                return false;
            }
        } else if (centralCounter == 3) {
            // If the path started outside centralRegion and the entered it, it cannot exit
            if (!lngLatHandler.isInCentralArea(possiblePoint, centralRegion)) {
                return false;
            }
        }

        // The next point must not be in a noFlyZone
        for (NamedRegion noFlyZone : noFlyZones) {
            if (lngLatHandler.isInRegion(possiblePoint, noFlyZone)) {
                return false;
            }
        }

        for (Map.Entry<LngLat[], Double[]> frontierPoint : frontier) {
            double currPointLng = frontierPoint.getKey()[0].lng();
            double currPointLat = frontierPoint.getKey()[0].lng();

            if (currPointLng == possiblePoint.lng() && currPointLat == possiblePoint.lat()) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param possiblePoint The possible point
     * @param noFlyZones The no-fly zones
     * @return The updated no-fly zone counter determining if the point is in a no-fly zone
     */
    private static int closeNoFlyZone (LngLat possiblePoint, NamedRegion[] noFlyZones, LngLatHandler lngLatHandler) {
        // Updates the noFlyZoneCounter to 1 is there is a no-fly zone close
        for (NamedRegion noFlyZone : noFlyZones) {
            if (lngLatHandler.isInRegion(possiblePoint, noFlyZone)) {
                return 1;
            }
        }
        return 0;
    }

    /**
     *
     * @param flightpaths The current list of flighpaths
     * @param start The start point
     * @param orderNo The current oreder number
     * @return A reconfigured list of flighpaths that represent the most optimal path
     */
    private static ArrayList<Flightpath> reconfigureFlightpaths(ArrayList<Flightpath> flightpaths, LngLat start, String orderNo) {
        ArrayList<Flightpath> newFlightpaths = new ArrayList<>();

        // Find the lng and lat of the last element of the flightpath, the goal
        Flightpath currEnd = flightpaths.get(flightpaths.size()-1);
        double fromLng = currEnd.fromLongitude();
        double fromLat = currEnd.fromLatitude();

        Flightpath endHover = new Flightpath(orderNo, fromLng, fromLat, 999.0, fromLng, fromLat);

        // Adds the end hover instruction and the goal to the reconstructed flightpath
        newFlightpaths.add(endHover);
        newFlightpaths.add(currEnd);

        // For each point, starting with the goal, find its parent node and add it to the reconstructed flightpath
        while (!(start.lng() == fromLng && start.lat() == fromLat)) {
            for (Flightpath flightpath : flightpaths) {
                if (flightpath.toLongitude() == fromLng && flightpath.toLatitude() == fromLat) {
                    currEnd = flightpath;
                    fromLng = currEnd.fromLongitude();
                    fromLat = currEnd.fromLatitude();
                    newFlightpaths.add(flightpath);
                    flightpaths.remove(flightpath);
                    break;
                }
            }
        }

        // Reverse the list so it travels to the goal
        Collections.reverse(newFlightpaths);

        return newFlightpaths;
    }
}