package com.uber.sdk.core.auth.internal;

import com.squareup.moshi.Json;

public class LoginPARResponse {
        @Json(name = "request_uri")
        public String requestUri;
        @Json(name = "expires_in")
        public String expiresIn;
    }