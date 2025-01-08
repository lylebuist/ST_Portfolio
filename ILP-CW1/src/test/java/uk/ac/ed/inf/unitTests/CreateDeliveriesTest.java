package uk.ac.ed.inf.unitTests;

import junit.framework.TestCase;
import uk.ac.ed.inf.data.Delivery;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import static uk.ac.ed.inf.orderHandling.CreateDeliveries.createDeliveries;
import static uk.ac.ed.inf.unitTests.OrderValidatorTest.random;

public class CreateDeliveriesTest extends TestCase{
    public static Order createValidOrder() {
        var order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));

        order.setCreditCardInformation(
                new CreditCardInformation(
                        "0000000000000000",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(24, 30)),
                        "222"
                )
        );

        // every order has the defined outcome
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);

        return order;
    }

    public static Order createValidPizza(Restaurant restaurant, Order order) {
        ArrayList<Pizza> currentOrder = new ArrayList<>();
        Pizza[] pizzas = order.getPizzasInOrder();
        if (pizzas.length > 0) {
            Collections.addAll(currentOrder, pizzas);
        }

        // Takes random pizza from given restaurant
        int noPizzas = restaurant.menu().length;
        Pizza pizza = restaurant.menu()[random.nextInt(noPizzas)];
        int currentPrice = order.getPriceTotalInPence();

        currentOrder.add(pizza);

        Pizza[] newOrder = currentOrder.toArray(new Pizza[0]);

        order.setPizzasInOrder(newOrder);

        // If there was nothing in the order
        if (currentPrice == 0) {
            order.setPriceTotalInPence(pizza.priceInPence() + SystemConstants.ORDER_CHARGE_IN_PENCE);
        } else {
            order.setPriceTotalInPence(currentPrice + pizza.priceInPence());
        }

        return order;
    }

    public static Restaurant createValidRestaurant() {
        // Creates valid restaurant
        return new Restaurant("testRestaurant",
                new LngLat(55.945535152517735, -3.1912869215011597),
                new DayOfWeek[] {
                        DayOfWeek.MONDAY, DayOfWeek.FRIDAY
                },
                new Pizza[]{
                        new Pizza("Pizza A", 2300),
                        new Pizza("Pizza B", 2400),
                        new Pizza("Pizza C", 2500)
                }
        ) ;
    }

    public void testCreateDeliveriesCorrect() {
        Order validOrder = createValidOrder();

        Restaurant restaurant = createValidRestaurant();

        validOrder = createValidPizza(restaurant, validOrder);
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(validOrder);

        ArrayList<Delivery> deliveries = new ArrayList<>();
        for (Order order : orders) {
            Delivery delivery = new Delivery(order.getOrderNo(), order.getOrderStatus(), order.getOrderValidationCode(),
                    order.getPriceTotalInPence() + SystemConstants.ORDER_CHARGE_IN_PENCE);
            deliveries.add(delivery);
        }

        ArrayList<Delivery> createdDeliveries = createDeliveries(orders);

        assertEquals(createdDeliveries, deliveries);
    }

}
