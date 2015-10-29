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

package com.desk.android.sdk.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.desk.android.sdk.R;

/**
 * <p>Headless fragment which adds a SearchView to the action bar and manages action bar state related
 * to the SearchView. Searches performed will notify via {@link com.desk.android.sdk.fragment.SearchViewHelper.SearchListener#onPerformSearch(String)}.</p>
 *
 * Created by Matt Kranzler on 7/9/15.
 */
public class SearchViewHelper extends Fragment {

    private static final String FRAG_TAG = SearchViewHelper.class.getCanonicalName();
    private static final String ARG_QUERY_HINT = "queryHint";

    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;

    public interface SearchListener {
        void onPerformSearch(String query);
    }

    /**
     * Attaches the fragment to the activity and adds the menu provided.
     * @param parent the activity which extends {@link Activity} & {@link com.desk.android.sdk.fragment.SearchViewHelper.SearchListener}
     * @param queryHint the hint for the search query field
     * @param <SearchViewActivity> extends {@link Activity} and implements {@link com.desk.android.sdk.fragment.SearchViewHelper.SearchListener}
     * @return the {@link SearchViewHelper} instance attached.
     */
    public static <SearchViewActivity extends Activity & SearchListener> SearchViewHelper attach(SearchViewActivity parent, String queryHint) {
        return attach(parent.getFragmentManager(), queryHint);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        mSearchMenuItem = menu.findItem(R.id.search);
        if (mSearchMenuItem != null) {
            mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
            if (mSearchView != null) {
                mSearchView.setQueryHint(getArguments().getString(ARG_QUERY_HINT));
                setupSearchViewListeners();
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupSearchViewListeners() {

        // search all topics when search is submitted
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (getParent() != null) {
                    getParent().onPerformSearch(query.trim());
                }
                closeSearchView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * Attempts to iconify the search view
     * @return true if the search view iconified (closed), false if not
     */
    public boolean closeSearchView() {
        if (mSearchMenuItem != null && mSearchMenuItem.isActionViewExpanded()) {
            mSearchMenuItem.collapseActionView();
            return true;
        } else {
            return false;
        }
    }

    private SearchListener getParent() {
        Activity activity = getActivity();
        if (activity instanceof SearchListener) {
            return (SearchListener) activity;
        }
        return null;
    }

    private static SearchViewHelper attach(FragmentManager fragmentManager, String queryHint) {
        Bundle args = new Bundle();
        args.putString(ARG_QUERY_HINT, queryHint);
        SearchViewHelper frag = (SearchViewHelper) fragmentManager.findFragmentByTag(FRAG_TAG);
        if (frag == null) {
            frag = new SearchViewHelper();
            frag.setArguments(args);
            fragmentManager.beginTransaction().add(frag, FRAG_TAG).commit();
        }
        return frag;
    }

}