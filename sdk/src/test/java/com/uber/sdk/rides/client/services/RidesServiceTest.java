package com.uber.sdk.rides.client.services;

import com.squareup.moshi.Moshi;
import com.uber.sdk.WireMockTest;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.Before;
import org.junit.Test;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class RidesServiceTest extends WireMockTest {

    private static final float PICKUP_LATITUDE = 37.7753f;
    private static final float PICKUP_LONGITUDE = -122.418f;
    private static final float DROPOFF_LATITUDE = 37.787654f;
    private static final float DROPOFF_LONGITUDE = -122.40276f;
    private static final String UBER_POOL_PRODUCT_ID = "26546650-e557-4a7b-86e7-6a3942445247";
    private static final String UBER_X_PRODUCT_ID = "a1111c8c-c720-46c3-8534-2fcdd730040d";
    private static final String RIDE_REQUEST_UBER_POOL = "{\"end_latitude\":37.787655,\"end_longitude\":-122.40276,\"product_id\":\"26546650-e557-4a7b-86e7-6a3942445247\",\"seat_count\":2,\"start_latitude\":37.7753,\"start_longitude\":-122.418}";
    private static final String RIDE_REQUEST = "{\"end_latitude\":37.787655,\"end_longitude\":-122.40276,\"seat_count\":2,\"start_latitude\":37.7753,\"start_longitude\":-122.418}";
    private static final String FARE_ID = "2455e0e040da58e77babe4e32e4c771f89faf87778a95bc5aec2be406865ad30";

    private RidesService service;

    @Before
    public void setUp() throws Exception {

        service = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(new Moshi.Builder().build()))
                .client(new OkHttpClient.Builder()
                        .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                        .build())
                .baseUrl("http://localhost:" + wireMockRule.port())
                .build()
                .create(RidesService.class);


    }

    private static RideRequestParameters createRideRequest() {
        return new RideRequestParameters.Builder()
                .setPickupCoordinates(PICKUP_LATITUDE, PICKUP_LONGITUDE)
                .setDropoffCoordinates(DROPOFF_LATITUDE, DROPOFF_LONGITUDE)
                .setSeatCount(2)
                .build();
    }

    private static RideRequestParameters createUberPoolRideRequest() {
        return new RideRequestParameters.Builder()
                .setPickupCoordinates(PICKUP_LATITUDE, PICKUP_LONGITUDE)
                .setDropoffCoordinates(DROPOFF_LATITUDE, DROPOFF_LONGITUDE)
                .setProductId(UBER_POOL_PRODUCT_ID)
                .setSeatCount(2)
                .build();
    }

    @Test
    public void testGetProducts() throws Exception {
        stubFor(get(urlPathEqualTo("/v1/products"))
                .willReturn(aResponse().withBodyFile("products.json")));

        final List<Product> products = service.getProducts(PICKUP_LATITUDE, PICKUP_LONGITUDE)
                .execute()
                .body()
                .getProducts();

        assertThat(products.size()).isEqualTo(9);

        final Product uberPool = products.get(0);
        assertThat(uberPool.getDisplayName()).isEqualTo("uberPOOL");
        assertThat(uberPool.getCapacity()).isEqualTo(2);
        assertThat(uberPool.getProductId()).isEqualTo(UBER_POOL_PRODUCT_ID);
        assertThat(uberPool.isShared()).isTrue();

        final Product uberX = products.get(1);
        assertThat(uberX.getDisplayName()).isEqualTo("uberX");
        assertThat(uberX.getCapacity()).isEqualTo(4);
        assertThat(uberX.getProductId()).isEqualTo(UBER_X_PRODUCT_ID);
        assertThat(uberX.isShared()).isFalse();
    }

    @Test
    public void testGetRideEstimate_withUberPoolProductId() throws Exception {
        stubFor(post(urlPathEqualTo("/v1/requests/estimate"))
                .withRequestBody(equalToJson(RIDE_REQUEST_UBER_POOL, true, false))
                .willReturn(aResponse().withBodyFile("request_estimate_UberPool.json")));

        final RideEstimate rideEstimate = service.estimateRide(createUberPoolRideRequest()).execute().body();

        assertThat(rideEstimate.getPrice().getFareId()).isEqualTo(FARE_ID);
        assertThat(rideEstimate.getPickupEstimate()).isEqualTo(4);
        assertThat(rideEstimate.getPrice().getHighEstimate()).isEqualTo(5);
        assertThat(rideEstimate.getPrice().getLowEstimate()).isEqualTo(4);
        assertThat(rideEstimate.getPrice().getDisplay()).isEqualTo("$4.87");

        assertThat(rideEstimate.getTrip()).isNotNull();
        assertThat(rideEstimate.getTrip().getDistanceUnit()).isEqualTo("mile");
        assertThat(rideEstimate.getTrip().getDurationEstimate()).isEqualTo(720);
        assertThat(rideEstimate.getTrip().getDistanceEstimate()).isEqualTo(1.88f);
    }

    @Test
    public void testGetRideEstimate_withoutProductId() throws Exception {
        stubFor(post(urlPathEqualTo("/v1/requests/estimate"))
                .withRequestBody(equalToJson(RIDE_REQUEST, true, false))
                .willReturn(aResponse().withBodyFile("requests_estimate.json")));

        final RideEstimate rideEstimate = service.estimateRide(createRideRequest()).execute().body();
        assertThat(rideEstimate.getPrice().getFareId()).isNull();
        assertThat(rideEstimate.getPrice().getHighEstimate()).isEqualTo(10);
        assertThat(rideEstimate.getPrice().getLowEstimate()).isEqualTo(7);
        assertThat(rideEstimate.getPrice().getDisplay()).isEqualTo("$7-10");

        assertThat(rideEstimate.getTrip()).isNotNull();
        assertThat(rideEstimate.getTrip().getDistanceUnit()).isEqualTo("mile");
        assertThat(rideEstimate.getTrip().getDurationEstimate()).isEqualTo(720);
        assertThat(rideEstimate.getTrip().getDistanceEstimate()).isEqualTo(1.88f);
    }

    @Test
    public void testRequestRide_withoutProductId() throws Exception {
        stubFor(post(urlPathEqualTo("/v1/requests"))
                .withRequestBody(equalToJson(RIDE_REQUEST, true, false))
                .willReturn(aResponse().withBodyFile("requests_current.json")));

        final Ride ride = service.requestRide(createRideRequest()).execute().body();

        assertThat(ride.getStatus()).isEqualTo("processing");
        assertThat(ride.getProductId()).isEqualTo(UBER_X_PRODUCT_ID);
        assertThat(ride.getRideId()).isNotEmpty();
        assertThat(ride.isShared()).isFalse();
        assertThat(ride.getEta()).isNull();
    }

    @Test
    public void testRequestRide_withUberPoolProductId() throws Exception {
        stubFor(post(urlPathEqualTo("/v1/requests"))
                .withRequestBody(equalToJson(RIDE_REQUEST_UBER_POOL, true, false))
                .willReturn(aResponse().withBodyFile("requests_current_UberPool.json")));

        final Ride ride = service.requestRide(createUberPoolRideRequest()).execute().body();
        assertThat(ride.getStatus()).isEqualTo("processing");

        assertThat(ride.getProductId()).isEqualTo(UBER_POOL_PRODUCT_ID);
        assertThat(ride.getRideId()).isNotEmpty();
        assertThat(ride.isShared()).isTrue();
    }
}
