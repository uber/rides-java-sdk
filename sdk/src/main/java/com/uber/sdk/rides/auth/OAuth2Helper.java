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

package com.uber.sdk.rides.auth;

import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;

/**
 * Helper class for {@link Credential} objects.
 */
public class OAuth2Helper {

    public static final long DEFAULT_REFRESH_WINDOW = 300L;

    private final long refreshWindowSeconds;

    /**
     * Constructor.
     * @param refreshWindowSeconds The window in which the credential will be refreshed.
     */
    public OAuth2Helper(long refreshWindowSeconds) {
        this.refreshWindowSeconds = refreshWindowSeconds;
    }

    /**
     * Constructor.
     */
    public OAuth2Helper() {
        this(DEFAULT_REFRESH_WINDOW);
    }

    /**
     * Attempts to refresh the Credential. Returns true if and only if the token was refreshed.
     * Otherwise, false.
     */
    public boolean refreshCredentialIfNeeded(Credential credential) throws AuthException {
        if (shouldRefreshCredential(credential)) {
            try {
               return credential.refreshToken();
            } catch (IOException e) {
                throw new AuthException("Unable to refresh credential.", e);
            }
        }
        return false;
    }

    /**
     * Returns true if and only if the credential can and should be refreshed.
     */
    public boolean shouldRefreshCredential(Credential credential) {
        return credential.getRefreshToken() != null
                && (credential.getExpiresInSeconds() != null
                        && credential.getExpiresInSeconds() <= refreshWindowSeconds);
    }
}
