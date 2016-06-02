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

import com.uber.sdk.core.auth.Scope;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.uber.sdk.rides.client.SessionConfiguration.EndpointRegion.CHINA;
import static com.uber.sdk.rides.client.SessionConfiguration.Environment.PRODUCTION;
import static com.uber.sdk.rides.client.SessionConfiguration.Environment.SANDBOX;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class SessionConfigurationTest {
    @Test
    public void getClientId_whenSetOnBuilder_setsOnConfiguration() throws Exception {
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .build();

        assertEquals("clientId", config.getClientId());
    }

    @Test
    public void getRedirectUri_whenSetOnBuilder_setsOnConfiguration() throws Exception {
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .setRedirectUri("redirectUri")
                .build();
        assertEquals("redirectUri", config.getRedirectUri());
    }

    @Test
    public void getEndpointRegion_whenSetOnBuilder_setsOnConfiguration() throws Exception {
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .setEndpointRegion(CHINA)
                .build();
        assertEquals(CHINA, config.getEndpointRegion());
    }

    @Test
    public void getEnvironment_whenSetOnBuilder_setsOnConfiguration() throws Exception {
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .setEnvironment(SANDBOX)
                .build();
        assertEquals(SANDBOX, config.getEnvironment());
    }

    @Test
    public void getScopes_whenSetOnBuilder_setsOnConfiguration() throws Exception {
        List<Scope> scopes = Arrays.asList(Scope.ALL_TRIPS, Scope.HISTORY);
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .setScopes(scopes)
                .build();
        assertTrue(scopes.containsAll(config.getScopes()));
    }

    @Test
    public void newBuilder_copiesConfiguration() throws Exception {
        SessionConfiguration config = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .setRedirectUri("test")
                .build();

        SessionConfiguration config2 = config.newBuilder().setClientId("clientId2").build();

        assertEquals("clientId", config.getClientId());
        assertEquals("clientId2", config2.getClientId());
        assertEquals("test", config2.getRedirectUri());
    }

    @Test(expected = NullPointerException.class)
    public void testBuilder_noClientId_throwsException() {
        new SessionConfiguration.Builder().build();
    }

    @Test
    public void testBuilder_withClientId_doesNotThrowError() {
        new SessionConfiguration.Builder().setClientId("clientId").build();
    }

    @Test
    public void buildSession_whenLocalizationProvided_shouldSucceed() throws Exception {
        new SessionConfiguration.Builder().setEnvironment(PRODUCTION)
                .setClientId("clientId")
                .setLocale(new Locale("sv", "SE"))
                .build();
    }

    @Test
    public void buildSession_whenNoEnvironmentSupplied_shouldUseProduction() throws Exception {
        SessionConfiguration sessionConfig = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .build();
        assertEquals(SessionConfiguration.Environment.PRODUCTION, sessionConfig.getEnvironment());
    }

    @Test
    public void buildSession_whenProductionEnvAndNotChina_shouldGiveNonCnProductionEndpointHost() throws Exception {
        SessionConfiguration sessionConfig = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .setEnvironment(PRODUCTION).build();
        assertEquals("https://api.uber.com", sessionConfig.getEndpointHost());
    }

    @Test
    public void buildSession_whenSandboxEnvAndNotChina_shouldGiveNonCnSandboxEndpointHost() throws Exception {
        SessionConfiguration sessionConfig = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .setEnvironment(SANDBOX).build();
        assertEquals("https://sandbox-api.uber.com", sessionConfig.getEndpointHost());
    }

    @Test
    public void buildSession_whenProductionEnvAndInChina_shouldGiveChinaProductionEndpointHost() throws Exception {
        SessionConfiguration sessionConfig = new SessionConfiguration.Builder()
                .setClientId("clientId")
                .setEnvironment(PRODUCTION)
                .setEndpointRegion(CHINA)
                .build();
        assertEquals("https://api.uber.com.cn", sessionConfig.getEndpointHost());
    }

    @Test
    public void buildSession_whenSandboxEnvAndInChina_shouldGiveChinaSandboxEndpointHost() throws Exception {
        SessionConfiguration sessionConfig = new SessionConfiguration.Builder().setEnvironment(SANDBOX)
                .setClientId("clientId")
                .setEndpointRegion(CHINA)
                .build();
        assertEquals("https://sandbox-api.uber.com.cn", sessionConfig.getEndpointHost());
    }
}