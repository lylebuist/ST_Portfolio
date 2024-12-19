package uk.ac.ed.inf.pathFinding;

import uk.ac.ed.inf.data.Flightpath;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static uk.ac.ed.inf.pathFinding.FindPath.findPath;

public class FindAllPaths {

    static LngLat appletonTowerLocation = new LngLat(-3.186874, 55.944494);

    /**
     *
     * @param validatedOrders The list of valid orders
     * @param restaurants The list of restaurants
     * @param noFlyZones The no-fly zones
     * @param centralRegion The central region
     * @return A list of flightpaths for every valid order
     */
    public static ArrayList<Flightpath> findAllPaths(ArrayList<Order> validatedOrders, Restaurant[] restaurants, NamedRegion[] noFlyZones, NamedRegion centralRegion) {

        ArrayList<Flightpath> flightpaths = new ArrayList<>();

        // A way to store calculated flightpaths to cut down on time
        HashMap<Restaurant, ArrayList<ArrayList<Flightpath>>> calculatedFlightpaths = new HashMap<>();

        for (Order order : validatedOrders) {
            OrderStatus orderStatus = order.getOrderStatus();

            if (orderStatus == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                // Finds the restaurant ordered from

                Restaurant restaurantToPickUpFrom = findRestaurant(order, restaurants);

                // Gets the restaurant's'location and the current order number
                LngLat restaurantLocation = restaurantToPickUpFrom.location();
                String orderNo = order.getOrderNo();

                // If the path to the restaurant is already calculated there is no need to recalculate it, so flightpaths is just updated the already calculated path
                if (!calculatedFlightpaths.containsKey(restaurantToPickUpFrom)) {
                    ArrayList<ArrayList<Flightpath>> restaurantsPath = new ArrayList<>();

                    // Calculates the path to the restaurant from appleton tower and adds the flightpath to the total flightpath
                    ArrayList<Flightpath> flightpathToRestaurant = findPath(appletonTowerLocation, restaurantLocation, orderNo, noFlyZones, centralRegion);
                    flightpaths.addAll(flightpathToRestaurant);
                    restaurantsPath.add(flightpathToRestaurant);

                    // Calculates the path from the restaurant to appleton tower and adds the flightpath to the total flightpath
                    ArrayList<Flightpath> flightpathFromRestaurant = findPath(restaurantLocation, appletonTowerLocation, orderNo, noFlyZones, centralRegion);
                    flightpaths.addAll(flightpathFromRestaurant);
                    restaurantsPath.add(flightpathFromRestaurant);

                    calculatedFlightpaths.put(restaurantToPickUpFrom, restaurantsPath);

                    System.out.println("Successfully created path for order " + order.getOrderNo());
                } else {
                    // Gets the paths corresponding to the desired restaurant
                    ArrayList<Flightpath> toRestaurant = calculatedFlightpaths.get(restaurantToPickUpFrom).get(0);
                    ArrayList<Flightpath> fromRestaurant = calculatedFlightpaths.get(restaurantToPickUpFrom).get(1);
                    flightpaths.addAll(toRestaurant);
                    flightpaths.addAll(fromRestaurant);
                    System.out.println("Successfully created path for order " + order.getOrderNo());
                }
            }
        }
        return flightpaths;
    }

    /**
     *
     * @param order The current order
     * @param restaurants The list of restaurants
     * @return The restaurant ordered from
     */
    private static Restaurant findRestaurant(Order order, Restaurant[] restaurants) {

        // Finds the restaurant ordered from
        for (Restaurant restaurant : restaurants) {
            ArrayList<Pizza> menu = new ArrayList<>();
            Collections.addAll(menu, restaurant.menu());
            if (menu.contains(order.getPizzasInOrder()[0])) {
                return restaurant;
            }
        }
        // As the order will be valid, the restaurant is guaranteed to exist mitigating this null value from ever being returned
        return null;
    }
}
