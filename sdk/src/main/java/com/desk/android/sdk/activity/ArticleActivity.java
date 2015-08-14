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
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;

import com.desk.android.sdk.R;
import com.desk.android.sdk.fragment.ContactUsHelper;
import com.desk.android.sdk.helper.DeskThemeHelper;
import com.desk.android.sdk.widget.ArticleView;
import com.desk.java.apiclient.model.Article;

import static com.desk.android.sdk.helper.DeskThemeHelper.EXTRA_THEME_RES_ID;
import static com.desk.android.sdk.helper.DeskThemeHelper.NO_THEME_RES_ID;

/**
 * <p>Displays article details within an {@link com.desk.android.sdk.widget.ArticleView}.</p>
 *
 * <p>To start this activity call {@link #start(Activity, Article)} or
 * {@link #start(Activity, Article, int)}.</p>
 *
 * Created by Matt Kranzler on 6/30/15.
 */
public class ArticleActivity extends AppCompatActivity {

    public static final String EXTRA_ARTICLE = "com.desk.android.sdk.EXTRA_ARTICLE";

    private ArticleView mArticleDetails;
    private Article mArticle;
    private DeskThemeHelper mThemeHelper;

    /**
     * View the details of the article provided
     * @param activity the activity
     * @param article the article to view the details
     */
    public static void start(Activity activity, Article article) {
        start(activity, article, NO_THEME_RES_ID);
    }

    /**
     * View the details of the article provided with a custom theme
     * @param activity the activity
     * @param article the article to view the details
     * @param themeResId the resource id of the theme to use
     */
    public static void start(Activity activity, Article article, @StyleRes int themeResId) {
        Intent intent = new Intent(activity, ArticleActivity.class);
        intent.putExtra(EXTRA_ARTICLE, article);
        intent.putExtra(EXTRA_THEME_RES_ID, themeResId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeHelper = new DeskThemeHelper(this);
        super.onCreate(savedInstanceState);
        ContactUsHelper.attach(this);
        mArticle = (Article) getIntent().getSerializableExtra(EXTRA_ARTICLE);
        setContentView(R.layout.article_activity);
        mArticleDetails = (ArticleView) findViewById(R.id.article_details);
        mArticleDetails.loadArticle(mArticle);
        setTitle(mArticle.getSubject());
    }

    @Override
    public void onBackPressed() {

        // only exit the activity if the article details view can no longer go back
        if (!mArticleDetails.wentBack()) {
            super.onBackPressed();
        }
    }
}
