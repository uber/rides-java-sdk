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

package com.uber.sdk.rides.client.internal;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveAdapterTest {

    @Test
    public void fromJson_whenNull_shouldGoToZero() throws IOException {
        Moshi moshi = new Moshi.Builder().add(new PrimitiveAdapter()).build();

        JsonAdapter<PrimitiveModel> adapter = moshi.adapter(PrimitiveModel.class);
        PrimitiveModel model = adapter.fromJson("{\"someint\":null,\"somefloat\":null,\"somelong\":null}");
        assertThat(model.getSomeint()).isZero();
        assertThat(model.getSomefloat()).isZero();
        assertThat(model.getSomelong()).isZero();
    }

    @Test
    public void fromJson_whenValueProvided_shouldReturnValue() throws IOException {
        Moshi moshi = new Moshi.Builder().add(new PrimitiveAdapter()).build();

        JsonAdapter<PrimitiveModel> adapter = moshi.adapter(PrimitiveModel.class);
        PrimitiveModel model = adapter.fromJson("{\"someint\":1,\"somefloat\":1.0,\"somelong\":11}");
        assertThat(model.getSomeint()).isEqualTo(1);
        assertThat(model.getSomefloat()).isEqualTo(1.0f);
        assertThat(model.getSomelong()).isEqualTo(11l);
    }
}
