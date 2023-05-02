package com.uber.sdk.core.auth;

import com.squareup.moshi.Json;

import java.io.Serializable;

import javax.annotation.Nonnull;

public class ProfileHint implements Serializable {

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

    public static class Builder {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        public Builder firstName(@Nonnull String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(@Nonnull String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(@Nonnull String email) {
            this.email = email;
            return this;
        }

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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Builder newBuilder() {
        return new Builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone);
    }
}
