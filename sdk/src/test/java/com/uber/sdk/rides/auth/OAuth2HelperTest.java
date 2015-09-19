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

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link com.uber.sdk.rides.auth.OAuth2Helper}.
 */
public class OAuth2HelperTest {

    private static final String ACCESS_TOKEN = "accessToken";
    private static final GenericUrl TOKEN_SERVER_URL = new GenericUrl("http://api.uber.com/token");
    private static final String REFRESH_TOKEN = "refreshToken";

    @Rule public ExpectedException exception = ExpectedException.none();

    private OAuth2Helper oAuth2Helper;
    private Credential credential;
    private MockHttpTransport mockHttpTransport;

    @Before
    public void setUp() {
        mockHttpTransport = new MockHttpTransport();
        credential =
                new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                        .setTransport(mockHttpTransport)
                        .setJsonFactory(new JacksonFactory())
                        .setClientAuthentication(new ClientParametersAuthentication("CLIENT_ID", "CLIENT_SECRET"))
                        .setTokenServerUrl(TOKEN_SERVER_URL).build();
        oAuth2Helper = new OAuth2Helper();
    }

    @Test
    public void refreshCredential_whenNeeded_shouldRefresh() throws Exception {
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(REFRESH_TOKEN);
        credential.setExpiresInSeconds(OAuth2Helper.DEFAULT_REFRESH_WINDOW - 60L);

        assertTrue(oAuth2Helper.refreshCredentialIfNeeded(credential));
    }

    @Test
    public void refreshCredential_whenNotNeeded_shouldNotRefresh() throws Exception {
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(REFRESH_TOKEN);
        credential.setExpiresInSeconds(OAuth2Helper.DEFAULT_REFRESH_WINDOW + 60L);

        assertFalse(oAuth2Helper.refreshCredentialIfNeeded(credential));
    }

    @Test
    public void refreshCredential_whenUnknownExpires_shouldNotRefresh() throws Exception {
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(REFRESH_TOKEN);
        credential.setExpiresInSeconds(null);

        assertFalse(oAuth2Helper.refreshCredentialIfNeeded(credential));
    }

    @Test
    public void refreshCredential_whenNeeded_shouldThrowException() throws Exception {
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(REFRESH_TOKEN);
        credential.setExpiresInSeconds(OAuth2Helper.DEFAULT_REFRESH_WINDOW - 60L);

        mockHttpTransport.setHttpStatusCode(404);
        mockHttpTransport.setHttpResponseContent("failed");

        exception.expect(AuthException.class);
        exception.expectCause(isA(IOException.class));

        oAuth2Helper.refreshCredentialIfNeeded(credential);
    }

    @Test
    public void refreshCredential_whenNeeded_shouldReturnFalse() throws Exception {
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(REFRESH_TOKEN);
        credential.setExpiresInSeconds(OAuth2Helper.DEFAULT_REFRESH_WINDOW - 60L);

        mockHttpTransport.setHttpStatusCode(500);

        assertFalse(oAuth2Helper.refreshCredentialIfNeeded(credential));
    }

    @Test
    public void shouldRefreshCredential_whenNeeded() throws Exception {
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(REFRESH_TOKEN);
        credential.setExpiresInSeconds(OAuth2Helper.DEFAULT_REFRESH_WINDOW - 60L);

        assertTrue(oAuth2Helper.shouldRefreshCredential(credential));
    }

    @Test
    public void shouldRefreshCredential_whenNoRefreshToken_shouldReturnFalse() throws Exception {
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(null);
        credential.setExpiresInSeconds(OAuth2Helper.DEFAULT_REFRESH_WINDOW - 60L);

        assertFalse(oAuth2Helper.shouldRefreshCredential(credential));
    }

    @Test
    public void shouldRefreshCredential_whenNotNeeded_shouldReturnFalse() throws Exception {
        credential.setAccessToken(ACCESS_TOKEN);
        credential.setRefreshToken(null);
        credential.setExpiresInSeconds(OAuth2Helper.DEFAULT_REFRESH_WINDOW + 60L);

        assertFalse(oAuth2Helper.shouldRefreshCredential(credential));
    }

    private static class MockHttpTransport extends com.google.api.client.testing.http.MockHttpTransport {

        private int httpStatusCode = 200;
        private String httpResponseContent = "{\n" +
                "  \"access_token\" : \"accessToken2\",\n" +
                "  \"token_type\" : \"Bearer\",\n" +
                "  \"expires_in\" : " + OAuth2Helper.DEFAULT_REFRESH_WINDOW + ",\n" +
                "  \"refresh_token\" : \"refreshToken2\"\n" +
                "}";

        public void setHttpStatusCode(int httpStatusCode) {
            this.httpStatusCode = httpStatusCode;
        }

        public void setHttpResponseContent(String httpResponseContent) {
            this.httpResponseContent = httpResponseContent;
        }

        @Override
        public LowLevelHttpRequest buildRequest(String method, final String url) throws IOException {
            return new MockLowLevelHttpRequest() {

                @Override
                public String getUrl() {
                    return url;
                }

                @Override
                public LowLevelHttpResponse execute() throws IOException {
                    MockLowLevelHttpResponse mock = new MockLowLevelHttpResponse();
                    mock.setStatusCode(httpStatusCode);
                    mock.setContent(httpResponseContent);
                    return mock;
                }
            };
        }
    }
}
