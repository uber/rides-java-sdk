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

/**
 * An ongoing or completed ride. See
 * <a href="https://developer.uber.com/v1/endpoints/#request-details">Requests</a>
 * for more information.
 */
public class Ride {

    private String request_id;
    private String status;
    private Driver driver;
    private int eta;
    private float surge_multiplier;
    private Location location;
    private Vehicle vehicle;

    /**
     * The unique ID of the ride.
     */
    public String getRideId() {
        return request_id;
    }

    /**
     * The status of the ride indicating state.
     */
    public String getStatus() {
        return status;
    }

    /**
     * The object that contains driver details.
     */
    public Driver getDriver() {
        return driver;
    }

    /**
     * The estimated time of vehicle arrival in minutes.
     */
    public Integer getEta() {
        return eta;
    }

    /**
     * The object that contains the location information of the vehicle and driver.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * The object that contains vehicle details.
     */
    public Vehicle getVehicle() {
        return vehicle;
    }

    /**
     * The surge pricing multiplier used to calculate the increased price of a Request. A multiplier
     * of 1.0 means surge pricing is not in effect.
     */
    public Float getSurgeMultiplier() {
        return surge_multiplier;
    }
}