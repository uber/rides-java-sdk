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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.uber.sdk.rides.client.model.Place.Places.HOME;

public class RideUpdateParametersTest {

    @Rule public ExpectedException exception = ExpectedException.none();

    @Test
    public void onBuild_whenJustDropoffPlaceIdProvided_shouldSucceed() throws Exception {
        new RideUpdateParameters.Builder().setDropoffPlaceId("home").build();
    }

    @Test
    public void onBuild_whenJustDropoffPlaceProvided_shouldSucceed() throws Exception {
        new RideUpdateParameters.Builder().setDropoffPlace(HOME).build();
    }

    @Test
    public void onBuild_whenJustDropoffCoordinatesProvided_shouldSucceed() throws Exception {
        new RideUpdateParameters.Builder().setDropoffCoordinates(30f, -122f).build();
    }

    @Test
    public void onBuild_whenNoInfoProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of dropoff place or dropoff coordinates is required.");

        new RideUpdateParameters.Builder().build();
    }

    @Test
    public void build_whenBothDropoffPlaceIdAndCoordinatesProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of dropoff place or dropoff coordinates is required.");

        new RideUpdateParameters.Builder()
                .setDropoffCoordinates(30f, -122f)
                .setDropoffPlaceId("home")
                .build();
    }

    @Test
    public void build_whenBothDropoffPlaceAndCoordinatesProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of dropoff place or dropoff coordinates is required.");

        new RideUpdateParameters.Builder()
                .setDropoffCoordinates(30f, -122f)
                .setDropoffPlace(HOME)
                .build();
    }

    @Test
    public void build_whenBothDropoffPlaceAndLatitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of dropoff place or dropoff coordinates is required.");

        new RideUpdateParameters.Builder()
                .setDropoffCoordinates(30f, null)
                .setDropoffPlace(HOME)
                .build();
    }

    @Test
    public void build_whenBothDropoffPlaceAndLongitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of dropoff place or dropoff coordinates is required.");

        new RideUpdateParameters.Builder()
                .setDropoffCoordinates(null, -122f)
                .setDropoffPlace(HOME)
                .build();
    }

    @Test
    public void build_whenJustDropoffLatitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Need both dropoff latitude and dropoff longitude");

        new RideUpdateParameters.Builder()
                .setDropoffCoordinates(30f, null)
                .build();
    }

    @Test
    public void build_whenJustDropoffLongitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Need both dropoff latitude and dropoff longitude");

        new RideUpdateParameters.Builder()
                .setDropoffCoordinates(null, -122f)
                .build();
    }

}
