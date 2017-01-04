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

package com.uber.sdk.rides.client.services;

import com.uber.sdk.rides.client.SessionConfiguration;
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

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RidesService {


    /**
     * Gets information about the promotion that will be available to a new user based on their
     * activity's location.
     *
     * @param startLatitude Latitude component of start location.
     * @param startLongitude Longitude component of start location.
     * @param endLatitude Latitude component of end location.
     * @param endLongitude Longitude component of end location.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/promotions")
    Call<Promotion> getPromotions(@Query("start_latitude") float startLatitude,
                                  @Query("start_longitude") float startLongitude,
                                  @Query("end_latitude") float endLatitude,
                                  @Query("end_longitude") float endLongitude);

    /**
     * Gets a limited amount of data about a user's lifetime activity.
     *
     * @param offset Offset the list of returned results by this amount. Default is zero.
     * @param limit Number of items to retrieve. Default is 5, maximum is 50.
     *
     * @return the request {@link Call}
     * */
    @GET("/v1.2/history")
    Call<UserActivityPage> getUserActivity(@Nullable @Query("offset") Integer offset,
                         @Nullable @Query("limit") Integer limit);

    /**
     * Gets information about the user that has authorized with the application.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/me")
    Call<UserProfile> getUserProfile();

    /**
     * Gets an estimated price range for each product offered at a given location.
     *
     * @param startLatitude Latitude component of start location.
     * @param startLongitude Longitude component of start location.
     * @param endLatitude Latitude component of end location.
     * @param endLongitude Longitude component of end location.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/estimates/price")
    Call<PriceEstimatesResponse> getPriceEstimates(@Query("start_latitude") float startLatitude,
                           @Query("start_longitude") float startLongitude,
                           @Query("end_latitude") float endLatitude,
                           @Query("end_longitude") float endLongitude);

    /**
     * Gets ETAs for all products offered at a given location, with the responses expressed as
     * integers in seconds.
     *
     * @param startLatitude Latitude component of start location.
     * @param startLongitude Longitude component of start location.
     * @param productId Unique identifier representing a specific product for a given latitude &amp;
     *                  longitude.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/estimates/time")
    Call<TimeEstimatesResponse> getPickupTimeEstimate(@Query("start_latitude") float startLatitude,
                               @Query("start_longitude") float startLongitude,
                               @Nullable @Query("product_id") String productId);

    /**
     * Gets information about the products offered at a given location.
     *
     * @param latitude Latitude component of location.
     * @param longitude Longitude component of location.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/products")
    Call<ProductsResponse> getProducts(@Query("latitude") float latitude,
                     @Query("longitude") float longitude);

    /**
     * Gets information about a specific product.
     *
     * @param productId The unique product ID to fetch information about.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/products/{product_id}")
    Call<Product> getProduct(@Path("product_id") String productId);

    /**
     * Cancels an ongoing Ride for a user.
     *
     * @param rideId Unique identifier representing a Request.
     *
     * @return the request {@link Call}
     */
    @DELETE("/v1.2/requests/{request_id}")
    Call<Void> cancelRide(@Path("request_id") String rideId);

    /**
     * Requests a ride on behalf of a user given their desired product, start, and end locations.
     *
     * @param rideRequestParameters The ride request parameters.
     *
     * @return the request {@link Call}
     */
    @POST("/v1.2/requests")
    Call<Ride> requestRide(@Body RideRequestParameters rideRequestParameters);

    /**
     * Gets the current ride a user is on.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/requests/current")
    Call<Ride> getCurrentRide();

    /**
     * Cancels the current ride of a user.
     *
     * @return the request {@link Call}
     */
    @DELETE("/v1.2/requests/current")
    Call<Void> cancelCurrentRide();

    /**
     * Update an ongoing request's destination.
     *
     * @param rideUpdateParameters The ride request parameters.
     *
     * @return the request {@link Call}
     */
    @PATCH("/v1.2/requests/{request_id}")
    Call<Void> updateRide(@Nonnull @Path("request_id") String rideId,
                    @Body RideUpdateParameters rideUpdateParameters);

    /**
     * Gets information about a user's Place.
     *
     * @param placeId The identifier of a Place.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/places/{place_id}")
    Call<Place> getPlace(@Nonnull @Path("place_id") String placeId);

    /**
     * Sets information about a user's Place.
     *
     * @param placeId The identifier of a Place.
     * @param placeParameters The place parameters.
     *
     * @return the request {@link Call}
     */
    @PUT("/v1.2/places/{place_id}")
    Call<Place> setPlace(@Nonnull @Path("place_id") String placeId,
                  @Nonnull @Body PlaceParameters placeParameters);

    /**
     * Gets details about a specific ride.
     *
     * @param rideId The unique identifier for a ride.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/requests/{request_id}")
    Call<Ride> getRideDetails(@Nonnull @Path("request_id") String rideId);

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
     *
     * @return the request {@link Call}
     */
    @POST("/v1.2/requests/estimate")
    Call<RideEstimate> estimateRide(@Body RideRequestParameters rideRequestParameters);

    /**
     * Get a map with a visual representation of a ride for tracking purposes.
     *
     * Maps are only available after a ride has been accepted by a driver and is in the 'accepted' state. Attempting
     * to get a map before that will result in a 404 error.
     *
     * @param rideId Unique identifier representing a ride.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/requests/{request_id}/map")
    Call<RideMap> getRideMap(@Nonnull @Path("request_id") String rideId);

    /**
     * Get a receipt of a ride.
     *
     * @param rideId Unique identifier representing a ride.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/requests/{request_id}/receipt")
    Call<RideReceipt> getRideReceipt(@Nonnull @Path("request_id") String rideId);

    /**
     * Gets the {@link PaymentMethod PaymentMethods} of user and their last used method ID.
     *
     * @return the request {@link Call}
     */
    @GET("/v1.2/payment-methods")
    Call<PaymentMethodsResponse> getPaymentMethods();

    /**
     * Updates the product in the {@link SessionConfiguration.Environment#SANDBOX sandbox environement} to simulate the
     * possible responses the Request endpoint will return when requesting a particular product,
     * such as surge pricing and driver availability.
     *
     * Will fail when called in {@link SessionConfiguration.Environment#PRODUCTION}.
     *
     * @param productId The unique product ID to update.
     * @param sandboxProductRequestParameters The sandbox product request parameters.
     *
     * @return the request {@link Call}
     */
    @PUT("/v1.2/sandbox/products/{product_id}")
    Call<Void> updateSandboxProduct(@Path("product_id") String productId,
                                    @Body SandboxProductRequestParameters sandboxProductRequestParameters);

    /**
     * Updates the ride in the {@link SessionConfiguration.Environment#SANDBOX sandbox environement} to simulate the
     * possible states of a the Request.
     *
     * Will fail when called in {@link SessionConfiguration.Environment#PRODUCTION}.
     *
     * @param rideId Unique identifier representing a Request.
     * @param sandboxRideRequestParameters The sandbox ride request parameters.
     *
     * @return the request {@link Call}
     */
    @PUT("/v1.2/sandbox/requests/{request_id}")
    Call<Void> updateSandboxRide(@Path("request_id") String rideId,
                                 @Body SandboxRideRequestParameters sandboxRideRequestParameters);
}
