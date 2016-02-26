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

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.io.Files;
import com.google.common.util.concurrent.SettableFuture;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import com.squareup.okhttp.mockwebserver.SocketPolicy;
import com.uber.sdk.rides.auth.OAuth2Helper;
import com.uber.sdk.rides.client.Callback;
import com.uber.sdk.rides.client.Response;
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.UberRidesAsyncService;
import com.uber.sdk.rides.client.UberRidesServices;
import com.uber.sdk.rides.client.UberRidesSyncService;
import com.uber.sdk.rides.client.error.ApiException;
import com.uber.sdk.rides.client.error.ClientError;
import com.uber.sdk.rides.client.error.NetworkException;
import com.uber.sdk.rides.client.error.UberError;
import com.uber.sdk.rides.client.model.Product;
import com.uber.sdk.rides.client.model.ProductsResponse;
import com.uber.sdk.rides.client.model.Ride;
import com.uber.sdk.rides.client.model.RideEstimate;
import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.model.SandboxProductRequestParameters;
import com.uber.sdk.rides.client.model.UserProfile;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.uber.sdk.rides.client.Session.Environment.PRODUCTION;
import static com.uber.sdk.rides.client.Session.Environment.SANDBOX;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for the Uber API client.
 */
public class RetrofitUberRidesClientIntegrationTest {

    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final float START_LAT = 37.613f;
    private static final float START_LONG = -122.477f;
    private static final float END_LAT = 37.808f;
    private static final float END_LONG = -122.416f;

    @Rule public ExpectedException exception = ExpectedException.none();

    private Credential credential;
    private Session sandboxSession;
    private MockWebServer server;
    private OkHttpClient okHttpClient;
    private String endpointHost;
    private UberRidesAsyncService uberApiAsyncService;
    private UberRidesSyncService uberApiSyncService;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();

        server.start();

        endpointHost = server.url("").toString();

        credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setClientAuthentication(new ClientParametersAuthentication("CLIENT_ID", "CLIENT_SECRET"))
                .setTokenServerUrl(new GenericUrl(endpointHost))
                .build();

        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(REFRESH_TOKEN);
        credential.setExpiresInSeconds(3600L);

        sandboxSession = new Session.Builder()
                .setCredential(credential)
                .setEnvironment(SANDBOX)
                .build();

        okHttpClient = new OkHttpClient();

        uberApiAsyncService = RetrofitUberRidesClient.getUberApiService(sandboxSession,
                new OAuth2Helper(),
                UberRidesServices.LogLevel.FULL,
                endpointHost,
                okHttpClient, RetrofitUberRidesService.class);

        uberApiSyncService = (UberRidesSyncService) uberApiAsyncService;
    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    public void onGet_whenAsyncHappyPath_shouldSucceed() throws Exception {
        String body = readFile("src/test/resources/mockresponses/get_products_response");

        server.enqueue(new MockResponse().setBody(body));

        SettableFuture<SettableResponse<ProductsResponse>> future = SettableFuture.create();

        uberApiAsyncService.getProducts(37.613f, -122.477f, new SettableFutureCallback<>(future));

        SettableResponse<ProductsResponse> response = future.get(1, TimeUnit.SECONDS);

        assertProductsWithCredential(server.takeRequest(), response.response,
                response.apiException, response.networkException);
    }

    @Test
    public void onGet_whenSyncHappyPath_shouldSucceed() throws Exception {
        String body = readFile("src/test/resources/mockresponses/get_products_response");

        server.enqueue(new MockResponse().setBody(body));

        Response<ProductsResponse> response = uberApiSyncService.getProducts(37.613f, -122.477f);

        assertProductsWithCredential(server.takeRequest(), response, null, null);
    }

