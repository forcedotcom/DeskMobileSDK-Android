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
import com.desk.android.sdk.brand.BrandProvider;
import com.desk.android.sdk.fragment.ContactUsHelper;
import com.desk.android.sdk.fragment.SearchViewHelper;
import com.desk.android.sdk.helper.DeskThemeHelper;
import com.desk.android.sdk.widget.TopicListView;
import com.desk.java.apiclient.model.Topic;
import com.desk.java.apiclient.util.StringUtils;

import static com.desk.android.sdk.helper.DeskThemeHelper.EXTRA_THEME_RES_ID;
import static com.desk.android.sdk.helper.DeskThemeHelper.NO_THEME_RES_ID;

/**
 * <p>Displays a list of topics in a {@link TopicListView} and launches the {@link ArticleListActivity} on
 * selection of a topic in the list.</p>
 *
 * <p>This activity also provides a search view within the action bar to allow searching all articles.
 * A search will forward the query on to {@link ArticleListActivity} to perform the search.</p>
 *
 * <p>To start the activity call one of the following: {@link #start(Activity)}, {@link #start(Activity, String)}
 * to provide a title, {@link #start(Activity, int)} to provide a custom theme, or {@link #start(Activity, String, int)}
 * to provide a title and custom theme.</p>
 *
 * <p>To display only topics for a specific brand, create a custom theme that contains the {@link com.desk.android.sdk.R.attr#dk_brandId}
 * attribute and call a start method that supports a custom theme.</p>
 *
 * Created by Matt Kranzler on 6/30/15.
 */
public class TopicListActivity extends AppCompatActivity implements TopicListView.TopicSelectedListener,
        SearchViewHelper.SearchListener, BrandProvider {

    private static final String EXTRA_TITLE = "com.desk.android.sdk.EXTRA_TITLE";

    private DeskThemeHelper mThemeHelper;
    private SearchViewHelper mSearchViewHelper;

    /**
     * View a list of all topics
     * @param activity the activity
     */
    public static void start(Activity activity) {
        start(activity, null);
    }

    /**
     * View a list of all topics with a custom title
     * @param activity the activity
     * @param title the title
     */
    public static void start(Activity activity, String title) {
        start(activity, title, NO_THEME_RES_ID);
    }

    /**
     * View a list of all topics with a custom theme
     * @param activity the activity
     * @param themeResId the resource id of the theme to use
     */
    public static void start(Activity activity, @StyleRes int themeResId) {
        start(activity, null, themeResId);
    }

    /**
     * View a list of all topics with a custom title and custom theme
     * @param activity the activity
     * @param title the title
     * @param themeResId the resource id of the theme to use
     */
    public static void start(Activity activity, String title, @StyleRes int themeResId) {
        Intent intent = new Intent(activity, TopicListActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_THEME_RES_ID, themeResId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeHelper = new DeskThemeHelper(this);
        super.onCreate(savedInstanceState);
        ContactUsHelper.attach(this);
        mSearchViewHelper = SearchViewHelper.attach(this, mThemeHelper.getAllArticlesSearchQueryHint());
        setTitle();
        setContentView(R.layout.topic_list_activity);
        TopicListView topicsView = (TopicListView) findViewById(R.id.topics);
        topicsView.setTopicSelectedListener(this);
        if (savedInstanceState == null) {
            topicsView.loadTopics();
        }
    }

    private void setTitle() {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        if (!StringUtils.isEmpty(title)) {
            setTitle(title);
        }
    }

    @Override
    public void onTopicSelected(Topic topic) {
        ArticleListActivity.start(this, topic, mThemeHelper.getThemeResId());
    }

    @Override
    public void onPerformSearch(String query) {
        mSearchViewHelper.closeSearchView();
        ArticleListActivity.start(TopicListActivity.this, query, mThemeHelper.getThemeResId());
    }

    @Override
    public void onBackPressed() {

        // first close the search view if applicable
        if (mSearchViewHelper.closeSearchView()) {
            return;
        }

        super.onBackPressed();
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
