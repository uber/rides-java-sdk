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

package com.uber.sdk.core.client.internal;

import com.uber.sdk.core.auth.Authenticator;
import com.uber.sdk.core.client.SessionConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.Request;

import static com.uber.sdk.core.client.internal.ApiInterceptor.HEADER_ACCESS_TOKEN;
import static com.uber.sdk.core.client.internal.ApiInterceptor.LIB_VERSION;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ApiInterceptorTest {

    @Mock
    Authenticator authenticator;

    @Mock
    Interceptor.Chain chain;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testIntercept() throws Exception {
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        ApiInterceptor interceptor = new ApiInterceptor(authenticator);
        when(chain.request()).thenReturn(new Request.Builder().url("http://test").build());
        when(chain.proceed(captor.capture())).thenReturn(null);
        SessionConfiguration config = mock(SessionConfiguration.class);
        when(config.getLocale()).thenReturn(Locale.US);
        when(authenticator.getSessionConfiguration()).thenReturn(config);

        interceptor.intercept(chain);

        verify(authenticator).signRequest(any(Request.Builder.class));
        Request request = captor.getValue();
        assertEquals(Locale.US.getLanguage(), request.headers().get(ApiInterceptor.HEADER_ACCEPT_LANGUAGE));
        assertEquals("Java Rides SDK v" + LIB_VERSION, request.headers().get(ApiInterceptor.HEADER_USER_AGENT));
    }

    @Test
    public void testSetAuthorizationHeader_withExistingToken() {
        Request.Builder builder = new Request.Builder().url("http://test");
        builder.header(HEADER_ACCESS_TOKEN, "accessToken");
        ApiInterceptor.setAuthorizationHeader(builder, "accessToken2");
        Request request = builder.build();
        assertEquals("accessToken2", request.header(HEADER_ACCESS_TOKEN));
    }

    @Test
    public void testSetAuthorizationHeader_withoutExistingToken() {
        Request.Builder builder = new Request.Builder().url("http://test");
        ApiInterceptor.setAuthorizationHeader(builder, "accessToken");
        Request request = builder.build();
        assertEquals("accessToken", request.header(HEADER_ACCESS_TOKEN));
    }

    @Test
    public void testGetAuthorizationHeader() {
        Request request = new Request.Builder()
                .url("http://test")
                .header(HEADER_ACCESS_TOKEN, "accessToken")
                .build();

        assertEquals("accessToken", ApiInterceptor.getAuthorizationHeader(request));
    }

}