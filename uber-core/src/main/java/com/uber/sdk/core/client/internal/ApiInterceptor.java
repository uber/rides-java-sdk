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

import com.uber.sdk.BuildConfig;
import com.uber.sdk.core.auth.Authenticator;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiInterceptor implements Interceptor {
    static final String HEADER_ACCESS_TOKEN = "Authorization";

    static final String LIB_VERSION = BuildConfig.VERSION;
    static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    static final String HEADER_USER_AGENT = "X-Uber-User-Agent";

    public final Authenticator authenticator;

    public ApiInterceptor(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();

        requestBuilder.addHeader(HEADER_ACCEPT_LANGUAGE,
                authenticator.getSessionConfiguration().getLocale().getLanguage());

        requestBuilder.addHeader(HEADER_USER_AGENT, "Java Rides SDK v" + LIB_VERSION);

        authenticator.signRequest(requestBuilder);
        return chain.proceed(requestBuilder.build());
    }

    public static void setAuthorizationHeader(Request.Builder builder, String authorizationHeader) {
        builder.removeHeader(HEADER_ACCESS_TOKEN);
        builder.addHeader(HEADER_ACCESS_TOKEN, authorizationHeader);
    }

    public static String getAuthorizationHeader(Request request) {
        return request.header(HEADER_ACCESS_TOKEN);
    }
}
