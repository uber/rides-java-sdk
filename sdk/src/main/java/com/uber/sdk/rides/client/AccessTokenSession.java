package com.uber.sdk.rides.client;

import com.uber.sdk.core.auth.AccessTokenAuthenticator;
import com.uber.sdk.core.auth.AccessTokenStorage;

import javax.annotation.Nonnull;

/**
 * A session containing the details of how an {@link UberRidesApi} will interact with the API.
 * Does authentication through either a server token or OAuth 2.0 credential, exactly one of which must exist.
 * Uses {@link AccessTokenStorage} for connection
 */
public class AccessTokenSession extends Session<AccessTokenAuthenticator> {
    /**
     * @param config config to define connection parameters
     * @param accessTokenStorage to access and refresh tokens
     */
    public AccessTokenSession(@Nonnull SessionConfiguration config, @Nonnull AccessTokenStorage accessTokenStorage) {
        super(new AccessTokenAuthenticator(config, accessTokenStorage));
    }
}
