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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.uber.sdk.rides.client.Callback;
import com.uber.sdk.rides.client.Response;
import com.uber.sdk.rides.client.UberRidesAsyncService;
import com.uber.sdk.rides.client.UberRidesService;
import com.uber.sdk.rides.client.UberRidesSyncService;
import com.uber.sdk.rides.client.error.ApiException;
import com.uber.sdk.rides.client.error.ClientError;
import com.uber.sdk.rides.client.error.NetworkException;
import com.uber.sdk.rides.client.error.SurgeError;
import com.uber.sdk.rides.client.error.UberError;
import com.uber.sdk.rides.client.model.PaymentMethod;
import com.uber.sdk.rides.client.model.PaymentMethodsResponse;
import com.uber.sdk.rides.client.model.Place;
import com.uber.sdk.rides.client.model.Place.Places;
import com.uber.sdk.rides.client.model.PlaceParameters;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.mime.TypedByteArray;

/**
 * An adapter uses Retrofit to back our {@link RetrofitUberRidesService} calls.
 */
public class RetrofitAdapter<T extends RetrofitUberRidesService> implements UberRidesAsyncService, UberRidesSyncService,
        UberRidesService {

    private final T service;

    /**
     * Constructor.
     * @param retrofitService The Retrofit service.
     */
    public RetrofitAdapter(T retrofitService) {
        this.service = retrofitService;
    }

    @Override
    public void getPromotions(float startLatitude,
            float startLongitude,
            float endLatitude,
            float endLongitude,
            Callback<Promotion> callback) {
        service.getPromotions(startLatitude,
                startLongitude, endLatitude,
                endLongitude, new InternalCallback<>(callback));
    }

    @Override
    public Response<Promotion> getPromotions(float startLatitude,
            float startLongitude,
            float endLatitude,
            float endLongitude) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<Promotion>> future = SettableFuture.create();

        getPromotions(startLatitude, startLongitude, endLatitude, endLongitude, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getUserActivity(@Nullable Integer offset,
            @Nullable Integer limit,
            Callback<UserActivityPage> callback) {
        service.getUserActivity(offset, limit, new InternalCallback<>(callback));
    }

    @Override
    public Response<UserActivityPage> getUserActivity(@Nullable Integer offset, @Nullable Integer limit) throws
            ApiException, NetworkException {
        final SettableFuture<ResponseOrException<UserActivityPage>> future = SettableFuture.create();

        getUserActivity(offset, limit, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getUserProfile(final Callback<UserProfile> callback) {
        service.getUserProfile(new InternalCallback<>(callback));
    }

    @Override
    public Response<UserProfile> getUserProfile() throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<UserProfile>> future = SettableFuture.create();

        getUserProfile(new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getPriceEstimates(float startLatitude,
            float startLongitude,
            float endLatitude,
            float endLongitude,
            Callback<PriceEstimatesResponse> callback) {
        service.getPriceEstimates(startLatitude, startLongitude, endLatitude, endLongitude,
                new InternalCallback<>(callback));
    }

    @Override
    public Response<PriceEstimatesResponse> getPriceEstimates(float startLatitude,
            float startLongitude,
            float endLatitude,
            float endLongitude) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<PriceEstimatesResponse>> future = SettableFuture.create();

        getPriceEstimates(startLatitude, startLongitude, endLatitude, endLongitude,
                new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getPickupTimeEstimates(float startLatitude,
            float startLongitude,
            @Nullable String productId,
            Callback<TimeEstimatesResponse> callback) {
        service.getPickupTimeEstimate(startLatitude,
                startLongitude,
                productId,
                new InternalCallback<>(callback));
    }

    @Override
    public Response<TimeEstimatesResponse> getPickupTimeEstimates(float startLatitude,
            float startLongitude,
            @Nullable String productId) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<TimeEstimatesResponse>> future = SettableFuture.create();

        getPickupTimeEstimates(startLatitude, startLongitude, productId, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getProducts(float latitude, float longitude, final Callback<ProductsResponse> callback) {
        service.getProducts(latitude, longitude, new InternalCallback<>(callback));
    }

    @Override
    public Response<ProductsResponse> getProducts(float latitude, float longitude) throws NetworkException {
        final SettableFuture<ResponseOrException<ProductsResponse>> future = SettableFuture.create();

        getProducts(latitude, longitude, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getProduct(@Nonnull String productId, Callback<Product> callback) {
        service.getProduct(productId, new InternalCallback<>(callback));
    }

    @Override
    public Response<Product> getProduct(@Nonnull String productId) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<Product>> future = SettableFuture.create();

        getProduct(productId, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void cancelRide(@Nonnull String rideId, Callback<Void> callback) {
        service.cancelRide(rideId, new InternalCallback<>(callback));
    }

    @Override
    public Response<Void> cancelRide(@Nonnull String rideId) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<Void>> future = SettableFuture.create();

        cancelRide(rideId, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void requestRide(RideRequestParameters rideRequestParameters, Callback<Ride> callback) {
        service.requestRide(rideRequestParameters, new InternalCallback<>(callback));
    }

    @Override
    public Response<Ride> requestRide(RideRequestParameters rideRequestParameters) throws ApiException,
            NetworkException {
        final SettableFuture<ResponseOrException<Ride>> future = SettableFuture.create();

        requestRide(rideRequestParameters, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void updateRide(@Nonnull String rideId,
            @Nonnull RideUpdateParameters rideUpdateParameters,
            Callback<Void> callback) {
        service.updateRide(rideId, rideUpdateParameters, new InternalCallback<>(callback));
    }

    @Override
    public Response<Void> updateRide(@Nonnull String rideId,
            @Nonnull RideUpdateParameters rideUpdateParameters) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<Void>> future = SettableFuture.create();

        updateRide(rideId, rideUpdateParameters, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getCurrentRide(Callback<Ride> callback) {
        service.getCurrentRide(new InternalCallback<>(callback));
    }

    @Override
    public Response<Ride> getCurrentRide() throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<Ride>> future = SettableFuture.create();

        getCurrentRide(new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void cancelCurrentRide(Callback<Void> callback) {
        service.cancelCurrentRide(new InternalCallback<>(callback));
    }

    @Override
    public Response<Void> cancelCurrentRide() throws ApiException, NetworkException{
        final SettableFuture<ResponseOrException<Void>> future = SettableFuture.create();

        cancelCurrentRide(new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getPlace(@Nonnull String placeId, Callback<Place> callback) {
        service.getPlace(placeId, new InternalCallback<>(callback));
    }

    @Override
    public Response<Place> getPlace(@Nonnull String placeId) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<Place>> future = SettableFuture.create();

        getPlace(placeId, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getPlace(@Nonnull Places place, Callback<Place> callback) {
        service.getPlace(place.toString(), new InternalCallback<>(callback));
    }

    @Override
    public Response<Place> getPlace(@Nonnull Places place) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<Place>> future = SettableFuture.create();

        getPlace(place, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void setPlace(@Nonnull String placeId, @Nonnull String address, Callback<Place> callback) {
        PlaceParameters placeParameters = new PlaceParameters.Builder().setAddress(address).build();

        service.setPlace(placeId, placeParameters, new InternalCallback<>(callback));
    }

    @Override
    public Response<Place> setPlace(@Nonnull String placeId, @Nonnull String address) throws ApiException,
            NetworkException {
        final SettableFuture<ResponseOrException<Place>> future = SettableFuture.create();

        setPlace(placeId, address, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void setPlace(@Nonnull Places place, @Nonnull String address, Callback<Place> callback) {
        PlaceParameters placeParameters = new PlaceParameters.Builder().setAddress(address).build();

        service.setPlace(place.toString(), placeParameters, new InternalCallback<>(callback));
    }

    @Override
    public Response<Place> setPlace(@Nonnull Places place, @Nonnull String address) throws ApiException,
            NetworkException {
        final SettableFuture<ResponseOrException<Place>> future = SettableFuture.create();

        setPlace(place, address, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getRideDetails(@Nonnull String rideId, Callback<Ride> callback) {
        service.getRideDetails(rideId, new InternalCallback<>(callback));
    }

    @Override
    public Response<Ride> getRideDetails(@Nonnull String rideId) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<Ride>> future = SettableFuture.create();

        getRideDetails(rideId, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void estimateRide(RideRequestParameters rideRequestParameters, Callback<RideEstimate> callback) {
        service.estimateRide(rideRequestParameters, new InternalCallback<>(callback));
    }

    @Override
    public Response<RideEstimate> estimateRide(RideRequestParameters rideRequestParameters) throws
            ApiException, NetworkException {
        final SettableFuture<ResponseOrException<RideEstimate>> future = SettableFuture.create();

        estimateRide(rideRequestParameters, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getRideMap(@Nonnull String rideId, Callback<RideMap> callback) {
        service.getRideMap(rideId, new InternalCallback<>(callback));
    }

    @Override
    public Response<RideMap> getRideMap(@Nonnull String rideId) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<RideMap>> future = SettableFuture.create();

        getRideMap(rideId, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void getPaymentMethods(Callback<PaymentMethodsResponse> callback) {
        service.getPaymentMethods(new InternalCallback<>(callback));
    }

    @Override
    public Response<PaymentMethodsResponse> getPaymentMethods() throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<PaymentMethodsResponse>> future = SettableFuture.create();

        getPaymentMethods(new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void updateSandboxProduct(String productId,
            SandboxProductRequestParameters sandboxProductRequestParameters,
            final Callback<Void> callback) {
        service.updateSandboxProduct(productId, sandboxProductRequestParameters, new InternalCallback<>(callback));
    }

    @Override
    public Response<Void> updateSandboxProduct(String productId,
                                               SandboxProductRequestParameters sandboxProductRequestParameters)
            throws NetworkException {
        final SettableFuture<ResponseOrException<Void>> future = SettableFuture.create();

        updateSandboxProduct(productId, sandboxProductRequestParameters, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    @Override
    public void updateSandboxRide(String rideId,
            SandboxRideRequestParameters sandboxRideRequestParameters,
            Callback<Void> callback) {
        service.updateSandboxRide(rideId, sandboxRideRequestParameters, new InternalCallback<>(callback));
    }

    @Override
    public Response<Void> updateSandboxRide(String rideId,
            SandboxRideRequestParameters sandboxRideRequestParameters) throws ApiException, NetworkException {
        final SettableFuture<ResponseOrException<Void>> future = SettableFuture.create();

        updateSandboxRide(rideId, sandboxRideRequestParameters, new SettableFutureCallback<>(future));

        return transformFuture(future);
    }

    /**
     * Callback that sets a {@code SettableFuture}.
     * @param <T> The response object type.
     */
    static class SettableFutureCallback<T> implements Callback<T> {

        private SettableFuture<ResponseOrException<T>> settableFuture;

        public SettableFutureCallback(SettableFuture<ResponseOrException<T>> settableFuture) {
            this.settableFuture = settableFuture;
        }

        @Override
        public void success(T obj, Response<T> response) {
            settableFuture.set(new ResponseOrException<T>(response));
        }

        @Override
        public void failure(NetworkException exception) {
            settableFuture.set(new ResponseOrException<T>(exception));
        }

        @Override
        public void failure(ApiException exception) {
            settableFuture.set(new ResponseOrException<T>(exception));
        }

        @Override
        public void failure(Throwable exception) {
            settableFuture.set(new ResponseOrException<T>(exception));
        }
    }

    /**
     * Retrofit callback that proxies to a library callback.
     * @param <T> The response object type.
     */
    @VisibleForTesting
    static class InternalCallback<T> implements retrofit.Callback<T> {

        private Callback<T> callback;

        private InternalCallback(Callback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void success(T t, retrofit.client.Response response) {
            callback.success(t, transformResponse(response, t));
        }

        @Override
        public void failure(RetrofitError error) {
            Throwable throwable = transformError(error);
            if (throwable instanceof ApiException) {
                callback.failure((ApiException) throwable);
            } else if (throwable instanceof NetworkException) {
                callback.failure((NetworkException) throwable);
            } else {
                callback.failure(throwable);
            }

        }

        private static <T> Response<T> transformResponse(retrofit.client.Response response, T responseObject) {
            return new Response<T>(response.getUrl(), response.getStatus(), response.getReason(),
                    Lists.transform(response.getHeaders(), transformHeader), responseObject);
        };

        private static Function<Header, com.uber.sdk.rides.client.Header> transformHeader = new Function<Header, com.uber.sdk.rides.client.Header>() {
            @Override
            public com.uber.sdk.rides.client.Header apply(Header header) {
                return new com.uber.sdk.rides.client.Header(header.getName(), header.getValue());
            }
        };

        private static Throwable transformError(RetrofitError error) {
            if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                return new NetworkException("Network error: " + error.getCause().getClass(),
                        error.getCause(), error.getUrl());
            } else if (error.getKind().equals(RetrofitError.Kind.HTTP)) {
                retrofit.client.Response response = error.getResponse();
                List<UberError> errors = getErrors(new String(((TypedByteArray) error.getResponse().getBody()).getBytes()));
                return new ApiException(error.getMessage(), error, transformResponse(response, null), errors);
            }

            return error.getCause();
        }

        @VisibleForTesting
        static List<UberError> getErrors(String body) {
            JsonParser jsonParser = new JsonParser();
            JsonObject bodyObject = jsonParser.parse(body).getAsJsonObject();

            List<UberError> errors = new ArrayList<>();

            if (bodyObject.has("errors")) {
                JsonObject meta = bodyObject.getAsJsonObject("meta");
                JsonArray errorsArray = bodyObject.getAsJsonArray("errors");
                for (int i = 0; i < errorsArray.size(); i++) {
                    JsonObject error = errorsArray.get(i).getAsJsonObject();
                    errors.add(createError(meta, error));
                }
            } else if (bodyObject.has("code")) {
                errors.add(new ClientError(bodyObject.get("code").getAsString(), bodyObject.get("message").getAsString()));
            } else if (bodyObject.has("error")) {
                errors.add(new ClientError("error", bodyObject.get("error").getAsString()));
            } else {
                errors.add(new ClientError("unknown", "unknown error, inspect response body for more information"));
            }
            return errors;
        }

        @VisibleForTesting
        static UberError createError(JsonObject meta, JsonObject error) {
            String code = error.get("code").getAsString();
            String message = error.get("title").getAsString();
            if ("surge".equals(code)) {
                JsonObject surgeConfirmationObject = meta.getAsJsonObject("surge_confirmation");
                return new SurgeError(code, message, surgeConfirmationObject.get("href").getAsString(),
                        surgeConfirmationObject.get("surge_confirmation_id").getAsString());
            } else {
                return new ClientError(code, message);
            }
        }
    }

    private static Function<Header, com.uber.sdk.rides.client.Header> transformHeader =
            new Function<Header, com.uber.sdk.rides.client.Header>() {
        @Override
        public com.uber.sdk.rides.client.Header apply(Header header) {
            return new com.uber.sdk.rides.client.Header(header.getName(), header.getValue());
        }
    };

    private static <T> Response<T> transformResponse(retrofit.client.Response response, T responseObject) {
        return new Response<T>(response.getUrl(), response.getStatus(), response.getReason(),
                Lists.transform(response.getHeaders(), transformHeader), responseObject);
    };

    private static <T> Response<T> transformFuture(SettableFuture<ResponseOrException<T>> future)
            throws NetworkException {
        final Response<T> response;
        try {
            ResponseOrException<T> responseOrException = future.get();

            if (responseOrException.exception != null) {
                if (responseOrException.exception instanceof NetworkException) {
                    throw (NetworkException) responseOrException.exception;
                } else if (responseOrException.exception instanceof ApiException) {
                    throw (ApiException) responseOrException.exception;
                } else {
                    Throwables.propagate(responseOrException.exception);
                }
            }

            response = responseOrException.response;
        } catch (InterruptedException e) {
            throw new NetworkException("Executor thread interrupted.", e, null);
        } catch (ExecutionException e) {
            throw new NetworkException("Executor thread interrupted.", e, null);
        }

        if (response != null) {
            return response;
        } else {
            throw new IllegalStateException("Response cannot be null.");
        }
    }

    private static class ResponseOrException<T> {

        @Nullable private Response<T> response;
        @Nullable private Throwable exception;

        public ResponseOrException(@Nullable Response<T> response) {
            this.response = response;
        }

        public ResponseOrException(@Nullable Throwable exception) {
            this.exception = exception;
        }
    }
}
