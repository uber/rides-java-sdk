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


import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import static com.uber.sdk.core.auth.Scope.ScopeType.GENERAL;
import static com.uber.sdk.core.auth.Scope.ScopeType.PRIVILEGED;

/**
 * An Uber API scope. See
 * <a href="https://developer.uber.com/v1/api-reference/#scopes">Scopes</a> for more
 * information.
 */
public enum Scope {

    /**
     * Pull trip data of a user's historical pickups and drop-offs.
     */
    HISTORY(GENERAL, 1),

    /**
     * Same as History without city information.
     */
    HISTORY_LITE(GENERAL, 2),

    /**
     * Retrieve user's available registered payment methods.
     */
    PAYMENT_METHODS(GENERAL, 4),

    /**
     * Save and retrieve user's favorite places.
     */
    PLACES(GENERAL, 8),

    /**
     * Access basic profile information on a user's Uber account.
     */
    PROFILE(GENERAL, 16),

    /**
     * Access the Ride Request Widget.
     */
    RIDE_WIDGETS(GENERAL, 32),

    /**
     * Request ride on the behalf of an Uber account.
     */
    REQUEST(PRIVILEGED, 64),

    /**
     * Request ride for a ride on the behalf of an Uber account.
     */
    REQUEST_RECEIPT(PRIVILEGED, 128),

    /**
     * Request list of all trips belong to a user.
     */
    ALL_TRIPS(PRIVILEGED, 256);


    private ScopeType mScopeType;

    /**
     * Use powers of two to allow bit masking operation.
     */
    private int mBitValue;

    Scope(ScopeType scopeType, int bitValue) {
        this.mScopeType = scopeType;
        this.mBitValue = bitValue;
    }

    /**
     * Gets the {@link ScopeType} associated with this {@link Scope}.
     *
     * @return the type of scope.
     */
    public ScopeType getScopeType() {
        return mScopeType;
    }

    /**
     * Gets the bit value that represents this.
     *
     * @return the int value that represents this’.
     */
    public int getBitValue() {
        return mBitValue;
    }

    /**
     * Category of {@link Scope} that describes its level of access.
     */
    public enum ScopeType {

        /**
         * {@link Scope}s that can be used without review.
         */
        GENERAL,

        /**
         * {@link Scope}s that require approval before opened to your users in production.
         */
        PRIVILEGED
    }

    public static Set<Scope> parseScopes(String concatenatedScopes) {
        Set<Scope> scopes = new LinkedHashSet<>();
        for (String scopeString : concatenatedScopes.split(" ")) {
            try {
                Scope scope = Scope.valueOf(scopeString.toUpperCase());
                scopes.add(scope);
            } catch (IllegalArgumentException ex) {
            }
        }
        return scopes;
    }

    public static Set<Scope> parseScopes(int bitValues) {
        Set<Scope> scopes = new LinkedHashSet<>();
        if (bitValues <= 0) {
            return scopes;
        }

        for (Scope scope : Scope.values()) {
            if ((bitValues & scope.mBitValue) == scope.mBitValue) {
                scopes.add(scope);
            }
        }

        return scopes;
    }

    public static String toStandardString(@Nonnull Collection<Scope> scopes) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        for (Scope scope : scopes) {
            stringBuilder.append(scope.toString().toLowerCase());
            if (i < scopes.size() - 1) {
                stringBuilder.append(' ');
            }
            i++;
        }
        return stringBuilder.toString();
    }
}
