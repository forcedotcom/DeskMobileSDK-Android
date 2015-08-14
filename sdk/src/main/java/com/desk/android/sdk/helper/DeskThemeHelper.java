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

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;

import com.desk.android.sdk.R;

/**
 * <p>Parses the {@link Activity}'s intent to see if a theme resource id is set with the key {@link #EXTRA_THEME_RES_ID}
 * and sets the theme on the activity if one is set. Also takes care of extracting theme attributes
 * that the sdk cares about and exposes them through getter methods. You must create this class
 * before your activity's onCreate() method for the theme to work.</p>
 *
 * Created by Matt Kranzler on 7/9/15.
 */
public class DeskThemeHelper {

    private static final int[] ATTRS = new int[] {
            R.attr.dk_brandId,
            R.attr.dk_createCaseSuccessToast,
            R.attr.dk_createCaseErrorToast,
            R.attr.dk_allArticlesSearchQueryHint,
            R.attr.dk_articlesOfTopicSearchQueryHint,
            R.attr.actionBarTheme
    };

    private static final int[] APP_BAR_THEME_ATTRS = new int[] {
            R.attr.colorControlNormal
    };

    /**
     * Intent extra representing a theme resource id
     */
    public static final String EXTRA_THEME_RES_ID = "com.desk.android.sdk.EXTRA_THEME_RES_ID";

    /**
     * Represents no custom theme
     */
    public static final int NO_THEME_RES_ID = 0;

    /**
     * Represents no specific brand
     */
    public static final int ALL_BRANDS = 0;

    private Activity mActivity;

    @StyleRes
    private int mThemeResId;
    private int mBrandId;
    private String mCreateCaseSuccessToast;
    private String mCreateCaseErrorToast;
    private String mAllArticlesSearchQueryHint;
    private String mArticlesOfTopicSearchQueryHint;
    private int mColorControlNormal;

    public DeskThemeHelper(@NonNull Activity activity) {
        mActivity = activity;
        mThemeResId = mActivity.getIntent().getIntExtra(EXTRA_THEME_RES_ID, NO_THEME_RES_ID);
        if (NO_THEME_RES_ID != mThemeResId) {
            mActivity.setTheme(mThemeResId);
        }
        parseAttributes();
    }

    private void parseAttributes() {
        TypedArray ta = null;
        try {
            ta = mActivity.getTheme().obtainStyledAttributes(ATTRS);
            mBrandId = ta.getInteger(0, ALL_BRANDS);
            mCreateCaseSuccessToast = ta.getString(1);
            mCreateCaseErrorToast = ta.getString(2);
            mAllArticlesSearchQueryHint = ta.getString(3);
            mArticlesOfTopicSearchQueryHint = ta.getString(4);
            if (TextUtils.isEmpty(mCreateCaseSuccessToast)) {
                mCreateCaseSuccessToast = mActivity.getString(R.string.def_create_case_success_toast);
            }
            if (TextUtils.isEmpty(mCreateCaseErrorToast)) {
                mCreateCaseErrorToast = mActivity.getString(R.string.def_create_case_error_toast);
            }
            if (TextUtils.isEmpty(mAllArticlesSearchQueryHint)) {
                mAllArticlesSearchQueryHint = mActivity.getString(R.string.def_all_articles_search_text);
            }
            if (TextUtils.isEmpty(mArticlesOfTopicSearchQueryHint)) {
                mArticlesOfTopicSearchQueryHint = mActivity.getString(R.string.def_topic_articles_search_text);
            }
            int appBarTheme = ta.getResourceId(5, -1);
            if (appBarTheme != -1) {
                ta = mActivity.getTheme().obtainStyledAttributes(appBarTheme, APP_BAR_THEME_ATTRS);
                mColorControlNormal = ta.getColor(0, Color.WHITE);
            } else {
                mColorControlNormal = Color.WHITE;
            }
        } finally {
            if (ta != null) {
                ta.recycle();
            }
        }
    }

    /**
     * Get the theme resource id extracted from the intent extras
     * @return the theme resource id or {@link #NO_THEME_RES_ID} if no custom theme is specified
     */
    @StyleRes
    public int getThemeResId() {
        return mThemeResId;
    }

    /**
     * Get the brand id extracted from the custom theme
     * @return the brand id or {@link #ALL_BRANDS} if no brand is specified
     */
    public int getBrandId() {
        return mBrandId;
    }

    /**
     * Returns whether a specific brand has been specified for the theme
     * @return true if there is a specific brand, false if no brand is specified
     */
    public boolean hasBrandId() {
        return ALL_BRANDS != mBrandId;
    }

    /**
     * Get the toast text to use when a case has been created successfully
     * @return the text
     */
    public String getCreateCaseSuccessToast() {
        return mCreateCaseSuccessToast;
    }

    /**
     * Get the toast text to use when an error has occurred creating a case
     * @return the text
     */
    public String getCreateCaseErrorToast() {
        return mCreateCaseErrorToast;
    }

    /**
     * Get the text to use as the query hint for a SearchView that searches all articles
     * @return the text
     */
    public String getAllArticlesSearchQueryHint() {
        return mAllArticlesSearchQueryHint;
    }

    /**
     * Get the text to use as the query hint for a SearchView that searches articles of a specific topic
     * @return the text
     */
    public String getArticlesOfTopicSearchQueryHint() {
        return mArticlesOfTopicSearchQueryHint;
    }

    /**
     * Get the color to use for the Toolbar/ActionBar icons
     * @return the color
     */
    public int getColorControlNormal() {
        return mColorControlNormal;
    }
}
