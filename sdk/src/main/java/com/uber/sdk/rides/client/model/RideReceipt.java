
package com.uber.sdk.rides.client.model;

import java.util.List;

import javax.annotation.Nullable;

/**
 * A receipt for a completed request.
 * See
 * <a href="https://developer.uber.com/docs/v1-requests-receipt">Ride Request Receipt</a>
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
     * Gets the total amount charged to the users payment method.<br/>
     * This is the the subtotal (split if applicable) with taxes included.
     */
    public String getTotalCharged() {
        return total_charged;
    }

    /**
     * Gets the total amount still owed after attempting to charge the user.<br/>
     * May be {@code null} if amount was paid in full.
     */
    @Nullable
    public Float getTotalOwed() {
        return total_owed;
    }

    /**
     * Gets the ISO 4217 currency code.
     */
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
