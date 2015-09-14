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

import com.desk.android.sdk.model.CustomFieldProperties;

import java.util.HashMap;
import java.util.List;

/**
 * Configuration options for 'Contact Us' which allows submitting feedback / issues and creating a
 * case.
 */
public interface ContactUsConfig {

    /**
     * Whether or not contact us is enabled.
     * @return true to enable contact us, false to disable it
     */
    boolean isContactUsEnabled();

    /**
     * Whether or not contact us is enabled for the brand specified.
     * @param brandId the brand id
     * @return true to enable contact us, false to disable it
     */
    boolean isContactUsEnabled(int brandId);

    /**
     * This will be the case's subject when creating a case.
     * @return the subject
     */
    String getSubject();

    /**
     * This will be the case's subject when creating a case for the brand specified.
     * @param brandId the brand id
     * @return the subject
     */
    String getSubject(int brandId);

    /**
     * Whether or not to display the subject field on the contact us form
     * @return true to display, false to hide
     */
    boolean isSubjectEnabled();

    /**
     * Whether or not to display the subject field on the contact us form for the brand specified
     * @param brandId the brand id
     * @return true to display, false to hide
     */
    boolean isSubjectEnabled(int brandId);

    /**
     * Whether or not to display the name field on the contact us form.
     * @return true to display, false to hide
     */
    boolean isUserNameEnabled();

    /**
     * Whether or not to display the name field on the contact us form for the brand specified.
     * @param brandId the brand id
     * @return true to display, false to hide
     */
    boolean isUserNameEnabled(int brandId);

    /**
     * Whether or not to display a web based form as opposed to the native form.
     * @return true to display the web based form, false to display the native form
     */
    boolean isWebFormEnabled();

    /**
     * Whether or not to display a web based form as opposed to the native form for the brand specified.
     * @param brandId the brand id
     * @return true to display the web based form, false to display the native form
     */
    boolean isWebFormEnabled(int brandId);

    /**
     * The email address that will be used in the 'to' field when creating a case.
     * @return the email address
     */
    String getEmailAddress();

    /**
     * The email address that will be used in the 'to' field when creating a case for the brand specified.
     * @param brandId the brand id
     * @return the email address
     */
    String getEmailAddress(int brandId);

    /**
     * Whether or not to display the call us option. NOTE: if this is enabled you MUST also return
     * a legit phone number in {@link #getCallUsPhoneNumber()}.
     * @return true to enable the option, false to disable it
     */
    boolean isCallUsEnabled();

    /**
     * Whether or not to display the call us option for the brand provided. NOTE: if this is enabled
     * you MUST also return a legit phone number in {@link #getCallUsPhoneNumber()}.
     * @param brandId the brand id
     * @return true to enable the option, false to disable it
     */
    boolean isCallUsEnabled(int brandId);

    /**
     * The phone number to be dialed when the call us option is selected.
     * @return the phone number
     */
    String getCallUsPhoneNumber();

    /**
     * The phone number to be dialed when the call us option is selected for the brand specified.
     * @param brandId the brand id
     * @return the phone number
     */
    String getCallUsPhoneNumber(int brandId);

    /**
     * The custom field keys supported when creating a case
     * @return the keys
     */
    List<String> getCustomFieldKeys();

    /**
     * The custom field keys supported when creating a case for the brand specified
     * @param brandId the brand id
     * @return the keys
     */
    List<String> getCustomFieldKeys(int brandId);

    /**
     * The custom field properties for the custom fields defined in {@link #getCustomFieldKeys()}
     * @return the custom fields
     */
    HashMap<String, CustomFieldProperties> getCustomFieldProperties();

    /**
     * The custom field properties for the custom fields defined in {@link #getCustomFieldKeys(int)} for
     * the brand specified
     * @param brandId the brand id
     * @return the custom fields
     */
    HashMap<String, CustomFieldProperties> getCustomFieldProperties(int brandId);
}
