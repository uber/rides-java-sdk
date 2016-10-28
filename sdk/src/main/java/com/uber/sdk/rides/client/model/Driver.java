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

package com.uber.sdk.rides.client.model;

import javax.annotation.Nullable;

/**
 * The vehicle's driver.
 */
public class Driver {

    private String phone_number;
    @Nullable
    private String sms_number;
    @Nullable
    private Float rating;
    @Nullable
    private String picture_url;
    private String name;

    /**
     * The formatted phone number for contacting the driver.
     */
    public String getPhoneNumber() {
        return phone_number;
    }

    /**
     * The formatted sms number for contacting the driver.
     */
    @Nullable
    public String getSmsNumber() {
        return sms_number;
    }

    /**
     * The driver's star rating out of 5 stars.
     */
    @Nullable
    public Float getRating() {
        return rating;
    }

    /**
     * The URL to the photo of the driver.
     */
    @Nullable
    public String getPictureUrl() {
        return picture_url;
    }

    /**
     * The first name of the driver.
     */
    public String getName() {
        return name;
    }
}