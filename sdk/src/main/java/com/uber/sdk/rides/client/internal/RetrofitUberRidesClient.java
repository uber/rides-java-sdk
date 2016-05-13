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

package com.uber.sdk.rides.client.internal;

import com.google.api.client.auth.oauth2.Credential;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.net.HttpHeaders;
import com.google.common.reflect.Reflection;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.uber.sdk.rides.auth.OAuth2Helper;
import com.uber.sdk.rides.client.Session;
import com.uber.sdk.rides.client.Session.Environment;
import com.uber.sdk.rides.client.UberRidesService;
import com.uber.sdk.rides.client.UberRidesServices;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Internal Client for creating {@link RetrofitUberRidesService services}.
 */
public class RetrofitUberRidesClient {

    @VisibleForTesting static final String LIB_VERSION = "0.3.0";

    /**
     * Gets a new Uber API service client.
     *
     * @param session The Uber API session
     * @param oAuth2Helper The OAuth 2.0 Helper
     * @param logLevel The log level.
     * @return A Uber API service client.
     */
    @SuppressWarnings("unchecked") // Class casting is ensured.
    public static <T extends UberRidesService> T getUberApiService(Session session,
            OAuth2Helper oAuth2Helper, UberRidesServices.LogLevel logLevel) {
        return (T) getUberApiService(session, oAuth2Helper, logLevel,
                session.getEndpointHost(), null, RetrofitUberRidesService.class);
    }

    /**
     * Gets an Uber API service client with a specified endpoint.
     *
     * @param session The Uber API session
     * @param oAuth2Helper The OAuth 2.0 Helper
     * @param logLevel The log level.
     * @param endpointHost The endpoint host for the API client.
     * @param httpClient The HTTP client
     * @return An Uber API service client.
     */
    @VisibleForTesting
    @SuppressWarnings("unchecked") // Class casting is ensured.
    static <T extends RetrofitUberRidesService, U extends UberRidesService> U getUberApiService(Session session,
            OAuth2Helper oAuth2Helper, UberRidesServices.LogLevel logLevel, String endpointHost,
            @Nullable OkHttpClient httpClient, Class<? extends T> internalApiServiceClass) {

        RestAdapter.LogLevel retrofitLogLevel = UberRidesServices.LogLevel.FULL.equals(logLevel) ?
                RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE;

        RestAdapter restAdapter;
        try {
            restAdapter = buildRestAdapter(session, endpointHost, oAuth2Helper, retrofitLogLevel, httpClient);
        } catch (IOException e) {
            throw new IllegalStateException("Could not build REST adapter.", e);
        }

        T internalService = Reflection.newProxy(internalApiServiceClass,
                new InvocationHandler<>(session.getEnvironment(), internalApiServiceClass,
                        restAdapter.create(internalApiServiceClass)));
        
        return (U) new RetrofitAdapter(internalService);
    }

    /**
     * Builds a RestAdapter.
     */
    private static RestAdapter buildRestAdapter(final Session session,
            String endpointHost,
            final OAuth2Helper oAuth2Helper,
            RestAdapter.LogLevel logLevel,
            OkHttpClient httpClient) throws IOException {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {

            @Override
            public void intercept(RequestFacade requestFacade) {
                Credential credential = session.getCredential();
                if (credential != null) {
                    oAuth2Helper.refreshCredentialIfNeeded(credential);
                    requestFacade.addHeader("Authorization", "Bearer " + credential.getAccessToken());
                } else {
                    requestFacade.addHeader("Authorization", "Token " + session.getServerToken());
                }

                if (session.getLocale() != null) {
                    requestFacade.addHeader("Accept-Language", session.getLocale().getLanguage());
                }

                requestFacade.addHeader("X-Uber-User-Agent", "Java Rides SDK v" + LIB_VERSION);
            }
        };

        if (httpClient == null) {
            httpClient = new OkHttpClient();
            httpClient.setConnectTimeout(1, TimeUnit.MINUTES);
            httpClient.setReadTimeout(1, TimeUnit.MINUTES);
            httpClient.setFollowRedirects(false);
            httpClient.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request oldRequest = chain.request();
                    Response response = chain.proceed(oldRequest);
                    if (response.isRedirect()) {
                        String redirectUrl = response.header(HttpHeaders.LOCATION);
                        Request newRequest = oldRequest.newBuilder()
                                .url(redirectUrl)
                                .build();
                        return chain.proceed(newRequest);
                    }
                    return response;
                }
            });
        }

        return new RestAdapter.Builder()
                .setEndpoint(endpointHost)
                .setConverter(new GsonConverter(new GsonBuilder().create()))
                .setRequestInterceptor(requestInterceptor)
                .setClient(new OkClient(httpClient))
                .setLogLevel(logLevel)
                .build();
    }

    /**
     * Invocation handler for API service calls.
     * @param <T> The API service type.
     */
    @VisibleForTesting
    static class InvocationHandler<T> implements java.lang.reflect.InvocationHandler {

        private final Environment environment;
        private final Class<?> uberApiServiceClass;
        private final T uberApiService;

        /**
         * Invocation handler for API service class.
         *
         * @param environment The API environment.
         * @param uberApiServiceClass The API service class.
         * @param uberApiService The API service client.
         */
        @VisibleForTesting
        InvocationHandler(Environment environment,
                Class<? extends T> uberApiServiceClass,
                T uberApiService) {
            this.environment = environment;
            this.uberApiServiceClass = uberApiServiceClass;
            this.uberApiService = uberApiService;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method uberApiServiceMethod = getMethodWithName(method.getName());
            if (environment.equals(Environment.PRODUCTION)
                    && uberApiServiceMethod.isAnnotationPresent(SandboxOnly.class)) {
                throw new IllegalStateException("Sandbox only methods can't be called in production.");
            }

            return uberApiServiceMethod.invoke(uberApiService, args);
        }

        /**
         * Gets the method with name from the underlying API service class.
         */
        private Method getMethodWithName(final String name) {
            try {
                return Iterables.getOnlyElement(
                        Sets.filter(Sets.newHashSet(uberApiServiceClass.getMethods()),
                                new Predicate<Method>() {
                                    @Override
                                    public boolean apply(@Nullable Method input) {
                                        return input != null && input.getName().equals(name);
                                    }
                                }));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Services may not contain duplicate names.", e);
            }
        }
    }

    /**
     * Indicates that methods can only be called on the
     * {@link Environment#SANDBOX Sandbox environment}.
     */
    @Target(ElementType.METHOD)
    @Inherited
    @Retention(RetentionPolicy.RUNTIME)
    @interface SandboxOnly {}
}
