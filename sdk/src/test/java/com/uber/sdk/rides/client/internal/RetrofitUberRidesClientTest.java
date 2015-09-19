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
import com.google.api.client.auth.oauth2.Credential;
import com.google.common.reflect.Reflection;
import com.uber.sdk.rides.auth.OAuth2Helper;
import com.uber.sdk.rides.client.Callback;
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.UberRidesAsyncService;
import com.uber.sdk.rides.client.UberRidesServices;
import com.uber.sdk.rides.client.UberRidesSyncService;
import com.uber.sdk.rides.client.model.SandboxProductRequestParameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import retrofit.RestAdapter;
import retrofit.http.PUT;
import retrofit.http.Path;

import static org.mockito.Mockito.mock;

public class RetrofitUberRidesClientTest {

    @Rule public ExpectedException exception = ExpectedException.none();

    private Credential credential;

    @Before
    public void setUp() throws Exception {
        credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod()).build();
    }

    @Test
    public void onSandboxCall_whenAsyncInProductionEnvironment_shouldThrowException() throws Exception {
        Session session = new Session.Builder()
                .setCredential(credential)
                .setEnvironment(Session.Environment.PRODUCTION)
                .build();

        UberRidesAsyncService uberApiAsyncService = RetrofitUberRidesClient.getUberApiService(session,
                new OAuth2Helper(),
                UberRidesServices.LogLevel.FULL);

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Sandbox only methods can't be called in production.");

        Callback<Void> callback = mock(Callback.class);

        uberApiAsyncService.updateSandboxProduct("thisIsNotAProductId",
                new SandboxProductRequestParameters.Builder().build(),callback);
    }

    @Test
    public void onSandboxCall_whenSyncInProductionEnvironment_shouldThrowException() throws Exception {
        Session session = new Session.Builder()
                .setCredential(credential)
                .setEnvironment(Session.Environment.PRODUCTION)
                .build();

        UberRidesSyncService uberApiSyncService = RetrofitUberRidesClient.getUberApiService(session,
                new OAuth2Helper(),
                UberRidesServices.LogLevel.FULL);

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Sandbox only methods can't be called in production.");

        uberApiSyncService.updateSandboxProduct("thisIsNotAProductId",
                new SandboxProductRequestParameters.Builder().build());
    }

    @Test
    public void onCreateService_whenDuplicateMethodNames_shouldThrowException() throws Exception {
        Session session = new Session.Builder().setCredential(credential).build();

        RestAdapter restAdapter = mock(RestAdapter.class);

        DuplicateNameUberApiService service = Reflection.newProxy(DuplicateNameUberApiService.class,
                new RetrofitUberRidesClient.InvocationHandler<>(session.getEnvironment(),
                        DuplicateNameUberApiService.class, restAdapter));

        exception.expect(IllegalStateException.class);
        exception.expectMessage("Services may not contain duplicate names.");

        Callback<Void> callback = mock(Callback.class);

        service.duplicateMethod("thisIsNotAProductId", callback);
    }

    private interface DuplicateNameUberApiService {

        @PUT("/not/a/real/path/{product_id}")
        void duplicateMethod(@Path("product_id") String productId, Callback<Void> callback);

        @PUT("/not/a/real/path/{product_id}")
        void duplicateMethod(@Path("product_id") double productId, Callback<Void> callback);
    }
}
