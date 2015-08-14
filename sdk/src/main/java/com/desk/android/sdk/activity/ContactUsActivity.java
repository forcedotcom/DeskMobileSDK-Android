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

package com.desk.android.sdk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.R;
import com.desk.android.sdk.brand.BrandProvider;
import com.desk.android.sdk.config.ContactUsConfig;
import com.desk.android.sdk.error.ErrorResponse;
import com.desk.android.sdk.fragment.CreateCaseHelper;
import com.desk.android.sdk.helper.DeskThemeHelper;
import com.desk.android.sdk.helper.MenuHelper;
import com.desk.android.sdk.model.CreateCaseRequest;
import com.desk.android.sdk.widget.ContactUsView;
import com.desk.java.apiclient.model.Case;
import com.desk.java.apiclient.util.StringUtils;

import static com.desk.android.sdk.helper.DeskThemeHelper.EXTRA_THEME_RES_ID;
import static com.desk.android.sdk.helper.DeskThemeHelper.NO_THEME_RES_ID;

/**
 * Displays a {@link ContactUsView} to allow a user to submit feedback which in return creates a Case.
 */
public class ContactUsActivity extends AppCompatActivity implements ContactUsView.FormListener,
        CreateCaseHelper.CreateCaseListener, BrandProvider {

    private static final String EXTRA_TO_EMAIL_ADDRESS = "com.desk.android.sdk.EXTRA_TO_EMAIL_ADDRESS";
    private static final String STATE_REQUEST = "request";

    /**
     * Starts the activity
     * @param activity the activity
     * @param toEmailAddress the to email address to create the case with
     */
    public static void start(Activity activity, @NonNull String toEmailAddress) {
        start(activity, toEmailAddress, NO_THEME_RES_ID);
    }

    /**
     * Starts the activity with a custom theme
     * @param activity the activity
     * @param toEmailAddress the to email address to create the case with
     * @param themeResId the resource id of the theme to use
     */
    public static void start(Activity activity, @NonNull String toEmailAddress, @StyleRes int themeResId) {
        Intent intent = new Intent(activity, ContactUsActivity.class);
        intent.putExtra(EXTRA_TO_EMAIL_ADDRESS, toEmailAddress);
        intent.putExtra(EXTRA_THEME_RES_ID, themeResId);
        activity.startActivity(intent);
    }

    private DeskThemeHelper mThemeHelper;
    private ContactUsConfig mConfig;

    private ContactUsView mContactUs;
    private ProgressBar mProgress;
    private CreateCaseHelper mCreateCaseHelper;

    private boolean mCallUsEnabled;
    private String mPhoneNumber;
    private String mToEmailAddress;

    private CreateCaseRequest mCreateCaseRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeHelper = new DeskThemeHelper(this);
        mConfig = Desk.with(this).getContactUsConfig();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us_activity);
        if (savedInstanceState != null) {
            mCreateCaseRequest = (CreateCaseRequest) savedInstanceState.getSerializable(STATE_REQUEST);
        }
        mToEmailAddress = getIntent().getStringExtra(EXTRA_TO_EMAIL_ADDRESS);
        mProgress = (ProgressBar) findViewById(android.R.id.progress);
        mContactUs = (ContactUsView) findViewById(R.id.contact_us_view);
        mContactUs.setFormListener(this);
        initializeVariables();
        mCreateCaseHelper = CreateCaseHelper.attach(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_REQUEST, mCreateCaseRequest);
    }

    private void initializeVariables() {
        if (mThemeHelper.hasBrandId()) {
            int brandId = mThemeHelper.getBrandId();
            mPhoneNumber = mConfig.getCallUsPhoneNumber(brandId);
            mCallUsEnabled = mConfig.isCallUsEnabled(brandId);
        } else {
            mPhoneNumber = mConfig.getCallUsPhoneNumber();
            mCallUsEnabled = mConfig.isCallUsEnabled();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContactUs.clearFormListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_us_activity_menu, menu);
        MenuHelper.tintIcons(menu, mThemeHelper.getColorControlNormal(), R.id.submit, R.id.call_us);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem submit = menu.findItem(R.id.submit);
        if (submit != null) {
            submit.setEnabled(canSubmit());

            // if we can't submit set the icon to 30% opacity
            if (submit.getIcon() != null) {
                if (canSubmit()) {
                    submit.getIcon().mutate().setAlpha(255);
                } else {
                    submit.getIcon().mutate().setAlpha(77);
                }
            }
        }
        MenuItem callUs = menu.findItem(R.id.call_us);
        if (callUs != null) {
            callUs.setVisible(mCallUsEnabled);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (R.id.submit == id) {
            submitForm();
            return true;
        } else if (R.id.call_us == id) {
            handleCallUs();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void handleCallUs() {
        if (StringUtils.isEmpty(mPhoneNumber)) {
            throw new IllegalStateException("You must specify a phone number to call in your ContactUsConfig.");
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhoneNumber));
        startActivity(intent);
    }

    private boolean canSubmit() {
        return mCreateCaseRequest == null && mContactUs.isFormValid();
    }

    @Override
    public void onFormValid() {
        invalidateOptionsMenu();
    }

    @Override
    public void onFormInvalid() {
        invalidateOptionsMenu();
    }

    private void submitForm() {
        mCreateCaseRequest = mContactUs.getRequest(mToEmailAddress);
        mCreateCaseHelper.createCase(mCreateCaseRequest);
        hideKeyboard();
        invalidateOptionsMenu();
        mContactUs.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mContactUs.getWindowToken(), 0);
    }

    @Override
    public void onCaseCreated(Case deskCase) {
        Toast.makeText(this, mThemeHelper.getCreateCaseSuccessToast(), Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onCreateCaseError(ErrorResponse error) {
        Toast.makeText(this, mThemeHelper.getCreateCaseErrorToast(), Toast.LENGTH_LONG).show();
        mCreateCaseRequest = null;
        invalidateOptionsMenu();
        mContactUs.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public boolean isBranded() {
        return mThemeHelper.hasBrandId();
    }

    @Override
    public int getBrandId() {
        return mThemeHelper.getBrandId();
    }
}
