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

package com.uber.sdk.rides.auth;

import com.uber.sdk.core.auth.Authenticator;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.internal.ApiInterceptor;

import okhttp3.Request;
import okhttp3.Response;

public class ServerTokenAuthenticator implements Authenticator {
    static final String HEADER_TOKEN_ACCESS_VALUE = "Token %s";

    private final SessionConfiguration sessionConfiguration;

    public ServerTokenAuthenticator(SessionConfiguration sessionConfiguration) {
        this.sessionConfiguration = sessionConfiguration;
    }

    @Override
    public void signRequest(Request.Builder builder) {
        ApiInterceptor.setAuthorizationHeader(builder,
                String.format(HEADER_TOKEN_ACCESS_VALUE, sessionConfiguration.getServerToken()));
    }

    @Override
    public Request refresh(Response response) {
        return null;
        //Do nothing, server token is not refreshable
    }

    @Override
    public boolean isRefreshable() {
        return false;
    }

    /**
     * Get {@link SessionConfiguration} used for authentication
     */
    @Override
    public SessionConfiguration getSessionConfiguration() {
        return sessionConfiguration;
    }
}
