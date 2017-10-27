package com.uber.sdk.core.client.internal;

import com.uber.sdk.core.auth.Authenticator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class RefreshAuthenticatorTest {

    @Mock
    Authenticator authenticator;

    Request request;

    Response response;

    RefreshAuthenticator refreshAuthenticator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        request = new Request.Builder().url("http://test").build();
        response = new Response.Builder()
                .request(request)
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .build();

        refreshAuthenticator = spy(new RefreshAuthenticator(authenticator));
    }

    @Test
    public void testAuthenticate_canReAuthAndRetry_callsRefresh() throws Exception {
        doReturn(new Request.Builder().url("http://test").build()).when(authenticator).refresh(eq(response));
        doReturn(true).when(authenticator).isRefreshable();
        doReturn(true).when(refreshAuthenticator).canRetry(eq(response));
        doReturn(true).when(refreshAuthenticator).canRefresh(eq(response));

        assertNotNull(refreshAuthenticator.authenticate(null, response));
        verify(authenticator).refresh(eq(response));
    }

    @Test
    public void testAuthenticate_canReAuthButCannotRetry_returnsNull() throws Exception {
        doReturn(true).when(authenticator).isRefreshable();
        doReturn(false).when(refreshAuthenticator).canRetry(eq(response));
        doReturn(true).when(refreshAuthenticator).canRefresh(eq(response));

        assertNull(refreshAuthenticator.authenticate(null, response));
        verify(authenticator, never()).refresh(eq(response));
    }

    @Test
    public void testAuthenticate_cannotReAuthButCanRetry_returnsNull() throws Exception {
        doReturn(false).when(authenticator).isRefreshable();
        doReturn(true).when(refreshAuthenticator).canRetry(eq(response));
        doReturn(true).when(refreshAuthenticator).canRefresh(eq(response));

        assertNull(refreshAuthenticator.authenticate(null, response));
        verify(authenticator, never()).refresh(eq(response));
    }

    @Test
    public void testAuthenticate_canReAuthRetryButCannotRefresh_returnsNull() throws Exception {
        doReturn(true).when(authenticator).isRefreshable();
        doReturn(true).when(refreshAuthenticator).canRetry(eq(response));
        doReturn(false).when(refreshAuthenticator).canRefresh(eq(response));

        assertNull(refreshAuthenticator.authenticate(null, response));
        verify(authenticator, never()).refresh(eq(response));
    }

    @Test
    public void testCanRefresh_whenContainsHeader_returnsFalse() {
        Response cannotRefresh = response.newBuilder().header(RefreshAuthenticator.HEADER_INVALID_SCOPES, "true").build();
        assertFalse(refreshAuthenticator.canRefresh(cannotRefresh));
    }

    @Test
    public void testCanRefresh_whenMissingHeader_returnsTrue() {
        Response canRefresh = response.newBuilder().build();
        assertTrue(refreshAuthenticator.canRefresh(canRefresh));
    }

    @Test
    public void testCanRetry_whenUnderMax_returnsTrue() throws Exception {
        Response underMax = response.newBuilder().priorResponse(response).build();
        assertTrue(refreshAuthenticator.canRetry(underMax));
    }

    @Test
    public void testCanRetry_whenOverMax_returnsFalse() throws Exception {
        Response wrapper1 = response.newBuilder().priorResponse(response).build();
        Response wrapper2 = wrapper1.newBuilder().priorResponse(wrapper1).build();
        Response wrapper3 = wrapper2.newBuilder().priorResponse(wrapper2).build();
        Response wrapper4 = wrapper3.newBuilder().priorResponse(wrapper3).build();

        assertFalse(refreshAuthenticator.canRetry(wrapper4));
    }
}