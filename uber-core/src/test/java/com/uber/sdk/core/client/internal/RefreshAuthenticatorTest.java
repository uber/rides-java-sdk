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
    public void testAuthenticate_callsAuthenticatorRefresh() throws Exception {
        refreshAuthenticator.authenticate(null, response);
        verify(authenticator).refresh(eq(response));
    }
}