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

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link PropertyHelper}
 */
@SmallTest
public class PropertyHelperTest {

    private static final String STRING_KEY = "string.key";
    private static final String STRING_KEY_ARGS = "string.key.%s";
    private static final String STRING_VALUE = "string.value";
    private static final String STRING_DEFAULT_VALUE = "default.value";

    private static final String BOOLEAN_KEY = "boolean.key";
    private static final String BOOLEAN_KEY_ARGS = "boolean.key.%s";
    private static final String BOOLEAN_STRING_VALUE = "true";
    private static final boolean BOOLEAN_VALUE = true;
    private static final boolean BOOLEAN_DEFAULT_VALUE = false;

    private Properties props;

    @Before
    public void setUp() throws Exception {
        props = new Properties();
    }

    @Test
    public void getStringReturnsCorrectValue() throws Exception {
        props.clear();
        props.put(STRING_KEY, STRING_VALUE);
        assertEquals(PropertyHelper.getString(STRING_KEY, props), STRING_VALUE);
    }

    @Test
    public void getStringReturnsEmpty() throws Exception {
        props.clear();
        assertEquals("", PropertyHelper.getString(STRING_KEY, props));
    }

    @Test
    public void getStringReturnsDefaultValue() throws Exception {
        props.clear();
        assertEquals(STRING_DEFAULT_VALUE, PropertyHelper.getString(STRING_KEY, STRING_DEFAULT_VALUE, props));
    }

    @Test
    public void getStringWithArgsReturnsCorrectValue() throws Exception {
        props.clear();
        props.put("string.key.1", STRING_VALUE);
        assertEquals(STRING_VALUE, PropertyHelper.getStringWithArgs(STRING_KEY_ARGS, props, 1));
    }

    @Test
    public void getStringWithArgsReturnsDefaultValue() throws Exception {
        props.clear();
        assertEquals(STRING_DEFAULT_VALUE, PropertyHelper.getStringWithArgs(STRING_KEY_ARGS, STRING_DEFAULT_VALUE, props, 1));
    }

    @Test
    public void getStringWithArgsReturnsEmpty() throws Exception {
        props.clear();
        assertEquals("", PropertyHelper.getStringWithArgs(STRING_KEY_ARGS, props, 1));
    }

    @Test
    public void getBooleanReturnsCorrectValue() throws Exception {
        props.clear();
        props.put(BOOLEAN_KEY, BOOLEAN_STRING_VALUE);
        assertEquals(PropertyHelper.getBoolean(BOOLEAN_KEY, props), BOOLEAN_VALUE);
    }

    @Test
    public void getBooleanReturnsFalse() throws Exception {
        props.clear();
        assertEquals(false, PropertyHelper.getBoolean(BOOLEAN_KEY, props));
    }

    @Test
    public void getBooleanReturnsDefaultValue() throws Exception {
        props.clear();
        assertEquals(BOOLEAN_DEFAULT_VALUE, PropertyHelper.getBoolean(BOOLEAN_KEY, BOOLEAN_DEFAULT_VALUE, props));
    }

    @Test
    public void getBooleanWithArgsReturnsCorrectValue() throws Exception {
        props.clear();
        props.put("boolean.key.1", BOOLEAN_STRING_VALUE);
        assertEquals(BOOLEAN_VALUE, PropertyHelper.getBooleanWithArgs(BOOLEAN_KEY_ARGS, props, 1));
    }

    @Test
    public void getBooleanWithArgsReturnsDefaultValue() throws Exception {
        props.clear();
        assertEquals(BOOLEAN_DEFAULT_VALUE, PropertyHelper.getBooleanWithArgs(BOOLEAN_KEY_ARGS, BOOLEAN_DEFAULT_VALUE, props, 1));
    }

    @Test
    public void getBooleanWithArgsReturnsFalse() throws Exception {
        props.clear();
        assertEquals(false, PropertyHelper.getBooleanWithArgs(BOOLEAN_KEY_ARGS, props, 1));
    }
}