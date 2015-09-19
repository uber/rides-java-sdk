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

package com.uber.sdk.rides.client.model;

import javax.annotation.Nullable;

/**
 * Parameters to update a sandbox product.
 */
public class SandboxProductRequestParameters {

    @Nullable private Float surge_multiplier;
    @Nullable private Boolean drivers_available;

    /**
     * Builder for product request parameters.
     */
    public static class Builder {

        @Nullable private Float surgeMultiplier;
        @Nullable private Boolean driversAvailable;

        /**
         * Sets the surge multiplier to a Product should have when making a Request in the Sandbox.
         * A multiplier greater than or equal to 2.0 will require the two stage confirmation screen.
         * If {@link #driversAvailable} is false, it will override any multiplier set here (i.e.
         * there are no drivers to apply surge pricing to).
         */
        public Builder setSurgeMultiplier(float surgeMultiplier) {
            this.surgeMultiplier = surgeMultiplier;
            return this;
        }

        /**
         * Sets the availability of drivers to simulate. If false, attempts to make a Request in the
         * Sandbox will return a no_drivers_available error
         */
        public Builder setDriversAvailable(boolean driversAvailable) {
            this.driversAvailable = driversAvailable;
            return this;
        }

        /**
         * Builds a {@link SandboxProductRequestParameters}.
         */
        public SandboxProductRequestParameters build() {
            SandboxProductRequestParameters sandboxProductRequestParameters = new SandboxProductRequestParameters();

            sandboxProductRequestParameters.surge_multiplier = surgeMultiplier;
            sandboxProductRequestParameters.drivers_available = driversAvailable;

            return sandboxProductRequestParameters;
        }
    }

    private SandboxProductRequestParameters() {}

    @Nullable
    public Float getSurgeMultiplier() {
        return surge_multiplier;
    }

    @Nullable
    public Boolean getDriversAvailable() {
        return drivers_available;
    }
}
