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
import com.desk.android.sdk.adapter.TopicListAdapter;
import com.desk.android.sdk.brand.BrandProvider;
import com.desk.android.sdk.error.ErrorResponse;
import com.desk.android.sdk.provider.TopicProvider;
import com.desk.java.apiclient.model.Topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.desk.android.sdk.provider.TopicProvider.ALL_BRANDS;

/**
 * <p>Layout which displays a list of topics and displays a progress bar during the initial load.
 * This layout will also display empty text if there are no topics and error text if an error
 * occurs while attempting to load the topics.</p>
 *
 * <p>To set the empty text for this view make sure to set a style via the {@link com.desk.android.sdk.R.attr#dk_topicListViewStyle}
 * and override the {@link com.desk.android.sdk.R.styleable#TopicListView_dk_emptyText} attribute, or
 * override the {@link com.desk.android.sdk.R.styleable#TopicListView_dk_emptyText} attribute in your layout file.</p>
 *
 * <p>To set the error text for this view make sure to set a style via the {@link com.desk.android.sdk.R.attr#dk_topicListViewStyle}
 * and override the {@link com.desk.android.sdk.R.styleable#TopicListView_dk_errorText} attribute, or
 * override the {@link com.desk.android.sdk.R.styleable#TopicListView_dk_errorText} attribute in your layout file.</p>
 *
 * Created by Matt Kranzler on 6/29/15.
 */
public class TopicListView extends FrameLayout implements AdapterView.OnItemClickListener {

    /**
     * Listener for when a topic is selected from the list
     */
    public interface TopicSelectedListener {
        void onTopicSelected(Topic topic);
    }

    private ListView mList;
    private ProgressBar mProgress;
    private TextView mEmpty;

    private String mEmptyText;
    private String mErrorText;

    private Desk mDesk;
    private TopicListAdapter mAdapter;
    private List<Topic> mTopics;
    private boolean mHaveError;

    private int mBrandId;
    private boolean mIsBranded;

    private TopicSelectedListener mTopicSelectedListener;

    public TopicListView(Context context) {
        this(context, null);
    }

    public TopicListView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dk_topicListViewStyle);
    }

    public TopicListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TopicListView, defStyleAttr, R.style.TopicListViewStyle);
        mEmptyText = ta.getString(R.styleable.TopicListView_dk_emptyText);
        mErrorText = ta.getString(R.styleable.TopicListView_dk_errorText);
        ta.recycle();
    }

    private void init(Context context) {
        mDesk = Desk.with(context);
        LayoutInflater.from(context).inflate(R.layout.list_view_with_progress_empty, this, true);
        mList = (ListView) findViewById(android.R.id.list);
        mProgress = (ProgressBar) findViewById(android.R.id.progress);
        mEmpty = (TextView) findViewById(android.R.id.empty);
        if (getContext() instanceof BrandProvider) {
            BrandProvider provider = (BrandProvider) getContext();
            mIsBranded = provider.isBranded();
            mBrandId = provider.getBrandId();
        }
        mTopics = new ArrayList<>();
        mAdapter = new TopicListAdapter(context, mTopics);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);
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
     * Loads all topics for all brands
     */
    public void loadTopics() {
        hideList();
        mAdapter.clear();
        hideEmptyView();
        showProgress();

        mDesk.getTopicProvider()
                .getTopics(mIsBranded ? mBrandId : ALL_BRANDS, new TopicProvider.TopicCallbacks()  {
                    @Override
                    public void onTopicsLoaded(List<Topic> topics) {
                        onLoaded(topics);
                    }

                    @Override
                    public void onTopicsLoadError(ErrorResponse error) {
                        onLoadError();
                    }
                });
    }

    @VisibleForTesting
    void onLoaded(List<Topic> topics) {
        if (getContext() != null) {
            hideProgress();
            if (topics.size() > 0) {
                mAdapter.addAll(topics);
                showList();
            } else {
                showEmptyView(mEmptyText);
            }
        }
    }

    @VisibleForTesting
    void onLoadError() {
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

    private void hideEmptyView() {
        mEmpty.setVisibility(View.GONE);
    }

    private void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * Set the listener to handle when a topic is selected
     * @param listener the listener
     */
    public void setTopicSelectedListener(TopicSelectedListener listener) {
        mTopicSelectedListener = listener;
    }

    @VisibleForTesting
    TopicSelectedListener getTopicSelectedListener() {
        return mTopicSelectedListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mTopicSelectedListener != null) {
            mTopicSelectedListener.onTopicSelected(mAdapter.getItem(position));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // clear reference so we don't leak
        mTopicSelectedListener = null;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.topics = mTopics;
        savedState.haveError = mHaveError;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mHaveError = savedState.haveError;
        if (mHaveError) {
            onLoadError();
        } else if (savedState.topics != null) {
            onLoaded(savedState.topics);
        }
    }

    static class SavedState extends BaseSavedState {

        List<Topic> topics;
        boolean haveError;

        SavedState(Parcelable superState) {
            super(superState);
        }

        @SuppressWarnings("unchecked")
        private SavedState(Parcel in) {
            super(in);
            topics = (List<Topic>) in.readSerializable();
            haveError = in.readInt() == 1;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeSerializable((Serializable) topics);
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
