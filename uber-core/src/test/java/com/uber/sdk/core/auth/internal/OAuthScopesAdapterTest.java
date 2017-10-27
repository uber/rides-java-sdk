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

package com.uber.sdk.core.auth.internal;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.uber.sdk.core.auth.AccessToken;
import com.uber.sdk.core.auth.Scope;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class OAuthScopesAdapterTest {
    private static final String ACCESS_TOKEN = "{" +
            "\"last_authenticated\":1464137596," +
            "\"access_token\":\"Access999Token\"," +
            "\"expires_in\":2592000," +
            "\"token_type\":\"Bearer\"," +
            "\"scope\":\"request all_trips profile\"," +
            "\"refresh_token\":\"888RefreshToken\"" +
            "}";

    private JsonAdapter<AccessToken> jsonAdapter;

    @Before
    public void setUp() {
        Moshi moshi = new Moshi.Builder().add(new OAuthScopesAdapter()).build();

        jsonAdapter = moshi.adapter(AccessToken.class);
    }

    @Test
    public void fromJson() throws Exception {
        AccessToken accessToken = jsonAdapter.fromJson(ACCESS_TOKEN);
        assertThat(accessToken.getToken()).isEqualTo("Access999Token");
        assertThat(accessToken.getExpiresIn()).isEqualTo(2592000);
        assertThat(accessToken.getRefreshToken()).isEqualTo("888RefreshToken");
        assertThat(accessToken.getScopes()).contains(Scope.REQUEST, Scope.ALL_TRIPS, Scope.PROFILE);
    }

    @Test
    public void toJson() throws Exception {
        AccessToken accessToken = new AccessToken(2592000,
                Arrays.asList(Scope.REQUEST, Scope.ALL_TRIPS, Scope.PROFILE),
                "Access999Token", "888RefreshToken", "Bearer");

        Collection<Scope> scopes = accessToken.getScopes();
        String json = jsonAdapter.toJson(accessToken);
        assertThat(json).containsOnlyOnce("\"scope\":\"" + Scope.toStandardString(scopes) + "\"");
    }
}