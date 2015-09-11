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

package com.desk.android.sdk.helper;

import java.util.Properties;

/**
 * Helper methods for interacting with {@link Properties}.
 */
public class PropertyHelper {

    /**
     * Gets the String property from the properties object.
     * @param key the key of the String property
     * @param properties the properties object to get the property from
     * @return the property value, an empty string if the property doesn't exist
     */
    public static String getString(String key, Properties properties) {
        return getString(key, "", properties);
    }

    /**
     * Gets the String property from the properties object.
     * @param key the key of the String property
     * @param properties the properties object to get the property from
     * @param keyArgs the args to format the key
     * @return the property value, an empty string if the property doesn't exist
     */
    public static String getStringWithArgs(String key, Properties properties, Object... keyArgs) {
        return getString(String.format(key, keyArgs), "", properties);
    }

    /**
     * Gets the String property from the properties object.
     * @param key the key of the String property
     * @param defaultValue the default value if the property doesn't exist
     * @param properties the properties object to get the property from
     * @param keyArgs the args to format the key
     * @return the property value, defaultValue if the property doesn't exist
     */
    public static String getStringWithArgs(String key, String defaultValue, Properties properties, Object... keyArgs) {
        return getString(String.format(key, keyArgs), defaultValue, properties);
    }

    /**
     * Gets the String property from the properties object.
     * @param key the key of the String property
     * @param defaultValue the default value if the property doesn't exist
     * @param properties the properties object to get the property from
     * @return the property value, defaultValue if the property doesn't exist
     */
    public static String getString(String key, String defaultValue, Properties properties) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Gets the boolean property from the properties object.
     * @param key the key of the boolean property
     * @param properties the properties object to get the property from
     * @return the property value, false if the property does not exist
     */
    public static boolean getBoolean(String key, Properties properties) {
        return getBoolean(key, false, properties);
    }

    /**
     * Gets the boolean property from the properties object with format args.
     * @param key the key of the boolean property
     * @param properties the properties object to get the property from
     * @param keyArgs the args to format the key
     * @return the property value, false if the property does not exist
     */
    public static boolean getBooleanWithArgs(String key, Properties properties, Object... keyArgs) {
        return getBoolean(String.format(key, keyArgs), properties);
    }

    /**
     * Gets the boolean property from the properties object with format args.
     * @param key the key of the boolean property
     * @param defaultValue the default value if the property doesn't exist
     * @param properties the properties object to get the property from
     * @param keyArgs the args to format the key
     * @return the property value, false if the property does not exist
     */
    public static boolean getBooleanWithArgs(String key, boolean defaultValue, Properties properties, Object... keyArgs) {
        return getBoolean(String.format(key, keyArgs), defaultValue, properties);
    }

    /**
     * Gets the boolean property from the properties object.
     * @param key the key of the boolean property
     * @param defaultValue the default value if the property doesn't exist
     * @param properties the properties object to get the property from
     * @return the property value, defaultValue if the property doesn't exist
     */
    public static boolean getBoolean(String key, boolean defaultValue, Properties properties) {
        if (properties.containsKey(key)) {
            return Boolean.valueOf(properties.getProperty(key, "false"));
        } else {
            return defaultValue;
        }
    }
}