    private static void assertProductsWithCredential(RecordedRequest request, Response<ProductsResponse> response,
            ApiException apiException,
            NetworkException networkException) {
        assertNotNull(response);
        assertNull(apiException);
        assertNull(networkException);

        ProductsResponse productsResponse = response.getBody();
        assertNotNull(productsResponse);

        assertEquals("Request Path does not match", "/v1/products?latitude=37.613&longitude=-122.477",
                request.getPath());
        assertEquals("Authorization Header not found", "Bearer accessToken", request.getHeader("Authorization"));
        assertEquals("Request method does not match", "GET", request.getMethod());

        assertEquals("Response code does not match", HttpURLConnection.HTTP_OK, response.getStatus());
        assertEquals("Response reason does not match", "OK", response.getReason());

        List<Product> products = productsResponse.getProducts();

        assertThat("Request body does not match", products,
                Matchers.<Product>hasItems(
                        allOf(hasProperty("productId", is("04a497f5-380d-47f2-bf1b-ad4cfdcb51f2")),
                                hasProperty("displayName", is("uberX")),
                                hasProperty("description", is("uberX")),
                                hasProperty("capacity", is(4))),
                        allOf(hasProperty("productId", is("b5e6755e-05dd-44cc-903d-94bdaa7ffc78")),
                                hasProperty("displayName", is("uberXL")),
                                hasProperty("description", is("Low-cost rides for large groups")),
                                hasProperty("capacity", is(6))),
                        allOf(hasProperty("productId", is("d4abaae7-f4d6-4152-91cc-77523e8165a4")),
                                hasProperty("displayName", is("UberBLACK")),
                                hasProperty("description", is("The original Uber")),
                                hasProperty("capacity", is(4))),
                        allOf(hasProperty("productId", is("8920cb5e-51a4-4fa4-acdf-dd86c5e18ae0")),
                                hasProperty("displayName", is("UberSUV")),
                                hasProperty("description", is("Room for everyone")),
                                hasProperty("capacity", is(6))),
                        allOf(hasProperty("productId", is("4057d8cc-f0f5-43fa-b448-1cd891b0fc66")),
                                hasProperty("displayName", is("ASSIST")),
                                hasProperty("description", is("ASSIST")),
                                hasProperty("capacity", is(4))),
                        allOf(hasProperty("productId", is("2832a1f5-cfc0-48bb-ab76-7ea7a62060e7")),
                                hasProperty("displayName", is("uberWAV")),
                                hasProperty("description", is("Wheelchair Accessible Vehicles")),
                                hasProperty("capacity", is(4)))));
    }

