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
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

import com.desk.android.sdk.R;
import com.desk.android.sdk.brand.BrandProvider;
import com.desk.android.sdk.fragment.ContactUsHelper;
import com.desk.android.sdk.fragment.SearchViewHelper;
import com.desk.android.sdk.helper.DeskThemeHelper;
import com.desk.android.sdk.widget.ArticleListView;
import com.desk.java.apiclient.model.Article;
import com.desk.java.apiclient.model.Topic;

import static com.desk.android.sdk.helper.DeskThemeHelper.EXTRA_THEME_RES_ID;
import static com.desk.android.sdk.helper.DeskThemeHelper.NO_THEME_RES_ID;

/**
 * <p>Displays a list of articles within a {@link ArticleListView} and launches the {@link ArticleActivity}
 * on selection of an article within the list.</p>
 *
 * <p>This activity has two modes; Viewing articles of a topic or searching for articles. To view articles
 * of a topic call either {@link #start(Activity, Topic)} or {@link #start(Activity, Topic, int)}.
 * To search all public articles launch either {@link #start(Activity, String)}
 * or {@link #start(Activity, String, int)}. To search for articles with a topic call either
 * {@link #start(Activity, Topic, String)} or {@link #start(Activity, Topic, String, int)}.</p>
 *
 * <p>To display only articles for a specific brand, create a custom theme that contains the {@link com.desk.android.sdk.R.attr#dk_brandId}
 * attribute and call a start method that support a custom theme.</p>
 *
 * Created by Matt Kranzler on 6/30/15.
 */
public class ArticleListActivity extends AppCompatActivity implements ArticleListView.ArticleSelectedListener,
        SearchViewHelper.SearchListener, BrandProvider {

    @VisibleForTesting static final String EXTRA_TOPIC = "com.desk.android.sdk.EXTRA_TOPIC";
    @VisibleForTesting static final String EXTRA_QUERY = "com.desk.android.sdk.EXTRA_QUERY";
    @VisibleForTesting static final String EXTRA_MODE = "com.desk.android.sdk.EXTRA_MODE";

    @VisibleForTesting static final int MODE_TOPIC = 1;
    @VisibleForTesting static final int MODE_SEARCH = 2;

    private DeskThemeHelper mThemeHelper;
    private SearchViewHelper mSearchViewHelper;
    private ArticleListView mArticlesView;
    private int mMode;
    private Topic mTopic;
    private String mQuery;

    /**
     * View a list of articles for a topic
     * @param activity the activity
     * @param topic the topic to view articles for
     */
    public static void start(Activity activity, Topic topic) {
        start(activity, topic, NO_THEME_RES_ID);
    }

    /**
     * View a list of articles for a topic with a custom theme
     * @param activity the activity
     * @param topic the topic to view articles for
     * @param themeResId the resource id of the theme to use
     */
    public static void start(Activity activity, Topic topic, @StyleRes int themeResId) {
        Intent intent = new Intent(activity, ArticleListActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_TOPIC);
        intent.putExtra(EXTRA_TOPIC, topic);
        intent.putExtra(EXTRA_THEME_RES_ID, themeResId);
        activity.startActivity(intent);
    }

    /**
     * Searches all topics for articles that match the search query and displays a list of the results
     * @param activity the activity
     * @param query the search query; Can be any text and will search across subject, body_text, keywords, question, answer
     */
    public static void start(Activity activity, String query) {
        start(activity, query, NO_THEME_RES_ID);
    }

    /**
     * Searches all topics for articles that match the search query and displays a list of the results
     * with a custom theme
     * @param activity the activity
     * @param query the search query; Can be any text and will search across subject, body_text, keywords, question, answer
     * @param themeResId the resource id of the theme to use
     */
    public static void start(Activity activity, String query, @StyleRes int themeResId) {
        Intent intent = new Intent(activity, ArticleListActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_SEARCH);
        intent.putExtra(EXTRA_QUERY, query);
        intent.putExtra(EXTRA_THEME_RES_ID, themeResId);
        activity.startActivity(intent);
    }

    /**
     * Searches a topic for article that match the search query and displays a list of the results
     * @param activity the activity
     * @param topic the topic to search
     * @param query the search query; Can be any text and will search across subject, body_text, keywords, question, answer
     */
    public static void start(Activity activity, Topic topic, String query) {
        start(activity, topic, query, NO_THEME_RES_ID);
    }

    /**
     * Searches a topic for article that match the search query and displays a list of the results
     * with a custom theme
     * @param activity the activity
     * @param topic the topic to search
     * @param query the search query; Can be any text and will search across subject, body_text, keywords, question, answer
     * @param themeResId the resource id of the theme to use
     */
    public static void start(Activity activity, Topic topic, String query, @StyleRes int themeResId) {
        Intent intent = new Intent(activity, ArticleListActivity.class);
        intent.putExtra(EXTRA_MODE, MODE_SEARCH);
        intent.putExtra(EXTRA_QUERY, query);
        intent.putExtra(EXTRA_TOPIC, topic);
        intent.putExtra(EXTRA_THEME_RES_ID, themeResId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeHelper = new DeskThemeHelper(this);
        super.onCreate(savedInstanceState);
        ContactUsHelper.attach(this);
        setContentView(R.layout.article_list_activity);
        mArticlesView = (ArticleListView) findViewById(R.id.articles);
        mArticlesView.setArticleSelectedListener(this);
        handleIntent(getIntent(), savedInstanceState != null);
    }

    private void handleIntent(Intent intent, boolean haveSavedState) {
        mMode = intent.getIntExtra(EXTRA_MODE, 0);
        mTopic = (Topic) intent.getSerializableExtra(EXTRA_TOPIC);
        mQuery = intent.getStringExtra(EXTRA_QUERY);
        switch (mMode) {
            case MODE_TOPIC:
                handleTopicMode(haveSavedState);
                mSearchViewHelper = SearchViewHelper.attach(this, mThemeHelper.getArticlesOfTopicSearchQueryHint());
                break;
            case MODE_SEARCH:
                handleSearchMode(haveSavedState);
                break;
            default:
                throw new IllegalStateException("Mode " + mMode + " is unsupported.");
        }
    }

    private void handleTopicMode(boolean haveSavedState) {
        if (mTopic == null) {
            throw new IllegalStateException("Mode " + mMode + " requires a " + EXTRA_TOPIC + " passed as an intent extra.");
        }
        if (!haveSavedState) {
            mArticlesView.loadArticles(mTopic.getId());
        }
        setTitle(mTopic.getName());
    }

    private void handleSearchMode(boolean haveSavedState) {
        if (mQuery == null) {
            throw new IllegalStateException("Mode " + mMode + " requires a " + EXTRA_QUERY + " passed as an intent extra.");
        }
        if (!haveSavedState) {
            if (mTopic != null) {
                mArticlesView.searchArticles(mTopic.getId(), mQuery);
            } else {
                mArticlesView.searchArticles(mQuery);
            }
        }
        setTitle(getString(R.string.def_articles_search_results_title, mQuery));
    }

    @Override
    public void onArticleSelected(Article article) {
        ArticleActivity.start(this, article, mThemeHelper.getThemeResId());
    }

    @Override
    public void onPerformSearch(String query) {
        mSearchViewHelper.closeSearchView();
        start(ArticleListActivity.this, mTopic, query, mThemeHelper.getThemeResId());
    }

    @Override
    public void onBackPressed() {

        // first close the search view if applicable
        if (mSearchViewHelper != null && mSearchViewHelper.closeSearchView()) {
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
