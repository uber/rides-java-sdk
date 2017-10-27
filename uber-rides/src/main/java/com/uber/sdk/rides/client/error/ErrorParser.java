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

package com.uber.sdk.rides.client.error;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import retrofit2.Response;

/**
 * Used to parse a {@link Response} to an {@link ApiError}.
 */
public final class ErrorParser {

    /**
     * Parses a {@link Response} into an {@link ApiError}.
     *
     * @param response the {@link Response} to parse.
     * @return an {@link ApiError} if parsable, or {@code null} if the response is not in error.
     */
    @Nullable
    public static ApiError parseError(@Nonnull Response<?> response) {
        if (response.isSuccessful()) {
            return null;
        }

        try {
            return parseError(response.errorBody().string(), response.code(), response.message());
        } catch (IOException e) {
            return new ApiError(null, response.code(), "Unknown Error");
        }
    }

    /**
     * Parses an error body and code into an {@link ApiError}.
     *
     * @param errorBody the error body from the response.
     * @param statusCode the status code from the response.
     * @param message the message from the response.
     * @return the parsed {@link ApiError}.
     */
    @Nonnull
    public static ApiError parseError(@Nullable String errorBody, int statusCode, @Nullable String message) {
        if (errorBody == null) {
            return new ApiError(null, statusCode, message);
        }

        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<CompatibilityApiError> oldApiErrorJsonAdapter = moshi.adapter(CompatibilityApiError.class).failOnUnknown();
        try {
             return new ApiError(oldApiErrorJsonAdapter.fromJson(errorBody), statusCode);
        } catch (IOException | JsonDataException exception) {
            // Not old type of error, move on
        }

        JsonAdapter<ApiError> apiErrorJsonAdapter = moshi.adapter(ApiError.class).failOnUnknown();
        try {
            return apiErrorJsonAdapter.fromJson(errorBody);
        } catch (IOException | JsonDataException exception) {
            return new ApiError(null, statusCode, "Unknown Error");
        }
    }
}
