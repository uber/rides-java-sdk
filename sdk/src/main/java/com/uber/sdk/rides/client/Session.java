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

import static com.uber.sdk.rides.client.Session.EndpointRegion.WORLD;

/**
 * A session containing the details of how an {@link UberRidesService} will interact with the API.
 * Does authentication through either a server token or OAuth 2.0 credential, exactly one of which must exist.
 */
public class Session {

    private final Credential credential;
    private final EndpointRegion endpointRegion;
    private final Environment environment;
    private final Locale locale;
    private final String serverToken;

    /**
     * An Uber API Environment. See
     * <a href="https://developer.uber.com/v1/sandbox">Sandbox</a> for more
     * information.
     */
    public enum Environment {
        PRODUCTION("api"),
        SANDBOX("sandbox-api");

        public String subDomain;

        Environment(String subDomain) {
            this.subDomain = subDomain;
        }
    }

    public enum EndpointRegion {
        WORLD("uber.com"),
        CHINA("uber.com.cn");

        public String domain;

        EndpointRegion(String domain) {
            this.domain = domain;
        }
    }

    private Session(@Nullable Credential credential,
            @Nonnull EndpointRegion endpointRegion,
            @Nonnull Environment environment,
            @Nullable Locale locale,
            @Nullable String serverToken) {
        this.credential = credential;
        this.endpointRegion = endpointRegion;
        this.environment = environment;
        this.locale = locale;
        this.serverToken = serverToken;
    }

    /**
     * Builder for {@link Session} objects.
     */
    public static class Builder {

        private EndpointRegion endpointRegion;
        private Credential credential;
        private Environment environment;
        private Locale locale;
        private String serverToken;

        /**
         * Sets the region to be used for requests.
         */
        public Builder setEndpointRegion(EndpointRegion endpointRegion) {
            this.endpointRegion = endpointRegion;
            return this;
        }

        /**
         * Sets the requested Locale through the Accept-Language HTTP header. See
         * <a href="https://developer.uber.com/docs/localization">Localization</a>
         * for possible locales.
         */
        public Builder setAcceptLanguage(Locale locale) {
            this.locale = locale;
            return this;
        }

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

        private void validate() {
            Preconditions.checkState(credential != null || serverToken != null,
                    "An OAuth 2.0 credential or a server token is required to create a session.");
            Preconditions.checkState((credential == null && serverToken != null) ||
                    (credential != null && serverToken == null), "Session must have either an OAuth 2.0 credential or a server token, not both.");
            Preconditions.checkState(environment != null, "Must supply an Environment");
        }

        /**
         * Builds a {@code Session} object;
         */
        public Session build() {
            validate();

            if (endpointRegion == null) {
                endpointRegion = WORLD;
            }

            return new Session(credential, endpointRegion, environment, locale, serverToken);
        }
    }

    /**
     * Gets the endpoint host used to hit the Uber API.
     */
    public String getEndpointHost() {
        return String.format("https://%s.%s", environment.subDomain, endpointRegion.domain);
    }

    /**
     * Gets the {@link EndpointRegion} to be used for requests.
     */
    @Nonnull
    public EndpointRegion getEndpointRegion() {
        return endpointRegion;
    }

    /**
     * Gets the credential requests are backed by if there is no server token.
     */
    @Nullable
    public Credential getCredential() {
        return credential;
    }

    /**
     * Gets the environment all requests are made against.
     */
    @Nonnull
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Get the requested language Locale for requests.
     */
    @Nullable
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets the server token requests are backed by if there is no OAuth 2.0 Credential.
     */
    @Nullable
    public String getServerToken() {
        return serverToken;
    }
}
