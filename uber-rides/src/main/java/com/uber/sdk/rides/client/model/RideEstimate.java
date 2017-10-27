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
 * An estimate for a ride. See
 * <a href="https://developer.uber.com/docs/rides/api/v1-requests-estimate">Request Estimate</a>
 * for more information.
 */
public class RideEstimate {

    @Nullable
    private Fare fare;
    @Nullable
    private Estimate estimate;
    @Nullable
    private Trip trip;
    @Nullable
    private Integer pickup_estimate;

    /**
     * Details of the fare for a product that uses up front pricing.
     */
    public static class Fare {
        private String display;
        @Nullable
        private String fare_id;
        @Nullable
        private String currency_code;
        @Nullable
        private Long expires_at;
        @Nullable
        private BigDecimal value;

        /**
         * A String representing the fare, should be used to communicate to the user.
         */
        public String getDisplay() {
            return display;
        }

        /**
         * The unique identifier of this fare for a user. Null if metered.
         */
        @Nullable
        public String getFareId() {
            return fare_id;
        }

        /**
         * ISO 4217 currency code.
         */
        @Nullable
        public String getCurrencyCode() {
            return currency_code;
        }

        /**
         * The UNIX time this fare expires at, if after this a new fare should be generated.
         */
        @Nullable
        public Long getExpiresAt() {
            return expires_at;
        }

        /**
         * The cost of this fare, to be paired with the currency code.
         */
        @Nullable
        public BigDecimal getValue() {
            return value;
        }
    }

    /**
     * Details of an estimate for a product that does not use up front pricing.
     */
    public static class Estimate {
        @Nullable
        private Integer minimum;
        @Nullable
        private String surge_confirmation_href;
        @Nullable
        private String surge_confirmation_id;
        @Nullable
        private Float surge_multiplier;
        @Nullable
        private BigDecimal high_estimate;
        @Nullable
        private BigDecimal low_estimate;
        @Nullable
        private String display;
        @Nullable
        private String currency_code;
        @Nullable
        private String fare_id;

        /**
         * The minimum price of the ride.
         */
        @Nullable
        public Integer getMinimum() {
            return minimum;
        }

        /**
         * The URL a user must visit to accept surge pricing.
         */
        @Nullable
        public String getSurgeConfirmationHref() {
            return surge_confirmation_href;
        }

        /**
         * The unique identifier of the surge session for a user. null if no surge is currently in
         * effect.
         */
        @Nullable
        public String getSurgeConfirmationId() {
            return surge_confirmation_id;
        }

        /**
         * Expected surge multiplier. Surge is active if surge_multiplier is greater than 1. Fare
         * estimates below factor in the surge multiplier.
         */
        @Nullable
        public Float getSurgeMultiplier() {
            return surge_multiplier;
        }

        /**
         * Upper bound of the estimated price.
         */
        @Nullable
        public BigDecimal getHighEstimate() {
            return high_estimate;
        }

        /**
         * Lower bound of the estimated price.
         */
        @Nullable
        public BigDecimal getLowEstimate() {
            return low_estimate;
        }

        /**
         * The license plate number of the vehicle.
         */
        @Nullable
        public String getDisplay() {
            return display;
        }

        /**
         * ISO 4217 currency code.
         */
        @Nullable
        public String getCurrencyCode() {
            return currency_code;
        }

        /**
         * The fare ID assigned for this estimate. Must send this back when requesting a ride to ensure ride requested
         * is based on this fare.
         */
        @Nullable
        public String getFareId() {
            return fare_id;
        }
    }

    /**
     * Details of the estimated distance.
     */
    public static class Trip {

        private String distance_unit;
        private int duration_estimate;
        private float distance_estimate;

        /**
         * The unit of distance (mile or km).
         */
        public String getDistanceUnit() {
            return distance_unit;
        }

        /**
         * Expected activity duration (in minutes).
         */
        public int getDurationEstimate() {
            return duration_estimate;
        }

        /**
         * Expected activity distance.
         */
        public float getDistanceEstimate() {
            return distance_estimate;
        }
    }

    /**
     * Details of the fare, if null, use {@link #getEstimate()}.
     */
    @Nullable
    public Fare getFare() {
        return fare;
    }

    /**
     * Details of the estimate, if null, use {@link #getFare()}.
     */
    @Nullable
    public Estimate getEstimate() {
        return estimate;
    }

    /**
     * Details of the estimated distance. null if end location is omitted.
     */
    @Nullable
    public Trip getTrip() {
        return trip;
    }

    /**
     * The estimated time of vehicle arrival in minutes. null if there are no cars available.
     */
    @Nullable
    public Integer getPickupEstimate() {
        return pickup_estimate;
    }
}