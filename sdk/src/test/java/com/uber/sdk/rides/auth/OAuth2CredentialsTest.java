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

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.LowLevelHttpRequest;
import com.google.api.client.http.LowLevelHttpResponse;
import com.google.api.client.testing.http.MockLowLevelHttpRequest;
import com.google.api.client.testing.http.MockLowLevelHttpResponse;
import com.google.api.client.testing.json.MockJsonFactory;
import com.google.api.client.util.store.AbstractDataStoreFactory;
import com.google.api.client.util.store.DataStore;
import com.uber.sdk.core.auth.Scope;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import static com.uber.sdk.rides.client.SessionConfiguration.EndpointRegion.CHINA;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link OAuth2Credentials}.
 */
public class OAuth2CredentialsTest {

    private static final String TOKEN_REQUEST_URL = "https://login.uber.com/oauth/v2/token";

    @Rule public ExpectedException exception = ExpectedException.none();

    private MockHttpTransport mockHttpTransport;

    @Before
    public void setUp() {
        mockHttpTransport = new MockHttpTransport();
    }

    @Test
    public void getAuthorizationUrl() throws Exception {
        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST,
                        Scope.HISTORY))
                .build();

        assertEquals("https://login.uber.com/oauth/v2/authorize?client_id=CLIENT_ID"
                        + "&response_type=code&scope=history%20profile%20request",
                oAuth2Credentials.getAuthorizationUrl());
    }

    @Test
    public void getAuthorizationUrl_whenThereAreNoScopes() throws Exception {
        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .build();

        assertEquals("https://login.uber.com/oauth/v2/authorize?client_id=CLIENT_ID&response_type=code",
                oAuth2Credentials.getAuthorizationUrl());
    }

    @Test
    public void getAuthorizationUrl_whenThereIsAnEmptyScopeList() throws Exception {
        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setScopes(new ArrayList<Scope>())
                .build();

        assertEquals("https://login.uber.com/oauth/v2/authorize?client_id=CLIENT_ID&response_type=code",
                oAuth2Credentials.getAuthorizationUrl());
    }

    @Test
    public void getAuthorizationUrl_whenThereAreCustomScopes() throws Exception {
        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setScopes(Arrays.asList(Scope.PROFILE))
                .setCustomScopes(Arrays.asList("custom"))
                .build();

        assertTrue(
                "https://login.uber.com/oauth/v2/authorize?client_id=CLIENT_ID&response_type=code&scope=custom%20profile"
                        .equals(oAuth2Credentials.getAuthorizationUrl()) ||
                        "https://login.uber.com/oauth/v2/authorize?client_id=CLIENT_ID&response_type=code&scope=profile%20custom"
                                .equals(oAuth2Credentials.getAuthorizationUrl()));
    }

    @Test
    public void getAuthorizationUrl_whenThereAreDuplicateCustomScopes() throws Exception {
        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setScopes(Arrays.asList(Scope.PROFILE))
                .setCustomScopes(Arrays.asList("profile"))
                .build();

        assertEquals("https://login.uber.com/oauth/v2/authorize?client_id=CLIENT_ID&response_type=code&scope=profile",
                oAuth2Credentials.getAuthorizationUrl());
    }

    @Test
    public void getAuthorizationUrl_whenThereIsARedirectUri() throws Exception {
        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setRedirectUri("https://localhost:8181/OAuth2Callback")
                .setScopes(Arrays.asList(Scope.PROFILE))
                .setCustomScopes(Arrays.asList("profile"))
                .build();

        assertEquals("https://login.uber.com/oauth/v2/authorize?client_id=CLIENT_ID" +
                        "&response_type=code&scope=profile&redirect_uri=https%3A%2F%2Flocalhost%3A8181%2FOAuth2Callback",
                oAuth2Credentials.getAuthorizationUrl());
    }

    @Test
    public void getAuthorizationUrl_whenUsingServerForChina() throws Exception {
        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST,
                        Scope.HISTORY))
                .setLoginRegion(CHINA)
                .build();

        assertEquals("https://login.uber.com.cn/oauth/v2/token",
                oAuth2Credentials.getAuthorizationCodeFlow().getTokenServerEncodedUrl());

        assertEquals("https://login.uber.com.cn/oauth/v2/authorize?client_id=CLIENT_ID"
                        + "&response_type=code&scope=history%20profile%20request",
                oAuth2Credentials.getAuthorizationUrl());
    }

    @Test
    public void build_whenClientIdIsNull() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage(containsString("Client ID and secret"));

        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets(null, "CLIENT_SECRET")
                .build();
    }

    @Test
    public void build_whenClientSecretIsNull() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage(containsString("Client ID and secret"));

        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", null)
                .build();
    }

    @Test
    public void build_whenClientSecretsAreNull() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage(containsString("Client ID and secret"));

        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets(null, null)
                .build();
    }

    @Test
    public void build_whenThereAreNoClientSecrets() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage(containsString("Client ID and secret"));

        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .build();
    }

    @Test
    public void authenticate() throws Exception {
        String authorizationCode = "authorizationCode";

        String expectedRequestContent = "code=authorizationCode&grant_type=authorization_code" +
                "&redirect_uri=http%3A%2F%2Fredirect&scope=profile+request" +
                "&client_id=CLIENT_ID&client_secret=CLIENT_SECRET";

        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setRedirectUri("http://redirect")
                .setHttpTransport(mockHttpTransport)
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST))
                .build();

        Credential credential = oAuth2Credentials.authenticate(authorizationCode, "userId");

        assertEquals("Request URL did not match.", TOKEN_REQUEST_URL, mockHttpTransport.lastRequestUrl);
        assertEquals("Request content did not match.", expectedRequestContent, mockHttpTransport.lastRequestContent);

        assertEquals("Refresh token does not match.", "refreshToken", credential.getRefreshToken());
        assertTrue("Expected expires_in between 0 and 3600. Was actually: " + credential.getExpiresInSeconds(),
                credential.getExpiresInSeconds() > 0 && credential.getExpiresInSeconds() <= 3600);
        assertEquals("Access token does not match.", "accessToken", credential.getAccessToken());
        assertEquals("Access method (Bearer) does not match",
                BearerToken.authorizationHeaderAccessMethod().getClass(), credential.getMethod().getClass());
    }

    @Test
    public void authenticate_whenThereAreNoScopes() throws Exception {
        String authorizationCode = "authorizationCode";

        String expectedRequestContent = "code=authorizationCode&grant_type=authorization_code" +
                "&redirect_uri=http%3A%2F%2Fredirect" +
                "&client_id=CLIENT_ID&client_secret=CLIENT_SECRET";

        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setRedirectUri("http://redirect")
                .setHttpTransport(mockHttpTransport)
                .build();

        Credential credential = oAuth2Credentials.authenticate(authorizationCode, "userId");

        assertEquals("Request URL did not match.", TOKEN_REQUEST_URL, mockHttpTransport.lastRequestUrl);
        assertEquals("Request content did not match.", expectedRequestContent, mockHttpTransport.lastRequestContent);

        assertEquals("Refresh token does not match.", "refreshToken", credential.getRefreshToken());
        assertTrue("Expected expires_in between 0 and 3600. Was actually: " + credential.getExpiresInSeconds(),
                credential.getExpiresInSeconds() > 0 && credential.getExpiresInSeconds() <= 3600);
        assertEquals("Access token does not match.", "accessToken", credential.getAccessToken());
        assertEquals("Access method (Bearer) does not match",
                BearerToken.authorizationHeaderAccessMethod().getClass(), credential.getMethod().getClass());
    }

    @Test
    public void authenticate_whenCantAuthenticate_shouldThrowException() throws Exception {
        exception.expect(AuthException.class);
        exception.expectCause(any(IOException.class));

        String authorizationCode = "authorizationCode";

        mockHttpTransport.setHttpResponseContent("failed");
        mockHttpTransport.setHttpStatusCode(403);

        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setRedirectUri("http://redirect")
                .setHttpTransport(mockHttpTransport)
                .build();

        oAuth2Credentials.authenticate(authorizationCode, "userId");
    }

    @Test
    public void loadCredential() throws Exception {
        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setRedirectUri("http://redirect")
                .setHttpTransport(mockHttpTransport)
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST))
                .build();

        oAuth2Credentials.authenticate("authorizationCode", "userId");

        Credential credential = oAuth2Credentials.loadCredential("userId");

        assertEquals("Refresh token does not match.", "refreshToken", credential.getRefreshToken());
        assertTrue("Expected expires_in between 0 and 3600. Was actually: " + credential.getExpiresInSeconds(),
                credential.getExpiresInSeconds() > 0 && credential.getExpiresInSeconds() <= 3600);
        assertEquals("Access token does not match.", "accessToken", credential.getAccessToken());
        assertEquals("Access method (Bearer) does not match",
                BearerToken.authorizationHeaderAccessMethod().getClass(), credential.getMethod().getClass());
    }

    @Test
    public void clearCredential() throws Exception {
        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setRedirectUri("http://redirect")
                .setHttpTransport(mockHttpTransport)
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST))
                .build();

        oAuth2Credentials.authenticate("authorizationCode", "userId");

        Credential credential = oAuth2Credentials.loadCredential("userId");

        assertNotNull(credential);

        oAuth2Credentials.clearCredential("userId");

        credential = oAuth2Credentials.loadCredential("userId");
        assertNull(credential);
    }

    @Test
    public void useCustomDataStore() throws Exception {
        Credential credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setTransport(new MockHttpTransport())
                .setJsonFactory(new MockJsonFactory())
                .setClientAuthentication(Mockito.mock(HttpExecuteInterceptor.class))
                .setTokenServerUrl(new GenericUrl(TOKEN_REQUEST_URL))
                .build();

        credential.setAccessToken("accessToken2");
        credential.setRefreshToken("refreshToken2");
        credential.setExpiresInSeconds(1000L);

        DataStore mockDataStore = mock(DataStore.class);
        MockDataStoreFactory mockDataStoreFactory = new MockDataStoreFactory(mockDataStore);
        when(mockDataStore.get(eq("userId"))).thenReturn(new StoredCredential(credential));

        OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
                .setClientSecrets("CLIENT_ID", "CLIENT_SECRET")
                .setRedirectUri("http://redirect")
                .setHttpTransport(mockHttpTransport)
                .setCredentialDataStoreFactory(mockDataStoreFactory)
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.REQUEST))
                .build();

        Credential storedCredential = oAuth2Credentials.authenticate("authorizationCode", "userId");
        Credential loadedCredential = oAuth2Credentials.loadCredential("userId");

        assertEquals("Refresh token does not match.", "refreshToken", storedCredential.getRefreshToken());
        assertTrue("Expected expires_in between 0 and 3600. Was actually: " + storedCredential.getExpiresInSeconds(),
                storedCredential.getExpiresInSeconds() > 0 && storedCredential.getExpiresInSeconds() <= 3600);
        assertEquals("Access token does not match.", "accessToken", storedCredential.getAccessToken());
        assertEquals("Access method (Bearer) does not match",
                BearerToken.authorizationHeaderAccessMethod().getClass(), storedCredential.getMethod().getClass());

        assertEquals("Refresh token does not match.", "refreshToken2", loadedCredential.getRefreshToken());
        assertTrue("Expected expires_in between 0 and 1000. Was actually: " + loadedCredential.getExpiresInSeconds(),
                loadedCredential.getExpiresInSeconds() > 0 && loadedCredential.getExpiresInSeconds() <= 1000L);
        assertEquals("Access token does not match.", "accessToken2", loadedCredential.getAccessToken());
        assertEquals("Access method (Bearer) does not match",
                BearerToken.authorizationHeaderAccessMethod().getClass(), loadedCredential.getMethod().getClass());
    }

    /**
     * Mock DataStoreFactory that returns a passed in datastore.
     */
    private static class MockDataStoreFactory extends AbstractDataStoreFactory {

        private final DataStore mockDataStore;

        public MockDataStoreFactory(DataStore mockDataStore) throws IOException {
            super();
            this.mockDataStore = mockDataStore;
        }

        @Override
        protected <V extends Serializable> DataStore<V> createDataStore(String id) throws IOException {
            @SuppressWarnings("unchecked")
            DataStore<V> dataStore = (DataStore<V>) mockDataStore;
            return dataStore;
        }
    }

    /**
     * Mock HttpTransport that captures last request URL and Content and relays a valid token.
     */
    private static class MockHttpTransport extends com.google.api.client.testing.http.MockHttpTransport {

        private String lastRequestUrl;
        private String lastRequestContent;

        private int httpStatusCode = 200;
        private String httpResponseContent = "{\n" +
                "  \"access_token\" : \"accessToken\",\n" +
                "  \"token_type\" : \"Bearer\",\n" +
                "  \"expires_in\" : " + 300L + ",\n" +
                "  \"refresh_token\" : \"refreshToken\"\n" +
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
                    lastRequestUrl = getUrl();
                    lastRequestContent = getContentAsString();

                    MockLowLevelHttpResponse mock = new MockLowLevelHttpResponse();
                    mock.setStatusCode(httpStatusCode);
                    mock.setContent(httpResponseContent);

                    return mock;
                }
            };
        }
    }
}
