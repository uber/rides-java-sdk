package com.uber.sdk.rides.client.model;

/**
 * A user's Payment Method. See
 * <a href="https://developer.uber.com/docs/v1-payment-methods-details">Payment Methods</a>
 * for more information.
 */
public class PaymentMethod {

    private String payment_method_id;
    private String type;
    private String description;

    /**
     * Gets the unique identifier of a Payment Method
     */
    public String getPaymentMethodId() {
        return payment_method_id;
    }

    /**
     * Gets the type of Payment Method. See
     * <a href="https://developer.uber.com/docs/v1-payment-methods-details">Payment Method Details</a>
     * for more information.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the description of a Payment Method.
     */
    public String getDescription() {
        return description;
    }
}
