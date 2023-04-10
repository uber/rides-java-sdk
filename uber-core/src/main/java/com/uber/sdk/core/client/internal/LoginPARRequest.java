package com.uber.sdk.core.client.internal;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.uber.sdk.core.auth.ProfileHintProvider;
import com.uber.sdk.core.auth.internal.LoginPARResponse;
import com.uber.sdk.core.auth.internal.OAuth2Service;
import com.uber.sdk.core.auth.internal.ProfileHint;
import com.uber.sdk.core.client.SessionConfiguration;

import java.io.IOException;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class LoginPARRequest {

    private final OAuth2Service oAuth2Service;

    private final Callback callback;
    private final ProfileHintProvider profileHintProvider;
    private final String clientId;
    private final String responseType;
    private final Moshi moshi;

    public LoginPARRequest(
            SessionConfiguration sessionConfiguration,
            String responseType,
            Callback callback
    ) {
        this(
            createOAuthService(sessionConfiguration.getLoginHost()),
            sessionConfiguration.getProfileHintProvider(),
            sessionConfiguration.getClientId(),
            responseType,
            callback
        );
    }

    public LoginPARRequest(
            OAuth2Service oAuth2Service,
            ProfileHintProvider profileHintProvider,
            String clientId,
            String responseType,
            Callback callback
    ) {
        this.oAuth2Service = oAuth2Service;
        this.profileHintProvider = profileHintProvider;
        this.clientId = clientId;
        this.responseType = responseType;
        this.callback = callback;
        this.moshi = new Moshi.Builder().build();
    }

    public void executePAR() throws IOException {
        JsonAdapter<ProfileHint> profileHintJsonAdapter = moshi.adapter(ProfileHint.class);
        String profileHint = new String(
                Base64.getEncoder().encode(
                        profileHintJsonAdapter.toJson(profileHintProvider.getProfileHint()).getBytes(UTF_8)
                )
        );
        oAuth2Service
                .loginParRequest(clientId, responseType, profileHint)
                .enqueue(new retrofit2.Callback<LoginPARResponse>() {
                    @Override
                    public void onResponse(Call<LoginPARResponse> call, Response<LoginPARResponse> response) {
                        callback.onSuccess(response.body().requestUri);
                    }

                    @Override
                    public void onFailure(Call<LoginPARResponse> call, Throwable t) {
                        callback.onError(new LoginPARRequestException(t));
                    }
                });
    }

    static OAuth2Service createOAuthService(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl("https://auth-test2.uber.com")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(OAuth2Service.class);
    }

    public static interface Callback {
        void onSuccess(String requestUri);

        void onError(LoginPARRequestException e);
    }
}
