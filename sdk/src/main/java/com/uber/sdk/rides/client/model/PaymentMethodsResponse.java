package com.uber.sdk.rides.client.model;

import java.util.List;

/**
 * Response object containing a user's Payment Methods. See
 * <a href="https://developer.uber.com/docs/v1-payment-methods">Payment Methods</a>
 * for more information.
 */
public class PaymentMethodsResponse {

    private List<PaymentMethod> payment_methods;
    private String last_used;

    /**
     * Gets a list of {@link PaymentMethod}s for a user.
     */
    public List<PaymentMethod> getPaymentMethods() {
        return payment_methods;
    }

    /**
     * Gets the identifier of the last used {@link PaymentMethod}.
     */
    public String getLastUsedPaymentMethodId() {
        return last_used;
    }
}
