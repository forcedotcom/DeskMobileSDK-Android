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
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.R;
import com.desk.android.sdk.adapter.ArticleListAdapter;
import com.desk.android.sdk.brand.BrandProvider;
import com.desk.android.sdk.error.ErrorResponse;
import com.desk.android.sdk.provider.ArticleProvider;
import com.desk.android.sdk.util.EndlessScrollListener;
import com.desk.java.apiclient.model.Article;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.desk.android.sdk.provider.ArticleProvider.ALL_BRANDS;
import static com.desk.android.sdk.provider.ArticleProvider.ALL_TOPICS;

/**
 * <p>Layout which displays a list of articles and displays a progress bar during the initial load.
 * This layout will also display empty text if there are no articles and error text if an error
 * occurs while attempting to load the articles.</p>
 *
 * <p>To set the empty text for this view make sure to set a style via the {@link com.desk.android.sdk.R.attr#dk_articleListViewStyle}
 * and override the {@link com.desk.android.sdk.R.styleable#ArticleListView_dk_emptyText} attribute, or
 * override the {@link com.desk.android.sdk.R.styleable#ArticleListView_dk_emptyText} attribute in your layout file.</p>
 *
 * <p>To set the error text for this view make sure to set a style via the {@link com.desk.android.sdk.R.attr#dk_articleListViewStyle}
 * and override the {@link com.desk.android.sdk.R.styleable#ArticleListView_dk_errorText} attribute, or
 * override the {@link com.desk.android.sdk.R.styleable#ArticleListView_dk_errorText} attribute in your layout file.</p>
 *
 * Created by Matt Kranzler on 6/29/15.
 */
public class ArticleListView extends FrameLayout implements AdapterView.OnItemClickListener {

    /**
     * Listener for when an article is selected from the list
     */
    public interface ArticleSelectedListener {
        void onArticleSelected(Article article);
    }

    @VisibleForTesting
    static final int MODE_TOPIC = 0;
    @VisibleForTesting
    static final int MODE_SEARCH = 1;

    private static final int VISIBLE_THRESHOLD = 10;

    private ListView mList;
    private ProgressBar mProgress;
    private TextView mEmpty;

    private String mEmptyText;
    private String mErrorText;

    private Desk mDesk;
    private ArticleListAdapter mAdapter;
    private List<Article> mArticles;

    private int mTopicId;
    private String mQuery;
    private int mMode;
    private int mCurrentPage;
    private boolean mHaveNextPage;
    private boolean mHaveError;

    private int mBrandId;
    private boolean mIsBranded;

    private ArticleSelectedListener mArticleSelectedListener;

    public ArticleListView(Context context) {
        this(context, null);
    }

