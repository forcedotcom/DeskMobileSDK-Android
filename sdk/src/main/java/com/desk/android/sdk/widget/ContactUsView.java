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

package com.desk.android.sdk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.R;
import com.desk.android.sdk.brand.BrandProvider;
import com.desk.android.sdk.config.ContactUsConfig;
import com.desk.android.sdk.error.IncompleteFormException;
import com.desk.android.sdk.identity.Identity;
import com.desk.android.sdk.identity.UserIdentity;
import com.desk.android.sdk.model.CreateCaseRequest;
import com.desk.android.sdk.model.CustomFieldProperties;
import com.desk.android.sdk.util.TextWatcherAdapter;
import com.desk.java.apiclient.model.CaseType;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays a contact us form in order for users to submit issues/feedback. To listen for callbacks
 * implement {@link com.desk.android.sdk.widget.ContactUsView.FormListener} and call
 * {@link #setFormListener(FormListener)}. Get a {@link CreateCaseRequest} by calling {@link #getRequest(String)}
 * which can be used to create a case.
 */
public class ContactUsView extends LinearLayout {

    /**
     * Callbacks for various form events
     */
    public interface FormListener {

        /**
         * The form is valid and can be submitted
         */
        void onFormValid();

        /**
         * The form is valid and cannot be submitted
         */
        void onFormInvalid();
    }

    private EditText mUserName;
    private EditText mUserEmail;
    private EditText mUserSubject;
    private EditText mUserFeedback;

    private String mName;
    private String mEmail;
    private String mSubject;
    private String mFeedback;

    private String mNameHint;
    private String mEmailHint;
    private String mSubjectHint;
    private String mFeedbackHint;

    private int mBrandId;
    private boolean mIsBranded;

    private boolean mUserNameEnabled;
    private boolean mUserSubjectEnabled;

    private HashMap<String, CustomFieldProperties> mCustomFieldProperties;

    private FormListener mListener;

    public ContactUsView(Context context) {
        this(context, null);
    }

    public ContactUsView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dk_contactUsViewStyle);
    }

    public ContactUsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ContactUsView, defStyleAttr, R.style.ContactUsViewStyle);
        mNameHint = ta.getString(R.styleable.ContactUsView_dk_nameHint);
        mEmailHint = ta.getString(R.styleable.ContactUsView_dk_emailHint);
        mSubjectHint = ta.getString(R.styleable.ContactUsView_dk_subjectHint);
        mFeedbackHint = ta.getString(R.styleable.ContactUsView_dk_feedbackHint);
        ta.recycle();
    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.contact_us_view, this, true);
        if (getContext() instanceof BrandProvider) {
            BrandProvider provider = (BrandProvider) getContext();
            mIsBranded = provider.isBranded();
            mBrandId = provider.getBrandId();
        }
        Desk desk = Desk.with(getContext());
        checkConfig(desk.getContactUsConfig());
        checkIdentity(desk.getIdentity());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mUserName = (EditText) findViewById(R.id.user_name);
        mUserName.setHint(mNameHint);
        mUserName.setText(mName);
        mUserEmail = (EditText) findViewById(R.id.user_email);
        mUserEmail.setHint(mEmailHint);
        mUserSubject = (EditText) findViewById(R.id.user_subject);
        mUserSubject.setText(mSubject);
        mUserSubject.setHint(mSubjectHint);
        mUserFeedback = (EditText) findViewById(R.id.user_feedback);
        mUserFeedback.setHint(mFeedbackHint);
        setupListeners();
    }

    private void setupListeners() {

        // show user name if enabled
        if (mUserNameEnabled) {
            mUserName.addTextChangedListener(new TextWatcherAdapter() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mName = s.toString().trim();
                }
            });
        } else {
            mUserName.setVisibility(View.GONE);
        }

        // show email if we don't have one from the identity
        if (TextUtils.isEmpty(mEmail)) {
            mUserEmail.addTextChangedListener(new TextWatcherAdapter() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mEmail = s.toString().trim();
                    checkForm();
                }
            });
        } else {
            mUserEmail.setVisibility(View.GONE);
        }

        // show subject if enabled
        if (mUserSubjectEnabled) {
            mUserSubject.addTextChangedListener(new TextWatcherAdapter() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    mSubject = s.toString().trim();
                    checkForm();
                }
            });
        } else {
            mUserSubject.setVisibility(View.GONE);
        }

        mUserFeedback.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mFeedback = s.toString().trim();
                checkForm();
            }
        });
    }

    /**
     * Set the form listener to listen for callbacks
     * @param listener the form listener
     */
    public void setFormListener(FormListener listener) {
        mListener = listener;
    }

    /**
     * Clears the form listener
     */
    public void clearFormListener() {
        mListener = null;
    }

    @VisibleForTesting
    FormListener getFormListener() {
        return mListener;
    }

    /**
     * Returns whether or not the form is currently valid and can be submitted
     * @return true if valid, false if there are errors
     */
    public boolean isFormValid() {
        // the form is valid if we have a valid email address, subject and feedback
        return !(TextUtils.isEmpty(mEmail) || !Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()
                || TextUtils.isEmpty(mSubject) || TextUtils.isEmpty(mFeedback));
    }

    /**
     * Creates a new {@link CreateCaseRequest}. Only call this when {@link #isFormValid()} is true or
     * a {@link IncompleteFormException} will be thrown.
     * @param to the to email address for the case
     * @return the request
     * @throws IncompleteFormException if the form is incomplete and {@link #isFormValid()} is false.
     */
    public CreateCaseRequest getRequest(String to) {
        if (!isFormValid()) {
            throw new IncompleteFormException();
        }
        return new CreateCaseRequest.Builder(CaseType.EMAIL, mFeedback, to, mEmail)
                .name(mName)
                .subject(mSubject)
                .customFields(getCustomFields())
                .create();
    }

    private void checkConfig(ContactUsConfig config) {
        mSubject = mIsBranded ? config.getSubject(mBrandId) : config.getSubject();
        mUserSubjectEnabled = mIsBranded ? config.isSubjectEnabled(mBrandId) : config.isSubjectEnabled();
        mUserNameEnabled = mIsBranded ? config.isUserNameEnabled(mBrandId) : config.isUserNameEnabled();
        mCustomFieldProperties = mIsBranded ? config.getCustomFieldProperties(mBrandId) : config.getCustomFieldProperties();
    }

    private void checkIdentity(Identity identity) {
        if (identity != null && identity instanceof UserIdentity) {
            UserIdentity userIdentity = (UserIdentity) identity;
            mName = userIdentity.getName();
            mEmail = userIdentity.getEmail();
        }
    }

    private HashMap<String, String> getCustomFields() {
        if (mCustomFieldProperties == null) {
            return null;
        }
        HashMap<String, String> customFields = new HashMap<>(mCustomFieldProperties.size());
        for (Map.Entry<String, CustomFieldProperties> entry : mCustomFieldProperties.entrySet()) {
            String key = entry.getKey();
            CustomFieldProperties properties = entry.getValue();
            customFields.put(key, properties.getValue());
        }
        return customFields;
    }

    private void checkForm() {
        if (mListener == null) {
            return;
        }
        if (isFormValid()) {
            mListener.onFormValid();
        } else {
            mListener.onFormInvalid();
        }
    }
}
