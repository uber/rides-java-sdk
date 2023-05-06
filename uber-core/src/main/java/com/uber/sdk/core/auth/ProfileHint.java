package com.uber.sdk.core.auth;

import com.squareup.moshi.Json;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * {@Link #ProfileHint} is builder to setup user's personal information like phone number, email
 * firstName and lastName
 */
final public class ProfileHint implements Serializable {
    /**
     * Builder class for {@link ProfileHint}
     */
    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;

        /**
         * Set first name
         *
         * @param firstName first name of the user
         */
        public Builder firstName(@Nonnull String firstName) {
            this.firstName = firstName;
            return this;
        }

        /**
         * Set last name
         *
         * @param lastName last name of the user
         */
        public Builder lastName(@Nonnull String lastName) {
            this.lastName = lastName;
            return this;
        }

        /**
         * Set email address
         *
         * @param email email address of the user
         */
        public Builder email(@Nonnull String email) {
            this.email = email;
            return this;
        }

        /**
         * Set phone number as a string including country code
         *
         * @param phone phone number of the user
         */
        public Builder phone(@Nonnull String phone) {
            this.phone = phone;
            return this;
        }

        public ProfileHint build() {
            return new ProfileHint(
                    firstName,
                    lastName,
                    email,
                    phone
            );
        }
    }

    /**
     * Gets the first name of the user
     *
     * @return The first name of the user if set, null otherwise
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the last name of the user
     *
     * @return The last name of the user if set, null otherwise
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the email address of the user
     *
     * @return The email address of the user if set, null otherwise
     */
    public String getEmail() {
        return email;
    }

    /**
     * Gets the phone number of the user as a string including country code
     *
     * @return The phone number of the user if set, null otherwise
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns new Builder with the current instance's properties as default values
     *
     * @return a new Builder object
     */
    public Builder newBuilder() {
        return new Builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone);
    }

    @Json(name = "first_name")
    private final String firstName;
    @Json(name = "last_name")
    private final String lastName;
    @Json(name = "email")
    private final String email;
    @Json(name = "phone")
    private final String phone;

    private ProfileHint(
            String firstName,
            String lastName,
            String email,
            String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
}
