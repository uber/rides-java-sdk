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

package com.uber.sdk.core.auth;

import com.uber.sdk.core.auth.internal.OAuthScopes;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An access token for making requests to the Uber API.
 */
public class AccessToken {
    private final long expires_in;
    @OAuthScopes
    private final Set<Scope> scope;
    private final String access_token;
    private final String refresh_token;
    private final String token_type;

    /**
     * @param expiresIn    the time that the access token expires.
     * @param scopes       the {@link Scope}s this access token works for.
     * @param token        the Uber API access token.
     * @param refreshToken the Uber API refresh token.
     * @param tokenType    the Uber API token type.
     */
    public AccessToken(
            long expiresIn,
            Collection<Scope> scopes,
            String token,
            String refreshToken,
            String tokenType) {
        expires_in = expiresIn;
        this.scope = new HashSet<>(scopes);
        access_token = token;
        refresh_token = refreshToken;
        token_type = tokenType;
    }

    /**
     * @param expiresIn    the time that the access token expires.
     * @param scope        space delimited list of {@link Scope}s.
     * @param token        the Uber API access token.
     * @param refreshToken the Uber API refresh token.
     * @param tokenType    the Uber API token type.
     */
    public AccessToken(
            long expiresIn,
            String scope,
            String token,
            String refreshToken,
            String tokenType) {
        expires_in = expiresIn;
        this.scope = Scope.parseScopes(scope);
        access_token = token;
        refresh_token = refreshToken;
        token_type = tokenType;
    }

    /**
     * Gets the time the {@link AccessToken} expires at.
     *
     * @return the expiration time.
     */
    public long getExpiresIn() {
        return expires_in;
    }

    /**
     * Gets the {@link Scope}s the access token works for.
     *
     * @return the scopes.
     */
    public Collection<Scope> getScopes() {
        return Collections.unmodifiableCollection(scope);
    }

    /**
     * Gets the raw token used to make API requests
     *
     * @return the raw token.
     */
    public String getToken() {
        return access_token;
    }

    /**
     * Gets the refresh token used to update the {@link AccessToken#access_token}.
     *
     * @return the raw refresh token.
     */
    public String getRefreshToken() {
        return refresh_token;
    }

    /**
     * Gets the type associated with {@link AccessToken#access_token}.
     *
     * @return the raw token type
     */
    public String getTokenType() {
        return token_type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccessToken that = (AccessToken) o;

        if (expires_in != that.expires_in) return false;
        if (scope != null ? !scope.equals(that.scope) : that.scope != null) return false;
        if (access_token != null ? !access_token.equals(that.access_token) : that.access_token != null)
            return false;
        if (refresh_token != null ? !refresh_token.equals(that.refresh_token) : that.refresh_token != null)
            return false;
        return token_type != null ? token_type.equals(that.token_type) : that.token_type == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (expires_in ^ (expires_in >>> 32));
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + (access_token != null ? access_token.hashCode() : 0);
        result = 31 * result + (refresh_token != null ? refresh_token.hashCode() : 0);
        result = 31 * result + (token_type != null ? token_type.hashCode() : 0);
        return result;
    }
}
