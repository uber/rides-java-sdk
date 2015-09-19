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

package com.uber.sdk.rides.client.error;

import com.google.common.collect.ImmutableList;
import com.uber.sdk.rides.client.Response;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an error returned by the API.
 */
public class ApiException extends RuntimeException {

    private final Response response;
    private final List<UberError> errors;

    public ApiException(String message, @Nullable Throwable exception,
            @Nonnull Response response, @Nonnull List<UberError> errors) {
        super(message, exception);
        this.response = response;
        this.errors = ImmutableList.copyOf(errors);
    }

    /**
     * Gets the response from the server
     */
    @Nonnull
    public Response getResponse() {
        return response;
    }

    /**
     * A list of errors detailing what caused this ApiException.
     */
    @Nonnull
    public List<UberError> getErrors() {
        return errors;
    }
}
