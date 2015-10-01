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

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.provider.ArticleProvider;
import com.desk.android.sdk.test.R;
import com.desk.android.sdk.util.DeskDefaultsRule;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.Article;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Collections;
import java.util.List;

import static com.desk.android.sdk.util.InstrumentationTestUtils.getMockedArticleResponse;
import static com.desk.android.sdk.util.TestUtils.getString;
import static com.desk.android.sdk.util.TestUtils.inflateView;
import static com.desk.android.sdk.util.TestUtils.readMockJsonFile;
import static org.assertj.android.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link ArticleListView}
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class ArticleListViewTest {

    @Mock
    static ArticleProvider mockArticleProvider = mock(ArticleProvider.class);

    @ClassRule
    public static DeskDefaultsRule resetRule = new DeskDefaultsRule();

    private ArticleListView articleListView;
    private List<Article> mockArticles;
    private ArticleListView.ArticleSelectedListener listener;

    @BeforeClass
    public static void preSetup() {
        Desk.with(InstrumentationRegistry.getContext()).setArticleProvider(mockArticleProvider);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                // create a thread to simulate the asynchronous retrofit call
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // sleep for a second to simulate network delay
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {/*ignore*/}
                        ApiResponse<Article> mockArticleResponse = getMockedArticleResponse();
                        ((ArticleProvider.ArticleCallbacks) invocation.getArguments()[3]).onArticlesLoaded(1, mockArticleResponse.getEntriesAsList(), false);
                    }
                }).start();
                return null;
            }
        }).when(mockArticleProvider).getArticles(anyInt(), anyInt(), anyInt(), any(ArticleProvider.ArticleCallbacks.class));
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                // create a thread to simulate the asynchronous retrofit call
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // sleep for a second to simulate network delay
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {/*ignore*/}
                        ApiResponse<Article> mockArticleResponse = getMockedArticleResponse();
                        ((ArticleProvider.ArticleCallbacks) invocation.getArguments()[4]).onArticlesLoaded(1, mockArticleResponse.getEntriesAsList(), false);
                    }
                }).start();
                return null;
            }
        }).when(mockArticleProvider).findArticles(anyInt(), anyInt(), anyString(), anyInt(), any(ArticleProvider.ArticleCallbacks.class));
    }

    @Before
    public void setUp() throws Exception {
        mockArticles = getMockArticles();
        listener = mock(ArticleListView.ArticleSelectedListener.class);
        articleListView = new ArticleListView(InstrumentationRegistry.getTargetContext());
        articleListView.setArticleSelectedListener(listener);
    }

    @Test
    public void emptyTextMatchesDefault() throws Exception {
        assertEquals(getString(R.string.def_articles_empty_text), articleListView.getEmptyText());
    }

    @Test
    public void errorTextMatchesDefault() throws Exception {
        assertEquals(getString(R.string.def_articles_error_text), articleListView.getErrorText());
    }

    @Test
    public void emptyTextMatchesValueFromLayoutAttrs() throws Exception {
        ArticleListView articleListView = inflateView(R.layout.article_list_view_with_attributes);
        assertEquals(getString(R.string.test_attr_empty_text), articleListView.getEmptyText());
    }

    @Test
    public void errorTextMatchesValueFromLayoutAttrs() throws Exception {
        ArticleListView articleListView = inflateView(R.layout.article_list_view_with_attributes);
        assertEquals(getString(R.string.test_attr_error_text), articleListView.getErrorText());
    }

    @Test
    public void emptyTextMatchesValueFromStyle() throws Exception {
        ArticleListView articleListView = inflateView(R.layout.article_list_view_with_style);
        assertEquals(getString(R.string.test_style_empty_text), articleListView.getEmptyText());
    }

    @Test
    public void errorTextMatchesValueFromStyle() throws Exception {
        ArticleListView articleListView = inflateView(R.layout.article_list_view_with_style);
        assertEquals(getString(R.string.test_style_error_text), articleListView.getErrorText());
    }

    @Test
    public void loadArticlesHidesList() throws Exception {
        ListView list = getList();
        assertThat(list).isVisible();
        articleListView.loadArticles(1);
        assertThat(list).isGone();
    }

    @Test
    public void loadArticlesShowsProgress() throws Exception {
        ProgressBar progress = getProgress();
        assertThat(progress).isGone();
        articleListView.loadArticles(1);
        assertThat(progress).isVisible();
    }

    @Test
    public void searchArticlesHidesList() throws Exception {
        ListView list = getList();
        assertThat(list).isVisible();
        articleListView.searchArticles("query");
        assertThat(list).isGone();
    }

    @Test
    public void searchArticlesShowsProgress() throws Exception {
        ProgressBar progress = getProgress();
        assertThat(progress).isGone();
        articleListView.searchArticles("query");
        assertThat(progress).isVisible();
    }

    @Test
    public void onPageLoadedHidesProgress() throws Exception {
        ProgressBar progress = getProgress();
        articleListView.onPageLoaded(mockArticles, 1, false);
        assertThat(progress).isGone();
    }

    @Test
    public void onPageLoadedShowsList() throws Exception {
        ListView list = getList();
        articleListView.onPageLoaded(mockArticles, 1, false);
        assertThat(list).isVisible();
    }

    @Test
    public void onPageLoadedShowsEmptyWithEmptyText() throws Exception {
        TextView empty = getEmpty();
        articleListView.onPageLoaded(Collections.<Article>emptyList(), 1, false);
        assertThat(empty).isVisible();
        assertThat(empty).hasText(articleListView.getEmptyText());
    }

    @Test
    public void onPageLoadedDoesNotShowEmpty() throws Exception {
        TextView empty = getEmpty();
        articleListView.onPageLoaded(Collections.<Article>emptyList(), 2, false);
        assertThat(empty).isGone();
    }

    @Test
    public void onArticleLoadErrorHidesProgress() throws Exception {
        ProgressBar progress = getProgress();
        articleListView.onArticleLoadError();
        assertThat(progress).isGone();
    }

    @Test
    public void onArticleLoadErrorShowsEmptyWithErrorText() throws Exception {
        TextView empty = getEmpty();
        articleListView.onArticleLoadError();
        assertThat(empty).isVisible();
        assertThat(empty).hasText(articleListView.getErrorText());
    }

    @Test
    public void onItemClickCallsListenerWithCorrectArticle() throws Exception {
        articleListView.onPageLoaded(mockArticles, 1, false);
        ListView list = getList();
        for (int i = 0; i < mockArticles.size(); i++) {
            articleListView.onItemClick(list, list.getChildAt(i), i, 0);
            verify(listener).onArticleSelected(mockArticles.get(i));
        }
    }

    @Test
    public void listenerClearedInOnDetachedFromWindow() throws Exception {
        assertNotNull(articleListView.getArticleSelectedListener());
        articleListView.onDetachedFromWindow();
        assertNull(articleListView.getArticleSelectedListener());
    }

    @Test
    public void onSaveInstanceStateSavesStateWithoutError() throws Exception {
        final int topicId = 1;
        articleListView.loadArticles(topicId);
        articleListView.onPageLoaded(mockArticles, 1, false);
        ArticleListView.SavedState savedState = (ArticleListView.SavedState) articleListView.onSaveInstanceState();
        assertEquals(savedState.articles, mockArticles);
        assertEquals(savedState.topicId, topicId);
        assertNull(savedState.query);
        assertEquals(savedState.mode, ArticleListView.MODE_TOPIC);
        assertEquals(savedState.currentPage, 1);
        assertFalse(savedState.haveNextPage);
        assertFalse(savedState.haveError);
    }

    @Test
    public void onSaveInstanceStateSavesStateWithError() throws Exception {
        final int topicId = 1;
        articleListView.loadArticles(topicId);
        articleListView.onArticleLoadError();
        ArticleListView.SavedState savedState = (ArticleListView.SavedState) articleListView.onSaveInstanceState();
        assertTrue(savedState.articles.isEmpty());
        assertEquals(savedState.topicId, topicId);
        assertNull(savedState.query);
        assertEquals(savedState.mode, ArticleListView.MODE_TOPIC);
        assertEquals(savedState.currentPage, 0);
        assertFalse(savedState.haveNextPage);
        assertTrue(savedState.haveError);
    }

    private ListView getList() {
        return (ListView) articleListView.findViewById(android.R.id.list);
    }

    private ProgressBar getProgress() {
        return (ProgressBar) articleListView.findViewById(android.R.id.progress);
    }

    private TextView getEmpty() {
        return (TextView) articleListView.findViewById(android.R.id.empty);
    }

    private List<Article> getMockArticles() {
        ApiResponse<Article> response = readMockJsonFile(
                new TypeToken<ApiResponse<Article>>(){}.getType(),
                "mock/mock_article_response.json"
        );
        assertNotNull(response);
        return response.getEntriesAsList();
    }
}