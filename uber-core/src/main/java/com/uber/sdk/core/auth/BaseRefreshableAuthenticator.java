package com.uber.sdk.core.auth;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseRefreshableAuthenticator implements Authenticator {

    static final String HEADER_INVALID_SCOPES = "X-Uber-Missing-Scopes";
    static final int MAX_RETRIES = 3;

    @Override
    public final Request refresh(Response response) throws IOException {
        if (isRefreshable() && canRefresh(response) && canRetry(response)) {
            return doRefresh(response);
        }
        return null;
    }

    protected abstract Request doRefresh(Response response) throws IOException;

    /**
     * The Uber API returns invalid scopes as 401's and will migrate to 403's in the future.
     * This is a temporary measure and will be updated in the future.
     *
     * @param response to check for {@link BaseRefreshableAuthenticator#HEADER_INVALID_SCOPES} header.
     * @return true if a true 401 and can refresh, otherwise false
     */
    protected boolean canRefresh(Response response) {
        return response.header(HEADER_INVALID_SCOPES) == null;
    }


    protected boolean canRetry(Response response) {
        int responseCount = 1;
        while ((response = response.priorResponse()) != null) {
            responseCount++;
        }

        return responseCount < MAX_RETRIES;
    }
}
