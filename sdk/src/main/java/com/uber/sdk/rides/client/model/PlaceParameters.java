package com.uber.sdk.rides.client.model;

import javax.annotation.Nonnull;

/**
 * Parameters that define a Place
 */
public class PlaceParameters {

    private String address;

    private PlaceParameters(@Nonnull String address) {
        this.address = address;
    }

    /**
     * Builder for place parameters
     */
    public static class Builder {

        private String address;

        public Builder setAddress(@Nonnull String address) {
            this.address = address;
            return this;
        }

        public PlaceParameters build() {
            return new PlaceParameters(address);
        }

    }

}
