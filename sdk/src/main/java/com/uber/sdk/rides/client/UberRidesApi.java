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

package com.uber.sdk.rides.client;

import com.squareup.moshi.Moshi;
import com.uber.sdk.rides.client.internal.ApiInterceptor;
import com.uber.sdk.rides.client.internal.PrimitiveAdapter;
import com.uber.sdk.rides.client.internal.RefreshAuthenticator;
import com.uber.sdk.rides.client.services.RidesService;

import javax.annotation.Nonnull;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class UberRidesApi {

    private final Retrofit retrofit;

    /**
     * Builder for {@link UberRidesApi}
     */
    public static class Builder {
        Session session;
        HttpLoggingInterceptor.Level logLevel;
        HttpLoggingInterceptor.Logger logger;
        OkHttpClient client;


        Builder(@Nonnull Session session) {
            this.session = session;
        }

        /**
         * Sets the Log level for requests.
         * Optional and defaults to {@link HttpLoggingInterceptor.Level#NONE}.
         */
        @Nonnull
        public Builder setLogLevel(@Nonnull HttpLoggingInterceptor.Level logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        /**
         * Sets the {@link okhttp3.logging.HttpLoggingInterceptor.Logger} to use.
         * Optional and defaults to {@link okhttp3.logging.HttpLoggingInterceptor.Logger#DEFAULT}
         */
        @Nonnull
        public Builder setLogger(@Nonnull HttpLoggingInterceptor.Logger logger) {
            this.logger = logger;
            return this;
        }

        /**
         * Sets an existing {@link OkHttpClient } to use for Uber Rides API requests
         *
         * @param client {@link OkHttpClient}
         * @return {@link Builder} for {@link UberRidesApi}
         */
        @Nonnull
        public Builder setOkHttpClient(@Nonnull OkHttpClient client) {
            this.client = client;
            return this;
        }

        /**
         * Create the {@link UberRidesApi} to be used.
         * @return {@link UberRidesApi}
         */
        public UberRidesApi build() {
            if (logLevel == null) {
                logLevel = HttpLoggingInterceptor.Level.NONE;
            }

            if (logger == null) {
                logger = HttpLoggingInterceptor.Logger.DEFAULT;
            }

            if (client == null) {
                client = new OkHttpClient();
            }

            HttpLoggingInterceptor loggingInterceptor = createLoggingInterceptor(logger, logLevel);
            OkHttpClient newClient = createClient(client, session, loggingInterceptor);
            Retrofit retrofit = createRetrofit(newClient, session);

            return new UberRidesApi(retrofit);
        }

        HttpLoggingInterceptor createLoggingInterceptor(HttpLoggingInterceptor.Logger logger,
                                                        HttpLoggingInterceptor.Level level) {
            final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(logger);
            loggingInterceptor.setLevel(level);
            return loggingInterceptor;
        }

        OkHttpClient createClient(OkHttpClient client,
                                  Session session,
                                  HttpLoggingInterceptor loggingInterceptor) {

            return client.newBuilder()
                    .authenticator(new RefreshAuthenticator(session.getAuthenticator()))
                    .addInterceptor(new ApiInterceptor(session.getAuthenticator()))
                    .addInterceptor(loggingInterceptor)
                    .build();
        }

        Retrofit createRetrofit(OkHttpClient client, Session session) {
            Moshi moshi = new Moshi.Builder().add(new PrimitiveAdapter()).build();

            return new Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .baseUrl(session.getAuthenticator().getSessionConfiguration().getEndpointHost())
                    .client(client)
                    .build();
        }
    }

    /**
     * Starts a {@link Builder} with a {@link Session} to create Uber Services.
     *
     * @param session required {@link Session}
     */
    @Nonnull
    public static Builder with(@Nonnull Session session) {
        return new Builder(session);
    }

    private UberRidesApi(@Nonnull Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    /**
     * Get the {@link RidesService} to use with the Uber API.
     * Consumers should cache and reuse this object.
     *
     * @return {@link RidesService}
     */
    public RidesService createService() {
        return retrofit.create(RidesService.class);
    }
}
