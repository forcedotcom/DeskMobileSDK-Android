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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.R;
import com.desk.android.sdk.config.ContactUsConfig;
import com.desk.android.sdk.helper.DeskThemeHelper;
import com.desk.android.sdk.helper.MenuHelper;
import com.desk.android.sdk.widget.ContactUsWebView;
import com.desk.java.apiclient.util.StringUtils;

import static com.desk.android.sdk.helper.DeskThemeHelper.EXTRA_THEME_RES_ID;
import static com.desk.android.sdk.helper.DeskThemeHelper.NO_THEME_RES_ID;

/**
 * <p>Displays a {@link ContactUsWebView} which will load the contact us web link in a web view.</p>
 *
 * Created by Matt Kranzler on 7/9/15.
 */
public class ContactUsWebActivity extends AppCompatActivity {

    private ContactUsWebView mContactUsWebView;

    /**
     * View the contact us form
     * @param activity the activity
     */
    public static void start(Activity activity) {
        start(activity, NO_THEME_RES_ID);
    }

    /**
     * View the contact us form with a custom theme
     * @param activity the activity
     * @param themeResId the resource id of the theme to use
     */
    public static void start(Activity activity, @StyleRes int themeResId) {
        Intent intent = new Intent(activity, ContactUsWebActivity.class);
        intent.putExtra(EXTRA_THEME_RES_ID, themeResId);
        activity.startActivity(intent);
    }

    private DeskThemeHelper mThemeHelper;
    private ContactUsConfig mConfig;
    private boolean mCallUsEnabled;
    private String mPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeHelper = new DeskThemeHelper(this);
        mConfig = Desk.with(this).getContactUsConfig();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us_web_activity);
        mContactUsWebView = (ContactUsWebView) findViewById(R.id.contact_us_form);
        initializeVariables();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_us_web_activity_menu, menu);
        MenuHelper.tintIcons(menu, mThemeHelper.getColorControlNormal(), R.id.submit, R.id.call_us);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem callUs = menu.findItem(R.id.call_us);
        if (callUs != null) {
            callUs.setVisible(mCallUsEnabled);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (R.id.call_us == id) {
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

    @Override
    public void onBackPressed() {

        // only exit the activity if the contact us view can no longer go back
        if (!mContactUsWebView.wentBack()) {
            super.onBackPressed();
        }
    }
}
