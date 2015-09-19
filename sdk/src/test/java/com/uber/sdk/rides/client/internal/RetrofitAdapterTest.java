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

package com.uber.sdk.rides.client.internal;

import com.google.common.io.Files;
import com.uber.sdk.rides.client.error.ClientError;
import com.uber.sdk.rides.client.error.SurgeError;
import com.uber.sdk.rides.client.error.UberError;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.uber.sdk.rides.client.internal.RetrofitAdapter.InternalCallback.getErrors;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RetrofitAdapterTest {

    @Test
    public void onGetErrors_whenSimpleError_parseSingleClientError() throws Exception {
        String errorBody = readFile("src/test/resources/mockresponses/simple_error_body");

        List<UberError> errors = getErrors(errorBody);
        assertEquals("Parsed more than one error", 1, errors.size());
        UberError uberError = errors.get(0);
        assertTrue("Error is not a ClientError", uberError instanceof ClientError);
        ClientError clientError = (ClientError) uberError;
        assertEquals("Error code did not match body", "not_found", clientError.getCode());
        assertEquals("Error message did not match body", "Unable to find product notAProductId", clientError.getMessage());
    }

    @Test
    public void onGetErrors_whenSurgeError_parseSurgeError() throws IOException {
        String errorBody = readFile("src/test/resources/mockresponses/surge_error_body");

        List<UberError> errors = getErrors(errorBody);
        assertEquals("Parsed more than one error", 1, errors.size());
        UberError uberError = errors.get(0);
        assertTrue(uberError instanceof SurgeError);
        SurgeError surgeError = (SurgeError) uberError;
        assertEquals("Error code did not match body", "surge", surgeError.getCode());
        assertEquals("Error message did not match body", "Surge pricing is currently in effect for this product.",
                surgeError.getMessage());
        assertEquals("SurgeConfirmation href did not match", "https://api.uber.com/v1/surge-confirmations/e100a670",
                surgeError.getHref());
        assertEquals("SurgeConfirmatoin id did not match", "e100a670",
                surgeError.getSurgeConfirmationId());
    }

    private String readFile(String path) throws IOException {
        return Files.toString(new File(path), StandardCharsets.UTF_8);
    }
}
