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

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ErrorParserTest {

    @Test
    public void parseError_whenSuccessfulResponse_shouldReturnNull() {
        ApiError apiError = ErrorParser.parseError(null, 404, "Not Found");
        assertThat(apiError.getMeta()).isNull();

        assertError(apiError.getClientErrors(), null, 404, "Not Found");
    }

    @Test
    public void parseError_whenOldError_shouldReturnProperApiError() {
        String body = "{\"message\":\"Unable to find product thisIsNotAProductId\",\"code\":\"not_found\"}";
        ApiError apiError = ErrorParser.parseError(body, 404, "Not Found");
        assertThat(apiError.getMeta()).isNull();
        assertError(apiError.getClientErrors(), "not_found", 404, "Unable to find product thisIsNotAProductId");
    }

    @Test
    public void parseError_whenErrorWithEmptyMeta_shouldReturnErrorWithMetaButNoCofirmation() {
        String body = "{\"meta\":{},\"errors\":[{\"status\":404,\"code\":\"unknown_place_id\",\"title\":\"Only "
                + "\\\"home\\\" and \\\"work\\\" are allowed.\"}]}";
        ApiError apiError = ErrorParser.parseError(body, 404, "Not Found");
        assertThat(apiError.getMeta().getSurgeConfirmation()).isNull();
        assertError(apiError.getClientErrors(), "unknown_place_id", 404, "Only \"home\" and \"work\" are allowed.");
    }

    @Test
    public void parseError_whenErrorWithSurge_shouldReturnFullError() {
        String body = "{\"meta\":{\"surge_confirmation\":{\"href\":\"https:\\/\\/api.uber.com\\/v1\\/surge-confirmations\\/a9ca7bf4-315c-4a8d-86a4-1697b7b94de4\",\"expires_at\":1464118311,\"multiplier\":2.1,\"surge_confirmation_id\":\"a9ca7bf4-315c-4a8d-86a4-1697b7b94de4\"}},\"errors\":[{\"status\":409,\"code\":\"surge\",\"title\":\"Surge pricing is currently in effect for this product.\"}]}";
        ApiError apiError = ErrorParser.parseError(body, 409, "Unavailable");

        SurgeConfirmation surgeConfirmation = apiError.getMeta().getSurgeConfirmation();
        assertThat(surgeConfirmation.getHref()).isEqualTo("https://api.uber.com/v1/surge-confirmations/a9ca7bf4-315c-4a8d-86a4-1697b7b94de4");
        assertThat(surgeConfirmation.getMultiplier()).isEqualTo(2.1f);
        assertThat(surgeConfirmation.getExpiresAt()).isEqualTo(1464118311);
        assertThat(surgeConfirmation.getSurgeConfirmationId()).isEqualTo("a9ca7bf4-315c-4a8d-86a4-1697b7b94de4");

        assertError(apiError.getClientErrors(), "surge", 409, "Surge pricing is currently in effect for this product.");
    }

    @Test
    public void parseError_whenUnknownError_shouldReturnKnownError() {
        String body = "{\"error\":\"This is not a supported Error\",\"random\":\"random field\"}";
        ApiError apiError = ErrorParser.parseError(body, 416, "ThisWasTheHttpMessage");

        assertError(apiError.getClientErrors(), null, 416, "Unknown Error");
    }

    private void assertError(List<ClientError> clientErrors, String code, int status, String title) {
        assertThat(clientErrors).hasSize(1);
        ClientError clientError =  clientErrors.get(0);
        assertThat(clientError.getCode()).isEqualTo(code);
        assertThat(clientError.getStatus()).isEqualTo(status);
        assertThat(clientError.getTitle()).isEqualTo(title);
    }
}
