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
 * Location in latitude and longitude in decimal notation.
 */
public class Location {

    private float latitude;
    private float longitude;
    @Nullable
    private Integer bearing;
    @Nullable
    private Integer eta;

    /**
     * Location must be created with a non-null latitude and longitude.
     */
    private Location() {}

    /**
     * Constructor.
     * @param latitude The latitude in decimal notation.
     * @param longitude The longitude in decimal notation.
     */
    public Location(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * The latitude in decimal notation.
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * The longitude in decimal notation.
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * The current bearing of the vehicle in degrees (0-359). {@code null} if the bearing is
     * unknown or no associated bearing.
     */
    @Nullable
    public Integer getBearing() {
        return bearing;
    }

    /**
     * If present, the ETA in minutes until this location is reached.
     */
    @Nullable
    public Integer getEta() {
        return eta;
    }
}