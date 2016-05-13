/*
 * Copyright (c) 2015 Uber Technologies, Inc.
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

package com.uber.sdk.rides.client.internal;

import com.uber.sdk.rides.client.model.PaymentMethod;
import com.uber.sdk.rides.client.model.PaymentMethodsResponse;
import com.uber.sdk.rides.client.model.Place;
import com.uber.sdk.rides.client.model.PlaceParameters;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.ProductsResponse;
import com.uber.sdk.rides.client.model.Promotion;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideMap;
import com.uber.sdk.rides.client.model.RideReceipt;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.model.RideUpdateParameters;
import com.uber.sdk.rides.client.model.SandboxProductRequestParameters;
import com.uber.sdk.rides.client.model.SandboxRideRequestParameters;
import com.uber.sdk.rides.client.model.TimeEstimatesResponse;
import com.uber.sdk.rides.client.model.UserActivityPage;
import com.uber.sdk.rides.client.model.UserProfile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

import static com.uber.sdk.rides.client.Session.Environment;

/**
 * Represents the RPC methods of the Uber API.
 */
public interface RetrofitUberRidesService {

    /**
     * Gets information about the promotion that will be available to a new user based on their
     * activity's location.
     *
     * @param startLatitude Latitude component of start location.
     * @param startLongitude Longitude component of start location.
     * @param endLatitude Latitude component of end location.
     * @param endLongitude Longitude component of end location.
     * @param callback The request callback.
     */
    @GET("/v1/promotions")
    void getPromotions(@Query("start_latitude") float startLatitude,
            @Query("start_longitude") float startLongitude,
            @Query("end_latitude") float endLatitude,
            @Query("end_longitude") float endLongitude,
            Callback<Promotion> callback);

    /**
     * Gets a limited amount of data about a user's lifetime activity.
     *
     * @param offset Offset the list of returned results by this amount. Default is zero.
     * @param limit Number of items to retrieve. Default is 5, maximum is 50.
     * @param callback The request callback.
     */
    @GET("/v1.2/history")
    void getUserActivity(@Nullable @Query("offset") Integer offset,
            @Nullable @Query("limit") Integer limit,
            Callback<UserActivityPage> callback);

    /**
     * Gets information about the user that has authorized with the application.
     *
     * @param callback The request callback.
     */
    @GET("/v1/me")
    void getUserProfile(Callback<UserProfile> callback);

    /**
     * Gets an estimated price range for each product offered at a given location.
     *
     * @param startLatitude Latitude component of start location.
     * @param startLongitude Longitude component of start location.
     * @param endLatitude Latitude component of end location.
     * @param endLongitude Longitude component of end location.
     * @param callback The request callback.
     */
    @GET("/v1/estimates/price")
    void getPriceEstimates(@Query("start_latitude") float startLatitude,
            @Query("start_longitude") float startLongitude,
            @Query("end_latitude") float endLatitude,
            @Query("end_longitude") float endLongitude,
            Callback<PriceEstimatesResponse> callback);

    /**
     * Gets ETAs for all products offered at a given location, with the responses expressed as
     * integers in seconds.
     *
     * @param startLatitude Latitude component of start location.
     * @param startLongitude Longitude component of start location.
     * @param productId Unique identifier representing a specific product for a given latitude &amp;
     *                  longitude.
     * @param callback The request callback.
     */
    @GET("/v1/estimates/time")
    void getPickupTimeEstimate(@Query("start_latitude") float startLatitude,
            @Query("start_longitude") float startLongitude,
            @Nullable @Query("product_id") String productId,
            Callback<TimeEstimatesResponse> callback);

    /**
     * Gets information about the products offered at a given location.
     *
     * @param latitude Latitude component of location.
     * @param longitude Longitude component of location.
     * @param callback The request callback.
     */
    @GET("/v1/products")
    void getProducts(@Query("latitude") float latitude,
            @Query("longitude") float longitude,
            Callback<ProductsResponse> callback);

    /**
     * Gets information about a specific product.
     *
     * @param productId The unique product ID to fetch information about.
     * @param callback The request callback.
     */
    @GET("/v1/products/{product_id}")
    void getProduct(@Path("product_id") String productId, Callback<Product> callback);

    /**
     * Cancels an ongoing Ride for a user.
     *
     * @param rideId Unique identifier representing a Request.
     * @param callback The request callback.
     */
    @DELETE("/v1/requests/{request_id}")
    void cancelRide(@Path("request_id") String rideId, Callback<Void> callback);

    /**
     * Requests a ride on behalf of a user given their desired product, start, and end locations.
     *
     * @param rideRequestParameters The ride request parameters.
     * @param callback The request callback.
     */
    @POST("/v1/requests")
    void requestRide(@Body RideRequestParameters rideRequestParameters, Callback<Ride> callback);

