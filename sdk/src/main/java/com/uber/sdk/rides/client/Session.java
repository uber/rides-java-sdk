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

package com.uber.sdk.rides.client;

import com.uber.sdk.core.auth.Authenticator;

import javax.annotation.Nonnull;

/**
 * A session containing the details of how an {@link UberRidesApi} will interact with the API.
 * Does authentication through either a server token or OAuth 2.0 credential, exactly one of which must exist.
 */
public abstract class Session<T extends Authenticator> {

    private final T authenticator;

    /**
     * @param authenticator used to sign outgoing requests
     */
    public Session(@Nonnull T authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * Gets the {@link Authenticator} provided after authentication is completed.
     * This is used to sign outgoing requests.
     */
    public T getAuthenticator() {
        return authenticator;
    }
}
