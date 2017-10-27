/*
 * Copyright (c) 2016 Uber Technologies, Inc.
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

package com.uber.sdk.rides.client.error;

import javax.annotation.Nullable;

/**
 * Meta information about an {@link ApiError}.  See specific endpoint
 * <a href="https://developer.uber.com/docs">documentation</a> for what can be contained here.
 */
public class Meta {

    @Nullable
    private final SurgeConfirmation surge_confirmation;

    public Meta(@Nullable SurgeConfirmation surgeConfirmation) {
        this.surge_confirmation = surgeConfirmation;
    }

    /**
     * @return The {@link SurgeConfirmation} from the meta data if it was present.
     */
    @Nullable
    public SurgeConfirmation getSurgeConfirmation() {
        return surge_confirmation;
    }
}
