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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.desk.android.sdk.model.CustomFieldProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {@link ContactUsPropertyConfig} which pull properties from the desk.properties
 * file in the androidTest/assets directory.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ContactUsPropertyConfigTest {

    private ContactUsPropertyConfig config;

    @Before
    public void setUp() throws Exception {
        config = new ContactUsPropertyConfig(InstrumentationRegistry.getContext());
    }

    @Test
    public void isContactUsEnabledReturnsTrue() throws Exception {
        assertTrue(config.isContactUsEnabled());
    }

    @Test
    public void isContactUsEnabledReturnsFalseForBrand1() throws Exception {
        assertFalse(config.isContactUsEnabled(1));
    }

    @Test
    public void isContactUsEnabledMatchesNoBrandIfNotSet() throws Exception {
        assertEquals(config.isContactUsEnabled(), config.isContactUsEnabled(2));
    }

    @Test
    public void getSubjectReturnsCorrectSubject() throws Exception {
        assertEquals("Test Subject", config.getSubject());
    }

    @Test
    public void getSubjectReturnsCorrectSubjectForBrand1() throws Exception {
        assertEquals("Brand 1 Subject", config.getSubject(1));
    }

    @Test
    public void getSubjectMatchesNoBrandIfNotSet() throws Exception {
        assertEquals(config.getSubject(), config.getSubject(2));
    }

    @Test
    public void isSubjectEnabledReturnsFalse() throws Exception {
        assertFalse(config.isSubjectEnabled());
    }

    @Test
    public void isSubjectEnabledReturnsTrueForBrand1() throws Exception {
        assertTrue(config.isSubjectEnabled(1));
    }

    @Test
    public void isSubjectEnabledMatchesNoBrandIfNotSet() throws Exception {
        assertFalse(config.isSubjectEnabled(2));
    }

    @Test
    public void isUserNameEnabledReturnsTrue() throws Exception {
        assertTrue(config.isUserNameEnabled());
    }

    @Test
    public void isUserNameEnabledReturnsFalseForBrand1() throws Exception {
        assertFalse(config.isUserNameEnabled(1));
    }

    @Test
    public void isUserNameEnabledMatchesNoBrandIfNotSet() throws Exception {
        assertEquals(config.isUserNameEnabled(), config.isUserNameEnabled(2));
    }

    @Test
    public void isWebFormEnabled() throws Exception {
        assertTrue(config.isWebFormEnabled());
    }

    @Test
    public void isWebFormEnabledReturnsFalseForBrand1() throws Exception {
        assertFalse(config.isWebFormEnabled(1));
    }

    @Test
    public void isWebFormEnabledMatchesNoBrandIfNotSet() throws Exception {
        assertEquals(config.isWebFormEnabled(), config.isWebFormEnabled(2));
    }

    @Test
    public void getEmailAddressReturnsCorrectEmailAddress() throws Exception {
        assertEquals("test@test.com", config.getEmailAddress());
    }

    @Test
    public void getEmailAddressReturnsCorrectEmailAddressForBrand1() throws Exception {
        assertEquals("brand1@test.com", config.getEmailAddress(1));
    }

    @Test
    public void getEmailAddressReturnsMatchesNoBrandIfNotSet() throws Exception {
        assertEquals(config.getEmailAddress(), config.getEmailAddress(2));
    }

    @Test
    public void isCallUsEnabledReturnsTrue() throws Exception {
        assertTrue(config.isCallUsEnabled());
    }

    @Test
    public void isCallUsEnabledReturnsFalseForBrand1() throws Exception {
        assertFalse(config.isCallUsEnabled(1));
    }

    @Test
    public void isCallUsEnabledMatchesNoBrandIfNotSet() throws Exception {
        assertEquals(config.isCallUsEnabled(), config.isCallUsEnabled(2));
    }

    @Test
    public void getCallUsPhoneNumberReturnsCorrectPhoneNumber() throws Exception {
        assertEquals("(555) 555-5555", config.getCallUsPhoneNumber());
    }

    @Test
    public void getCallUsPhoneNumberReturnsCorrectPhoneNumberForBrand1() throws Exception {
        assertEquals("(111) 111-1111", config.getCallUsPhoneNumber(1));
    }

    @Test
    public void getCallUsPhoneNumberMatchesNoBrandIfNotSet() throws Exception {
        assertEquals(config.getCallUsPhoneNumber(), config.getCallUsPhoneNumber(2));
    }

    @Test
    public void getCustomFieldKeysReturnsKeys() {
        List<String> keys = config.getCustomFieldKeys();
        assertTrue(keys.contains("key_1"));
        assertTrue(keys.contains("key_2"));
        assertEquals(2, keys.size());
    }

    @Test
    public void getCustomFieldKeysReturnsKeysForBrand1() throws Exception {
        List<String> keys = config.getCustomFieldKeys(1);
        assertTrue(keys.contains("key_3"));
        assertTrue(keys.contains("key_4"));
        assertEquals(2, keys.size());
    }

    @Test
    public void getCustomFieldPropertiesReturnsProperties() throws Exception {
        HashMap<String, CustomFieldProperties> properties = config.getCustomFieldProperties();
        assertTrue(properties.containsKey("key_1"));
        assertTrue(properties.containsKey("key_2"));
        CustomFieldProperties key1Properties = properties.get("key_1");
        assertEquals("key_1", key1Properties.getKey());
        assertEquals("value_1", key1Properties.getValue());
        CustomFieldProperties key2Properties = properties.get("key_2");
        assertEquals("key_2", key2Properties.getKey());
        assertEquals("value_2", key2Properties.getValue());
    }

    @Test
    public void getCustomFieldPropertiesReturnsPropertiesForBrand1() throws Exception {
        HashMap<String, CustomFieldProperties> properties = config.getCustomFieldProperties(1);
        assertTrue(properties.containsKey("key_3"));
        assertTrue(properties.containsKey("key_4"));
        CustomFieldProperties key3Properties = properties.get("key_3");
        assertEquals("key_3", key3Properties.getKey());
        assertEquals("value_3", key3Properties.getValue());
        CustomFieldProperties key4Properties = properties.get("key_4");
        assertEquals("key_4", key4Properties.getKey());
        assertEquals("value_4", key4Properties.getValue());
    }
}