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

package com.uber.sdk.rides.client;

import com.uber.sdk.core.auth.Authenticator;
import com.uber.sdk.rides.client.internal.ApiInterceptor;
import com.uber.sdk.rides.client.internal.RefreshAuthenticator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UberRidesApiTest {

    @Mock
    Session session;

    @Mock
    Authenticator authenticator;

    @Mock
    SessionConfiguration config;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(session.getAuthenticator()).thenReturn(authenticator);
        when(authenticator.getSessionConfiguration()).thenReturn(config);
        when(config.getLocale()).thenReturn(Locale.US);
        when(config.getEndpointHost()).thenReturn("http://api.uber.com");
    }

    @Test
    public void createLoggingInterceptor_containsLogLevel() {
        HttpLoggingInterceptor loggingInterceptor = UberRidesApi.with(session)
                .createLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT,
                HttpLoggingInterceptor.Level.BASIC);
        assertEquals(HttpLoggingInterceptor.Level.BASIC, loggingInterceptor.getLevel());
        assertEquals(HttpLoggingInterceptor.Level.BASIC, loggingInterceptor.getLevel());
    }

    @Test
    public void createClient_setsSessionAndLoggingInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        OkHttpClient client = UberRidesApi.with(session)
                .createClient(new OkHttpClient(), session, loggingInterceptor);

        assertEquals(authenticator,
                ((RefreshAuthenticator) client.authenticator()).authenticator);

        for (Interceptor interceptor : client.interceptors()) {
            if (interceptor instanceof ApiInterceptor) {
                assertEquals(authenticator, ((ApiInterceptor)interceptor).authenticator);
            } else if (interceptor instanceof HttpLoggingInterceptor){
                assertEquals(loggingInterceptor, interceptor);
            } else {
                fail("Interceptors did not match expected ones");
            }
        }
    }

    @Test
    public void setOkHttpClient_isSetAndCallsNewBuilderDuringConstruction() throws Exception {
        OkHttpClient client = mock(OkHttpClient.class);
        when(client.newBuilder()).thenReturn(new OkHttpClient.Builder());

        UberRidesApi.Builder builder = UberRidesApi.with(session).setOkHttpClient(client);
        builder.build();
        verify(client).newBuilder();
        assertEquals(client, builder.client);
    }

    @Test
    public void setOkHttpClient_whenNull_returnsNewClient() {
        UberRidesApi.Builder builder = UberRidesApi.with(session);
        builder.build();
        assertNotNull(builder.client);
    }

    @Test
    public void setLogger_isSetAfterBuild() {
        HttpLoggingInterceptor.Logger logger = HttpLoggingInterceptor.Logger.DEFAULT;
        UberRidesApi.Builder builder = UberRidesApi.with(session).setLogger(logger);
        builder.build();
        assertEquals(logger, builder.logger);
    }

    @Test
    public void setLogger_whenNull_returnsDefaultLogger() {
        UberRidesApi.Builder builder = UberRidesApi.with(session);
        builder.build();
        assertEquals(HttpLoggingInterceptor.Logger.DEFAULT, builder.logger);
    }

    @Test
    public void setLogLevel_isSetAfterBuild() {
        HttpLoggingInterceptor.Level level= HttpLoggingInterceptor.Level.BASIC;
        UberRidesApi.Builder builder = UberRidesApi.with(session).setLogLevel(level);
        builder.build();
        assertEquals(level, builder.logLevel);
    }

    @Test
    public void setLogLevel_whenNull_returnsNoLogging() {
        UberRidesApi.Builder builder = UberRidesApi.with(session);
        builder.build();
        assertEquals(HttpLoggingInterceptor.Level.NONE, builder.logLevel);
    }

    @Test
    public void build_withOnlySession_completesSuccesfully() throws Exception {
        assertNotNull(UberRidesApi.with(session).build());
    }
}