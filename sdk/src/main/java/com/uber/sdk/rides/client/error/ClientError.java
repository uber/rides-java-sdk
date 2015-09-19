/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uber.sdk.rides.client.error;

/**
 * An error due to some form of client error.  For example bad input or attempting unauthorized access.
 */
public class ClientError implements UberError {

    private String code;
    private String message;

    public ClientError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * A short code referring to the error
     * @see <a href="https://developer.uber.com/v1/endpoints/">https://developer.uber.com/v1/endpoints/</a>
     */
    public String getCode() {
        return code;
    }

    /**
     * A message describing the error and possibly how to resolve it.
     */
    public String getMessage() {
        return message;
    }
}
