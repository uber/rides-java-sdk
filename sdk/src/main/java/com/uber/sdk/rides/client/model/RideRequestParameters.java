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

import com.uber.sdk.rides.client.model.Place.Places;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Parameters to make a ride request. See
 * <a href="https://developer.uber.com/v1/endpoints/#request">Requests</a>
 * for more information.
 */
public class RideRequestParameters {

    @Nullable
    private String product_id;
    @Nullable
    private Float start_latitude;
    @Nullable
    private Float start_longitude;
    @Nullable
    private String start_nickname;
    @Nullable
    private String start_address;
    @Nullable
    private String start_place_id;
    @Nullable
    private Float end_latitude;
    @Nullable
    private Float end_longitude;
    @Nullable
    private String end_nickname;
    @Nullable
    private String end_address;
    @Nullable
    private String end_place_id;
    @Nullable
    private String surge_confirmation_id;
    @Nullable
    private String payment_method_id;
    @Nullable
    private Integer seat_count;
    @Nullable
    private String fare_id;

    private RideRequestParameters(@Nullable String productId,
                                  @Nullable Float startLatitude,
                                  @Nullable Float startLongitude,
                                  @Nullable String startNickname,
                                  @Nullable String startAddress,
                                  @Nullable String startPlaceId,
                                  @Nullable Float endLatitude,
                                  @Nullable Float endLongitude,
                                  @Nullable String endNickname,
                                  @Nullable String endAddress,
                                  @Nullable String endPlaceId,
                                  @Nullable String surgeConfirmationId,
                                  @Nullable String paymentMethodId,
                                  @Nullable Integer seatCount,
                                  @Nullable String fareId) {
        this.product_id = productId;
        this.start_latitude = startLatitude;
        this.start_longitude = startLongitude;
        this.start_nickname = startNickname;
        this.start_address = startAddress;
        this.start_place_id = startPlaceId;
        this.end_latitude = endLatitude;
        this.end_longitude = endLongitude;
        this.end_nickname = endNickname;
        this.end_address = endAddress;
        this.end_place_id = endPlaceId;
        this.surge_confirmation_id = surgeConfirmationId;
        this.payment_method_id = paymentMethodId;
        this.seat_count = seatCount;
        this.fare_id = fareId;
    }

    /**
     * Builder for ride request parameters.
     */
    public static class Builder {

        @Nullable
        private String productId;
        @Nullable
        private Float startLatitude;
        @Nullable
        private Float startLongitude;
        @Nullable
        private String startNickname;
        @Nullable
        private String startAddress;
        @Nullable
        private String startPlaceId;
        @Nullable
        private Float endLatitude;
        @Nullable
        private Float endLongitude;
        @Nullable
        private String endNickname;
        @Nullable
        private String endAddress;
        @Nullable
        private String endPlaceId;
        @Nullable
        private String surgeConfirmationId;
        @Nullable
        private String paymentMethodId;
        @Nullable
        private Integer seatCount;
        @Nullable
        private String fareId;

        public Builder() {
        }

        public Builder(
                @Nullable String productId,
                @Nullable Float startLatitude,
                @Nullable Float startLongitude,
                @Nullable String startNickname,
                @Nullable String startAddress,
                @Nullable String startPlaceId,
                @Nullable Float endLatitude,
                @Nullable Float endLongitude,
                @Nullable String endNickname,
                @Nullable String endAddress,
                @Nullable String endPlaceId,
                @Nullable String surgeConfirmationId,
                @Nullable String paymentMethodId,
                @Nullable Integer seatCount,
                @Nullable String fareId) {
            this.productId = productId;
            this.startLatitude = startLatitude;
            this.startLongitude = startLongitude;
            this.startNickname = startNickname;
            this.startAddress = startAddress;
            this.startPlaceId = startPlaceId;
            this.endLatitude = endLatitude;
            this.endLongitude = endLongitude;
            this.endNickname = endNickname;
            this.endAddress = endAddress;
            this.endPlaceId = endPlaceId;
            this.surgeConfirmationId = surgeConfirmationId;
            this.paymentMethodId = paymentMethodId;
            this.seatCount = seatCount;
            this.fareId = fareId;
        }

