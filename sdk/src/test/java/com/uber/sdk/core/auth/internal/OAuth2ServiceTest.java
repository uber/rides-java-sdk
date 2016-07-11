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

package com.uber.sdk.core.auth.internal;

import com.squareup.moshi.Moshi;
import com.uber.sdk.WireMockTest;
import com.uber.sdk.core.auth.AccessToken;

import org.junit.Before;
import org.junit.Test;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;


public class OAuth2ServiceTest extends WireMockTest {

    private static final String REFRESH_TOKEN = "RANDOM1234REFRESHTOKEN";
    private static final String CLIENT_ID = "MYCLIENTID123";
    private static final String REQUEST_BODY = "refresh_token=" + REFRESH_TOKEN + "&client_id=" + CLIENT_ID;

    private OAuth2Service oAuth2Service;

    @Before
    public void setUp() throws Exception {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        final Moshi moshi = new Moshi.Builder().add(new OAuthScopesAdapter()).build();

        oAuth2Service = new Retrofit.Builder().baseUrl("http://localhost:" + wireMockRule.port())
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OAuth2Service.class);
    }

    @Test
    public void testRefresh_whenSuccessful() throws Exception {
        stubFor(post(urlEqualTo("/token"))
                .withRequestBody(equalTo(REQUEST_BODY))
                .willReturn(aResponse()
                        .withBodyFile("token_token.json")));

        AccessToken accessToken = oAuth2Service.refresh(REFRESH_TOKEN, CLIENT_ID).execute().body();

        assertThat(accessToken.getExpiresIn()).isEqualTo(2592000);
        assertThat(accessToken.getToken()).isEqualTo("Access999Token");
        assertThat(accessToken.getRefreshToken()).isEqualTo("888RefreshToken");
    }
}
