package com.uber.sdk.core.auth;

import com.squareup.moshi.Moshi;
import com.uber.sdk.core.auth.internal.OAuth2Service;
import com.uber.sdk.core.auth.internal.OAuthScopesAdapter;
import com.uber.sdk.core.auth.internal.TokenRequestFlow;
import com.uber.sdk.core.client.SessionConfiguration;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Provides implementation for {@link TokenRequestFlow} where authorization code is used along with
 * proof key code exchange mechanism to fetch oauth tokens
 */
public class AuthorizationCodeGrantFlow implements TokenRequestFlow {

    private final static String GRANT_TYPE = "authorization_code";

    private final OAuth2Service oAuth2Service;
    private final SessionConfiguration sessionConfiguration;
    private final String authCode;
    private final String codeVerifier;

    public AuthorizationCodeGrantFlow(
        SessionConfiguration sessionConfiguration,
        String authCode,
        String codeVerifier
    ) {
        this(createOAuthService(sessionConfiguration.getLoginHost()),
                sessionConfiguration,
                authCode,
                codeVerifier
        );
    }

    /**
     * This contructor is used for testing only
     * @param ooAuth2Service
     * @param sessionConfiguration
     * @param authCode
     * @param codeVerifier
     */
    AuthorizationCodeGrantFlow(
            OAuth2Service ooAuth2Service,
            SessionConfiguration sessionConfiguration,
            String authCode,
            String codeVerifier
    ) {
        this.oAuth2Service = ooAuth2Service;
        this.sessionConfiguration = sessionConfiguration;
        this.authCode = authCode;
        this.codeVerifier = codeVerifier;
    }



    @Override
    public void execute(TokenRequestFlowCallback callback) {
        oAuth2Service.token(
                sessionConfiguration.getClientId(),
                codeVerifier,
                GRANT_TYPE,
                sessionConfiguration.getRedirectUri(),
                authCode
        ).enqueue(
                new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onFailure(new AuthException("Token request failed with code " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        callback.onFailure(t);
                    }
                }
        );
    }

    private static OAuth2Service createOAuthService(String baseUrl) {
        final Moshi moshi = new Moshi.Builder().add(new OAuthScopesAdapter()).build();
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OAuth2Service.class);
    }
}
