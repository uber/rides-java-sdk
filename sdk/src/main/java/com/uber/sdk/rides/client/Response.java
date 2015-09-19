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

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An HTTP Response.
 */
public final class Response<T> {

    private final String url;
    private final int status;
    private final String reason;
    private final ImmutableList<Header> headers;
    private final T responseObject;

    public Response(@Nonnull String url,
            int status,
            @Nonnull String reason,
            @Nonnull List<Header> headers,
            @Nullable T responseObject) {
        this.url = url;
        this.status = status;
        this.reason = reason;
        this.headers = ImmutableList.copyOf(headers);
        this.responseObject = responseObject;
    }

    /**
     * Gets the Request URL that lead to this response.
     */
    @Nonnull
    public String getUrl() {
        return url;
    }

    /**
     * Gets the status code.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Gets the status code reason phrase.
     */
    @Nonnull
    public String getReason() {
        return reason;
    }

    /**
     * Immutable collection of headers.
     */
    @Nonnull
    public List<Header> getHeaders() {
        return headers;
    }

    /**
     * The response object. May be null if the response has no body or there was an error.
     */
    @Nullable
    public T getResponseObject() {
        return responseObject;
    }
}
