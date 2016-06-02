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

package com.uber.sdk.rides.samples.servlet;

import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.auth.OAuth2Credentials;
import com.uber.sdk.rides.client.SessionConfiguration;

import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

/**
 * Sample server. Before starting, make sure to add the {@code REDIRECT_URI} to your application
 * on developer.uber.com and fill in {@code resources/secrets.properties} with your client
 * ID and secret.
 */
public class Server {

    public static final String USER_SESSION_ID = "userSessionId";

    // Set your server's port and callback URL.
    private static final int PORT = 8181;
    private static final String CALLBACK_URL = "/Callback";

    // IMPORTANT: Before starting the server, make sure to add this redirect URI to your
    // application at developers.uber.com.
    private static final String REDIRECT_URI = "http://localhost:" + PORT + CALLBACK_URL;

    public static void main(String[] args) throws Exception {
        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(PORT);

        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        server.setHandler(handler);

        handler.addServlet(SampleServlet.class, "/");
        handler.addServlet(OAuth2CallbackServlet.class, CALLBACK_URL);

        server.start();
        System.out.println("Server is running on: http://localhost:" + PORT );
        server.join();
    }

    /**
     * Creates an {@link OAuth2Credentials} object that can be used by any of the servlets.
     *
     * Throws an {@throws IOException} when no client ID or secret found in secrets.properties
     */
     static OAuth2Credentials createOAuth2Credentials(SessionConfiguration config) throws IOException {


        return new OAuth2Credentials.Builder()
                .setCredentialDataStoreFactory(MemoryDataStoreFactory.getDefaultInstance())
                .setRedirectUri(config.getRedirectUri())
                .setScopes(config.getScopes())
                .setClientSecrets(config.getClientId(), config.getClientSecret())
                .build();
    }

    static SessionConfiguration createSessionConfiguration() throws IOException {
        // Load the client ID and secret from a secrets properties file.
        Properties secrets = loadSecretProperties();

        String clientId = secrets.getProperty("clientId");
        String clientSecret = secrets.getProperty("clientSecret");

        if (clientId.equals("INSERT_CLIENT_ID_HERE") || clientSecret.equals("INSERT_CLIENT_SECRET_HERE")) {
            throw new IllegalArgumentException(
                    "Please enter your client ID and secret in the resoures/secrets.properties file.");
        }
        return new SessionConfiguration.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(REDIRECT_URI)
                .setScopes(Collections.singletonList(Scope.PROFILE))
                .build();
    }

    /**
     * Loads the application's secrets.
     */
    private static Properties loadSecretProperties() throws IOException {
        Properties properties = new Properties();
        InputStream propertiesStream = Server.class.getClassLoader().getResourceAsStream("secrets.properties");
        if (propertiesStream == null) {
            // Fallback to file access in the case of running from certain IDEs.
            File buildPropertiesFile = new File("src/main/resources/secrets.properties");
            if (buildPropertiesFile.exists()) {
                properties.load(new FileReader(buildPropertiesFile));
            } else {
                buildPropertiesFile = new File("samples/servlet-sample/src/main/resources/secrets.properties");
                if (buildPropertiesFile.exists()) {
                    properties.load(new FileReader(buildPropertiesFile));
                } else {
                    throw new IllegalStateException("Could not find secrets.properties");
                }
            }
        } else {
            properties.load(propertiesStream);
        }
        return properties;
    }
}
