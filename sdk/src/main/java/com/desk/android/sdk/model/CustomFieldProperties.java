/*
 * Copyright (c) 2015, Salesforce.com, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of Salesforce.com, Inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.desk.android.sdk.model;

/**
 * <p>
 *     Defines properties for a custom field
 * </p>
 *
 * Created by Matt Kranzler on 9/10/15.
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class CustomFieldProperties {

    private String key;
    private String value;

    private CustomFieldProperties(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Get the custom field key
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get the custom field value
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Builder which aids in creating a {@link CustomFieldProperties} instance
     */
    public static class Builder {

        private String key;
        private String value;

        /**
         * Default constructor with required fields
         * @param key the custom field key
         */
        public Builder(String key) {
            if (key == null) {
                throw new NullPointerException("key cannot be null.");
            }
            this.key = key;
        }

        /**
         * The value for the custom field to default to
         * @param value the value
         * @return the builder instance
         */
        public Builder value(String value) {
            this.value = value;
            return this;
        }

        /**
         * Creates the {@link CustomFieldProperties} instance
         * @return the instance
         */
        public CustomFieldProperties create() {
            return new CustomFieldProperties(key, value);
        }
    }

}