    /**
     * Gets the current ride a user is on.
     *
     * @param callback The request callback.
     */
    @GET("/v1/requests/current")
    void getCurrentRide(Callback<Ride> callback);

    /**
     * Cancels the current ride of a user.
     *
     * @param callback The request callback.
     */
    @DELETE("/v1/requests/current")
    void cancelCurrentRide(Callback<Void> callback);

    /**
     * Update an ongoing request's destination.
     *
     * @param rideUpdateParameters The ride request parameters.
     * @param callback The request callback.
     */
    @PATCH("/v1/requests/{request_id}")
    void updateRide(@Nonnull @Path("request_id") String rideId,
            @Body RideUpdateParameters rideUpdateParameters,
            Callback<Void> callback);

    /**
     * Gets information about a user's Place.
     *
     * @param placeId The identifier of a Place.
     * @param callback The request callback.
     */
    @GET("/v1/places/{place_id}")
    void getPlace(@Nonnull @Path("place_id") String placeId, Callback<Place> callback);

    /**
     * Sets information about a user's Place.
     *
     * @param placeId The identifier of a Place.
     * @param placeParameters The place parameters.
     * @param callback The request callback.
     */
    @PUT("/v1/places/{place_id}")
    void setPlace(@Nonnull @Path("place_id") String placeId,
            @Nonnull @Body PlaceParameters placeParameters,
            Callback<Place> callback);

    /**
     * Gets details about a specific ride.
     *
     * @param rideId The unique identifier for a ride.
     * @param callback The request callback.
     */
    @GET("/v1/requests/{request_id}")
    void getRideDetails(@Nonnull @Path("request_id") String rideId, Callback<Ride> callback);

    /**
     * Get receipt information for a completed request.
     * Access to this endpoint is restricted and requires whitelisting.
     *
     * @param rideId The unique identifier for a ride.
     * @param callback The request callback.
     */
    @GET("/v1/requests/{request_id}/receipt")
    void getRideReceipt(@Nonnull @Path("request_id") String rideId, Callback<RideReceipt> callback);

    /**
     * <p>
     * The request estimate endpoint allows a ride to be estimated given the desired product, start,
     * and end locations. If the end location is not provided, only the pickup ETA and details of
     * surge pricing information are provided. If the pickup ETA is null, there are no cars
     * available, but an estimate may still be given to the user.
     * </p>
     * <p>
     * You can use this endpoint to determine if surge pricing is in effect. Do this before
     * attempting to make a request so that you can preemptively have a user confirm surge by
     * sending them to the surge_confirmation_href provided in the response.
     * </p>
     *
     * @param rideRequestParameters The ride request parameters.
     * @param callback The request callback.
     */
    @POST("/v1/requests/estimate")
    void estimateRide(@Body RideRequestParameters rideRequestParameters,
            Callback<RideEstimate> callback);

    /**
     * Get a map with a visual representation of a ride for tracking purposes.
     *
     * Maps are only available after a ride has been accepted by a driver and is in the 'accepted' state. Attempting
     * to get a map before that will result in a 404 error.
     *
     * @param rideId Unique identifier representing a ride.
     * @param callback The request callback.
     */
    @GET("/v1/requests/{request_id}/map")
    void getRideMap(@Nonnull @Path("request_id") String rideId , Callback<RideMap> callback);

    /**
     * Gets the {@link PaymentMethod PaymentMethods} of user and their last used method ID.
     *
     * @param callback The request callback.
     */
    @GET("/v1/payment-methods")
    void getPaymentMethods(Callback<PaymentMethodsResponse> callback);

    /**
     * Updates the product in the {@link Environment#SANDBOX sandbox environement} to simulate the
     * possible responses the Request endpoint will return when requesting a particular product,
     * such as surge pricing and driver availability.
     *
     * @param productId The unique product ID to update.
     * @param sandboxProductRequestParameters The sandbox product request parameters.
     * @param callback The request callback.
     */
    @RetrofitUberRidesClient.SandboxOnly
    @PUT("/v1/sandbox/products/{product_id}")
    void updateSandboxProduct(@Path("product_id") String productId,
            @Body SandboxProductRequestParameters sandboxProductRequestParameters,
            Callback<Void> callback);

    /**
     * Updates the ride in the {@link Environment#SANDBOX sandbox environement} to simulate the
     * possible states of a the Request.
     *
     * @param rideId Unique identifier representing a Request.
     * @param sandboxRideRequestParameters The sandbox ride request parameters.
     * @param callback The request callback.
     */
    @RetrofitUberRidesClient.SandboxOnly
    @PUT("/v1/sandbox/requests/{request_id}")
    void updateSandboxRide(@Path("request_id") String rideId,
            @Body SandboxRideRequestParameters sandboxRideRequestParameters,
            Callback<Void> callback);
}
