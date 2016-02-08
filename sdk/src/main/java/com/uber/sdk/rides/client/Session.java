/*
 * Copyright (c) 2015 Uber Technologies, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.uber.sdk.rides.client;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.repackaged.com.google.common.base.Preconditions;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A session containing the details of how an {@link UberRidesService} will interact with the API.
 * Does authentication through either a server token or OAuth 2.0 credential, exactly one of which must exist.
 */
public class Session {

    private final Credential credential;
    private final Environment environment;
    private final String serverToken;
    private final Locale locale;

    /**
     * An Uber API Environment. See
     * <a href="https://developer.uber.com/v1/sandbox">Sandbox</a> for more
     * information.
     */
    public enum Environment {
        PRODUCTION("https://api.uber.com"),
        SANDBOX("https://sandbox-api.uber.com");

        public String endpointHost;

        Environment(String endpointHost) {
            this.endpointHost = endpointHost;
        }
    }

    private Session(@Nullable Credential credential, @Nonnull Environment environment, @Nullable String serverToken, @Nullable Locale locale) {
        this.credential = credential;
        this.environment = environment;
        this.serverToken = serverToken;
        this.locale = locale;
    }

    /**
     * Builder for {@link Session} objects.
     */
    public static class Builder {

        private Credential credential;
        private Environment environment;
        private String serverToken;
        private Locale locale;

        /**
         * Sets the OAuth 2.0 Credential. See {@link OAuth2Credentials} for
         * assistance with creating them.  Exactly one of OAuth 2.0 credential or the server token must be present.
         */
        public Builder setCredential(Credential credential) {
            this.credential = credential;
            return this;
        }

        /**
         * Sets the environment for all API requests. Optional and defaults to
         * {@link Environment#PRODUCTION}.
         */
        public Builder setEnvironment(Environment environment) {
            this.environment = environment;
            return this;
        }

        /**
         * Sets the Server Token to be used with requests.  Exactly one of the server token or the OAuth 2.0 credential
         * must be present.
         */
        public Builder setServerToken(String serverToken) {
            this.serverToken = serverToken;
            return this;
        }
        
        /**
         * Sets the requested Locale through the Accept-Language HTTP header. See https://developer.uber.com/docs/localization for
         * possible locales
         */
        public Builder setAcceptLanguage(Locale locale) {
            this.locale = locale;
            return this;
        }

        private void validate() {
            Preconditions.checkState(credential != null || serverToken != null,
                    "An OAuth 2.0 credential or a server token is required to create a session.");
            Preconditions.checkState((credential == null && serverToken != null) ||
                    (credential != null && serverToken == null), "Session must have either an OAuth 2.0 credential or a server token, not both.");
        }

        /**
         * Builds a {@code Session} object;
         */
        public Session build() {
            validate();

            Environment environment = this.environment != null ? this.environment : Environment.PRODUCTION;

            return new Session(credential, environment, serverToken, locale);
        }
    }

    /**
     * Gets the environment all requests are made against.
     */
    @Nonnull
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Gets the credential requests are backed by if there is no server token.
     */
    @Nullable
    public Credential getCredential() {
        return credential;
    }

    /**
     * Gets the server token requests are backed by if there is no OAuth 2.0 Credential.
     */
    @Nullable
    public String getServerToken() {
        return serverToken;
    }
    
    /**
     * Get the requested language Locale for API requests.
     */
    @Nullable
    public Locale getLocale() {
        return locale;
    }
}
