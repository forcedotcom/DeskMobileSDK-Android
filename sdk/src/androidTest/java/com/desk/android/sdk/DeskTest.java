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

package com.desk.android.sdk;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import com.desk.android.sdk.config.ContactUsConfig;
import com.desk.android.sdk.config.ContactUsPropertyConfig;
import com.desk.android.sdk.config.DeskConfig;
import com.desk.android.sdk.config.DeskPropertyConfig;
import com.desk.android.sdk.identity.Identity;
import com.desk.android.sdk.identity.UserIdentity;
import com.desk.android.sdk.util.DeskDefaultsRule;
import com.desk.java.apiclient.DeskClient;
import com.squareup.okhttp.Cache;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Unit tests for {@link Desk}
 *
 * Created by Matt Kranzler on 6/29/15.
 */
@SuppressWarnings("ALL")
@RunWith(AndroidJUnit4.class)
@SmallTest
public class DeskTest {

    @ClassRule
    public static DeskDefaultsRule resetRule = new DeskDefaultsRule();

    private DeskConfig config;
    private ContactUsConfig contactUsConfig;
    private Desk desk;

    @Before
    public void setUp() throws Exception {
        desk = Desk.with(InstrumentationRegistry.getContext());
        DeskProperties properties = DeskProperties.with(InstrumentationRegistry.getContext());
        config = new DeskPropertyConfig(properties);
        contactUsConfig = new ContactUsPropertyConfig(InstrumentationRegistry.getContext());
    }

    @Test
    public void withReturnsSingletonInstance() throws Exception {
        Desk desk2 = Desk.with(InstrumentationRegistry.getContext());
        assertTrue(desk == desk2);
    }

    @Test
    public void getClientReturnsClient() throws Exception {
        assertNotNull(desk.getClient());
    }

    @Test
    public void getResponseCacheReturnsResponseCache() throws Exception {
        Cache responseCache = desk.getResponseCache(InstrumentationRegistry.getContext());
        assertNotNull(responseCache);
        assertEquals(responseCache.getMaxSize(), Desk.CACHE_MAX_SIZE);
    }

    @Test
    public void getLanguageReturnsCorrectLanguage() throws Exception {
        Locale.setDefault(Locale.FRANCE);
        assertEquals("fr", Desk.getLanguage());
        Locale.setDefault(Locale.GERMANY);
        assertEquals("de", Desk.getLanguage());
        Locale.setDefault(Locale.ENGLISH);
        assertEquals("en", Desk.getLanguage());
    }

    @Test
    public void setConfigSetsConfig() throws Exception {
        desk.setConfig(config);
        assertTrue(config == desk.getConfig());
    }

    @Test
    public void setConfigClearsClient() throws Exception {
        DeskClient client = desk.getClient();
        desk.setConfig(config);
        DeskClient newClient = desk.getClient();
        assertFalse(client == newClient);
    }

    @Test(expected = NullPointerException.class)
    public void setConfigThrowsNullPointerException() {
        desk.setConfig(null);
    }

    @Test
    public void getConfigReturnsNonNull() throws Exception {
        assertNotNull(getNewDeskInstance().getConfig());
    }

    @Test
    public void getConfigReturnsDeskPropertyConfigByDefault() throws Exception {
        assertTrue(getNewDeskInstance().getConfig() instanceof DeskPropertyConfig);
    }

    @Test
    public void setIdentitySetsIdentity() throws Exception {
        Identity identity = new UserIdentity.Builder("test@test.com").create();
        desk.setIdentity(identity);
        assertTrue(identity == desk.getIdentity());
    }

    @Test
    public void setContactUsConfigSetsConfig() throws Exception {
        desk.setContactUsConfig(contactUsConfig);
        assertTrue(contactUsConfig == desk.getContactUsConfig());
    }

    @Test(expected = NullPointerException.class)
    public void setContactUsConfigThrowsNullPointerException() {
        desk.setContactUsConfig(null);
    }

    @Test
    public void getContactUsConfigReturnsNonNull() throws Exception {
        assertNotNull(getNewDeskInstance().getContactUsConfig());
    }

    @Test
    public void getContactUsConfigReturnsContactUsPropertyConfigByDefault() throws Exception {
        assertTrue(getNewDeskInstance().getContactUsConfig() instanceof ContactUsPropertyConfig);
    }

    @Test
    public void getCaseProviderReturnsNonNull() throws Exception {
        assertNotNull(getNewDeskInstance().getCaseProvider());
    }

    private Desk getNewDeskInstance() {
        Desk.release();
        return Desk.with(InstrumentationRegistry.getContext());
    }
}