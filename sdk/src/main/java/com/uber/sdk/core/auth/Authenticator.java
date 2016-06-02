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

package com.uber.sdk.core.auth;

import com.uber.sdk.rides.client.SessionConfiguration;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public interface Authenticator {

    /**
     * Indicates whether this authenticator can be refreshed.
     *
     * @return
     */
    boolean isRefreshable();

    /**
     * Add authentication header required to the request.
     *
     * @param builder
     */
    void signRequest(Request.Builder builder);

    /**
     * Refresh authentication token that is used to {@link #signRequest(Request.Builder)}
     *
     * @param response
     * @throws IOException
     */
    Request refresh(Response response) throws IOException;

    /**
     * Get {@link SessionConfiguration} used for providing signining information for requests
     */
    SessionConfiguration getSessionConfiguration();
}
