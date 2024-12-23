package uk.ac.ed.inf.orderHandling;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.orderHandling.OrderValidator;

import java.util.ArrayList;
import java.util.Arrays;

public class ValidateOrders {

    /**
     *
     * @param orders The list of orders
     * @param restaurants The list of restaurants
     * @return A list of validated orders
     */
    public static ArrayList<Order> validateOrders(ArrayList<Order> orders, Restaurant[] restaurants) {

        // Validates every order, updating its codes and adding it to deliveries
        OrderValidator orderValidator = new OrderValidator();
        for (Order orderToValidate : orders) {
            orderValidator.validateOrder(orderToValidate, restaurants);
        }

        return orders;
    }
}
