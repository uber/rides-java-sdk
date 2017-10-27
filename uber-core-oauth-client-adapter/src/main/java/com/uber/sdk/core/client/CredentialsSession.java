package com.uber.sdk.core.client;

import com.google.api.client.auth.oauth2.Credential;
import com.uber.sdk.core.auth.CredentialsAuthenticator;

import javax.annotation.Nonnull;

/**
 * A session containing the details of how an Uber authenticated service will interact with the API.
 * Does authentication through either a server token or OAuth 2.0 credential, exactly one of which must exist.
 * Uses {@link Credential} for connection
 */
public class CredentialsSession extends Session<CredentialsAuthenticator> {
    /**
     * @param config config to define connection parameters
     * @param credential to access and refresh token
     */
    public CredentialsSession(@Nonnull SessionConfiguration config, @Nonnull Credential credential) {
        super(new CredentialsAuthenticator(config, credential));
    }
}
