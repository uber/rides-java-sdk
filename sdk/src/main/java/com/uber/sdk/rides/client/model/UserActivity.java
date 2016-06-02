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

/**
 * A user's activity. See
 * <a href="https://developer.uber.com/v1/endpoints/#user-activity-v1-2">User Activity</a> for more
 * information.
 */
public class  UserActivity {

    private String request_id;
    private String status;
    private float distance;
    private long request_time;
    private long start_time;
    private long end_time;
    private String product_id;
    private City start_city;
    private float fare;
    private String currency_code;

    /**
     * The unique ID for a ride.
     */
    public String getRideId() {
        return request_id;
    }

    /**
     * Status of the activity. Only returns completed for now.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Length of activity in miles.
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Unix timestamp of activity request time.
     */
    public long getRequestTime() {
        return request_time;
    }

    /**
     * Unix timestamp of activity start time.
     */
    public long getStartTime() {
        return start_time;
    }

    /**
     * Unix timestamp of activity end time.
     */
    public long getEndTime() {
        return end_time;
    }

    /**
     * Unique identifier representing a specific product for a given latitude &amp; longitude. For
     * example, uberX in San Francisco will have a different product_id than uberX in Los Angeles.
     */
    public String getProductId() {
        return product_id;
    }

    /**
     * The city, represented by the center latitude and longitude, of the pickup location. May be
     * {@code null} if your application is not whitelisted for this information.
     */
    @Nullable
    public City getStartCity() {
        return start_city;
    }

    /**
     * The fare of the trip. May be {@code null} if your application is not whitelisted for this
     * information.
     */
    @Nullable
    public Float getFare() {
        return fare;
    }

    /**
     * The currency code of the fare. May be {@code null} if your application is not whitelisted for
     * this information.
     */
    @Nullable
    public String getCurrencyCode() {
        return currency_code;
    }

    /**
     * Represents a city by it's centered latitude and longitude.
     */
    public static class City {

        private String display_name;
        private float latitude;
        private float longitude;

        /**
         * The display name of the city.
         */
        public String getDisplayName() {
            return display_name;
        }

        /**
         * The latitude of the city center.
         */
        public float getLatitude() {
            return latitude;
        }

        /**
         * The longitude of the city center.
         */
        public float getLongitude() {
            return longitude;
        }
    }
}