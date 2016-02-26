package com.uber.sdk.rides.client;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.uber.sdk.rides.client.Session.EndpointRegion.CHINA;
import static com.uber.sdk.rides.client.Session.Environment.PRODUCTION;
import static com.uber.sdk.rides.client.Session.Environment.SANDBOX;
import static org.junit.Assert.assertEquals;

import java.util.Locale;

public class SessionTest {

    private static final GenericUrl TOKEN_SERVER_URL = new GenericUrl("https://login.uber.com/oauth/v2/token");

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
        new Session.Builder().setEnvironment(PRODUCTION).setCredential(credential).build();
    }

    @Test
    public void buildSession_whenServerTokenIsSuppliedAndNoCredential_shouldSucceed() throws Exception {
        new Session.Builder().setEnvironment(PRODUCTION).setServerToken("serverToken").build();
    }

    @Test
    public void buildSession_whenLocalizationProvided_shouldSucceed() throws Exception {
        new Session.Builder().setEnvironment(PRODUCTION)
                .setServerToken("serverToken")
                .setAcceptLanguage(new Locale("sv", "SE"))
                .build();
    }

    @Test
    public void buildSession_whenNoCredentialOrServerToken_shouldFail() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("An OAuth 2.0 credential or a server token is required to create a session.");

        new Session.Builder().setEnvironment(PRODUCTION).build();
    }

    @Test
    public void buildSession_whenBothCredentialAndServerToken_shouldFail() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Session must have either an OAuth 2.0 credential or a server token, not both.");

        new Session.Builder().setEnvironment(PRODUCTION)
                .setCredential(credential)
                .setServerToken("serverToken").build();
    }

    @Test
    public void buildSession_whenNoEnvironmentSupplied_shouldFail() throws Exception {
        exception.expect(IllegalStateException.class);
        exception.expectMessage("Must supply an Environment");

        new Session.Builder().setCredential(credential).build();
    }

    @Test
    public void buildSession_whenProductionEnvAndNotChina_shouldGiveNonCnProductionEndpointHost() throws Exception {
        Session session = new Session.Builder().setEnvironment(PRODUCTION).setCredential(credential).build();
        assertEquals("https://api.uber.com", session.getEndpointHost());
    }

    @Test
    public void buildSession_whenSandboxEnvAndNotChina_shouldGiveNonCnSandboxEndpointHost() throws Exception {
        Session session = new Session.Builder().setEnvironment(SANDBOX).setCredential(credential).build();
        assertEquals("https://sandbox-api.uber.com", session.getEndpointHost());
    }

    @Test
    public void buildSession_whenProductionEnvAndInChina_shouldGiveChinaProductionEndpointHost() throws Exception {
        Session session = new Session.Builder().setEnvironment(PRODUCTION)
                .setCredential(credential)
                .setEndpointRegion(CHINA)
                .build();
        assertEquals("https://api.uber.com.cn", session.getEndpointHost());
    }

    @Test
    public void buildSession_whenSandboxEnvAndInChina_shouldGiveChinaSandboxEndpointHost() throws Exception {
        Session session = new Session.Builder().setEnvironment(SANDBOX)
                .setCredential(credential)
                .setEndpointRegion(CHINA)
                .build();
        assertEquals("https://sandbox-api.uber.com.cn", session.getEndpointHost());
    }
}
