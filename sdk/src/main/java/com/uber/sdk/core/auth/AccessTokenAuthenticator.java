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

import com.squareup.moshi.Moshi;
import com.uber.sdk.core.auth.internal.AccessTokenRefreshFailedException;
import com.uber.sdk.core.auth.internal.OAuth2Service;
import com.uber.sdk.core.auth.internal.OAuthScopesAdapter;
import com.uber.sdk.rides.client.SessionConfiguration;
import com.uber.sdk.rides.client.error.ErrorParser;
import com.uber.sdk.rides.client.internal.ApiInterceptor;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class AccessTokenAuthenticator implements Authenticator {

    private static final String HEADER_BEARER_ACCESS_VALUE = "Bearer %s";
    private static final String TOKEN_URL = "https://login.%s/oauth/v2/";

    private final SessionConfiguration sessionConfiguration;
    private final AccessTokenStorage tokenStorage;
    private final OAuth2Service auth2Service;
    private static String REFRESH_GRANT_TYPE = "refresh_token";

    public AccessTokenAuthenticator(SessionConfiguration sessionConfiguration,
                                    AccessTokenStorage tokenStorage) {
        this(sessionConfiguration,
                tokenStorage,
                createOAuthService(String.format(TOKEN_URL,
                        sessionConfiguration.getEndpointRegion().domain)));
    }

    AccessTokenAuthenticator(SessionConfiguration sessionConfiguration,
                             AccessTokenStorage tokenStorage,
                             OAuth2Service auth2Service) {
        this.sessionConfiguration = sessionConfiguration;
        this.tokenStorage = tokenStorage;
        this.auth2Service = auth2Service;
    }

    @Override
    public void signRequest(Request.Builder builder) {
        setBearerToken(builder, tokenStorage.getAccessToken());
    }

    @Override
    public boolean isRefreshable() {
        return tokenStorage.getAccessToken() != null && tokenStorage.getAccessToken().getRefreshToken() != null;
    }

    @Override
    public Request refresh(Response response) throws IOException {
        return doRefresh(response);
    }

    /**
     * Get SessionConfiguration used for authentication
     */
    @Override
    public SessionConfiguration getSessionConfiguration() {
        return sessionConfiguration;
    }

    /**
     * Get AccessTokenStorage used for authentication
     */
    public AccessTokenStorage getTokenStorage() {
        return tokenStorage;
    }

    synchronized Request doRefresh(Response response) throws IOException {
        if (signedByOldToken(response, tokenStorage.getAccessToken())) {
            return resign(response, tokenStorage.getAccessToken());
        } else {
            return refreshAndSign(response, tokenStorage.getAccessToken());
        }
    }

    Request resign(Response response, AccessToken auth2Token) {
        Request.Builder builder = response.request().newBuilder();
        setBearerToken(builder, auth2Token);

        return builder.build();
    }

    Request refreshAndSign(Response response, AccessToken auth2Token) throws IOException {
        AccessToken token = refreshToken(auth2Token);
        return resign(response, token);
    }

    AccessToken refreshToken(AccessToken auth2Token) throws IOException {
        retrofit2.Response<AccessToken> response = auth2Service.refresh(auth2Token.getRefreshToken(),
                sessionConfiguration.getClientId(),
                sessionConfiguration.getClientSecret(),
                REFRESH_GRANT_TYPE)
                .execute();
        if (response.isSuccessful()) {
            AccessToken newToken = response.body();
            tokenStorage.setAccessToken(newToken);
            return newToken;
        } else {
            throw new AccessTokenRefreshFailedException(response.errorBody().string());
        }
    }

    boolean signedByOldToken(Response response, AccessToken oAuth2Token) {
        String value = ApiInterceptor.getAuthorizationHeader(response.request());

        String accessToken = createBearerToken(oAuth2Token);

        return value != null && !value.equals(accessToken);
    }

    void setBearerToken(Request.Builder builder, AccessToken OAuth2Token) {
        ApiInterceptor.setAuthorizationHeader(builder, createBearerToken(OAuth2Token));
    }

    String createBearerToken(AccessToken oAuth2Token) {
        return String.format(HEADER_BEARER_ACCESS_VALUE, oAuth2Token.getToken());
    }

    static OAuth2Service createOAuthService(String baseUrl) {
        final Moshi moshi = new Moshi.Builder().add(new OAuthScopesAdapter()).build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OAuth2Service.class);
    }
}
