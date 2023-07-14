package com.uber.sdk.core.auth.internal;

import com.uber.sdk.core.auth.AccessToken;

/**
 * Interface to request tokens from the backend
 */
public interface TokenRequestFlow {
    /**
     * Executes the token request
     * @param callback Callback will be invoked when the request succeeds or errors out.
     */
    void execute(TokenRequestFlowCallback callback);

    /**
     * Interface to provide callback for token request result
     */
    interface TokenRequestFlowCallback {
        /**
         * Called when token request finishes successfully
         * @param accessToken {@link AccessToken} object containing oauth tokens
         */
        void onSuccess(AccessToken accessToken);

        /**
         * Called when token request finishes with a failure
         * @param error
         */
        void onFailure(Throwable error);
    }
}
