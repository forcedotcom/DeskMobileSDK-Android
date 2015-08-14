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

package com.desk.android.sdk.provider;

import android.test.suitebuilder.annotation.SmallTest;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.error.ErrorResponse;
import com.desk.android.sdk.util.TestUtils;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.Article;
import com.desk.java.apiclient.model.BrandIds;
import com.desk.java.apiclient.model.SortDirection;
import com.desk.java.apiclient.model.TopicIds;
import com.desk.java.apiclient.service.ArticleService;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;

import retrofit.Callback;
import retrofit.RetrofitError;

import static com.desk.android.sdk.provider.ArticleProvider.ALL_BRANDS;
import static com.desk.android.sdk.provider.ArticleProvider.ALL_TOPICS;
import static com.desk.android.sdk.provider.ArticleProvider.ArticleCallbacks;
import static com.desk.android.sdk.provider.ArticleProvider.PER_PAGE;
import static com.desk.android.sdk.provider.ArticleProvider.RetrofitCallback;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNotNull;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link ArticleProvider}
 */
@SmallTest
@SuppressWarnings("unchecked")
public class ArticleProviderTest {

    private ArticleCallbacks callback;
    private MockArticleService mockArticleService;
    private ArticleProvider articleProvider;

    @Before
    public void setUp() throws Exception {
        mockArticleService = spy(new MockArticleService());
        articleProvider = new ArticleProvider(mockArticleService);
        callback = mock(ArticleCallbacks.class);
    }

    // region getArticles() Tests

