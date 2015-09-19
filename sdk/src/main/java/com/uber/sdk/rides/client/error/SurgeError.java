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

/**
 * An error caused by attempting to make a request without having
 * confirmed relevant surge pricing
 */
public class SurgeError extends ClientError {

    private String href;
    private String surgeConfirmationId;

    public SurgeError(String code, String message, String href, String surgeConfirmationId) {
        super(code, message);
        this.href = href;
        this.surgeConfirmationId = surgeConfirmationId;
    }

    /**
     * A link to send the user to for accepting surge pricing.
     */
    public String getHref() {
        return href;
    }

    /**
     * The confirmation ID to be added to ride requests once the user has confirmed the pricing.
     */
    public String getSurgeConfirmationId() {
        return surgeConfirmationId;
    }
}
