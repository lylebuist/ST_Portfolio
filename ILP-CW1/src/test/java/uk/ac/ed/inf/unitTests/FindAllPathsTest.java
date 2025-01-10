package uk.ac.ed.inf.unitTests;

import junit.framework.TestCase;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import uk.ac.ed.inf.data.Flightpath;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;
import uk.ac.ed.inf.orderHandling.OrderValidator;
import uk.ac.ed.inf.orderHandling.ValidateOrders;
import uk.ac.ed.inf.pathFinding.FindAllPaths;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

import static uk.ac.ed.inf.pathFinding.FindAllPaths.findAllPaths;
import static uk.ac.ed.inf.unitTests.OrderValidatorTest.random;

public class FindAllPathsTest extends TestCase {
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
    public void testFindAllPathsCorrect() {
        ArrayList<Order> orders = new ArrayList<>();

        Restaurant restaurant = new Restaurant("Civerinos Slice",
                new LngLat(-3.1912869215011597, 55.945535152517735),
                new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[] {
                        new Pizza("R1: Margarita", 1000),
                        new Pizza("R1: Calzone", 1400)
                }
        );

        Order validOrder1 = createValidOrder();
        validOrder1 = createValidPizza(restaurant, validOrder1);
        orders.add(validOrder1);
        Order validOrder2 = createValidOrder();
        validOrder2 = createValidPizza(restaurant, validOrder2);
        orders.add(validOrder2);
        Order validOrder3 = createValidOrder();
        validOrder3 = createValidPizza(restaurant, validOrder3);
        orders.add(validOrder3);

        orders = ValidateOrders.validateOrders(orders, new Restaurant[]{restaurant});
        ArrayList<Flightpath> allPaths = findAllPaths(orders, new Restaurant[]{restaurant}, TestConstants.NO_FLY_ZONES, TestConstants.CENTRAL_AREA);

        assertFalse(allPaths.isEmpty());
    }
}
