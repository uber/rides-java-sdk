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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.uber.sdk.rides.auth.OAuth2Helper;
import com.uber.sdk.rides.client.internal.RetrofitUberRidesClient;

import javax.annotation.Nonnull;

/**
 * A factory for creating {@link UberRidesSyncService synchronous} and
 * {@link UberRidesAsyncService asynchronous} services. One service should be used per Session to make
 * calls to any number of generated services as demonstrated:</p><br>
 * <pre><code>
 *     // Build an OAuth2Credentials object with your secrets.
 *     final OAuth2Credentials oAuth2Credentials = new OAuth2Credentials.Builder()
 *          // Set the appropriate properties.
 *          .build();
 *
 *     // Send user to authorize your application.
 *     String authorizationUrl = oAuth2Credentials.getAuthorizationUrl();
 *     String authorizationCode = redirectUserToAndCaptureCallback(authorizationUrl);
 *
 *     // Authenticate the user with the authorization code.
 *     final Credential credential = oAuth2Credentials.authenticate(authorizationCode);
 *
 *     // Build a Session object with your credentials and environment
 *     final Session session = new Session.Builder()
 *          .setCredential(credential)
 *          .setEnvironment(Session.Environment.SANDBOX)
 *          .build();
 *
 *     // Create the Uber API client once you are authenticated (either by loading an existing
 *     // Credential with oAuth2Credentials.loadCredential(...) or by running through the above
 *     // flow the first time).
 *     final UberRidesSyncService uberApiService = UberRidesServices.createSync(session);
 *
 *     // ...
 *
 *     // Call a service method.
 *     Response&lt;ResponseType&gt; uberApiService.serviceMethod("parameter1", "parameter2");
 * </code></pre>
 */
public class UberRidesServices<T extends UberRidesService> {

    private final Session session;
    private final LogLevel logLevel;

    /**
     * Determines the level of logging.
     */
    public enum LogLevel {

        /** No logging. */
        NONE,

        /**
         * Log the headers, body, and metadata for both requests and responses.
         */
        FULL;
    }

    private UberRidesServices(@Nonnull Session session, @Nonnull LogLevel logLevel) {
        this.session = session;
        this.logLevel = logLevel;
    }

    /**
     * Builder for {@link UberRidesAsyncService} or {@link UberRidesSyncService} objects. Instantiated
     * using either {@link Builder#sync()} or {@link Builder#async()}.
     */
    public static class Builder<U extends UberRidesService> {

        private Session session;
        private LogLevel logLevel;

        /**
         * Sets the Session for the service.  See {@link Session} for assistance with creating one.
         */
        public Builder<U> setSession(Session session) {
            this.session = session;
            return this;
        }

        /**
         * Sets the Log level for requests. Optional and defaults to {@link LogLevel#NONE}.
         */
        public Builder<U> setLogLevel(LogLevel logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        @VisibleForTesting
        UberRidesServices<U> buildUberApiServices() {
            LogLevel logLevel = this.logLevel != null ? this.logLevel : LogLevel.NONE;

            return new UberRidesServices<>(session, logLevel);
        }

        private void validate() {
            Preconditions.checkNotNull(session, "A session is required to create a service.");
        }

        /**
         * Builds a synchronous or asynchronous {@code UberRidesService} object.
         */
        public U build() {
            validate();
            return buildUberApiServices().create();
        }

        /**
         * Creates a {@code Builder} for asynchronous service clients.
         */
        public static Builder<UberRidesAsyncService> async() {
            return new Builder<>();
        }

        /**
         * Creates a {@code Builder} for synchronous service clients.
         */
        public static Builder<UberRidesSyncService> sync() {
            return new Builder<>();
        }
    }

    /**
     * Gets a new synchronous Uber API Service with default logging and a provided session.
     */
    public static UberRidesSyncService createSync(Session session) {
        return UberRidesServices.Builder.sync().setSession(session).build();
    }

    /**
     * Gets a new asynchronous Uber API Service with default logging and a provided session.
     */
    public static UberRidesAsyncService createAsync(Session session)  {
        return UberRidesServices.Builder.async().setSession(session).build();
    }

    /**
     * Gets the session that all requests are backed by.
     */
    @Nonnull
    @VisibleForTesting
    Session getSession() {
        return session;
    }

    /**
     * Gets the level of logging that all requests output.
     */
    @Nonnull
    @VisibleForTesting
    LogLevel getLogLevel() {
        return logLevel;
    }

    private <T extends UberRidesService> T create() {
        return RetrofitUberRidesClient.getUberApiService(session, new OAuth2Helper(), logLevel);
    }
}
