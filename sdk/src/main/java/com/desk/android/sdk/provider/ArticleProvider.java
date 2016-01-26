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

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.error.ErrorResponse;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.Article;
import com.desk.java.apiclient.model.BrandIds;
import com.desk.java.apiclient.model.TopicIds;
import com.desk.java.apiclient.service.ArticleService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

import static com.desk.java.apiclient.model.SortDirection.ASC;
import static com.desk.java.apiclient.service.ArticleService.FIELD_POSITION;

/**
 * <p>Wraps a {@link ArticleService} to provide a higher level of abstraction.</p>
 *
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class ArticleProvider {

    /**
     * Callbacks for loading articles
     */
    public interface ArticleCallbacks {

        /**
         * Called when articles have loaded successfully
         * @param page the page loaded
         * @param articles the articles
         * @param morePages true if there are additional pages, false if this is the last page
         */
        void onArticlesLoaded(int page, List<Article> articles, boolean morePages);

        /**
         * Called when there is an error loading articles
         * @param error the error response
         */
        void onArticlesLoadError(ErrorResponse error);
    }

    public static final int ALL_TOPICS = 0;
    public static final int ALL_BRANDS = 0;

    @VisibleForTesting
    static final int PER_PAGE = 25;

    private ArticleService mArticleService;

    public ArticleProvider(ArticleService articleService) {
        mArticleService = articleService;
    }

    /**
     * Retrieves {@link Article}s for the given topic and brand.
     *
     * @param topicId the topic Id
     * @param brandId the brand Id
     * @param page the current page
     * @param callback the callback upon success or failure
     */
    public void getArticles(int topicId, int brandId, int page, @NonNull final ArticleCallbacks callback) {
        TopicIds topicIds = ALL_TOPICS != topicId ? TopicIds.ids(topicId) : null;
        BrandIds brandIds = ALL_BRANDS != brandId ? BrandIds.ids(brandId) : null;

        mArticleService.getArticles(
                Desk.getLanguage(),
                page,
                PER_PAGE,
                true,
                topicIds,
                brandIds,
                FIELD_POSITION,
                ASC)
                .enqueue(new RetrofitCallback(callback));
    }

    /**
     * Finds {@link Article}s based on the query, topic and brand.
     *
     * @param topicId the topic Id
     * @param brandId the brand Id
     * @param query the search query
     * @param page the current page
     * @param callback the callback upon success or failure
     */
    public void findArticles(int topicId, int brandId, String query, int page, @NonNull final ArticleCallbacks callback) {
        TopicIds topicIds = ALL_TOPICS != topicId ? TopicIds.ids(topicId) : null;
        BrandIds brandIds = ALL_BRANDS != brandId ? BrandIds.ids(brandId) : null;

        mArticleService.searchArticles(
                Desk.getLanguage(),
                page,
                PER_PAGE,
                topicIds,
                brandIds,
                true,
                FIELD_POSITION,
                ASC,
                query)
                .enqueue(new RetrofitCallback(callback));
    }

    static class RetrofitCallback implements Callback<ApiResponse<Article>> {

        ArticleCallbacks callbacks;

        public RetrofitCallback(ArticleCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        @Override public void onResponse(Response<ApiResponse<Article>> response) {
            ApiResponse<Article> apiResponse = response.body();
            if (apiResponse == null) {
                callbacks.onArticlesLoaded(0, new ArrayList<Article>(), false);
                return;
            }
            callbacks.onArticlesLoaded(apiResponse.getPage(), apiResponse.getEntriesAsList(), apiResponse.hasNextPage());
        }

        @Override public void onFailure(Throwable throwable) {
            callbacks.onArticlesLoadError(new ErrorResponse(throwable));
        }
    }
}
