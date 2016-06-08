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

package com.uber.sdk.rides.client.internal;

import com.uber.sdk.core.auth.Authenticator;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

public class RefreshAuthenticator implements okhttp3.Authenticator {

    static final String HEADER_INVALID_SCOPES = "X-Uber-Missing-Scopes";
    static final int MAX_RETRIES = 3;
    public final Authenticator authenticator;

    public RefreshAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (authenticator.isRefreshable() && canRefresh(response) && canRetry(response)) {
            return authenticator.refresh(response);
        }

        return null;
    }

    /**
     * The Uber API returns invalid scopes as 401's and will migrate to 403's in the future.
     * This is a temporary measure and will be updated in the future.
     *
     * @param response to check for {@link RefreshAuthenticator#HEADER_INVALID_SCOPES} header.
     * @return true if a true 401 and can refresh, otherwise false
     */
    boolean canRefresh(Response response) {
        return response.header(HEADER_INVALID_SCOPES) == null;
    }


    boolean canRetry(Response response) {
        int responseCount = 1;
        while ((response = response.priorResponse()) != null) {
            responseCount++;
        }

        return responseCount < MAX_RETRIES;
    }
}
