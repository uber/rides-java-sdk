package com.uber.sdk.rides.client.model;

/**
 * A user's Place. See
 * <a href="https://developer.uber.com/docs/v1-places-get">Places</a>
 * for more information.
 */
public class Place {

    /**
     * Represents the defined Places for a user.
     */
    public enum Places {
        HOME("home"),
        WORK("work");

        private String placeId;

        Places(String placeId) {
            this.placeId = placeId;
        }

        @Override
        public String toString() {
            return this.placeId;
        }
    }

    private String address;

    /**
     * Gets the address of the Place
     */
    public String getAddress() {
        return address;
    }
}
