package uk.ac.ed.inf.unitTests;

import junit.framework.TestCase;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.orderHandling.OrderValidator;
import uk.ac.ed.inf.orderHandling.ValidateOrders;
import uk.ac.ed.inf.orderHandling.ValidateOrders.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

import static uk.ac.ed.inf.unitTests.OrderValidatorTest.random;

public class ValidateOrdersTest extends TestCase {
    public static Order createValidOrder() {
        var order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2024, 9, 2));

        order.setCreditCardInformation(
                new CreditCardInformation(
                        "0000000000000000",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(26, 32)),
                        "222"
                )
        );

        // every order has the defined outcome
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);

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

    @Test
    public void testValidateOrderValidCorrect() {
        ArrayList<Order> orders = new ArrayList<>();

        Restaurant restaurant = createValidRestaurant();

        Order validOrder1 = createValidOrder();
        validOrder1 = createValidPizza(restaurant, validOrder1);
        orders.add(validOrder1);
        Order validOrder2 = createValidOrder();
        validOrder2 = createValidPizza(restaurant, validOrder2);
        orders.add(validOrder2);
        Order validOrder3 = createValidOrder();
        validOrder3 = createValidPizza(restaurant, validOrder3);
        orders.add(validOrder3);

        ArrayList<Order> validatedOrders = new ArrayList<>();

        validatedOrders = ValidateOrders.validateOrders(orders, new Restaurant[]{restaurant});

        for (Order order : validatedOrders) {
            assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, order.getOrderStatus());
            assertEquals(OrderValidationCode.NO_ERROR, order.getOrderValidationCode());
        }
    }

    @Test
    public void testValidateOrderInvalidCorrect() {
        ArrayList<Order> orders = new ArrayList<>();

        Restaurant restaurant = createValidRestaurant();

        Order invalidOrder1 = createValidOrder();
        invalidOrder1.getCreditCardInformation().setCreditCardExpiry(null);
        invalidOrder1 = createValidPizza(restaurant, invalidOrder1);
        orders.add(invalidOrder1);
        Order invalidOrder2 = createValidOrder();
        invalidOrder2.getCreditCardInformation().setCreditCardExpiry(null);
        invalidOrder2 = createValidPizza(restaurant, invalidOrder2);
        orders.add(invalidOrder2);
        Order invalidOrder3 = createValidOrder();
        invalidOrder3.getCreditCardInformation().setCreditCardExpiry(null);
        invalidOrder3 = createValidPizza(restaurant, invalidOrder3);
        orders.add(invalidOrder3);

        ArrayList<Order> validatedOrders = new ArrayList<>();

        validatedOrders = ValidateOrders.validateOrders(orders, new Restaurant[]{restaurant});

        for (Order order : validatedOrders) {
            assertEquals(OrderStatus.INVALID, order.getOrderStatus());
            assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
        }
    }


}
