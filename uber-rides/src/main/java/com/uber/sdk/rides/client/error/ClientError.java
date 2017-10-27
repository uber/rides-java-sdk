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

import javax.annotation.Nullable;

/**
 * A specific error describing what went wrong.
 */
public final class ClientError {

    @Nullable
    private final String code;
    private final int status;
    @Nullable
    private final String title;

    public ClientError(@Nullable String code, int status, @Nullable String title) {
        this.code = code;
        this.status = status;
        this.title = title;
    }

    /**
     * @return the code for a specific endpoint error.
     * See endpoint <a href="https://developer.uber.com/docs">documentation</a>
     * for possible values and possible resolutions.
     */
    @Nullable
    public String getCode() {
        return code;
    }

    /**
     * @return the HTTP status code.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @return the description of the error.
     */
    @Nullable
    public String getTitle() {
        return title;
    }
}
