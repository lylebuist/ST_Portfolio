package uk.ac.ed.inf.orderHandling;

import uk.ac.ed.inf.data.Delivery;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;

import java.util.ArrayList;

public class CreateDeliveries {

    /**
     *
     * @param orders The list of orders
     * @return A list of deliveries representing each order
     */
    public static ArrayList<Delivery> createDeliveries(ArrayList<Order> orders) {
        // List of deliveries to be created into a JSON
        ArrayList<Delivery> deliveries = new ArrayList<>();

        // Validates every order, updating its codes and adding it to deliveries
        for (Order orderToValidate : orders) {
            Delivery delivery = new Delivery(orderToValidate.getOrderNo(), orderToValidate.getOrderStatus(), orderToValidate.getOrderValidationCode(),
                    orderToValidate.getPriceTotalInPence() + SystemConstants.ORDER_CHARGE_IN_PENCE);
            deliveries.add(delivery);
        }

        return deliveries;
    }
}
