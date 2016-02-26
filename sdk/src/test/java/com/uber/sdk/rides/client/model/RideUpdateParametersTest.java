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
