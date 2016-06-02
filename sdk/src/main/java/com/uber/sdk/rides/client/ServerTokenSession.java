package com.uber.sdk.rides.client;

import com.uber.sdk.rides.auth.ServerTokenAuthenticator;

import javax.annotation.Nonnull;

/**
 * A session containing the details of how an {@link UberRidesApi} will interact with the API.
 * Does authentication through either a server token or OAuth 2.0 credential, exactly one of which must exist.
 * Uses server token for connection
 */
public class ServerTokenSession extends Session<ServerTokenAuthenticator> {
    /**
     * @param config to define connection parameters
     */
    public ServerTokenSession(@Nonnull SessionConfiguration config) {
        super(new ServerTokenAuthenticator(config));
    }
}
