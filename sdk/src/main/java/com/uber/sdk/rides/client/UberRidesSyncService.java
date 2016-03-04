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

import com.uber.sdk.rides.client.error.ApiException;
import com.uber.sdk.rides.client.error.NetworkException;
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

import static com.uber.sdk.rides.client.Session.Environment;

/**
 * Represents the synchronous RPC methods of the Uber API. Can be built using
 * <pre><code>
 *
 * UberRidesSyncService uberApiService = UberRidesServices.createSync(session);
 *
 * // Or
 *
 * uberApiService = UberRidesServices.sync().setSession(session).build();
 * </code></pre>
 * @see UberRidesServices.Builder#sync()
 */
public interface UberRidesSyncService extends UberRidesService {

    /**
     * Gets information about the promotion that will be available to a new user based on their
     * activity's location.
     *
     * @param startLatitude Latitude component of start location.
     * @param startLongitude Longitude component of start location.
     * @param endLatitude Latitude component of end location.
     * @param endLongitude Longitude component of end location.
     */
    Response<Promotion> getPromotions(float startLatitude,
            float startLongitude,
            float endLatitude,
            float endLongitude) throws ApiException, NetworkException;

    /**
     * Gets a limited amount of data about a user's lifetime activity.
     *
     * @param offset Offset the list of returned results by this amount. Default is zero.
     * @param limit Number of items to retrieve. Default is 5, maximum is 50.
     */
    Response<UserActivityPage> getUserActivity(@Nullable Integer offset,
            @Nullable Integer limit) throws ApiException, NetworkException;

    /**
     * Gets information about the user that has authorized with the application.
     */
    Response<UserProfile> getUserProfile() throws ApiException, NetworkException;

    /**
     * Gets an estimated price range for each product offered at a given location.
     *
     * @param startLatitude Latitude component of start location.
     * @param startLongitude Longitude component of start location.
     * @param endLatitude Latitude component of end location.
     * @param endLongitude Longitude component of end location.
     */
    Response<PriceEstimatesResponse> getPriceEstimates(float startLatitude,
            float startLongitude,
            float endLatitude,
            float endLongitude) throws ApiException, NetworkException;

    /**
     * Gets ETAs for all products offered at a given location, with the responses expressed as
     * integers in seconds.
     *
     * @param startLatitude Latitude component of start location.
     * @param startLongitude Longitude component of start location.
     * @param productId Unique identifier representing a specific product for a given latitude &amp;
     *                  longitude.
     */
    Response<TimeEstimatesResponse> getPickupTimeEstimates(float startLatitude,
            float startLongitude,
            @Nullable String productId) throws ApiException, NetworkException;


    /**
     * Gets information about the products offered at a given location.
     *
     * @param latitude Latitude component of location.
     * @param longitude Longitude component of location.
     */
    Response<ProductsResponse> getProducts(float latitude, float longitude) throws ApiException, NetworkException;

    /**
     * Gets information about a specific product.
     *
     * @param productId The unique product ID to fetch information about.
     */
    Response<Product> getProduct(@Nonnull String productId) throws ApiException, NetworkException;


    /**
     * Cancel an ongoing ride request on behalf of a rider.
     *
     * @param rideId The unique identifier of a ride.
     */
    Response<Void> cancelRide(@Nonnull String rideId) throws ApiException, NetworkException;

    /**
     * Requests a ride on behalf of a user given their desired product, start, and end locations.
     *
     * @param rideRequestParameters The ride request parameters.
     */
    Response<Ride> requestRide(RideRequestParameters rideRequestParameters) throws ApiException, NetworkException;

    /**
     * Update an ongoing request's destination.
     *
     * @param rideId The unique identifier of a ride.
     * @param rideUpdateParameters The ride update parameters.
     */
    Response<Void> updateRide(@Nonnull String rideId, @Nonnull RideUpdateParameters rideUpdateParameters)
            throws ApiException, NetworkException;

    /**
     * Gets the current ride a user is on.
     */
    Response<Ride> getCurrentRide() throws ApiException, NetworkException;

    /**
     * Cancels the current ride of a user.
     */
    Response<Void> cancelCurrentRide() throws ApiException, NetworkException;

    /**
     * Gets information about a user's Place.
     *
     * @param placeId The identifier of a Place.
     */
    Response<Place> getPlace(@Nonnull String placeId) throws ApiException, NetworkException;

    /**
     * Gets information about a user's Place.
     *
     * @param place One of the defined user Places.
     */
    Response<Place> getPlace(@Nonnull Places place) throws ApiException, NetworkException;

    /**
     * Sets information about a user's {@link Place}.
     *
     * @param placeId The identifier of a Place.
     */
    Response<Place> setPlace(@Nonnull String placeId, @Nonnull String address) throws ApiException, NetworkException;

    /**
     * Sets information about a user's {@link Place}.
     *
     * @param place One of the defined user {@link Places}.
     */
    Response<Place> setPlace(@Nonnull Places place, @Nonnull String address) throws ApiException, NetworkException;

    /**
     * Gets details about a specific ride.
     *
     * @param rideId The unique identifier of a ride.
     */
    Response<Ride> getRideDetails(@Nonnull String rideId) throws ApiException, NetworkException;

    /**
     * Get receipt information for a completed request.
     *
     * @param rideId The unique identifier of a ride.
     */
    Response<RideReceipt> getRideReceipt(@Nonnull String rideId) throws ApiException, NetworkException;

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
     */
    Response<RideEstimate> estimateRide(RideRequestParameters rideRequestParameters) throws ApiException,
            NetworkException;

    /**
     * Get a map with a visual representation of a ride for tracking purposes.
     *
     * @param rideId Unique identifier representing a ride.
     */
    Response<RideMap> getRideMap(@Nonnull String rideId) throws ApiException, NetworkException;

    /**
     * Gets the {@link PaymentMethod PaymentMethods} of user and their last used method ID.
     */
    Response<PaymentMethodsResponse> getPaymentMethods() throws ApiException, NetworkException;

    /**
     * Updates the product in the {@link Environment#SANDBOX sandbox environement}
     * to simulate the possible responses the Request endpoint will return when requesting a particular product,
     * such as surge pricing and driver availability.
     *
     * @param productId The unique product ID to update.
     * @param sandboxProductRequestParameters The sandbox product request parameters.
     */
    Response<Void> updateSandboxProduct(String productId,
            SandboxProductRequestParameters sandboxProductRequestParameters) throws ApiException, NetworkException;


    /**
     * Updates the ride in the {@link Environment#SANDBOX sandbox environement}
     * to simulate the possible states of a ride.
     *
     * @param rideId Unique identifier representing a ride request.
     * @param sandboxRideRequestParameters The sandbox ride request parameters.
     */
    Response<Void> updateSandboxRide(String rideId,
            SandboxRideRequestParameters sandboxRideRequestParameters) throws ApiException, NetworkException;

}