        /**
         * Sets the unique ID of the product being requested. If none supplied, the cheapest product for the
         * location is used.
         */
        public Builder setProductId(@Nonnull String productId) {
            this.productId = productId;
            return this;
        }

        /**
         * Sets the pickup location's coordinates.
         *
         * @param latitude the pickup location's latitude.
         * @param longitude the pickup location's longitude .
         * @param longitude
         */
        public Builder setPickupCoordinates(@Nullable Float latitude, @Nullable Float longitude) {
            this.startLatitude = latitude;
            this.startLongitude = longitude;
            return this;
        }

        /**
         * Sets the pickup location's nickname.
         *
         * @param nickname the pickup location's nickname.
         */
        public Builder setPickupNickname(@Nullable String nickname) {
            this.startNickname = nickname;
            return this;
        }

        /**
         * Sets the pickup location's address.
         *
         * @param address the pickup location's nickname.
         */
        public Builder setPickupAddress(@Nullable String address) {
            this.startAddress = address;
            return this;
        }

        /**
         * Sets the pickup location via place identifier.
         *
         * @param placeId the pickup location's nickname.
         */
        public Builder setPickupPlaceId(@Nullable String placeId) {
            this.startPlaceId = placeId;
            return this;
        }

        /**
         * Sets the pickup location via place identifier.
         *
         * @param place the pickup location's nickname.
         */
        public Builder setPickupPlace(@Nullable Places place) {
            this.startPlaceId = place == null ? null : place.toString();
            return this;
        }

        /**
         * Sets the dropoff location's coordinates.
         *
         * @param latitude the dropoff location's latitude.
         * @param longitude the dropoff location's longitude.
         */
        public Builder setDropoffCoordinates(@Nullable Float latitude, @Nullable Float longitude) {
            this.endLatitude = latitude;
            this.endLongitude = longitude;
            return this;
        }

        /**
         * Sets the pickup location's nickname.
         *
         * @param nickname the pickup location's nickname.
         */
        public Builder setDropoffNickname(@Nullable String nickname) {
            this.endNickname = nickname;
            return this;
        }

        /**
         * Sets the pickup location's address.
         *
         * @param address the pickup location's nickname.
         */
        public Builder setDropoffAddress(@Nullable String address) {
            this.endAddress = address;
            return this;
        }

        /**
         * Sets the pickup location via place identifier.
         *
         * @param placeId the pickup location's nickname.
         */
        public Builder setDropoffPlaceId(@Nullable String placeId) {
            this.endPlaceId = placeId;
            return this;
        }

        /**
         * Sets the pickup location via place identifier.
         *
         * @param place the pickup location's nickname.
         */
        public Builder setDropoffPlace(@Nullable Places place) {
            this.endPlaceId = place == null ? null : place.toString();
            return this;
        }

        /**
         * Sets he unique identifier of the surge session for a user.
         * Required when returned from a 409 Conflict response on previous POST attempt. Optional
         * otherwise.
         */
        public Builder setSurgeConfirmationId(@Nullable String surgeConfirmationId) {
            this.surgeConfirmationId = surgeConfirmationId;
            return this;
        }

        /**
         * Sets the payment method to be used for this request.
         *
         * @param paymentMethodId the unique identifier of the payment method.
         */
        public Builder setPaymentMethodId(@Nullable String paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
            return this;
        }

        /**
         * Sets the number of seats required for this request.
         *
         * @param seatCount
         */
        public Builder setSeatCount(@Nullable Integer seatCount) {
            this.seatCount = seatCount;
            return this;
        }

