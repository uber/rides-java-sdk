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

package com.uber.sdk.rides.client.error;

import com.uber.sdk.rides.client.model.RideRequestParameters;
import com.uber.sdk.rides.client.services.RidesService;

import javax.annotation.Nonnull;

/**
 * Used to confirm surge pricing when {@link RidesService#requestRide(RideRequestParameters)} has failed.  See ride
 * request <a href="https://developer.uber.com/docs/v1-requests#section-error-responses">documentation</a> for more
 * info.
 */
public final class SurgeConfirmation {

    @Nonnull
    public final String href;
    @Nonnull
    public final String surge_confirmation_id;
    public final float multiplier;
    public final long expires_at;

    public SurgeConfirmation(
            @Nonnull String href,
            @Nonnull String surgeConfirmationId,
            float multiplier,
            long expiresAt) {
        this.href = href;
        this.surge_confirmation_id = surgeConfirmationId;
        this.multiplier = multiplier;
        this.expires_at = expiresAt;
    }

    /**
     * @return the href to be presented to the user for surge confirmation.
     */
    @Nonnull
    public String getHref() {
        return href;
    }

    /**
     * @return the unique identifier of this surge confirmation.
     */
    @Nonnull
    public String getSurgeConfirmationId() {
        return surge_confirmation_id;
    }

    /**
     * @return the surge multiplier.
     */
    public float getMultiplier() {
        return multiplier;
    }

    /**
     * @return the UTC expiration time for this surge confirmation.
     */
    public long getExpiresAt() {
        return expires_at;
    }
}
