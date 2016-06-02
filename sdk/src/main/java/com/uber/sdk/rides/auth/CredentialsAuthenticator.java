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

package com.uber.sdk.rides.auth;

import com.google.api.client.auth.oauth2.Credential;
import com.uber.sdk.core.auth.Authenticator;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.internal.ApiInterceptor;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class CredentialsAuthenticator implements Authenticator {
    static final String HEADER_BEARER_ACCESS_VALUE = "Bearer %s";

    private final Credential credential;
    private final SessionConfiguration sessionConfiguration;

    public CredentialsAuthenticator(SessionConfiguration sessionConfiguration, Credential credential) {
        this.credential = credential;
        this.sessionConfiguration = sessionConfiguration;
    }

    @Override
    public void signRequest(Request.Builder builder) {
        setBearerToken(builder, credential);
    }

    @Override
    public boolean isRefreshable() {
        return true;
    }

    @Override
    public Request refresh(Response response) throws IOException {
        return reauth(response);
    }

    @Override
    public SessionConfiguration getSessionConfiguration() {
        return sessionConfiguration;
    }

    /**
     * Get {@link Credential} used for authentication
     */
    public Credential getCredential() {
        return credential;
    }

    private synchronized Request reauth(Response response) throws IOException {
        if (signedByOldToken(response, credential)) {
            return resign(response, credential);
        } else {
            return refreshAndSign(response, credential);
        }
    }

    private static void setBearerToken(Request.Builder builder, Credential credential) {
        ApiInterceptor.setAuthorizationHeader(builder, createBearerToken(credential));
    }

    private static String createBearerToken(Credential credential) {
        return String.format(HEADER_BEARER_ACCESS_VALUE, credential.getAccessToken());
    }

    private Request resign(Response response, Credential credential) {
        Request.Builder builder = response.request().newBuilder();
        setBearerToken(builder, credential);

        return builder.build();
    }

    private Request refreshAndSign(Response response, Credential credential) throws IOException {
        credential.refreshToken();
        return resign(response, credential);
    }

    private boolean signedByOldToken(Response response, Credential credential) {
        String value = ApiInterceptor.getAuthorizationHeader(response.request());

        String accessToken = createBearerToken(credential);

        return value != null && !value.equals(accessToken);
    }
}
