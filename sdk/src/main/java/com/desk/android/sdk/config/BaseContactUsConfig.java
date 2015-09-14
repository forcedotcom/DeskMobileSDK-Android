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

import com.desk.android.sdk.R;
import com.desk.android.sdk.model.CustomFieldProperties;

import java.util.HashMap;
import java.util.List;

/**
 * Base implementation of {@link ContactUsConfig} which overrides each method with default values.
 * Extend this class to provide your own implementation.
 */
public abstract class BaseContactUsConfig implements ContactUsConfig {

    private Context mApplicationContext;

    public BaseContactUsConfig(Context applicationContext) {
        this.mApplicationContext = applicationContext.getApplicationContext();
    }

    @Override
    public boolean isContactUsEnabled() {
        return true;
    }

    @Override
    public boolean isContactUsEnabled(int brandId) {
        return isContactUsEnabled();
    }

    @Override
    public String getSubject() {
        return mApplicationContext.getString(R.string.def_subject);
    }

    @Override
    public String getSubject(int brandId) {
        return getSubject();
    }

    @Override
    public boolean isSubjectEnabled() {
        return false;
    }

    @Override
    public boolean isSubjectEnabled(int brandId) {
        return isSubjectEnabled();
    }

    @Override
    public boolean isUserNameEnabled() {
        return false;
    }

    @Override
    public boolean isUserNameEnabled(int brandId) {
        return isUserNameEnabled();
    }

    @Override
    public boolean isWebFormEnabled() {
        return false;
    }

    @Override
    public boolean isWebFormEnabled(int brandId) {
        return isWebFormEnabled();
    }

    @Override
    public String getEmailAddress() {
        return null;
    }

    @Override
    public String getEmailAddress(int brandId) {
        return getEmailAddress();
    }

    @Override
    public boolean isCallUsEnabled() {
        return false;
    }

    @Override
    public boolean isCallUsEnabled(int brandId) {
        return isCallUsEnabled();
    }

    @Override
    public String getCallUsPhoneNumber() {
        return null;
    }

    @Override
    public String getCallUsPhoneNumber(int brandId) {
        return getCallUsPhoneNumber();
    }

    @Override
    public List<String> getCustomFieldKeys() {
        return null;
    }

    @Override
    public List<String> getCustomFieldKeys(int brandId) {
        return null;
    }

    @Override
    public HashMap<String, CustomFieldProperties> getCustomFieldProperties() {
        return null;
    }

    @Override
    public HashMap<String, CustomFieldProperties> getCustomFieldProperties(int brandId) {
        return null;
    }
}
