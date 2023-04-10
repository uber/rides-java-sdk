package com.uber.sdk.core.client.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uber.sdk.core.auth.ProfileHintProvider;
import com.uber.sdk.core.auth.internal.LoginPARResponse;
import com.uber.sdk.core.auth.internal.OAuth2Service;
import com.uber.sdk.core.auth.internal.ProfileHint;
import com.uber.sdk.core.client.SessionConfiguration;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPARRequestTest {

    @Mock
    private LoginPARRequest.Callback callback;
    @Mock
    OAuth2Service oAuth2Service;
    @Mock
    Call<LoginPARResponse> loginPARResponseCall;
    private Response<LoginPARResponse> loginPARSuccessfulResponse;
    private LoginPARRequest loginPARRequest;
    private LoginPARResponse loginPARResponse = new LoginPARResponse();
    private Response<LoginPARResponse> loginPARFailureResponse = Response.error(400, ResponseBody.create(MediaType.parse(""), ""));
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        SessionConfiguration sessionConfiguration =
                new SessionConfiguration.Builder()
                        .setClientId("clientId")
                        .setProfileHintProvider(
                                new ProfileHintProvider() {
                                    @Override
                                    public ProfileHint getProfileHint() {
                                        return new ProfileHint
                                                ("par", "test", "abc@gmail.com", "+11234567890");
                                    }
                                }
                        )
                        .build();
        loginPARRequest = new LoginPARRequest(
                oAuth2Service,
                sessionConfiguration.getProfileHintProvider(),
                sessionConfiguration.getClientId(),
                "code",
                callback);
        loginPARResponse.expiresIn = "900";
        loginPARResponse.requestUri = "urn:ietf:params:oauth:request_uri:this-is-a-test-request-uri";
        loginPARSuccessfulResponse = Response.success(loginPARResponse);
        doNothing().when(callback).onError(any());
        doNothing().when(callback).onSuccess(anyString());
    }

    @Test
    public void executePAR_whenSuccess_retrievesRequestUri() throws Exception {
        Class<Callback<LoginPARResponse>> loginPARResponseCallback = (Class<Callback<LoginPARResponse>>)(Class) Callback.class;
        ArgumentCaptor<Callback<LoginPARResponse>> argumentCaptor = ArgumentCaptor.forClass(loginPARResponseCallback);
        when(oAuth2Service.loginParRequest(anyString(), anyString(), anyString())).thenReturn(loginPARResponseCall);
        loginPARRequest.executePAR();
        verify(loginPARResponseCall).enqueue(argumentCaptor.capture());

        argumentCaptor.getValue().onResponse(loginPARResponseCall, loginPARSuccessfulResponse);

        verify(callback).onSuccess("urn:ietf:params:oauth:request_uri:this-is-a-test-request-uri");
    }

    @Test
    public void executePAR_whenFailure_callsOnError() throws Exception {
        Class<Callback<LoginPARResponse>> loginPARResponseCallback = (Class<Callback<LoginPARResponse>>)(Class) Callback.class;
        ArgumentCaptor<Callback<LoginPARResponse>> argumentCaptor = ArgumentCaptor.forClass(loginPARResponseCallback);
        when(oAuth2Service.loginParRequest(anyString(), anyString(), anyString())).thenReturn(loginPARResponseCall);
        loginPARRequest.executePAR();
        verify(loginPARResponseCall).enqueue(argumentCaptor.capture());

        argumentCaptor.getValue().onFailure(loginPARResponseCall, new RuntimeException());

        verify(callback).onError(isA(LoginPARRequestException.class));
    }
}
