package com.uber.sdk.core.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uber.sdk.core.auth.internal.OAuth2Service;
import com.uber.sdk.core.auth.internal.TokenRequestFlow;
import com.uber.sdk.core.auth.internal.TokenRequestFlow.TokenRequestFlowCallback;
import com.uber.sdk.core.client.SessionConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationCodeGrantFlowTest {

    @Mock private TokenRequestFlowCallback callback;

    @Mock
    OAuth2Service oAuth2Service;

    @Mock
    Call<AccessToken> accessTokenCall;

    private TokenRequestFlow tokenRequestFlow;

    AccessToken accessToken = new AccessToken(
            1,
            Arrays.asList(Scope.PROFILE),
            "accessToken",
            "refreshToken",
            "Bearer"
    );

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        SessionConfiguration sessionConfiguration =
                new SessionConfiguration.Builder()
                        .setClientId("clientId")
                        .setProfileHint(
                                new ProfileHint
                                        .Builder()
                                        .firstName("par")
                                        .lastName("test")
                                        .email("abc@gmail.com")
                                        .phone("+11234567890")
                                        .build()
                        )
                        .build();
        tokenRequestFlow = new AuthorizationCodeGrantFlow(
                oAuth2Service,
                sessionConfiguration,
                "code",
                "verifier"
        );
    }

    @Test
    public void execute_whenSuccessful_shouldReturnAccessToken() {
        Class<Callback<AccessToken>> tokenRequestFlowCallback = (Class<Callback<AccessToken>>)(Class) Callback.class;
        ArgumentCaptor<Callback<AccessToken>> argumentCaptor = ArgumentCaptor.forClass(tokenRequestFlowCallback);
        doNothing().when(callback).onSuccess(any());
        when(oAuth2Service.token(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString())
        ).thenReturn(accessTokenCall);
        tokenRequestFlow.execute(callback);
        verify(accessTokenCall).enqueue(argumentCaptor.capture());
        argumentCaptor.getValue().onResponse(accessTokenCall, Response.success(accessToken));

        verify(callback).onSuccess(accessToken);
        assertThat(accessToken.getToken()).isEqualTo("accessToken");
    }

    @Test
    public void execute_whenResponseIsNot2XX_shouldThrowError() {
        Class<Callback<AccessToken>> tokenRequestFlowCallback = (Class<Callback<AccessToken>>)(Class) Callback.class;
        ArgumentCaptor<Callback<AccessToken>> argumentCaptor = ArgumentCaptor.forClass(tokenRequestFlowCallback);
        ArgumentCaptor<AuthException> exceptionCaptor = ArgumentCaptor.forClass(AuthException.class);
        doNothing().when(callback).onSuccess(any());
        when(oAuth2Service.token(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString())
        ).thenReturn(accessTokenCall);
        tokenRequestFlow.execute(callback);
        verify(accessTokenCall).enqueue(argumentCaptor.capture());
        argumentCaptor.getValue().onResponse(
                accessTokenCall,
                Response.error(
                        400,
                        ResponseBody.create(MediaType.parse(""), "")
                )
        );

        verify(callback).onFailure(exceptionCaptor.capture());
        assertThat(exceptionCaptor.getValue().getMessage()).isEqualTo("Token request failed with code 400");
    }

    @Test
    public void execute_whenFailure_shouldReturnError() {
        Class<Callback<AccessToken>> tokenRequestFlowCallback = (Class<Callback<AccessToken>>)(Class) Callback.class;
        ArgumentCaptor<Callback<AccessToken>> argumentCaptor = ArgumentCaptor.forClass(tokenRequestFlowCallback);
        doNothing().when(callback).onSuccess(any());
        when(oAuth2Service.token(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString())
        ).thenReturn(accessTokenCall);
        tokenRequestFlow.execute(callback);
        verify(accessTokenCall).enqueue(argumentCaptor.capture());
        Throwable t = new RuntimeException("exception occurred");
        argumentCaptor.getValue().onFailure(accessTokenCall, t);

        verify(callback).onFailure(t);
        assertThat(t.getMessage()).isEqualTo("exception occurred");
    }
}
