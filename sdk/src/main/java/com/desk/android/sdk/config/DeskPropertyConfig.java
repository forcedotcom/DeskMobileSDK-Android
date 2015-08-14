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

package com.desk.android.sdk.config;

import android.content.Context;

import com.desk.android.sdk.DeskProperties;

import java.util.Properties;

import static com.desk.android.sdk.helper.PropertyHelper.getString;

/**
 * {@link com.desk.android.sdk.config.DeskConfig} which pulls configuration out of a {@link Properties}
 * object.
 */
public class DeskPropertyConfig extends BaseDeskConfig implements PropertyConfig {

    /**
     * Property which maps to {@link DeskConfig#getApiToken()}
     */
    public static final String KEY_API_TOKEN = "desk.api.token";

    /**
     * Property which maps to {@link DeskConfig#getHostname()}
     */
    public static final String KEY_HOSTNAME = "desk.hostname";

    private Properties properties;

    /**
     * Creates an instance which will pull properties from {@link DeskProperties}.
     * @param applicationContext the application context
     */
    public DeskPropertyConfig(Context applicationContext) {
        this(DeskProperties.with(applicationContext));
    }

    /**
     * Creates an instance which will pull properties from the {@link Properties} object passed in.
     * @param properties the properties
     */
    public DeskPropertyConfig(Properties properties) {
        this.properties = properties;
    }

    /**
     * See {@link DeskConfig#getApiToken()}
     */
    @Override
    public String getApiToken() {
        return getString(properties, KEY_API_TOKEN);
    }

    /**
     * See {@link DeskConfig#getHostname()}
     */
    @Override
    public String getHostname() {
        return getString(properties, KEY_HOSTNAME);
    }
}
