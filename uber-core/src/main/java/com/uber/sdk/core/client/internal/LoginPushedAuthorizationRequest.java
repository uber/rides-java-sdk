package com.uber.sdk.core.client.internal;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.uber.sdk.core.auth.internal.LoginPARResponse;
import com.uber.sdk.core.auth.internal.OAuth2Service;
import com.uber.sdk.core.auth.ProfileHint;
import com.uber.sdk.core.client.SessionConfiguration;

import java.util.Base64;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class LoginPushedAuthorizationRequest {

    private final OAuth2Service oAuth2Service;

    private final Callback callback;
    private final ProfileHint profileHint;
    private final String clientId;
    private final String responseType;
    private final Moshi moshi;

    public LoginPushedAuthorizationRequest(
            SessionConfiguration sessionConfiguration,
            String responseType,
            Callback callback
    ) {
        this(
            createOAuthService(sessionConfiguration.getLoginHost()),
            sessionConfiguration.getProfileHint(),
            sessionConfiguration.getClientId(),
            responseType,
            callback
        );
    }

    public LoginPushedAuthorizationRequest(
            OAuth2Service oAuth2Service,
            ProfileHint profileHint,
            String clientId,
            String responseType,
            Callback callback
    ) {
        this.oAuth2Service = oAuth2Service;
        this.profileHint = profileHint;
        this.clientId = clientId;
        this.responseType = responseType.toLowerCase(Locale.US);
        this.callback = callback;
        this.moshi = new Moshi.Builder().build();
    }

    public void execute() {
        if (profileHint == null) {
            callback.onSuccess("");
            return;
        }
        JsonAdapter<ProfileHint> profileHintJsonAdapter = moshi.adapter(ProfileHint.class);
        String profileHintString = new String(
                Base64.getEncoder().encode(
                        profileHintJsonAdapter.toJson(profileHint).getBytes(UTF_8)
                )
        );
        oAuth2Service
                .loginParRequest(clientId, responseType, profileHintString)
                .enqueue(new retrofit2.Callback<LoginPARResponse>() {
                    @Override
                    public void onResponse(Call<LoginPARResponse> call, Response<LoginPARResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body().requestUri);
                        } else {
                            onFailure(call, new RuntimeException("bad response"));
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginPARResponse> call, Throwable t) {
                        callback.onError(new LoginPARRequestException(t));
                    }
                });
    }

    static OAuth2Service createOAuthService(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(OAuth2Service.class);
    }

    public static interface Callback {
        void onSuccess(String requestUri);

        void onError(LoginPARRequestException e);
    }
}
