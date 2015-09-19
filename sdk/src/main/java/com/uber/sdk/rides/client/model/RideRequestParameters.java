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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Parameters to make a ride request. See
 * <a href="https://developer.uber.com/v1/endpoints/#request">Requests</a>
 * for more information.
 */
public class RideRequestParameters {

    private String product_id;
    private float start_latitude;
    private float start_longitude;
    @Nullable private Float end_latitude;
    @Nullable private Float end_longitude;
    @Nullable private String surge_confirmation_id;

    private RideRequestParameters(@Nonnull String productId,
            float startLatitude,
            float startLongitude,
            @Nullable Float endLatitude,
            @Nullable Float endLongitude,
            @Nullable String surgeConfirmationId) {
        this.product_id = productId;
        this.start_latitude = startLatitude;
        this.start_longitude = startLongitude;
        this.end_latitude = endLatitude;
        this.end_longitude = endLongitude;
        this.surge_confirmation_id = surgeConfirmationId;
    }

    /**
     * Builder for ride request parameters.
     */
    public static class Builder {

        private String productId;
        private Float startLatitude;
        private Float startLongitude;
        @Nullable private Float endLatitude;
        @Nullable private Float endLongitude;
        private String surgeConfirmationId;

        /**
         * Sets the unique ID of the product being requested. Required.
         */
        public Builder setProductId(@Nonnull String productId) {
            this.productId = productId;
            return this;
        }

        /**
         * Sets the beginning or "pickup" location. Required.
         */
        public Builder setStartLocation(@Nonnull Location location) {
            this.startLatitude = location.getLatitude();
            this.startLongitude = location.getLongitude();
            return this;
        }

        /**
         * Sets the final or destination location. Optional.
         */
        public Builder setEndLocation(@Nonnull Location location) {
            this.endLatitude = location.getLatitude();
            this.endLongitude = location.getLongitude();
            return this;
        }

        /**
         * Sets he unique identifier of the surge session for a user.
         * Required when returned from a 409 Conflict response on previous POST attempt. Optional
         * otherwise.
         */
        public Builder setSurgeConfirmationId(String surgeConfirmationId) {
            this.surgeConfirmationId = surgeConfirmationId;
            return this;
        }

        private void validate() {
            if (productId == null) {
                throw new IllegalArgumentException("Product ID must be set.");
            }

            if (startLatitude == null || startLongitude == null) {
                throw new IllegalArgumentException("Start latitude and longitude must be set.");
            }
        }

        /**
         * Builds a {@link RideRequestParameters}.
         */
        public RideRequestParameters build() {
            validate();

            return new RideRequestParameters(productId,
                    startLatitude,
                    startLongitude,
                    endLatitude,
                    endLongitude,
                    surgeConfirmationId);
        }
    }

    /**
     * Gets the product Id for this Ride Request
     */
    @Nonnull
    public String getProductId() {
        return product_id;
    }

    /**
     * Gets the start location's Latitude for this Ride Request
     */
    @Nonnull
    public float getStartLatitude() {
        return start_latitude;
    }

    /**
     * Gets the start location's Longitude for this Ride Request
     */
    @Nonnull
    public float getStartLongitude() {
        return start_longitude;
    }

    /**
     * Gets the start location for this Ride Request
     */
    @Nonnull
    public Location getStartLocation() {
        return new Location(start_latitude, start_longitude);
    }

    /**
     * Gets the end location's Latitude for this Ride Request
     */
    @Nullable
    public Float getEndLatitude() {
        return end_latitude;
    }

    /**
     * Gets the end location's Longitude for this Ride Request
     */
    @Nullable
    public Float getEndLongitude() {
        return end_longitude;
    }

    /**
     * Gets the end location for this Ride Request
     */
    @Nullable
    public Location getEndLocation() {
        return new Location(end_latitude, end_longitude);
    }

    /**
     * Gets the unique identifier of the surge session.
     */
    @Nullable
    public String getSurgeConfirmationId() {
        return surge_confirmation_id;
    }

    /**
     * Sets the unique identifier of the surge session for a user. Required when returned from a 409
     * Conflict response on previous POST attempt.
     */
    public void setSurgeConfirmationId(@Nullable String surgeConfirmationId) {
        this.surge_confirmation_id = surgeConfirmationId;
    }
}