    @Test
    public void onGet_whenAsyncInvalidProductId_shouldFail() throws Exception {
        String body = readFile("src/test/resources/mockresponses/get_product_error");
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_NOT_FOUND + " Not Found")
                .setBody(body));

        final SettableFuture<SettableResponse<Product>> future = SettableFuture.create();

        uberApiAsyncService.getProduct("thisIsNotAProductId", new SettableFutureCallback<>(future));

        SettableResponse response = future.get(1, TimeUnit.SECONDS);

        assertProductError(server.takeRequest(), response.response, response.apiException, response.networkException);
    }

    @Test
    public void onGet_whenSyncInvalidProductId_shouldFail() throws Exception {
        String body = readFile("src/test/resources/mockresponses/get_product_error");
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_NOT_FOUND + " Not Found")
                .setBody(body));

        ApiException exception = null;
        Response<Product> response = null;

        try {
            response = uberApiSyncService.getProduct("thisIsNotAProductId");
        } catch (ApiException e) {
            exception = e;
        }

        assertProductError(server.takeRequest(), response, exception, null);
    }

    private static void assertProductError(RecordedRequest request, Response<Product> response,
            ApiException apiException, NetworkException networkException) {
        assertNull(response);
        assertNull(networkException);

        assertEquals("Request path does not match", "/v1/products/thisIsNotAProductId", request.getPath());
        assertEquals("Authorization Header not found", "Bearer accessToken", request.getHeader("Authorization"));
        assertEquals("Request method does not match", "GET", request.getMethod());

        assertEquals("Response code does not match", HttpURLConnection.HTTP_NOT_FOUND,
                apiException.getResponse().getStatus());
        assertEquals("Response reason does not match", "Not Found", apiException.getResponse().getReason());

        List<UberError> errors = apiException.getErrors();
        assertEquals("More than one error", 1, errors.size());
        UberError uberError = errors.get(0);
        assertTrue("Error is not a ClientError", uberError instanceof ClientError);
        ClientError clientError = (ClientError) uberError;
        assertEquals("Error code did not match body", "not_found", clientError.getCode());
        assertEquals("Error message did not match body", "Unable to find product thisIsNotAProductId",
                clientError.getMessage());
    }

    @Test
    public void onGet_withLanguage() throws Exception {
        Session localizedSandboxSession = new Session.Builder()
                .setAcceptLanguage(new Locale("sv", "SE"))
                .setCredential(credential)
                .setEnvironment(Session.Environment.SANDBOX)
                .build();

        UberRidesSyncService localizedUberApiSyncService = RetrofitUberRidesClient.getUberApiService(
                localizedSandboxSession,
                new OAuth2Helper(),
                UberRidesServices.LogLevel.FULL,
                endpointHost,
                okHttpClient, RetrofitUberRidesService.class);

        String body = readFile("src/test/resources/mockresponses/get_localized_products_response");

        server.enqueue(new MockResponse().setBody(body));
        Response < ProductsResponse > response = localizedUberApiSyncService.getProducts(37.613f, -122.477f);

        assertLocalizedProducts(server.takeRequest(), response, null, null);
    }

    private static void assertLocalizedProducts(RecordedRequest request, Response<ProductsResponse> response,
            ApiException apiException,
            NetworkException networkException) {
        assertNotNull(response);
        assertNull(apiException);
        assertNull(networkException);

        ProductsResponse productsResponse = response.getBody();
        assertNotNull(productsResponse);

        assertEquals("Accept-Language Header not found", "sv", request.getHeader("Accept-Language"));
        assertEquals("Response code does not match", HttpURLConnection.HTTP_OK, response.getStatus());
        assertEquals("Response reason does not match", "OK", response.getReason());

        List<Product> products = productsResponse.getProducts();

        assertThat("Request body does not match", products,
                Matchers.<Product>hasItems(
                        allOf(hasProperty("productId", is("a1111c8c-c720-46c3-8534-2fcdd730040d")),
                                hasProperty("displayName", is("uberX")),
                                hasProperty("description", is("Den billigare Ubern")))));
    }

    @Test
    public void onPut_whenAsyncHappyPath_shouldSucceed() throws Exception {
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_NO_CONTENT + " NO CONTENT"));

        SandboxProductRequestParameters sandboxProductRequestParameters = new SandboxProductRequestParameters.Builder()
                .setDriversAvailable(true)
                .setSurgeMultiplier(2.4f)
                .build();

        SettableFuture<SettableResponse<Void>> future = SettableFuture.create();

        uberApiAsyncService.updateSandboxProduct("d4abaae7-f4d6-4152-91cc-77523e8165a4",
                sandboxProductRequestParameters,
                new SettableFutureCallback<>(future));

        SettableResponse<Void> response = future.get(1, TimeUnit.SECONDS);

        assertSandboxProduct(server.takeRequest(), response.response, response.apiException,
                response.networkException);
    }

    @Test
    public void onPut_whenSyncHappyPath_shouldSucceed() throws Exception {
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_NO_CONTENT + " NO CONTENT"));

        SandboxProductRequestParameters sandboxProductRequestParameters = new SandboxProductRequestParameters.Builder()
                .setDriversAvailable(true)
                .setSurgeMultiplier(2.4f)
                .build();

        Response<Void> response = uberApiSyncService.updateSandboxProduct("d4abaae7-f4d6-4152-91cc-77523e8165a4",
                sandboxProductRequestParameters);

        assertSandboxProduct(server.takeRequest(), response, null, null);
    }

    private static void assertSandboxProduct(
            RecordedRequest request, Response<Void> response,
                                             ApiException apiException,
                                             NetworkException networkException) {
        assertNotNull(response);
        assertNull(response.getBody());
        assertNull(apiException);
        assertNull(networkException);

        assertEquals("Request Path does not match", "/v1/sandbox/products/d4abaae7-f4d6-4152-91cc-77523e8165a4",
                request.getPath());
        assertEquals("Authorization Header not found", "Bearer accessToken", request.getHeader("Authorization"));
        assertEquals("Request method does not match", "PUT", request.getMethod());
        assertEquals("Request body does not match", "{\"surge_multiplier\":2.4,\"drivers_available\":true}",
                request.getBody().readUtf8());

        assertEquals("Response code does not match", HttpURLConnection.HTTP_NO_CONTENT, response.getStatus());
        assertEquals("Response reason does not match", "NO CONTENT", response.getReason());
    }

    @Test
    public void onPut_whenAsyncInvalidProductId_shouldFail() throws Exception {
        String body = readFile("src/test/resources/mockresponses/put_sandbox_params_no_product_id_response");

        server.enqueue(new MockResponse().setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_NOT_FOUND + " Not Found")
                .setBody(body));

        SandboxProductRequestParameters sandboxProductRequestParameters = new SandboxProductRequestParameters.Builder()
                .setDriversAvailable(true)
                .setSurgeMultiplier(2.4f)
                .build();

        SettableFuture<SettableResponse<Void>> future = SettableFuture.create();

        uberApiAsyncService.updateSandboxProduct("thisIsNotAProductId", sandboxProductRequestParameters,
                new SettableFutureCallback<>(future));

        SettableResponse<Void> response = future.get(1, TimeUnit.SECONDS);

        assertSandboxProductError(server.takeRequest(), response.response, response.apiException,
                response.networkException);

    }

    @Test
    public void onPut_whenSyncInvalidProductId_shouldFail() throws Exception {
        String body = readFile("src/test/resources/mockresponses/put_sandbox_params_no_product_id_response");

        server.enqueue(new MockResponse().setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_NOT_FOUND + " Not Found")
                .setBody(body));

        SandboxProductRequestParameters sandboxProductRequestParameters = new SandboxProductRequestParameters.Builder()
                .setDriversAvailable(true)
                .setSurgeMultiplier(2.4f)
                .build();

        ApiException exception = null;
        Response<Void> response = null;

        try {
            response = uberApiSyncService.updateSandboxProduct("thisIsNotAProductId", sandboxProductRequestParameters);
        } catch (ApiException e) {
            exception = e;
        }

        assertSandboxProductError(server.takeRequest(), response, exception, null);
    }

    private static void assertSandboxProductError(RecordedRequest request, Response<Void> response,
            ApiException apiException,
            NetworkException networkException) {
        assertNull(response);
        assertNull(networkException);

        assertEquals("Request path does not match", "/v1/sandbox/products/thisIsNotAProductId", request.getPath());
        assertEquals("Authorization Header not found", "Bearer accessToken", request.getHeader("Authorization"));
        assertEquals("Request method does not match", "PUT", request.getMethod());

        assertEquals("Response code does not match", HttpURLConnection.HTTP_NOT_FOUND,
                apiException.getResponse().getStatus());
        assertEquals("Response reason does not match", "Not Found", apiException.getResponse().getReason());
    }

    @Test
    public void onPost_whenAsyncHappyPath_shouldSucceed() throws Exception {
        String body = readFile("src/test/resources/mockresponses/post_ride_request");
        server.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_ACCEPTED + " Accepted")
                .setBody(body));


        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder()
                .setPickupCoordinates(START_LAT, START_LONG)
                .setDropoffCoordinates(END_LAT, END_LONG)
                .setProductId("d4abaae7-f4d6-4152-91cc-77523e8165a4")
                .build();

        SettableFuture<SettableResponse<Ride>> future = SettableFuture.create();

        uberApiAsyncService.requestRide(rideRequestParameters, new SettableFutureCallback<>(future));

        SettableResponse<Ride> response = future.get(1, TimeUnit.SECONDS);

        assertRideRequest(server.takeRequest(), response.response, null, null);
    }

    @Test
    public void onPost_whenSyncHappyPath_shouldSucceed() throws Exception {
        String body = readFile("src/test/resources/mockresponses/post_ride_request");
        server.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_ACCEPTED + " Accepted")
                .setBody(body));

        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder()
                .setPickupCoordinates(START_LAT, START_LONG)
                .setDropoffCoordinates(END_LAT, END_LONG)
                .setProductId("d4abaae7-f4d6-4152-91cc-77523e8165a4")
                .build();

        Response<Ride> response = uberApiSyncService.requestRide(rideRequestParameters);

        assertRideRequest(server.takeRequest(), response, null, null);
    }

    private static void assertRideRequest(RecordedRequest request, Response<Ride> response, ApiException apiException,
            NetworkException networkException) {
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertNull(apiException);
        assertNull(networkException);

        assertEquals("Request Path does not match", "/v1/requests",
                request.getPath());
        assertEquals("Authorization Header not found", "Bearer accessToken", request.getHeader("Authorization"));
        assertEquals("Request method does not match", "POST", request.getMethod());
        assertEquals("Request body does not match",
                "{\"product_id\":\"d4abaae7-f4d6-4152-91cc-77523e8165a4\",\"start_latitude\":37.613,\"start_longitude\":-122.477,\"end_latitude\":37.808,\"end_longitude\":-122.416}",
                request.getBody().readUtf8());

        assertNotNull(response);
        assertEquals("Response code does not match", HttpURLConnection.HTTP_ACCEPTED, response.getStatus());
        assertEquals("Response reason does not match", "Accepted", response.getReason());

        assertThat("Request body does not match", response.getBody(),
                allOf(hasProperty("rideId", is("1033c525-746e-48ab-8eee-ab983e039e78")),
                        hasProperty("status", is("processing")),
                        hasProperty("driver", nullValue()),
                        hasProperty("eta", is(13)),
                        hasProperty("surgeMultiplier", is(1.0f)),
                        hasProperty("location", nullValue()),
                        hasProperty("vehicle", nullValue())));

    }

    @Test
    public void onPost_whenAysncInvalidProductId_shouldFail() throws Exception {
        String body = readFile("src/test/resources/mockresponses/post_ride_error");
        server.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_NOT_FOUND + " Not Found")
                .setBody(body));

        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder()
                .setPickupCoordinates(START_LAT, START_LONG)
                .setDropoffCoordinates(END_LAT, END_LONG)
                .setProductId("thisIsNotAProductId")
                .build();

        SettableFuture<SettableResponse<Ride>> future = SettableFuture.create();

        uberApiAsyncService.requestRide(rideRequestParameters, new SettableFutureCallback<>(future));

        SettableResponse response = future.get(1, TimeUnit.SECONDS);

        assertRideRequestError(server.takeRequest(), response.response, response.apiException,
                response.networkException);
    }

    @Test
    public void onPost_whenSyncInvalidProductId_shouldFail() throws Exception {
        String body = readFile("src/test/resources/mockresponses/post_ride_error");
        server.enqueue(new MockResponse()
                .setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_NOT_FOUND + " Not Found")
                .setBody(body));

        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder()
                .setPickupCoordinates(START_LAT, START_LONG)
                .setDropoffCoordinates(END_LAT, END_LONG)
                .setProductId("thisIsNotAProductId")
                .build();

        ApiException exception = null;
        Response<Ride> response = null;

        try {
            response = uberApiSyncService.requestRide(rideRequestParameters);
        } catch (ApiException e) {
            exception = e;
        }

        assertRideRequestError(server.takeRequest(), response, exception, null);
    }

    private static void assertRideRequestError(RecordedRequest request, Response<Ride> response,
            ApiException apiException, NetworkException networkException) {
        assertNull(response);
        assertNull(networkException);

        assertEquals("Request path does not match", "/v1/requests", request.getPath());
        assertEquals("Authorization Header not found", "Bearer accessToken", request.getHeader("Authorization"));
        assertEquals("Request method does not match", "POST", request.getMethod());

        assertEquals("Response code does not match", HttpURLConnection.HTTP_NOT_FOUND,
                apiException.getResponse().getStatus());
        assertEquals("Response reason does not match", "Not Found", apiException.getResponse().getReason());

        List<UberError> errors = apiException.getErrors();
        assertEquals("More than one error", 1, errors.size());
        UberError uberError = errors.get(0);
        assertTrue("Error is not a ClientError", uberError instanceof ClientError);
        ClientError clientError = (ClientError) uberError;
        assertEquals("Error code did not match body", "not_found", clientError.getCode());
        assertEquals("Error message did not match body",
                "Invalid product \"thisIsNotAProductId\". "
                        + "Available: d4abaae7-f4d6-4152-91cc-77523e8165a4, 8920cb5e-51a4-4fa4-acdf-dd86c5e18ae0, "
                        + "04a497f5-380d-47f2-bf1b-ad4cfdcb51f2, 2832a1f5-cfc0-48bb-ab76-7ea7a62060e7, "
                        + "4057d8cc-f0f5-43fa-b448-1cd891b0fc66, b5e6755e-05dd-44cc-903d-94bdaa7ffc78",
                clientError.getMessage());
    }

    @Test
    public void onDelete_whenAsyncHappyPath_shouldSucceed() throws Exception {
        server.enqueue(new MockResponse().setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_NO_CONTENT + " NO CONTENT"));

        String rideId = "542498f1-5096-418e-96c0-0cba096cb70c";

        SettableFuture<SettableResponse<Void>> future = SettableFuture.create();

        uberApiAsyncService.cancelRide(rideId, new SettableFutureCallback<>(future));

        SettableResponse<Void> response = future.get(1, TimeUnit.SECONDS);

        assertDeleteRide(server.takeRequest(), response.response);
    }

    private static void assertDeleteRide(RecordedRequest request, Response<Void> response) {
        assertNotNull(request);
        assertNotNull(response);

        assertEquals("Request Path does not match",
                "/v1/requests/542498f1-5096-418e-96c0-0cba096cb70c",
                request.getPath());
        assertEquals("Authorization Header not found", "Bearer accessToken", request.getHeader("Authorization"));
        assertEquals("Request method does not match", "DELETE", request.getMethod());

        assertEquals("Response code does not match", HttpURLConnection.HTTP_NO_CONTENT, response.getStatus());
        assertEquals("Response reason does not match", "NO CONTENT", response.getReason());
    }

    @Test
    public void onCall_whenAsyncCredentialNeedsRefresh_shouldRefreshCredential() throws Exception {
        String refreshResponseBody = readFile("src/test/resources/mockresponses/post_refresh_token_response");
        server.enqueue(new MockResponse().setBody(refreshResponseBody));

        String getProfileResponseBody = readFile("src/test/resources/mockresponses/get_user_profile_response");
        server.enqueue(new MockResponse().setBody(getProfileResponseBody));

        credential.setExpiresInSeconds(OAuth2Helper.DEFAULT_REFRESH_WINDOW - 60L);

        SettableFuture<SettableResponse<UserProfile>> future = SettableFuture.create();

        uberApiAsyncService.getUserProfile(new SettableFutureCallback<>(future));

        SettableResponse<UserProfile> response = future.get(1, TimeUnit.SECONDS);

        assertCredentialRefresh(response.response, response.responseObject, response.apiException,
                response.networkException);
    }

    @Test
    public void onCall_whenSyncCredentialNeedsRefresh_shouldRefreshCredential() throws Exception {
        String refreshResponseBody = readFile("src/test/resources/mockresponses/post_refresh_token_response");
        server.enqueue(new MockResponse().setBody(refreshResponseBody));

        String getProfileResponseBody = readFile("src/test/resources/mockresponses/get_user_profile_response");
        server.enqueue(new MockResponse().setBody(getProfileResponseBody));

        credential.setExpiresInSeconds(OAuth2Helper.DEFAULT_REFRESH_WINDOW - 60L);

        Response<UserProfile> response = uberApiSyncService.getUserProfile();

        assertCredentialRefresh(response, response.getBody(), null, null);
    }

    private void assertCredentialRefresh(Response response, UserProfile userProfile,
            ApiException apiException, NetworkException networkException)
            throws InterruptedException {
        assertNotNull(response);
        assertNotNull(userProfile);
        assertNull(apiException);
        assertNull(networkException);

        RecordedRequest refreshRequest = server.takeRequest();

        assertEquals("Refresh request body does not match",
                "grant_type=refresh_token&refresh_token=refreshToken&client_id=CLIENT_ID&client_secret=CLIENT_SECRET",
                refreshRequest.getBody().readUtf8());
        assertEquals("Refresh request method does not match", "POST", refreshRequest.getMethod());

        RecordedRequest getProfileRequest = server.takeRequest();

        assertEquals("Get Profile Request path does not match", "/v1/me", getProfileRequest.getPath());
        assertEquals("Authorization Header does not match", "Bearer accessToken2",
                getProfileRequest.getHeader("Authorization"));
        assertEquals("Get Profile Request method does not match", "GET", getProfileRequest.getMethod());

        assertEquals("accessToken2", credential.getAccessToken());
        assertEquals("refreshToken2", credential.getRefreshToken());
    }

    @Test
    public void onCall_whenAsyncServerToken_shouldSendServerToken() throws Exception {
        Session session = new Session.Builder().setEnvironment(PRODUCTION).setServerToken("serverToken").build();

        uberApiAsyncService = RetrofitUberRidesClient.getUberApiService(session,
                new OAuth2Helper(),
                UberRidesServices.LogLevel.FULL,
                endpointHost,
                okHttpClient, RetrofitUberRidesService.class);

        String body = readFile("src/test/resources/mockresponses/get_products_response");
        server.enqueue(new MockResponse().setBody(body));

        SettableFuture<SettableResponse<ProductsResponse>> future = SettableFuture.create();

        uberApiAsyncService.getProducts(37.613f, -122.477f, new SettableFutureCallback<>(future));

        SettableResponse<ProductsResponse> response = future.get(1, TimeUnit.SECONDS);

        assertProductsWithServerToken(server.takeRequest(), response.response, response.responseObject,
                response.apiException, response.networkException);
    }

    @Test
    public void onCall_whenSyncServerToken_shouldSendServerToken() throws Exception {
        Session session = new Session.Builder().setEnvironment(PRODUCTION).setServerToken("serverToken").build();

        uberApiSyncService = RetrofitUberRidesClient.getUberApiService(session,
                new OAuth2Helper(),
                UberRidesServices.LogLevel.FULL,
                endpointHost,
                okHttpClient, RetrofitUberRidesService.class);

        String body = readFile("src/test/resources/mockresponses/get_products_response");
        server.enqueue(new MockResponse().setBody(body));

        Response<ProductsResponse> response = uberApiSyncService.getProducts(37.613f, -122.477f);

        assertProductsWithServerToken(server.takeRequest(), response, response.getBody(), null, null);
    }

    private void assertProductsWithServerToken(
            RecordedRequest request, Response response,
            ProductsResponse productsResponse, ApiException apiException,
            NetworkException networkException) {
        assertNotNull(productsResponse);
        assertNotNull(response);
        assertNull(apiException);
        assertNull(networkException);

        assertEquals("Request Path does not match", "/v1/products?latitude=37.613&longitude=-122.477",
                request.getPath());
        assertEquals("Authorization Header not found", "Token serverToken", request.getHeader("Authorization"));
        assertEquals("Request method does not match", "GET", request.getMethod());

        assertEquals("Response code does not match", HttpURLConnection.HTTP_OK, response.getStatus());
        assertEquals("Response reason does not match", "OK", response.getReason());

        List<Product> products = productsResponse.getProducts();

        assertThat("Request body does not match", products,
                Matchers.<Product>hasItems(
                        allOf(hasProperty("productId", is("04a497f5-380d-47f2-bf1b-ad4cfdcb51f2")),
                                hasProperty("displayName", is("uberX")),
                                hasProperty("description", is("uberX")),
                                hasProperty("capacity", is(4))),
                        allOf(hasProperty("productId", is("b5e6755e-05dd-44cc-903d-94bdaa7ffc78")),
                                hasProperty("displayName", is("uberXL")),
                                hasProperty("description", is("Low-cost rides for large groups")),
                                hasProperty("capacity", is(6))),
                        allOf(hasProperty("productId", is("d4abaae7-f4d6-4152-91cc-77523e8165a4")),
                                hasProperty("displayName", is("UberBLACK")),
                                hasProperty("description", is("The original Uber")),
                                hasProperty("capacity", is(4))),
                        allOf(hasProperty("productId", is("8920cb5e-51a4-4fa4-acdf-dd86c5e18ae0")),
                                hasProperty("displayName", is("UberSUV")),
                                hasProperty("description", is("Room for everyone")),
                                hasProperty("capacity", is(6))),
                        allOf(hasProperty("productId", is("4057d8cc-f0f5-43fa-b448-1cd891b0fc66")),
                                hasProperty("displayName", is("ASSIST")),
                                hasProperty("description", is("ASSIST")),
                                hasProperty("capacity", is(4))),
                        allOf(hasProperty("productId", is("2832a1f5-cfc0-48bb-ab76-7ea7a62060e7")),
                                hasProperty("displayName", is("uberWAV")),
                                hasProperty("description", is("Wheelchair Accessible Vehicles")),
                                hasProperty("capacity", is(4)))));
    }

    @Test
     public void onCall_whenSyncTimeout_shouldThrowException() throws Exception {
        okHttpClient.setConnectTimeout(1, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(1, TimeUnit.MILLISECONDS);

        exception.expect(NetworkException.class);

        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

        uberApiSyncService.getUserProfile();
    }

    @Test
    public void onCall_whenAsyncTimeout_shouldThrowException() throws Exception{
        okHttpClient.setConnectTimeout(1, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(1, TimeUnit.MILLISECONDS);

        server.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE));

        SettableFuture<SettableResponse<UserProfile>> future = SettableFuture.create();

        uberApiAsyncService.getUserProfile(new SettableFutureCallback<>(future));

        SettableResponse<UserProfile> response = future.get();

        assertNotNull(response.networkException);
    }

    @Test
    public void onCall_whenSyncUnexpectedException_shouldThrowException() throws Exception {
        exception.expect(RuntimeException.class);

        server.enqueue(new MockResponse().setBody("Not JSON"));

        uberApiSyncService.getUserProfile();
    }

    @Test
    public void onCall_whenAsyncUnexpectedException_shouldThrowException() throws Exception {
        server.enqueue(new MockResponse().setBody("Not JSON"));

        SettableFuture<SettableResponse<UserProfile>> future = SettableFuture.create();

        uberApiAsyncService.getUserProfile(new SettableFutureCallback<>(future));

        SettableResponse<UserProfile> response = future.get();

        assertNotNull(response.unknownException);
    }

    @Test
    public void onCall_whenAsyncHappyPath_shouldHaveHeaders() throws Exception {
        String refreshResponseBody = readFile("src/test/resources/mockresponses/post_refresh_token_response");
        server.enqueue(new MockResponse().setBody(refreshResponseBody));

        uberApiAsyncService.getUserProfile(
                new SettableFutureCallback<>(SettableFuture.<SettableResponse<UserProfile>>create()));

        assertHeaders(server.takeRequest(1, TimeUnit.SECONDS));
    }

    @Test
    public void onCall_whenSyncHappyPath_shouldHaveHeaders() throws Exception {
        String refreshResponseBody = readFile("src/test/resources/mockresponses/post_refresh_token_response");
        server.enqueue(new MockResponse().setBody(refreshResponseBody));

        uberApiSyncService.getUserProfile();

        assertHeaders(server.takeRequest());
    }

    @Test
    public void onCall_whenAsyncRedirect_shouldAutomaticallyRedirectAndPreserveHeaders() throws Exception {
        String redirectedResponseBody = readFile("src/test/resources/mockresponses/estimate_ride_redirected");
        MockWebServer redirectServer = new MockWebServer();
        redirectServer.enqueue(new MockResponse().setBody(redirectedResponseBody));

        String redirectResponseBody = readFile("src/test/resources/mockresponses/estimate_ride_redirect");
        server.enqueue(new MockResponse()
                .setBody(redirectResponseBody)
                .setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_MOVED_TEMP + " Found")
                .setHeader("Location", redirectServer.getUrl("/v1/requests/estimate")));

        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder()
                .setPickupCoordinates(START_LAT, START_LONG)
                .setProductId("2832a1f5-cfc0-48bb-ab76-7ea7a62060e7")
                .setDropoffCoordinates(END_LAT, END_LONG).build();

        uberApiAsyncService = RetrofitUberRidesClient.getUberApiService(sandboxSession,
                new OAuth2Helper(),
                UberRidesServices.LogLevel.FULL,
                endpointHost,
                null,
                RetrofitUberRidesService.class);

        uberApiAsyncService.estimateRide(rideRequestParameters,
                new SettableFutureCallback<>(SettableFuture.<SettableResponse<RideEstimate>>create()));

        RecordedRequest firstRequest = server.takeRequest(1, TimeUnit.SECONDS);
        assertHeaders(firstRequest);
        RecordedRequest redirectedRequest = redirectServer.takeRequest(1, TimeUnit.SECONDS);
        assertHeaders(redirectedRequest);
        assertEquals("Redirected Request Line does not match",
                "POST /v1/requests/estimate HTTP/1.1",
                redirectedRequest.getRequestLine());
    }

    @Test
    public void onCall_whenSyncRedirect_shouldAutomaticallyRedirectAndPreserveHeaders() throws Exception {
        String redirectedResponseBody = readFile("src/test/resources/mockresponses/estimate_ride_redirected");
        MockWebServer redirectServer = new MockWebServer();
        redirectServer.enqueue(new MockResponse().setBody(redirectedResponseBody));

        String redirectResponseBody = readFile("src/test/resources/mockresponses/estimate_ride_redirect");
        server.enqueue(new MockResponse()
                .setBody(redirectResponseBody)
                .setStatus("HTTP/1.1 " + HttpURLConnection.HTTP_MOVED_TEMP + " Found")
                .setHeader("Location", redirectServer.getUrl("/v1/requests/estimate")));

        RideRequestParameters rideRequestParameters = new RideRequestParameters.Builder()
                .setPickupCoordinates(START_LAT, START_LONG)
                .setProductId("2832a1f5-cfc0-48bb-ab76-7ea7a62060e7")
                .setDropoffCoordinates(END_LAT, END_LONG).build();

        uberApiSyncService = RetrofitUberRidesClient.getUberApiService(sandboxSession,
                new OAuth2Helper(),
                UberRidesServices.LogLevel.FULL,
                endpointHost,
                null, RetrofitUberRidesService.class);

        uberApiSyncService.estimateRide(rideRequestParameters);

        RecordedRequest firstRequest = server.takeRequest();
        assertHeaders(firstRequest);
        RecordedRequest redirectedRequest = redirectServer.takeRequest();
        assertHeaders(redirectedRequest);
        assertEquals("Redirected Request Line does not match",
                "POST /v1/requests/estimate HTTP/1.1",
                redirectedRequest.getRequestLine());
    }

    private static void assertHeaders(RecordedRequest request) {
        assertEquals("Authorization Header not found", "Bearer accessToken", request.getHeader("Authorization"));
        assertEquals("User Agent Header not found", "Java Rides SDK v" + RetrofitUberRidesClient.LIB_VERSION,
                request.getHeader("X-Uber-User-Agent"));
    }

    private String readFile(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file = new File("sdk/" + path);
        }
        return Files.toString(file, StandardCharsets.UTF_8);
    }

    private static class SettableFutureCallback<T> implements Callback<T> {

        private SettableFuture<SettableResponse<T>> settableFuture;

        public SettableFutureCallback(SettableFuture<SettableResponse<T>> settableFuture) {
            this.settableFuture = settableFuture;
        }

        @Override
        public void success(T obj, Response<T> response) {
            settableFuture.set(new SettableResponse<>(obj, response, null, null, null));
        }

        @Override
        public void failure(NetworkException exception) {
            settableFuture.set(new SettableResponse<T>(null, null, exception, null, null));
        }

        @Override
        public void failure(ApiException exception) {
            settableFuture.set(new SettableResponse<T>(null, null, null, exception, null));
        }

        @Override
        public void failure(Throwable exception) {
            settableFuture.set(new SettableResponse<T>(null, null, null, null, exception));
        }
    }

    private static class SettableResponse<T> {

        T responseObject;
        Response response;
        NetworkException networkException;
        ApiException apiException;
        Throwable unknownException;

        public SettableResponse(T responseObject,
                Response response,
                NetworkException networkException,
                ApiException apiException,
                Throwable unknownException) {
            this.responseObject = responseObject;
            this.response = response;
            this.networkException = networkException;
            this.apiException = apiException;
            this.unknownException = unknownException;
        }
    }
}
