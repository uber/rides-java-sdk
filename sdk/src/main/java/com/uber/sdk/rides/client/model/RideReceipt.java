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
import java.util.List;

/**
 * A receipt for a completed request.
 * See
 * <a href="https://developer.uber.com/docs/rides/api/v1-requests-receipt">Ride Request Receipt</a>
 * for more information.
 */
public class RideReceipt {
    private String request_id;
    private List<Charge> charges;
    @Nullable
    private Charge surge_charge;
    private List<Charge> charge_adjustments;
    private String normal_fare;
    private String subtotal;
    private String total_charged;
    @Nullable
    private Float total_owed;
    @Nullable
    private String currency_code;
    private String duration;
    private String distance;
    private String distance_label;

    /**
     * Gets the unique ID of the ride.
     */
    public String getRideId() {
        return request_id;
    }

    /**
     * Gets the charges made against the rider.
     */
    public List<Charge> getCharges() {
        return charges;
    }

    /**
     * Gets the surge charge. May be {@code null} if surge pricing was not in effect.
     */
    @Nullable
    public Charge getSurgeCharge() {
        return surge_charge;
    }

    /**
     * Gets the adjustments made to the charges such as promotions, and fees.
     */
    public List<Charge> getChargeAdjustments() {
        return charge_adjustments;
    }

    /**
     * Gets the summation of the charges.
     */
    public String getNormalFare() {
        return normal_fare;
    }

    /**
     * Gets the summation of the normal_fare and surge_charge.
     */
    public String getSubTotal() {
        return subtotal;
    }

    /**
     * Gets the total amount charged to the users payment method.
     * This is the the subtotal (split if applicable) with taxes included.
     */
    public String getTotalCharged() {
        return total_charged;
    }

    /**
     * Gets the total amount still owed after attempting to charge the user.
     * May be {@code null} if amount was paid in full.
     */
    @Nullable
    public Float getTotalOwed() {
        return total_owed;
    }

    /**
     * Gets the ISO 4217 currency code.
     */
    @Nullable
    public String getCurrencyCode() {
        return currency_code;
    }

    /**
     * Gets the time duration of the trip in ISO 8601 HH:MM:SS format.
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Gets the distance of the trip charged.
     */
    public String getDistance() {
        return distance;
    }

    /**
     * Gets the localized unit of distance.
     */
    public String getDistanceLabel() {
        return distance_label;
    }

    public static class Charge {
        private String name;
        private float amount;
        private String type;

        /**
         * Gets the name of the charge.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets the amount of the charge.
         */
        public float getAmount() {
            return amount;
        }

        /**
         * Gets the type of the charge.
         */
        public String getType() {
            return type;
        }
    }
}
