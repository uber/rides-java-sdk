package com.uber.sdk.core.auth;

import com.uber.sdk.core.client.SessionConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BaseRefreshableAuthenticatorTest {

    BaseRefreshableAuthenticator authenticator;

    Request request;

    Response response;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        authenticator = spy(new StubRefreshableAuthenticator());
        request = new Request.Builder().url("http://test").build();
        response = new Response.Builder()
                .request(request)
                .code(200)
                .protocol(Protocol.HTTP_1_1)
                .build();
    }

    @Test
    public void testRefresh_canReAuthAndRetry_callsRefresh() throws Exception {
        doReturn(request).when(authenticator).doRefresh(response);
        doReturn(true).when(authenticator).isRefreshable();
        doReturn(true).when(authenticator).canRetry(eq(response));
        doReturn(true).when(authenticator).canRefresh(eq(response));

        assertNotNull(authenticator.refresh(response));
        verify(authenticator).doRefresh(response);
    }

    @Test
    public void testRefresh_canReAuthButCannotRetry_returnsNull() throws Exception {
        doReturn(true).when(authenticator).isRefreshable();
        doReturn(false).when(authenticator).canRetry(eq(response));
        doReturn(true).when(authenticator).canRefresh(eq(response));

        assertNull(authenticator.refresh(response));
        verify(authenticator, never()).doRefresh(response);
    }

    @Test
    public void testRefresh_cannotReAuthButCanRetry_returnsNull() throws Exception {
        doReturn(false).when(authenticator).isRefreshable();
        doReturn(true).when(authenticator).canRetry(eq(response));
        doReturn(true).when(authenticator).canRefresh(eq(response));

        assertNull(authenticator.refresh(response));
        verify(authenticator, never()).doRefresh(response);
    }

    @Test
    public void testRefresh_canReAuthRetryButCannotRefresh_returnsNull() throws Exception {
        doReturn(true).when(authenticator).isRefreshable();
        doReturn(true).when(authenticator).canRetry(eq(response));
        doReturn(false).when(authenticator).canRefresh(eq(response));

        assertNull(authenticator.refresh(response));
        verify(authenticator, never()).doRefresh(response);
    }

    @Test
    public void testCanRefresh_whenContainsHeader_returnsFalse() {
        Response cannotRefresh = response.newBuilder().header(BaseRefreshableAuthenticator.HEADER_INVALID_SCOPES, "true")
                .build();
        assertFalse(authenticator.canRefresh(cannotRefresh));
    }

    @Test
    public void testCanRefresh_whenMissingHeader_returnsTrue() {
        Response canRefresh = response.newBuilder().build();
        assertTrue(authenticator.canRefresh(canRefresh));
    }

    @Test
    public void testCanRetry_whenUnderMax_returnsTrue() throws Exception {
        Response underMax = response.newBuilder().priorResponse(response).build();
        assertTrue(authenticator.canRetry(underMax));
    }

    @Test
    public void testCanRetry_whenOverMax_returnsFalse() throws Exception {
        Response wrapper1 = response.newBuilder().priorResponse(response).build();
        Response wrapper2 = wrapper1.newBuilder().priorResponse(wrapper1).build();
        Response wrapper3 = wrapper2.newBuilder().priorResponse(wrapper2).build();
        Response wrapper4 = wrapper3.newBuilder().priorResponse(wrapper3).build();

        assertFalse(authenticator.canRetry(wrapper4));
    }

    private static class StubRefreshableAuthenticator extends BaseRefreshableAuthenticator {

        @Override
        protected Request doRefresh(Response response) throws IOException {
            return null;
        }

        @Override
        public boolean isRefreshable() {
            return false;
        }

        @Override
        public void signRequest(Request.Builder builder) {

        }

        @Override
        public SessionConfiguration getSessionConfiguration() {
            return null;
        }
    }

}