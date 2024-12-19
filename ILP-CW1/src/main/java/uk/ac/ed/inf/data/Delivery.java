package uk.ac.ed.inf.data;

import uk.ac.ed.inf.ilp.constant.*;

public record Delivery(String orderNo, OrderStatus orderStatus, OrderValidationCode orderValidationCode, Integer costInPence) {
}
