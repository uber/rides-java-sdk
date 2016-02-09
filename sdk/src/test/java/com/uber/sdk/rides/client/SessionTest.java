package com.uber.sdk.rides.client;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SessionTest {

    private static final GenericUrl TOKEN_SERVER_URL = new GenericUrl(OAuth2Credentials.TOKEN_SERVER_URL);

    @Rule public ExpectedException exception = ExpectedException.none();

    private Credential credential;

    @Before
    public void setUp() throws Exception {
        credential = new Credential.Builder(BearerToken.authorizationHeaderAccessMethod())
                .setJsonFactory(new JacksonFactory())
                .setClientAuthentication(new ClientParametersAuthentication("CLIENT_ID", "CLIENT_SECRET"))
                .setTokenServerUrl(TOKEN_SERVER_URL)
                .build();
        credential.setAccessToken("accessToken");
    }

    @Test
    public void buildSession_whenCredentialIsSuppliedAndNoServerToken_shouldSucceed() throws Exception {
        new Session.Builder().setCredential(credential).build();
    }

    @Test
    public void buildSession_whenServerTokenIsSuppliedAndNoCredential_shouldSucceed() throws Exception {
        new Session.Builder().setServerToken("serverToken").build();
    }

    @Test
    public void buildSession_whenNoCredentialOrServerToken_shouldFail() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("An OAuth 2.0 credential or a server token is required to create a session.");

        new Session.Builder().build();
    }

    @Test
    public void buildSession_whenBothCredentialAndServerToken_shouldFail() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Session must have either an OAuth 2.0 credential or a server token, not both.");

        new Session.Builder().setCredential(credential).setServerToken("serverToken").build();
    }    
    
    @Test
    public void buildSession_withLocalization_shouldSucceed() throws Exception {
        new Session.Builder().setServerToken("serverToken").setAcceptLanguage(new Locale("sv","SE")).build();
    }
}
