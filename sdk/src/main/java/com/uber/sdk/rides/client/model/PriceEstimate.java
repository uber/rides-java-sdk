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

import java.math.BigDecimal;

import javax.annotation.Nullable;

/**
 * An estimated price for a product on the Uber platform. See
 * <a href="https://developer.uber.com/docs/rides/api/v1-estimates-price">Price Estimates</a>
 * for more information.
 */
public class PriceEstimate {

    private String product_id;
    @Nullable
    private String currency_code;
    private String display_name;
    private String estimate;
    @Nullable
    private BigDecimal low_estimate;
    @Nullable
    private BigDecimal high_estimate;
    @Nullable
    private Float surge_multiplier;
    @Nullable
    private Integer duration;
    @Nullable
    private Float distance;

    /**
     * Unique identifier representing a specific product for a given latitude &amp; longitude. For
     * example, uberX in San Francisco will have a different product_id than uberX in Los Angeles.
     */
    public String getProductId() {
        return product_id;
    }

    /**
     * ISO 4217 currency code.
     */
    @Nullable
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
    @Nullable
    public BigDecimal getLowEstimate() {
        return low_estimate;
    }

    /**
     * Upper bound of the estimated price.
     */
    @Nullable
    public BigDecimal getHighEstimate() {
        return high_estimate;
    }

    /**
     * Expected surge multiplier. Surge is active if surge_multiplier is greater than 1. Price
     * estimate already factors in the surge multiplier.
     */
    @Nullable
    public Float getSurgeMultiplier() {
        return surge_multiplier;
    }

    /**
     * Expected activity duration (in seconds). Always show duration in minutes.
     */
    @Nullable
    public Integer getDuration() {
        return duration;
    }

    /**
     * Expected activity distance (in miles).
     */
    @Nullable
    public Float getDistance() {
        return distance;
    }
}