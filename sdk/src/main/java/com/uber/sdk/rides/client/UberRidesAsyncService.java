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

package com.uber.sdk.rides.client;

import com.uber.sdk.rides.client.model.PaymentMethod;
import com.uber.sdk.rides.client.model.PaymentMethodsResponse;
import com.uber.sdk.rides.client.model.Place;
import com.uber.sdk.rides.client.model.Place.Places;
import com.uber.sdk.rides.client.model.PriceEstimatesResponse;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.ProductsResponse;
import com.uber.sdk.rides.client.model.Promotion;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideMap;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.model.RideUpdateParameters;
import com.uber.sdk.rides.client.model.SandboxProductRequestParameters;
import com.uber.sdk.rides.client.model.SandboxRideRequestParameters;
import com.uber.sdk.rides.client.model.TimeEstimatesResponse;
import com.uber.sdk.rides.client.model.UserActivityPage;
import com.uber.sdk.rides.client.model.UserProfile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.uber.sdk.rides.client.Session.Environment;

/**
 * Represents the asynchronous RPC methods of the Uber API. Can be built using
 *
 * <pre><code>
 *
 * UberRidesSyncService uberApiService = UberRidesServices.createSync(session);
 *
 * // Or
 *
 * uberApiService = UberRidesServices.sync().setSession(session).build();
 * </code></pre>
 * @see UberRidesServices.Builder#async()
 */
public interface UberRidesAsyncService extends UberRidesService {

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
    void getPromotions(float startLatitude,
            float startLongitude,
            float endLatitude,
            float endLongitude,
            Callback<Promotion> callback);

    /**
     * Gets a limited amount of data about a user's lifetime activity.
     *
     * @param offset Offset the list of returned results by this amount. Default is zero.
     * @param limit Number of items to retrieve. Default is 5, maximum is 50.
     * @param callback The request callback.
     */
    void getUserActivity(@Nullable Integer offset,
            @Nullable Integer limit,
            Callback<UserActivityPage> callback);

    /**
     * Gets information about the user that has authorized with the application.
     *
     * @param callback The request callback.
     */
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
    void getPriceEstimates(float startLatitude,
            float startLongitude,
            float endLatitude,
            float endLongitude,
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
    void getPickupTimeEstimates(float startLatitude,
            float startLongitude,
            @Nullable String productId,
            Callback<TimeEstimatesResponse> callback);

    /**
     * Gets information about the products offered at a given location.
     *
     * @param latitude Latitude component of location.
     * @param longitude Longitude component of location.
     * @param callback The request callback.
     */
    void getProducts(float latitude, float longitude, Callback<ProductsResponse> callback);

    /**
     * Gets information about a specific product.
     *
     * @param productId The unique product ID to fetch information about.
     * @param callback The request callback.
     */
    void getProduct(@Nonnull String productId, Callback<Product> callback);

    /**
     * Cancel an ongoing ride request on behalf of a rider.
     *
     * @param rideId The unique identifier of a ride.
     * @param callback The request callback.
     */
    void cancelRide(@Nonnull String rideId, Callback<Void> callback);

    /**
     * Requests a ride on behalf of an user given their desired product, start, and end locations.
     *
     * @param rideRequestParameters The ride request parameters.
     * @param callback The request callback.
     */
    void requestRide(RideRequestParameters rideRequestParameters, Callback<Ride> callback);

    /**
     * Update an ongoing request's destination.
     *
     * @param rideId The unique identifier of a ride.
     * @param rideUpdateParameters The ride update parameters.
     * @param callback The request callback.
     */
    void updateRide(@Nonnull String rideId,
            @Nonnull RideUpdateParameters rideUpdateParameters,
            Callback<Void> callback);

    /**
     * Gets the current ride an user is on.
     *
     * @param callback The request callback.
     */
    void getCurrentRide(Callback<Ride> callback);

    /**
     * Cancels the current ride of a user.
     *
     * @param callback The request callback.
     */
    void cancelCurrentRide(Callback<Void> callback);

    /**
     * Gets information about a user's {@link Place}.
     *
     * @param placeId The identifier of a Place.
     * @param callback The request callback.
     */
    void getPlace(@Nonnull String placeId, Callback<Place> callback);

    /**
     * Gets information about a user's {@link Place}.
     *
     * @param place One of the defined user {@link Places}.
     * @param callback The request callback.
     */
    void getPlace(@Nonnull Places place, Callback<Place> callback);

    /**
     * Sets information about a user's {@link Place}.
     *
     * @param placeId The identifier of a Place.
     * @param callback The request callback.
     */
    void setPlace(@Nonnull String placeId, @Nonnull String address, Callback<Place> callback);

    /**
     * Sets information about a user's {@link Place}.
     *
     * @param place One of the defined user {@link Places}.
     * @param callback The request callback.
     */
    void setPlace(@Nonnull Places place, @Nonnull String address, Callback<Place> callback);

    /**
     * Gets details about a specific ride.
     *
     * @param rideId The unique identifier for a ride.
     * @param callback The request callback.
     */
    void getRideDetails(@Nonnull String rideId, Callback<Ride> callback);

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
    void estimateRide(RideRequestParameters rideRequestParameters,
            Callback<RideEstimate> callback);

    /**
     * Get a map with a visual representation of a ride for tracking purposes.
     *
     * @param rideId Unique identifier representing a ride.
     * @param callback The request callback.
     */
    void getRideMap(@Nonnull String rideId, Callback<RideMap> callback);

    /**
     * Gets the {@link PaymentMethod PaymentMethods} of user and their last used method ID.
     *
     * @param callback The request callback.
     */
    void getPaymentMethods(Callback<PaymentMethodsResponse> callback);

    /**
     * Updates the product in the {@link Environment#SANDBOX sandbox environement} to
     * simulate the possible responses the Request endpoint will return when requesting a particular
     * product, such as surge pricing and driver availability.
     *
     * @param productId The unique product ID to update.
     * @param sandboxProductRequestParameters The sandbox product request parameters.
     * @param callback The request callback.
     */
    void updateSandboxProduct(String productId,
            SandboxProductRequestParameters sandboxProductRequestParameters,
            Callback<Void> callback);

    /**
     * Updates the ride in the {@link Environment#SANDBOX
     * sandbox environement} to simulate the possible states of a ride.
     *
     * @param rideId Unique identifier representing a ride.
     * @param sandboxRideRequestParameters The sandbox ride request parameters.
     * @param callback The request callback.
     */
    void updateSandboxRide(String rideId,
            SandboxRideRequestParameters sandboxRideRequestParameters,
            Callback<Void> callback);
}
