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

import javax.annotation.Nullable;

/**
 * Parameters to update a ride. See
 * <a href="https://developer.uber.com/docs/v1-requests-patch">Update Request</a>
 * for more information.
 */
public class RideUpdateParameters {

    @Nullable private Float end_latitude;
    @Nullable private Float end_longitude;
    @Nullable private String end_nickname;
    @Nullable private String end_address;
    @Nullable private String end_place_id;

    private RideUpdateParameters(
            @Nullable Float endLatitude,
            @Nullable Float endLongitude,
            @Nullable String endNickname,
            @Nullable String endAddress,
            @Nullable String endPlaceId) {
        this.end_latitude = endLatitude;
        this.end_longitude = endLongitude;
        this.end_nickname = endNickname;
        this.end_address = endAddress;
        this.end_place_id =endPlaceId;
    }

    /**
     * Builder for ride update parameters.
     */
    public static class Builder {

        @Nullable private Float endLatitude;
        @Nullable private Float endLongitude;
        @Nullable private String endNickname;
        @Nullable private String endAddress;
        @Nullable private String endPlaceId;

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
         * Sets the dropoff location's nickname.
         *
         * @param nickname the dropoff location's nickname.
         */
        public Builder setDropoffNickname(@Nullable String nickname) {
            this.endNickname = nickname;
            return this;
        }

        /**
         * Sets the dropoff location's address.
         *
         * @param address the dropoff location's nickname.
         */
        public Builder setDropoffAddress(@Nullable String address) {
            this.endAddress = address;
            return this;
        }

        /**
         * Sets the dropoff location via place identifier.
         *
         * @param placeId the dropoff location's nickname.
         */
        public Builder setDropoffPlaceId(@Nullable String placeId) {
            this.endPlaceId = placeId;
            return this;
        }

        /**
         * Sets the dropoff location via place identifier.
         *
         * @param place the dropoff location's nickname.
         */
        public Builder setDropoffPlace(@Nullable Places place) {
            this.endPlaceId = place == null ? null : place.toString();
            return this;
        }

        private void validate() {
            if (endPlaceId != null) {
                if (endLatitude != null || endLongitude != null) {
                    throw new IllegalArgumentException("Exactly one of dropoff place or dropoff coordinates is "
                            + "required.");
                }
            } else {
                if (endLatitude == null && endLongitude == null) {
                    throw new IllegalArgumentException("Exactly one of dropoff place or dropoff coordinates is "
                            + "required.");
                } else if (endLatitude == null || endLongitude == null) {
                    throw new IllegalArgumentException("Need both dropoff latitude and dropoff longitude");
                }
            }
        }


        /**
         * Builds a {@link RideUpdateParameters}.
         */
        public RideUpdateParameters build() {
            validate();

            return new RideUpdateParameters(
                    endLatitude,
                    endLongitude,
                    endNickname,
                    endAddress,
                    endPlaceId);
        }
    }

    /**
     * Gets the dropoff location's Latitude for this Ride update.
     */
    @Nullable
    public Float getDropoffatitude() {
        return end_latitude;
    }

    /**
     * Gets the dropoff location's Longitude for this Ride update.
     */
    @Nullable
    public Float getDropoffLongitude() {
        return end_longitude;
    }

    /**
     * Gets the dropoff location's nickname for this Ride update.
     */
    @Nullable
    public String getDropoffNickname() {
        return end_nickname;
    }

    /**
     * Gets the dropoff location's address for this Ride update.
     */
    @Nullable
    public String getDropoffAddress() {
        return end_address;
    }

    /**
     * Gets the dropoff place identifier for this Ride update.
     */
    @Nullable
    public String getDropoffPlaceId() {
        return end_place_id;
    }
}