        /**
         * Sets the fare Id requested for this ride.
         *
         * @param fareId
         */
        public Builder setFareId(@Nullable String fareId) {
            this.fareId = fareId;
            return this;
        }

        private void validate() {
            if (startPlaceId != null) {
                if (startLatitude != null || startLongitude != null) {
                    throw new IllegalArgumentException("Exactly one of pickup place or pickup coordinates is required.");
                }
            } else {
                if (startLatitude == null && startLongitude == null) {
                    throw new IllegalArgumentException("Exactly one of pickup place or pickup coordinates is required.");
                } else if (startLatitude == null || startLongitude == null) {
                    throw new IllegalArgumentException("Need both pickup latitude and pickup longitude");
                }
            }

            if (endPlaceId != null && (endLatitude != null || endLongitude != null)) {
                throw new IllegalArgumentException("Cannot have both dropoff place and dropoff coordinates");
            } else {
                if ((endLatitude != null && endLongitude == null) || (endLatitude == null && endLongitude != null)) {
                    throw new IllegalArgumentException("Need both dropoff latitude and dropoff longitude");
                }
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
                    startNickname,
                    startAddress,
                    startPlaceId,
                    endLatitude,
                    endLongitude,
                    endNickname,
                    endAddress,
                    endPlaceId,
                    surgeConfirmationId,
                    paymentMethodId,
                    seatCount,
                    fareId);
        }
    }

    @Nonnull
    public Builder newBuilder() {
        return new Builder(product_id, start_latitude, start_longitude, start_nickname, start_address, start_place_id,
                end_latitude, end_longitude, end_nickname, end_address, end_place_id, surge_confirmation_id,
                payment_method_id, seat_count, fare_id);
    }

    /**
     * Gets the product Id for this Ride Request
     */
    @Nullable
    public String getProductId() {
        return product_id;
    }

    /**
     * Gets the pickup location's Latitude for this Ride Request.
     */
    @Nullable
    public Float getPickupLatitude() {
        return start_latitude;
    }

    /**
     * Gets the pickup location's Longitude for this Ride Request.
     */
    @Nullable
    public Float getPickupLongitude() {
        return start_longitude;
    }

    /**
     * Gets the pickup location's nickname for this Ride Request.
     */
    @Nullable
    public String getPickupNickname() {
        return start_nickname;
    }

    /**
     * Gets the pickup location's address for this Ride Request.
     */
    @Nullable
    public String getPickupAddress() {
        return start_address;
    }

    /**
     * Gets the pickup place identifier for this Ride Request.
     */
    @Nullable
    public String getPickupPlaceId() {
        return start_place_id;
    }

    /**
     * Gets the dropoff location's Latitude for this Ride Request.
     */
    @Nullable
    public Float getDropoffLatitude() {
        return end_latitude;
    }

    /**
     * Gets the dropoff location's Longitude for this Ride Request.
     */
    @Nullable
    public Float getDropoffLongitude() {
        return end_longitude;
    }

    /**
     * Gets the dropoff location's nickname for this Ride Request.
     */
    @Nullable
    public String getDropoffNickname() {
        return end_nickname;
    }

    /**
     * Gets the dropoff location's address for this Ride Request.
     */
    @Nullable
    public String getDropoffAddress() {
        return end_address;
    }

    /**
     * Gets the dropoff place identifier for this Ride Request.
     */
    @Nullable
    public String getDropoffPlaceId() {
        return end_place_id;
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

    /**
     * Gets the payment method identifier to be used for this Ride Request.
     */
    @Nullable
    public String getPaymentMethodId() {
        return payment_method_id;
    }

    /**
     * Gets the number of seats required for this Ride Request.
     */
    @Nullable
    public Integer getSeatCount() {
        return seat_count;
    }

    /**
     * Gets the fare ID to be used for this Ride Request, shared ride (Uber Pool) will return this
     * otherwise it will be null.
     */
    @Nullable
    public String getFareId() {
        return fare_id;
    }
}