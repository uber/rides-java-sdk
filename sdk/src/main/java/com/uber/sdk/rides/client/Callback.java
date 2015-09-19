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

import com.uber.sdk.rides.client.error.ApiException;
import com.uber.sdk.rides.client.error.NetworkException;

/**
 * Communicates responses from a server.  Only one method will be called in response
 * to a given request.
 */
public interface Callback<T> {

    /**
     * Successful HTTP response.
     * @param t The model returned by the request.
     * @param response The HTTP response to the request.
     */
    void success(T t, Response<T> response);

    /**
     * Unsuccessful HTTP response due to network failure.
     */
    void failure(NetworkException exception);

    /**
     * Unsuccessful HTTP response due to a non-2XX status code.
     */
    void failure(ApiException exception);

    /**
     * An unexpected exception due that should be rethrown in your application.
     */
    void failure(Throwable exception);
}
