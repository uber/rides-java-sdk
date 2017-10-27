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

package com.uber.sdk.core.client.internal;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class BigDecimalAdapterTest {

    @Test
    public void toJson_shouldWork() {
        Moshi moshi = new Moshi.Builder().add(new BigDecimalAdapter()).build();
        JsonAdapter<BigDecimalModel> adapter = moshi.adapter(BigDecimalModel.class);
        BigDecimalModel bigDecimalModel = new BigDecimalModel();
        bigDecimalModel.presentDecimal = new BigDecimal("1.23");
        String json = adapter.toJson(bigDecimalModel);
        assertThat(json).isEqualTo("{\"presentDecimal\":1.23}");
    }

    @Test
    public void fromJson_shouldHandleAbsent_andNull_andNonNullValues() throws IOException {
        Moshi moshi = new Moshi.Builder().add(new BigDecimalAdapter()).build();

        JsonAdapter<BigDecimalModel> adapter = moshi.adapter(BigDecimalModel.class);
        BigDecimalModel model = adapter.fromJson("{\"nullDecimal\":null,\"presentDecimal\":1.23}");
        assertThat(model.absentDecimal).isNull();
        assertThat(model.nullDecimal).isNull();
        assertThat(model.presentDecimal).isEqualTo(new BigDecimal("1.23"));
    }

    private static class BigDecimalModel {

        private BigDecimal absentDecimal;
        private BigDecimal nullDecimal;
        private BigDecimal presentDecimal;
    }
}
