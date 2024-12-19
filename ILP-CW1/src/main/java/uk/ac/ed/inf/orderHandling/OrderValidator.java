package uk.ac.ed.inf.orderHandling;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrderValidator implements OrderValidation {
    /**
     * validate an order and deliver a validated version where the
     *
     * @param orderToValidate    is the order which needs validation
     * @param definedRestaurants is the vector of defined restaurants with their according menu structure
     * @return the validated order
     */
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        int total = 0;
        ArrayList<Restaurant> restaurantsOrderedFrom = new ArrayList<>();
        Pizza[] pizzas = orderToValidate.getPizzasInOrder();

        // Checks if all the pizzas are contained in one restaurant's menu
        for (Restaurant restaurant : definedRestaurants) {
            ArrayList<Pizza> menu = new ArrayList<>();
            Collections.addAll(menu, restaurant.menu());
            if (menu.containsAll(List.of(pizzas)) && !(restaurantsOrderedFrom.contains(restaurant))) {
                restaurantsOrderedFrom.add(restaurant);
            }
        }

        for (Pizza pizza : pizzas) {
            // Adds each pizzas cost to the total
            total = total + pizza.priceInPence();
        }
        total = total + 100;

        // Checks if there are more than 4 pizzas ordered
        if (PizzaCountExceeded(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        // Checks if a pizza is present in the order
        else if (PizzaNotDefined(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        // Checks if pizzas have been ordered from multiple restaurants
        else if (MultipleRestaurants(restaurantsOrderedFrom, definedRestaurants)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        // Checks if ordered pizza/s is present on any menu
        else if (PizzaDoesNotExist(restaurantsOrderedFrom)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        // Checks of the restaurants ordered from is closed
        else if (RestaurantClosed(orderToValidate, restaurantsOrderedFrom)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        // Checks of the manually calculated order total matches the entered total
        else if (TotalIncorrect(orderToValidate, total)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        // Checks if the CVV is valid
        else if (CVVInvalid(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        // Checks if the expiry date is valid
        else if (InvalidExpiryDate(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        // Checks if the card number is valid
        else if (CardNumberInvalid(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }

        // If all checks pass the order is validated
        else {
            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
            orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        }
        return orderToValidate;
    }

    /**
     * @param orderToValidate
     * @return is pizza count exceeded
     */
    private boolean PizzaCountExceeded(Order orderToValidate) { return (orderToValidate.getPizzasInOrder() == null) || orderToValidate.getPizzasInOrder().length > 4; }

    /**
     * @param orderToValidate
     * @return if pizza is defined
     */
    private boolean PizzaNotDefined(Order orderToValidate) { return (orderToValidate.getPizzasInOrder() == null) || orderToValidate.getPizzasInOrder().length == 0; }

    /**
     * @param restaurantsOrderedFrom
     * @param definedRestaurants
     * @return has multiple restaurants has been ordered from
     */
    private boolean MultipleRestaurants(ArrayList<Restaurant> restaurantsOrderedFrom, Restaurant[] definedRestaurants) { return (restaurantsOrderedFrom == null) || (restaurantsOrderedFrom.isEmpty() && definedRestaurants.length > 1); }

    /**
     * @param restaurantsOrderedFrom
     * @return does ordered pizza exists
     */
    private boolean PizzaDoesNotExist(ArrayList<Restaurant> restaurantsOrderedFrom) { return (restaurantsOrderedFrom == null) || restaurantsOrderedFrom.isEmpty(); }

    /**
     * @param orderToValidate
     * @param restaurantsOrderedFrom
     * @return is restaurant is closed
     */
    private boolean RestaurantClosed(Order orderToValidate, ArrayList<Restaurant> restaurantsOrderedFrom) { return (restaurantsOrderedFrom == null) || !Arrays.asList(restaurantsOrderedFrom.get(0).openingDays()).contains(orderToValidate.getOrderDate().getDayOfWeek()); }

    /**
     * @param orderToValidate
     * @param total
     * @return is total incorrect
     */
    private boolean TotalIncorrect(Order orderToValidate, Integer total) { return (orderToValidate == null) || (total != orderToValidate.getPriceTotalInPence()); }

    /**
     * @param orderToValidate
     * @return is cvv invalid
     */
    private boolean CVVInvalid(Order orderToValidate) { return (orderToValidate.getCreditCardInformation().getCvv() == null) || (!orderToValidate.getCreditCardInformation().getCvv().matches("([0-9]{3})")); }

    /**
     * @param orderToValidate
     * @return is expiry date invalid
     */
    private boolean InvalidExpiryDate(Order orderToValidate) {
        String expDate = orderToValidate.getCreditCardInformation().getCreditCardExpiry();
        if (orderToValidate.getCreditCardInformation().getCreditCardExpiry() == null) {
            return true;
        }
        // Checks if the entered date matches the desired format
        boolean isDate = (expDate.matches("(0[1-9]|1[0-2])/([0-9][0-9])"));

        if (isDate) {
            String[] dateAndYear = expDate.split("/");
            String orderYearString = Integer.toString(orderToValidate.getOrderDate().getYear());
            // Drops the first two digits to match required format for comparison
            Integer orderYear = Integer.valueOf(orderYearString.substring(2));

            // Converts the entered date into LocalDate to check if it is in the future
            LocalDate newExpDate = LocalDate.of(Integer.parseInt(dateAndYear[1]), Integer.parseInt(dateAndYear[0]), 1);
            if ((newExpDate.getYear() < orderYear) || (newExpDate.getYear() == orderYear) && (newExpDate.getMonth().getValue() < orderToValidate.getOrderDate().getMonth().getValue())) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * @param orderToValidate
     * @return is card number invalid
     */
    private boolean CardNumberInvalid(Order orderToValidate) { return (orderToValidate.getCreditCardInformation().getCreditCardNumber() == null) || (!(orderToValidate.getCreditCardInformation().getCreditCardNumber().matches("([0-9]{16})"))); }
}