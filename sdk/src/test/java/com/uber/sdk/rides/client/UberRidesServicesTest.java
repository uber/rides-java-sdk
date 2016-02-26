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

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import static com.uber.sdk.rides.client.Session.Environment.PRODUCTION;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests {@link UberRidesServices}.
 */
public class UberRidesServicesTest {

    private static final GenericUrl TOKEN_SERVER_URL = new GenericUrl("https://login.uber.com/oauth/token");

    @Rule public ExpectedException exception = ExpectedException.none();

    private Credential credential;
    private Session session;

    @Before
    public void setUp() throws Exception {
        credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new NetHttpTransport())
                .setJsonFactory(new JacksonFactory())
                .setClientAuthentication(new ClientParametersAuthentication("CLIENT_ID", "CLIENT_SECRET"))
                .setTokenServerUrl(TOKEN_SERVER_URL)
                .build();

        credential.setAccessToken("accessToken");
        credential.setRefreshToken("refreshToken");
        credential.setExpiresInSeconds(3600L);

        session = new Session.Builder().setEnvironment(PRODUCTION).setCredential(credential).build();
    }

    @Test
    public void buildUberApiSyncService_whenSessionIsSupplied_shouldSucceed() throws Exception {
        UberRidesServices.Builder.sync().setSession(session).build();
    }

    @Test
    public void buildUberApiAsyncService_whenSessionIsSupplied_shouldSucceed() throws Exception {
        UberRidesServices.Builder.async().setSession(session).build();
    }

    @Test
    public void buildUberApiSyncServiceWithStatic_whenSessionIsSupplied_shouldSucceed() throws Exception {
        UberRidesSyncService uberApiSyncService = UberRidesServices.createSync(session);
    }

    @Test
    public void buildUberApiAsyncServiceWithStatic_whenSessionIsSupplied_shouldSucceed() throws Exception {
        UberRidesAsyncService uberApiAsyncService = UberRidesServices.createAsync(session);
    }

    @Test
    public void buildUberApiSyncService_whenNoSessionSupplied_shouldFail() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("A session is required to create a service.");

        UberRidesServices.Builder.sync().build();
    }

    @Test
    public void buildUberApiAsyncService_whenNoSessionSupplied_shouldFail() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("A session is required to create a service.");

        UberRidesServices.Builder.async().build();
    }

    @Test
    public void buildUberApiSyncService_whenSessionIsNull_shouldFail() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("A session is required to create a service.");

        UberRidesServices.Builder.sync().setSession(null).build();
    }

    @Test
    public void buildUberApiAsyncService_whenSessionIsNull_shouldFail() throws Exception {
        exception.expect(NullPointerException.class);
        exception.expectMessage("A session is required to create a service.");

        UberRidesServices.Builder.async().setSession(null).build();
    }

    @Test
    public void buildUberApiSyncService_whenNoLogLevel_shouldDefaultToNone() {
        UberRidesServices uberRidesServices = UberRidesServices.Builder.sync().setSession(session).buildUberApiServices();

        assertEquals(UberRidesServices.LogLevel.NONE, uberRidesServices.getLogLevel());
    }

    @Test
    public void buildUberApiAsyncService_whenNoLogLevel_shouldDefaultToNone() {
        UberRidesServices uberRidesServices = UberRidesServices.Builder.async().setSession(session).buildUberApiServices();

        assertEquals(UberRidesServices.LogLevel.NONE, uberRidesServices.getLogLevel());
    }

    @Test
    public void buildUberApiSyncService_whenNullLogLevel_shouldDefaultToNone() {
        UberRidesServices uberRidesServices = UberRidesServices.Builder.sync()
                .setSession(session)
                .setLogLevel(null)
                .buildUberApiServices();

        assertEquals(UberRidesServices.LogLevel.NONE, uberRidesServices.getLogLevel());
    }

    @Test
    public void buildUberApiAsyncService_whenNullLogLevel_shouldDefaultToNone() {
        UberRidesServices uberRidesServices = UberRidesServices.Builder.async()
                .setSession(session)
                .setLogLevel(null)
                .buildUberApiServices();

        assertEquals(UberRidesServices.LogLevel.NONE, uberRidesServices.getLogLevel());
    }

    @Test
    public void serviceMethodParity() {
        List<String> syncMethods = Lists.transform(
                Arrays.asList(UberRidesSyncService.class.getMethods()),
                new Function<Method, String>() {
                    @Nullable
                    @Override
                    public String apply(Method input) {
                        return input.getName();
                    }
                });

        List<String> asyncMethods = Lists.transform(
                Arrays.asList(UberRidesAsyncService.class.getMethods()),
                new Function<Method, String>() {
                    @Nullable
                    @Override
                    public String apply(Method input) {
                        return input.getName();
                    }
                });

        assertThat(syncMethods, is(asyncMethods));
    }
}
