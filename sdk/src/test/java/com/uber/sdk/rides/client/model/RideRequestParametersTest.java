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
