/*
 * Copyright (c) 2015 Uber Technologies, Inc.
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

/**
 * A promotion for a new user. See
 * <a href="https://developer.uber.com/v1/endpoints/#promotions">Promotions</a> for more
 * information.
 */
public class Promotion {

    private String display_text;
    private String localized_value;
    private String type;

    /**
     * A localized string we recommend to use when offering the promotion to users.
     */
    public String getDisplayText() {
        return display_text;
    }

    /**
     * The value of the promotion that is available to a user in this location in the local
     * currency.
     */
    public String getLocalizedValue() {
        return localized_value;
    }

    /**
     * The type of the promo which is either "trip_credit" or "account_credit".
     */
    public String getType() {
        return type;
    }
}