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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.uber.sdk.rides.client.model.Place.Places.*;

public class RideRequestParametersTest {

    @Rule public ExpectedException exception = ExpectedException.none();

    @Test
    public void onBuild_whenJustPickupPlaceIdProvided_shouldSucceed() throws Exception {
        new RideRequestParameters.Builder().setPickupPlaceId("home");
    }

    @Test
    public void onBuild_whenJustPickupPlaceProvided_shouldSucceed() throws Exception {
        new RideRequestParameters.Builder().setPickupPlace(HOME);
    }

    @Test
    public void onBuild_whenJustPickupCoordinatesProvided_shouldSucceed() throws Exception {
        new RideRequestParameters.Builder().setPickupCoordinates(30f, -122f);
    }

    @Test
    public void build_whenNoPickupPlaceOrCoordinatesProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of pickup place or pickup coordinates is required.");

        new RideRequestParameters.Builder().build();
    }

    @Test
    public void build_whenBothPickupPlaceIdAndCoordinatesProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of pickup place or pickup coordinates is required.");

        new RideRequestParameters.Builder().setPickupCoordinates(30f, -122f).setPickupPlaceId("home").build();
    }

    @Test
    public void build_whenBothPickupPlaceAndCoordinatesProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of pickup place or pickup coordinates is required.");

        new RideRequestParameters.Builder().setPickupCoordinates(30f, -122f).setPickupPlace(HOME).build();
    }

    @Test
    public void build_whenBothPickupPlaceAndLatitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of pickup place or pickup coordinates is required.");

        new RideRequestParameters.Builder().setPickupCoordinates(30f, null).setPickupPlace(HOME).build();
    }

    @Test
    public void build_whenBothPickupPlaceAndLongitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Exactly one of pickup place or pickup coordinates is required.");

        new RideRequestParameters.Builder().setPickupCoordinates(null, -122f).setPickupPlace(HOME).build();
    }

    @Test
    public void build_whenJustPickupLatitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Need both pickup latitude and pickup longitude");

        new RideRequestParameters.Builder().setPickupCoordinates(30f, null).build();
    }

    @Test
    public void build_whenJustPickupLongitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Need both pickup latitude and pickup longitude");

        new RideRequestParameters.Builder().setPickupCoordinates(null, -122f).build();
    }

    @Test
    public void onBuild_whenJustDropoffPlaceIdProvided_shouldSucceed() throws Exception {
        new RideRequestParameters.Builder().setPickupPlace(HOME).setDropoffPlaceId("home").build();
    }

    @Test
    public void onBuild_whenJustDropoffPlaceProvided_shouldSucceed() throws Exception {
        new RideRequestParameters.Builder().setPickupPlace(HOME).setDropoffPlace(HOME).build();
    }

    @Test
    public void onBuild_whenJustDropoffCoordinatesProvided_shouldSucceed() throws Exception {
        new RideRequestParameters.Builder().setPickupPlace(HOME).setDropoffCoordinates(30f, -122f).build();
    }

    @Test
    public void build_whenBothDropoffPlaceIdAndCoordinatesProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Cannot have both dropoff place and dropoff coordinates");

        new RideRequestParameters.Builder()
                .setPickupPlace(HOME)
                .setDropoffCoordinates(30f, -122f)
                .setDropoffPlaceId("home")
                .build();
    }

    @Test
    public void build_whenBothDropoffPlaceAndCoordinatesProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Cannot have both dropoff place and dropoff coordinates");

        new RideRequestParameters.Builder()
                .setPickupPlace(HOME)
                .setDropoffCoordinates(30f, -122f)
                .setDropoffPlace(HOME)
                .build();
    }

    @Test
    public void build_whenBothDropoffPlaceAndLatitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Cannot have both dropoff place and dropoff coordinates");

        new RideRequestParameters.Builder()
                .setPickupPlace(HOME)
                .setDropoffCoordinates(30f, null)
                .setDropoffPlace(HOME)
                .build();
    }

    @Test
    public void build_whenBothDropoffPlaceAndLongitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Cannot have both dropoff place and dropoff coordinates");

        new RideRequestParameters.Builder()
                .setPickupPlace(HOME)
                .setDropoffCoordinates(null, -122f)
                .setDropoffPlace(HOME)
                .build();
    }

    @Test
    public void build_whenJustDropoffLatitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Need both dropoff latitude and dropoff longitude");

        new RideRequestParameters.Builder()
                .setPickupPlace(HOME)
                .setDropoffCoordinates(30f, null)
                .build();
    }

    @Test
    public void build_whenJustDropoffLongitudeProvided_shouldFail() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Need both dropoff latitude and dropoff longitude");

        new RideRequestParameters.Builder()
                .setPickupPlace(HOME)
                .setDropoffCoordinates(null, -122f)
                .build();
    }

}
