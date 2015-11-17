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

import android.test.suitebuilder.annotation.SmallTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * <p>
 *     Unit tests for {@link CustomFieldProperties}
 * </p>
 *
 * Created by Matt Kranzler on 9/10/15.
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
@SuppressWarnings("ALL")
@SmallTest
public class CustomFieldPropertiesTest {

    private static final String KEY = "key";
    private static final String VALUE = "value";

    private CustomFieldProperties properties;

    @Before
    public void setUp() throws Exception {
        properties = new CustomFieldProperties.Builder(KEY).value(VALUE).create();
    }

    @Test
    public void getKeyDoesReturnKey() throws Exception {
        assertEquals(KEY, properties.getKey());
    }

    @Test
    public void getValueDoesReturnValue() throws Exception {
        assertEquals(VALUE, properties.getValue());
    }

    @Test(expected = NullPointerException.class)
    public void builderDoesThrowNPEWithNullKey() throws Exception {
        new CustomFieldProperties.Builder(null).create();
    }
}