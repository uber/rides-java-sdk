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
 * An estimated price for a product on the Uber platform. See
 * <a href="https://developer.uber.com/v1/endpoints/#price-estimates">Price Estimates</a>
 * for more information.
 */
public class PriceEstimate {

    private String product_id;
    private String currency_code;
    private String display_name;
    private String estimate;
    private int low_estimate;
    private int high_estimate;
    private float surge_multiplier;
    private int duration;
    private float distance;

    /**
     * Unique identifier representing a specific product for a given latitude & longitude. For
     * example, uberX in San Francisco will have a different product_id than uberX in Los Angeles.
     */
    public String getProductId() {
        return product_id;
    }

    /**
     * ISO 4217 currency code.
     */
    public String getCurrencyCode() {
        return currency_code;
    }

    /**
     * Display name of product.
     */
    public String getDisplayName() {
        return display_name;
    }

    /**
     * Formatted string of estimate in local currency of the start location. Estimate could be a
     * range, a single number (flat rate) or "Metered" for TAXI.
     */
    public String getEstimate() {
        return estimate;
    }

    /**
     * Lower bound of the estimated price.
     */
    public int getLowEstimate() {
        return low_estimate;
    }

    /**
     * Upper bound of the estimated price.
     */
    public int getHighEstimate() {
        return high_estimate;
    }

    /**
     * Expected surge multiplier. Surge is active if surge_multiplier is greater than 1. Price
     * estimate already factors in the surge multiplier.
     */
    public float getSurgeMultiplier() {
        return surge_multiplier;
    }

    /**
     * Expected activity duration (in seconds). Always show duration in minutes.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Expected activity distance (in miles).
     */
    public float getDistance() {
        return distance;
    }
}