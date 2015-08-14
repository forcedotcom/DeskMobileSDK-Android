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

import static com.desk.android.sdk.helper.PropertyHelper.getBoolean;
import static com.desk.android.sdk.helper.PropertyHelper.getString;

/**
 * {@link com.desk.android.sdk.config.ContactUsConfig} which pulls configuration out of a {@link Properties}
 * object.
 */
public class ContactUsPropertyConfig extends BaseContactUsConfig implements PropertyConfig {

    /**
     * Property which maps to {@link ContactUsConfig#isCallUsEnabled()}.
     */
    public static final String KEY_CONTACT_US_ENABLED = "contact.us.enabled";

    /**
     * Property which maps to {@link ContactUsConfig#isWebFormEnabled()}.
     */
    public static final String KEY_CONTACT_US_WEB_FORM_ENABLED = "contact.us.web.form.enabled";

    /**
     * Property which maps to {@link ContactUsConfig#getSubject()}.
     */
    public static final String KEY_CONTACT_US_SUBJECT = "contact.us.subject";

    /**
     * Property which maps to {@link ContactUsConfig#isSubjectEnabled()}
     */
    public static final String KEY_CONTACT_US_SUBJECT_ENABLED = "contact.us.subject.enabled";

    /**
     * Property which maps to {@link ContactUsConfig#isUserNameEnabled()}.
     */
    public static final String KEY_CONTACT_US_USER_NAME_ENABLED = "contact.us.user.name.enabled";

    /**
     * Property which maps to {@link ContactUsConfig#isCallUsEnabled()}.
     */
    public static final String KEY_CONTACT_US_CALL_US_ENABLED = "contact.us.call.us.enabled";

    /**
     * Property which maps to {@link ContactUsConfig#getEmailAddress()}.
     */
    public static final String KEY_CONTACT_US_EMAIL_ADDRESS = "contact.us.email.address";

    /**
     * Property which maps to {@link ContactUsConfig#getCallUsPhoneNumber()}.
     */
    public static final String KEY_CONTACT_US_PHONE_NUMBER = "contact.us.phone.number";

    private Properties properties;

    /**
     * Creates an instance which will pull properties from {@link DeskProperties}.
     * @param applicationContext the application context
     */
    public ContactUsPropertyConfig(Context applicationContext) {
        super(applicationContext);
        this.properties = DeskProperties.with(applicationContext);
    }

    /**
     * See {@link ContactUsConfig#isCallUsEnabled()}
     */
    @Override
    public boolean isContactUsEnabled() {
        return getBoolean(properties, super.isContactUsEnabled(), KEY_CONTACT_US_ENABLED);
    }

    /**
     * See {@link ContactUsConfig#isCallUsEnabled(int)}
     */
    @Override
    public boolean isContactUsEnabled(int brandId) {
        return getBoolean(properties, isCallUsEnabled(), buildBrandKey(KEY_CONTACT_US_ENABLED), brandId);
    }

    /**
     * See {@link ContactUsConfig#getSubject()}
     */
    @Override
    public String getSubject() {
        return getString(properties, super.getSubject(), KEY_CONTACT_US_SUBJECT);
    }

    /**
     * See {@link ContactUsConfig#getSubject(int)}
     */
    @Override
    public String getSubject(int brandId) {
        return getString(properties, getSubject(), buildBrandKey(KEY_CONTACT_US_SUBJECT), brandId);
    }

    /**
     * See {@link ContactUsConfig#isSubjectEnabled()}
     */
    @Override
    public boolean isSubjectEnabled() {
        return getBoolean(properties, KEY_CONTACT_US_SUBJECT_ENABLED);
    }

    /**
     * See {@link ContactUsConfig#isSubjectEnabled(int)}
     */
    @Override
    public boolean isSubjectEnabled(int brandId) {
        return getBoolean(properties, isSubjectEnabled(), buildBrandKey(KEY_CONTACT_US_SUBJECT_ENABLED), brandId);
    }

    /**
     * See {@link ContactUsConfig#isUserNameEnabled()}
     */
    @Override
    public boolean isUserNameEnabled() {
        return getBoolean(properties, KEY_CONTACT_US_USER_NAME_ENABLED);
    }

    /**
     * See {@link ContactUsConfig#isUserNameEnabled(int)}
     */
    @Override
    public boolean isUserNameEnabled(int brandId) {
        return getBoolean(properties, isUserNameEnabled(), buildBrandKey(KEY_CONTACT_US_USER_NAME_ENABLED), brandId);
    }

    /**
     * See {@link ContactUsConfig#isWebFormEnabled()}
     */
    @Override
    public boolean isWebFormEnabled() {
        return getBoolean(properties, KEY_CONTACT_US_WEB_FORM_ENABLED);
    }

    /**
     * See {@link ContactUsConfig#isWebFormEnabled(int)}
     */
    @Override
    public boolean isWebFormEnabled(int brandId) {
        return getBoolean(properties, isWebFormEnabled(), buildBrandKey(KEY_CONTACT_US_WEB_FORM_ENABLED), brandId);
    }

    /**
     * See {@link ContactUsConfig#getEmailAddress()}
     */
    @Override
    public String getEmailAddress() {
        return getString(properties, KEY_CONTACT_US_EMAIL_ADDRESS);
    }

    /**
     * See {@link ContactUsConfig#getEmailAddress(int)} l}
     */
    @Override
    public String getEmailAddress(int brandId) {
        return getString(properties, getEmailAddress(), buildBrandKey(KEY_CONTACT_US_EMAIL_ADDRESS), brandId);
    }

    /**
     * See {@link ContactUsConfig#isCallUsEnabled()}
     */
    @Override
    public boolean isCallUsEnabled() {
        return getBoolean(properties, KEY_CONTACT_US_CALL_US_ENABLED);
    }

    /**
     * See {@link ContactUsConfig#isCallUsEnabled(int)}
     */
    @Override
    public boolean isCallUsEnabled(int brandId) {
        return getBoolean(properties, isCallUsEnabled(), buildBrandKey(KEY_CONTACT_US_CALL_US_ENABLED), brandId);
    }

    /**
     * See {@link ContactUsConfig#getCallUsPhoneNumber()}
     */
    @Override
    public String getCallUsPhoneNumber() {
        return getString(properties, KEY_CONTACT_US_PHONE_NUMBER);
    }

    /**
     * See {@link ContactUsConfig#getCallUsPhoneNumber(int)}
     */
    @Override
    public String getCallUsPhoneNumber(int brandId) {
        return getString(properties, getCallUsPhoneNumber(), buildBrandKey(KEY_CONTACT_US_PHONE_NUMBER), brandId);
    }

    private String buildBrandKey(String key) {
        return key + BRAND_SUFFIX;
    }
}