    public ArticleListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dk_articleListViewStyle);
    }

    public ArticleListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArticleListView, defStyleAttr, R.style.ArticleListViewStyle);
        mEmptyText = ta.getString(R.styleable.ArticleListView_dk_emptyText);
        mErrorText = ta.getString(R.styleable.ArticleListView_dk_errorText);
        ta.recycle();
    }

    private void init(Context context) {
        mDesk = Desk.with(context);
        LayoutInflater.from(context).inflate(R.layout.list_view_with_progress_empty, this, true);
        if (getContext() instanceof BrandProvider) {
            BrandProvider provider = (BrandProvider) getContext();
            mIsBranded = provider.isBranded();
            mBrandId = mIsBranded ? provider.getBrandId() : ALL_BRANDS;
        }
        mArticles = new ArrayList<>();
        mAdapter = new ArticleListAdapter(getContext(), mArticles);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mList = (ListView) findViewById(android.R.id.list);
        mProgress = (ProgressBar) findViewById(android.R.id.progress);
        mEmpty = (TextView) findViewById(android.R.id.empty);
    }

    private void initializeList() {
        if (mList.getAdapter() == null) {
            mList.setAdapter(mAdapter);
            mList.setOnScrollListener(new EndlessScrollListener(VISIBLE_THRESHOLD, mCurrentPage) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    if (mHaveNextPage) {
                        loadPage(page);
                    }
                }
            });
            mList.setOnItemClickListener(this);
        }
    }

    @VisibleForTesting
    String getEmptyText() {
        return mEmptyText;
    }

    @VisibleForTesting
    String getErrorText() {
        return mErrorText;
    }

    /**
     * Loads all articles for the topic provided
     * @param topicId the topic id to limit articles to
     */
    public void loadArticles(int topicId) {
        mMode = MODE_TOPIC;
        mTopicId = topicId;
        mQuery = null;
        mCurrentPage = 0;
        hideList();
        showProgress();
        loadPage(1);
    }

    /**
     * Searches for articles that contain the query provided across
     * subject, body_text, keywords, question, answer
     * @param query the search query
     */
    public void searchArticles(String query) {
        searchArticles(ALL_TOPICS, query);
    }

    /**
     * Searches for articles for a given topic and given brand that contain the query provided across
     * subject, body_text, keywords, question, answer
     * @param topicId the topic id to limit results to
     * @param query the search query
     */
    public void searchArticles(int topicId, String query) {
        mMode = MODE_SEARCH;
        mTopicId = topicId;
        mQuery = query;
        mCurrentPage = 0;
        hideList();
        showProgress();
        loadPage(1);
    }

    private void loadPage(int page) {
        ArticleProvider provider = mDesk.getArticleProvider();
        if (MODE_TOPIC == mMode) {
            provider.getArticles(mTopicId, mBrandId, page, new Callback());
        } else if (MODE_SEARCH == mMode) {
            provider.findArticles(mTopicId, mBrandId, mQuery, page, new Callback());
        } else {
            throw new IllegalStateException("Unexpected mode " + mMode);
        }
    }

    @VisibleForTesting
    void onPageLoaded(List<Article> articles, int page, boolean haveNextPage) {
        if (getContext() != null) {
            mCurrentPage = page;
            mHaveNextPage = haveNextPage;
            hideProgress();
            if (articles.isEmpty()) {
                if (page == 1) {
                    showEmptyView(mEmptyText);
                }
                return;
            }
            initializeList();
            mAdapter.addAll(articles);
            showList();
        }
    }

    @VisibleForTesting
    void onArticleLoadError() {
        if (getContext() != null) {
            mHaveError = true;
            hideProgress();
            showEmptyView(mErrorText);
        }
    }

    private void showList() {
        mList.setVisibility(View.VISIBLE);
    }

    private void hideList() {
        mList.setVisibility(View.GONE);
    }

    private void showEmptyView(String text) {
        mEmpty.setText(text);
        mEmpty.setVisibility(View.VISIBLE);
    }

    private void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * Set the listener to handle when an article is selected
     * @param listener the listener
     */
    public void setArticleSelectedListener(ArticleSelectedListener listener) {
        mArticleSelectedListener = listener;
    }

    @VisibleForTesting
    ArticleSelectedListener getArticleSelectedListener() {
        return mArticleSelectedListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mArticleSelectedListener != null) {
            mArticleSelectedListener.onArticleSelected(mAdapter.getItem(position));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // clear reference so we don't leak
        mArticleSelectedListener = null;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.articles = mArticles;
        savedState.topicId = mTopicId;
        savedState.query = mQuery;
        savedState.mode = mMode;
        savedState.currentPage = mCurrentPage;
        savedState.haveNextPage = mHaveNextPage;
        savedState.haveError = mHaveError;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mTopicId = savedState.topicId;
        mQuery = savedState.query;
        mMode = savedState.mode;
        mCurrentPage = savedState.currentPage;
        mHaveNextPage = savedState.haveNextPage;
        mHaveError = savedState.haveError;

        // if we have articles restore them
        if (mHaveError) {
            onArticleLoadError();
        } else if (savedState.articles != null) {
            onPageLoaded(savedState.articles, mCurrentPage, mHaveNextPage);
        }
    }

    class Callback implements ArticleProvider.ArticleCallbacks {

        @Override
        public void onArticlesLoaded(int page, List<Article> articles, boolean morePages) {
            onPageLoaded(articles, page, morePages);
        }

        @Override
        public void onArticlesLoadError(ErrorResponse error) {
            onArticleLoadError();
        }
    }

    static class SavedState extends BaseSavedState {

        List<Article> articles;
        int topicId;
        String query;
        int mode;
        int currentPage;
        boolean haveNextPage;
        boolean haveError;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @SuppressWarnings("unchecked")
        private SavedState(Parcel in) {
            super(in);
            articles = (List<Article>) in.readSerializable();
            topicId = in.readInt();
            query = in.readString();
            mode = in.readInt();
            currentPage = in.readInt();
            haveNextPage = in.readInt() == 1;
            haveError = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeSerializable((Serializable) articles);
            dest.writeInt(topicId);
            dest.writeString(query);
            dest.writeInt(mode);
            dest.writeInt(currentPage);
            dest.writeInt(haveNextPage ? 1 : 0);
            dest.writeInt(haveError ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
