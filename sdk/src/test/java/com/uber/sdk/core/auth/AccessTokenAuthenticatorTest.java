package com.uber.sdk.core.auth;

import com.uber.sdk.core.auth.internal.OAuth2Service;
import com.uber.sdk.rides.client.SessionConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccessTokenAuthenticatorTest {

    @Mock
    AccessTokenStorage accessTokenStorage;

    @Mock
    AccessToken accessToken;

    @Mock
    OAuth2Service service;

    @Mock
    SessionConfiguration config;

    @Mock
    Call<AccessToken> serviceResult;

    AccessTokenAuthenticator authenticator;

    Request dummyRequest;
    Response dummyResponse;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        authenticator = spy(new AccessTokenAuthenticator(config, accessTokenStorage, service));
        dummyRequest = new Request.Builder()
                .url("http://test")
                .build();

        dummyResponse = new Response.Builder()
                .request(dummyRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .build();
    }

    @Test
    public void testSignRequest_callsSetBearerToken() throws Exception {
        when(accessTokenStorage.getAccessToken()).thenReturn(accessToken);
        Request.Builder builder = dummyRequest.newBuilder();

        authenticator.signRequest(builder);
        verify(authenticator).setBearerToken(eq(builder), eq(accessToken));
    }

    @Test
    public void testIsRefreshable_noToken_returnsFalse() throws Exception {
        when(accessTokenStorage.getAccessToken()).thenReturn(null);
        assertFalse(authenticator.isRefreshable());
    }

    @Test
    public void testIsRefreshable_noRefreshToken_returnsFalse() throws Exception {
        when(accessTokenStorage.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getRefreshToken()).thenReturn(null);
        assertFalse(authenticator.isRefreshable());
    }

    @Test
    public void testIsRefreshable_validToken_returnsTrue() throws Exception {
        when(accessTokenStorage.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getRefreshToken()).thenReturn("refreshToken");
        assertTrue(authenticator.isRefreshable());
    }

    @Test
    public void testRefresh_callsRefreshToken() throws Exception {
        doReturn(dummyRequest).when(authenticator).doRefresh(eq(dummyResponse));
        authenticator.refresh(dummyResponse);
        verify(authenticator).doRefresh(eq(dummyResponse));
    }

    @Test
    public void testDoRefreshToken_ifSignedByOldToken_resign() throws Exception {
        when(accessTokenStorage.getAccessToken()).thenReturn(accessToken);
        doReturn(true).when(authenticator).signedByOldToken(eq(dummyResponse), eq(accessToken));
        doReturn(dummyRequest).when(authenticator).resign(eq(dummyResponse), eq(accessToken));
        Request result = authenticator.doRefresh(dummyResponse);
        verify(authenticator).resign(eq(dummyResponse), eq(accessToken));
        assertEquals(dummyRequest, result);
    }

    @Test
    public void testDoRefreshToken_ifNotSignedByOldToken_refreshAndResign() throws Exception {
        when(accessTokenStorage.getAccessToken()).thenReturn(accessToken);
        doReturn(false).when(authenticator).signedByOldToken(eq(dummyResponse), eq(accessToken));
        doReturn(dummyRequest).when(authenticator).refreshAndSign(eq(dummyResponse), eq(accessToken));
        Request result = authenticator.doRefresh(dummyResponse);
        verify(authenticator).refreshAndSign(eq(dummyResponse), eq(accessToken));
        assertEquals(dummyRequest, result);
    }

    @Test
    public void testResign_callsSetBearerToken() throws Exception {
        authenticator.resign(dummyResponse, accessToken);
        verify(authenticator).setBearerToken(any(Request.Builder.class), eq(accessToken));
    }

    @Test
    public void testRefreshAndSign_callsRefreshAndCallsResign() throws Exception {

        AccessToken newToken = mock(AccessToken.class);
        doReturn(newToken).when(authenticator).refreshToken(eq(accessToken));

        authenticator.refreshAndSign(dummyResponse, accessToken);

        verify(authenticator).refreshToken(eq(accessToken));
        verify(authenticator).resign(eq(dummyResponse), eq(newToken));
    }

    @Test
    public void testRefreshToken() throws Exception {
        when(accessToken.getRefreshToken()).thenReturn("refresh");
        when(config.getClientId()).thenReturn("clientId");
        when(service.refresh(eq("refresh"), eq("clientId"))).thenReturn(serviceResult);
        when(serviceResult.execute()).thenReturn(retrofit2.Response.success(accessToken));
        assertEquals(accessToken, authenticator.refreshToken(accessToken));
        verify(accessTokenStorage).setAccessToken(accessToken);
    }

    @Test
    public void testSignedByOldToken_whenEqual_returnFalse() throws Exception {
        when(accessToken.getToken()).thenReturn("token1234");
        doCallRealMethod().when(authenticator).createBearerToken(eq(accessToken));

        Request request = dummyRequest.newBuilder().header("Authorization", "Bearer token1234").build();
        Response response = dummyResponse.newBuilder().request(request).build();

        assertFalse(authenticator.signedByOldToken(response, accessToken));
    }

    @Test
    public void testSignedByOldToken_whenNotEqual_returnTrue() throws Exception {
        when(accessToken.getToken()).thenReturn("token1234");
        doCallRealMethod().when(authenticator).createBearerToken(eq(accessToken));

        Request request = dummyRequest.newBuilder().header("Authorization", "Bearer token123").build();
        Response response = dummyResponse.newBuilder().request(request).build();

        assertTrue(authenticator.signedByOldToken(response, accessToken));
    }

    @Test
    public void testSetBearerToken_formatsCorrectly() throws Exception {
        Request.Builder builder = dummyRequest.newBuilder();
        when(accessToken.getToken()).thenReturn("token1234");
        authenticator.setBearerToken(builder, accessToken);
        assertEquals("Bearer token1234", builder.build().header("Authorization"));
    }

    @Test
    public void testCreateBearerToken_formatsCorrectly() throws Exception {
        when(accessToken.getToken()).thenReturn("token1234");
        assertEquals("Bearer token1234", authenticator.createBearerToken(accessToken));
    }

    @Test
    public void testCreateOAuthService_notNull() throws Exception {
        OAuth2Service service = AccessTokenAuthenticator.createOAuthService("http://test");
        assertNotNull(service);
    }

}