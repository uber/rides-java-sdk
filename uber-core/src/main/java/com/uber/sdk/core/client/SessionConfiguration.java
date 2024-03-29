/*
 * Copyright (c) 2016 Uber Technologies, Inc.
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

package com.uber.sdk.core.client;

import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.core.auth.ProfileHint;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;

import javax.annotation.Nonnull;

import static com.uber.sdk.core.client.SessionConfiguration.EndpointRegion.DEFAULT;
import static com.uber.sdk.core.client.utils.Preconditions.checkNotNull;

/**
 * LoginConfiguration is used to setup primitives needed for the Uber SDK to authenticate.
 */
public class SessionConfiguration implements Serializable {
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
        DEFAULT("uber.com");

        private String domain;

        EndpointRegion(String domain) {
            this.domain = domain;
        }

        /**
         * @return domain to use.
         */
        public String getDomain() {
            return domain;
        }
    }

    /**
     * Builder for {@link SessionConfiguration}
     */
    public static class Builder {
        private String clientId;
        private String clientSecret;
        private String serverToken;
        private String redirectUri;
        private Environment environment;
        private Collection<Scope> scopes;
        private Collection<String> customScopes;
        private Locale locale;
        private ProfileHint profileHint;

        /**
         * The Uber API requires a registered clientId to be sent along with API requests and Deeplinks.
         * This can be registered and retrieved on the developer dashboard at https://developer.uber.com/
         *
         * @param clientId to be used for identification
         * @return
         */
        public Builder setClientId(@Nonnull String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * The Uber API requires a registered clientSecret to be used for Authentication.
         * This can be registered and retrieved on the developer dashboard at https://developer.uber.com/
         *
         * Do not set client secret for Android or client side applications.
         *
         * @param clientSecret to be used for identification
         * @return
         */
        public Builder setClientSecret(@Nonnull String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * The Uber API can use a server token for some endpoints.
         *
         * @param serverToken to be used for identification
         * @return
         */
        public Builder setServerToken(@Nonnull String serverToken) {
            this.serverToken = serverToken;
            return this;
        }

        /**
         * Sets the redirect URI that is registered for this application.
         *
         * @param redirectUri the redirect URI {@link String} for this application.
         */
        public Builder setRedirectUri(@Nonnull String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        /**
         * Sets the {@link Environment} to be used for API requests
         *
         * @param environment to be set
         * @return
         */
        public Builder setEnvironment(@Nonnull Environment environment) {
            this.environment = environment;
            return this;
        }

        /**
         * Sets the Scope Collection to be used when requesting authentication
         *
         * @param scopes to be set
         * @return
         */
        public Builder setScopes(@Nonnull Collection<Scope> scopes) {
            this.scopes = scopes;
            return this;
        }

        /**
         * Sets a list of custom scopes that your application must be explicitly whitelisted
         * for. For any documented scopes, please use {@link #setScopes(Collection)} instead.
         */
        public Builder setCustomScopes(@Nonnull Collection<String> scopes) {
            this.customScopes = scopes;
            return this;
        }

        /**
         * Sets the requested Locale through the Accept-Language HTTP header. See
         * <a href="https://developer.uber.com/docs/localization">Localization</a>
         * for possible locales.
         */
        public Builder setLocale(@Nonnull Locale locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Sets the {@link ProfileHint} for prefilling some user personal information during onboarding
         * for possible locales.
         */
        public Builder setProfileHint(@Nonnull ProfileHint profileHint) {
            this.profileHint = profileHint;
            return this;
        }

        /**
         * Constructs {@link SessionConfiguration} from set Builder parameters.
         *
         * @return {@link SessionConfiguration}
         * @throws NullPointerException when clientId has not been set
         */
        public SessionConfiguration build() {
            checkNotNull(clientId, "Client must be set");

            if (environment == null) {
                environment = Environment.PRODUCTION;
            }

            if (locale == null) {
                locale = Locale.US;
            }

            if (scopes == null) {
                scopes = new HashSet<>();
            } else {
                scopes = new HashSet<>(scopes);
            }

            if (customScopes == null) {
                customScopes = new HashSet<>();
            } else {
                customScopes = new HashSet<>(customScopes);
            }

            return new SessionConfiguration(
                    clientId,
                    clientSecret,
                    serverToken,
                    redirectUri,
                    DEFAULT,
                    environment,
                    scopes,
                    customScopes,
                    locale,
                    profileHint);
        }
    }

    private final String clientId;
    private final String clientSecret;
    private final String serverToken;
    private final String redirectUri;
    private final EndpointRegion endpointRegion;
    private final Environment environment;
    private final Collection<Scope> scopes;
    private final Collection<String> customScopes;
    private final Locale locale;
    private ProfileHint profileHint;

    protected SessionConfiguration(@Nonnull String clientId,
                                   @Nonnull String clientSecret,
                                   @Nonnull String serverToken,
                                   @Nonnull String redirectUri,
                                   @Nonnull EndpointRegion endpointRegion,
                                   @Nonnull Environment environment,
                                   @Nonnull Collection<Scope> scopes,
                                   @Nonnull Collection<String> customScopes,
                                   @Nonnull Locale locale,
                                   ProfileHint profileHint) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.serverToken = serverToken;
        this.redirectUri = redirectUri;
        this.endpointRegion = endpointRegion;
        this.environment = environment;
        this.scopes = scopes;
        this.customScopes = customScopes;
        this.locale = locale;
        this.profileHint = profileHint;
    }

    /**
     * Gets the Client ID to be used by the SDK for requests.
     *
     * @return The Client ID.
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Gets the Client Secret to be used by the SDK for requests.
     *
     * @return The Client Secret.
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * Gets the server Token to be used by the SDK for requests
     *
     * @return The Server Token.
     */
    public String getServerToken() {
        return serverToken;
    }

    /**
     * Gets the Redirect URI to be used for implicit grant.
     *
     * @return The Redirect URI.
     */
    public String getRedirectUri() {
        return redirectUri;
    }

    /**
     * Gets the environment configured, either {@link Environment#PRODUCTION} or {@link Environment#SANDBOX}
     *
     * @return {@link Environment} that is configured
     */
    public Environment getEnvironment() {
        return environment;
    }

    /**
     * Gets the current {@link EndpointRegion} the SDK is using.
     * Defaults to {@link EndpointRegion#DEFAULT}.
     *
     * @return the {@link EndpointRegion} the SDK us using.
     */
    public EndpointRegion getEndpointRegion() {
        return endpointRegion;
    }

    /**
     * Gets the endpoint host used to hit the Uber API.
     */
    @Nonnull
    public String getEndpointHost() {
        return String.format("https://%s.%s", environment.subDomain, DEFAULT.getDomain());
    }

    /**
     * Gets the login host used to sign in to the Uber API.
     */
    @Nonnull
    public String getLoginHost() {
        return String.format("https://auth.%s", DEFAULT.getDomain());
    }

    /**
     * Gets the {@link Scope}'s set for authentication
     *
     * @return The Scope Collection
     */
    public Collection<Scope> getScopes() {
        return scopes;
    }

    /**
     * Gets a list of custom scopes that your application must be explicitly whitelisted
     * for. For any documented scopes, please use {@link #getScopes()} instead.
     *
     * @return The Scope Collection
     */
    public Collection<String> getCustomScopes() {
        return customScopes;
    }

    /**
     * Get the requested language Locale for requests
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets the {@link ProfileHint} used to prefill user's profile information
     * @return
     */
    public ProfileHint getProfileHint() {
        return profileHint;
    }

    public Builder newBuilder() {
        return new Builder()
                .setClientId(clientId)
                .setRedirectUri(redirectUri)
                .setEnvironment(environment)
                .setScopes(scopes)
                .setProfileHint(profileHint);
    }
}
