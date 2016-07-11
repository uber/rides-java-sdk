/*
 * Copyright (c) 2016 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.uber.sdk.rides.client.model;

import javax.annotation.Nullable;

/**
 * A user's Payment Method. See
 * <a href="https://developer.uber.com/docs/rides/api/v1-payment-methods">Payment Methods</a>
 * for more information.
 */
public class PaymentMethod {

    private String payment_method_id;
    private String type;
    @Nullable
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
    @Nullable
    public String getDescription() {
        return description;
    }
}
