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
