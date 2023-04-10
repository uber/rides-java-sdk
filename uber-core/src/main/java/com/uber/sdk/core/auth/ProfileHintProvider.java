package com.uber.sdk.core.auth;

import com.uber.sdk.core.auth.internal.ProfileHint;

public interface ProfileHintProvider {
    ProfileHint getProfileHint();
}
