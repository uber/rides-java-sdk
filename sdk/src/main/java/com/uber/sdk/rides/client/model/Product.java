/*
 * Copyright (c) 2015 Uber Technologies, Inc.
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

/**
 * A product representing a type of ride on the Uber platform. See
 * <a href="https://developer.uber.com/v1/endpoints/#product-types">Products</a>
 * for more information.
 */
public class Product {

    private String product_id;
    private String display_name;
    private String description;
    private int capacity;
    private String image;

    /**
     * @return a unique identifier representing a specific product for a given latitude & longitude. For
     * example, uberX in San Francisco will have a different product_id than uberX in Los Angeles.
     */
    public String getProductId() {
        return product_id;
    }

    /**
     * Display name of product.
     */
    public String getDisplayName() {
        return display_name;
    }

    /**
     * Description of product.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Capacity of product. For samples, 4 people.
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Image URL representing the product.
     */
    public String getImage() {
        return image;
    }
}