    @Test
    public void getArticlesDoesNotPassTopicIdForAllTopics() throws Exception {
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, 1, callback);
        verify(mockArticleService).getArticles(
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                isNull(TopicIds.class), // testing that this is null
                any(BrandIds.class),
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesDoesPassTopicId() throws Exception {
        articleProvider.getArticles(1, ALL_BRANDS, 1, callback);
        verify(mockArticleService).getArticles(
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                isNotNull(TopicIds.class), // testing that this is not null
                any(BrandIds.class),
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesDoesNotPassBrandIdForAllBrands() throws Exception {
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, 1, callback);
        verify(mockArticleService).getArticles(
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                any(TopicIds.class),
                isNull(BrandIds.class), // testing that this is null
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesDoesPassBrandId() throws Exception {
        articleProvider.getArticles(ALL_TOPICS, 1, 1, callback);
        verify(mockArticleService).getArticles(
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                any(TopicIds.class),
                isNotNull(BrandIds.class), // testing that this is null
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesUsesCorrectLanguage() throws Exception {
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, 1, callback);
        verify(mockArticleService).getArticles(
                eq(Desk.getLanguage()), // testing that this is correct
                anyInt(),
                anyInt(),
                anyBoolean(),
                any(TopicIds.class),
                any(BrandIds.class),
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesUsesCorrectPage() throws Exception {
        final int page = 1;
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, page, callback);
        verify(mockArticleService).getArticles(
                anyString(),
                eq(page), // testing that this is correct
                anyInt(),
                anyBoolean(),
                any(TopicIds.class),
                any(BrandIds.class),
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesUsesCorrectAmountPerPage() throws Exception {
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, 1, callback);
        verify(mockArticleService).getArticles(
                anyString(),
                anyInt(),
                eq(PER_PAGE), // testing that this is correct
                anyBoolean(),
                any(TopicIds.class),
                any(BrandIds.class),
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesOnlyGetsArticlesInSupportCenter() throws Exception {
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, 1, callback);
        verify(mockArticleService).getArticles(
                anyString(),
                anyInt(),
                anyInt(),
                eq(true), // testing that this is true
                any(TopicIds.class),
                any(BrandIds.class),
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesSortsByPosition() throws Exception {
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, 1, callback);
        verify(mockArticleService).getArticles(
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                any(TopicIds.class),
                any(BrandIds.class),
                eq(ArticleService.FIELD_POSITION),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesOrdersAscending() throws Exception {
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, 1, callback);
        verify(mockArticleService).getArticles(
                anyString(),
                anyInt(),
                anyInt(),
                anyBoolean(),
                any(TopicIds.class),
                any(BrandIds.class),
                anyString(),
                eq(SortDirection.ASC), // testing that this is ascending
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesNotifiesCallbackOnSuccess() throws Exception {
        mockArticleService.setError(false);
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, 1, callback);
        verify(callback).onArticlesLoaded(anyInt(), anyListOf(Article.class), anyBoolean());
    }

    @Test
    public void getArticlesNotifiesCallbackOnError() throws Exception {
        mockArticleService.setError(true);
        articleProvider.getArticles(ALL_TOPICS, ALL_BRANDS, 1, callback);
        verify(callback).onArticlesLoadError(any(ErrorResponse.class));
    }

    // endregion

    // region getArticles() Tests

    @Test
    public void findArticlesDoesNotPassTopicIdForAllTopics() throws Exception {
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", 1, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                anyInt(),
                anyInt(),
                isNull(TopicIds.class), // testing that this is null
                any(BrandIds.class),
                anyBoolean(),
                anyString(),
                any(SortDirection.class),
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesDoesPassTopicId() throws Exception {
        articleProvider.findArticles(1, ALL_BRANDS, "query", 1, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                anyInt(),
                anyInt(),
                isNotNull(TopicIds.class), // testing that this is not null
                any(BrandIds.class),
                anyBoolean(),
                anyString(),
                any(SortDirection.class),
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesDoesNotPassBrandIdForAllBrands() throws Exception {
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", 1, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                anyInt(),
                anyInt(),
                any(TopicIds.class),
                isNull(BrandIds.class), // testing that this is null
                anyBoolean(),
                anyString(),
                any(SortDirection.class),
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesDoesPassBrandId() throws Exception {
        articleProvider.findArticles(ALL_TOPICS, 1, "query", 1, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                anyInt(),
                anyInt(),
                any(TopicIds.class),
                isNotNull(BrandIds.class), // testing that this is notnull
                anyBoolean(),
                anyString(),
                any(SortDirection.class),
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesUsesCorrectLanguage() throws Exception {
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", 1, callback);
        verify(mockArticleService).searchArticles(
                eq(Desk.getLanguage()), // testing that this is correct language
                anyInt(),
                anyInt(),
                any(TopicIds.class),
                any(BrandIds.class),
                anyBoolean(),
                anyString(),
                any(SortDirection.class),
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesUsesCorrectPage() throws Exception {
        final int page = 1;
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", page, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                eq(page), // testing that this is the correct page
                anyInt(),
                any(TopicIds.class),
                any(BrandIds.class),
                anyBoolean(),
                anyString(),
                any(SortDirection.class),
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesUsesCorrectAmountPerPage() throws Exception {
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", 1, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                anyInt(),
                eq(PER_PAGE), // testing that this is the correct value
                any(TopicIds.class),
                any(BrandIds.class),
                anyBoolean(),
                anyString(),
                any(SortDirection.class),
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesOnlyGetsArticlesInSupportCenter() throws Exception {
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", 1, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                anyInt(),
                anyInt(),
                any(TopicIds.class),
                any(BrandIds.class),
                eq(true), // testing that this is true
                anyString(),
                any(SortDirection.class),
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesSortsByPosition() throws Exception {
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", 1, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                anyInt(),
                anyInt(),
                any(TopicIds.class),
                any(BrandIds.class),
                anyBoolean(),
                eq(ArticleService.FIELD_POSITION),
                any(SortDirection.class),
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesOrdersAscending() throws Exception {
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", 1, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                anyInt(),
                anyInt(),
                any(TopicIds.class),
                any(BrandIds.class),
                anyBoolean(),
                anyString(),
                eq(SortDirection.ASC), // testing that this is ascending
                anyString(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesPassesCorrectQuery() throws Exception {
        final String query = "query";
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, query, 1, callback);
        verify(mockArticleService).searchArticles(
                anyString(),
                anyInt(),
                anyInt(),
                any(TopicIds.class),
                any(BrandIds.class),
                anyBoolean(),
                anyString(),
                any(SortDirection.class),
                eq(query), // testing that this is the correct value
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void findArticlesNotifiesCallbackOnSuccess() throws Exception {
        mockArticleService.setError(false);
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", 1, callback);
        verify(callback).onArticlesLoaded(anyInt(), anyListOf(Article.class), anyBoolean());
    }

    @Test
    public void findArticlesNotifiesCallbackOnError() throws Exception {
        mockArticleService.setError(true);
        articleProvider.findArticles(ALL_TOPICS, ALL_BRANDS, "query", 1, callback);
        verify(callback).onArticlesLoadError(any(ErrorResponse.class));
    }

    // endregion

    // region ArticleCallback tests

    @Test
    public void retrofitCallbackPassesCorrectPage() throws Exception {
        ApiResponse<Article> response = getMockApiResponse("/mock_article_response_page_2.json");
        RetrofitCallback articleCallback = new RetrofitCallback(callback);
        articleCallback.success(response, null);
        verify(callback).onArticlesLoaded(
                eq(response.getPage()),
                anyListOf(Article.class),
                anyBoolean()
        );
    }

    @Test
    public void retrofitCallbackPassesHasNextPage() throws Exception {
        ApiResponse<Article> response = getMockApiResponse("/mock_article_response_with_next.json");
        RetrofitCallback articleCallback = new RetrofitCallback(callback);
        articleCallback.success(response, null);
        verify(callback).onArticlesLoaded(
                anyInt(),
                anyListOf(Article.class),
                eq(true)
        );
    }

    // endregion

    /**
     * Mock ArticleService for unit testing purposes
     */
    private class MockArticleService implements ArticleService {

        private boolean error = false;

        public void setError(boolean error) {
            this.error = error;
        }

        @Override
        public void getArticles(String s, int i, int i1, Boolean aBoolean, Callback<ApiResponse<Article>> callback) {

        }

        @Override
        public ApiResponse<Article> getArticles(String s, int i, int i1, Boolean aBoolean) {
            return null;
        }

        @Override
        public void getArticles(String s, int i, int i1, Boolean aBoolean, TopicIds topicIds, BrandIds brandIds, String s1, SortDirection sortDirection, Callback<ApiResponse<Article>> callback) {
            if (error) {
                error(callback);
            } else {
                success(callback);
            }
        }

        @Override
        public ApiResponse<Article> getArticles(String s, int i, int i1, Boolean aBoolean, TopicIds topicIds, BrandIds brandIds, String s1, SortDirection sortDirection) {
            return null;
        }

        @Override
        public void searchArticles(String s, int i, int i1, TopicIds topicIds, BrandIds brandIds, Boolean aBoolean, String s1, SortDirection sortDirection, String s2, Callback<ApiResponse<Article>> callback) {
            if (error) {
                error(callback);
            } else {
                success(callback);
            }
        }

        @Override
        public ApiResponse<Article> searchArticles(String s, int i, int i1, TopicIds topicIds, BrandIds brandIds, Boolean aBoolean, String s1, SortDirection sortDirection, String s2) {
            return null;
        }

        private void success(Callback<ApiResponse<Article>> callback) {
            callback.success(getMockApiResponse("/mock_article_response.json"), null);
        }

        private void error(Callback<ApiResponse<Article>> callback) {
            callback.failure(RetrofitError.unexpectedError("", new RuntimeException()));
        }
    }

    private ApiResponse<Article> getMockApiResponse(String jsonFile) {
        return TestUtils.readMockJsonFile(
                new TypeToken<ApiResponse<Article>>() {}.getType(),
                jsonFile
        );
    }
}