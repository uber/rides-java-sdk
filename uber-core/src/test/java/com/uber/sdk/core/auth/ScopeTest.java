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

package com.uber.sdk.core.auth;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopeTest {

    @Test
    public void testParseScopesWithZero_shouldReturnNothing() throws Exception {
        Set<Scope> scopes = Scope.parseScopes(0);

        assertThat(scopes).isEmpty();
    }

    @Test
    public void testParseScopesWithNegativeValue_shouldReturnNothing() throws Exception {
        Set<Scope> scopes = Scope.parseScopes(-32);

        assertThat(scopes).isEmpty();
    }

    @Test
    public void testParseScopesWithOneScope_shouldReturn() throws Exception {
        Set<Scope> scopes = Scope.parseScopes(Scope.HISTORY.getBitValue());

        assertThat(scopes).contains(Scope.HISTORY);
    }

    @Test
    public void testParseScopesWithMultipleGeneralScopes_shouldReturn() throws Exception {
        Set<Scope> scopes = Scope.parseScopes(Scope.HISTORY.getBitValue() | Scope.PROFILE.getBitValue());

        assertThat(scopes).contains(Scope.HISTORY, Scope.PROFILE);
    }

    @Test
    public void testParseScopesWithMixLevelScopes_shouldReturn() throws Exception {
        Set<Scope> scopes = Scope.parseScopes(
                Scope.HISTORY.getBitValue() | Scope.REQUEST.getBitValue() | Scope.PROFILE.getBitValue());

        assertThat(scopes).contains(Scope.HISTORY, Scope.REQUEST, Scope.PROFILE);
    }

    @Test
    public void testCustomScopes_shouldIgnore() throws Exception {
        String scopeString = "history profile test";
        Set<Scope> scopes = Scope.parseScopes(scopeString);
        assertThat(scopes).contains(Scope.HISTORY, Scope.PROFILE);
        assertThat(scopes.size()).isEqualTo(2);
    }

    @Test
    public void testToStandardStringOneScope_noSpace() {
        Collection<Scope> scopes = Arrays.asList(Scope.HISTORY);

        assertThat(Scope.toStandardString(scopes)).isEqualTo("history");
    }

    @Test
    public void testToStandardStringMultiScopes_spaceDelimited() {
        Collection<Scope> scopes = Arrays.asList(Scope.HISTORY, Scope.ALL_TRIPS);

        assertThat(Scope.toStandardString(scopes)).isEqualTo("history all_trips");
    }

    @Test
    public void testParseOneScope_shouldCreateCollection() {
        assertThat(Scope.parseScopes("all_trips")).contains(Scope.ALL_TRIPS);
    }

    @Test
    public void testParseMultiScopes_shouldCreateCollection() {
        assertThat(Scope.parseScopes("history all_trips")).contains(Scope.HISTORY, Scope.ALL_TRIPS);
    }
}