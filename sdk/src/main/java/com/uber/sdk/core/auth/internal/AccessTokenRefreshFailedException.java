package com.uber.sdk.core.auth.internal;

public class AccessTokenRefreshFailedException extends RuntimeException{

    public AccessTokenRefreshFailedException(String message) { super(message); }
}
