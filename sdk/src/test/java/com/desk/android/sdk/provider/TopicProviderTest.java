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

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.error.ErrorResponse;
import com.desk.android.sdk.util.TestUtils;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.Article;
import com.desk.java.apiclient.model.SortDirection;
import com.desk.java.apiclient.model.Topic;
import com.desk.java.apiclient.service.TopicService;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;

import retrofit.Callback;
import retrofit.RetrofitError;

import static com.desk.android.sdk.provider.TopicProvider.ALL_BRANDS;
import static com.desk.android.sdk.provider.TopicProvider.RetrofitCallback;
import static com.desk.android.sdk.provider.TopicProvider.TopicCallbacks;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link TopicProvider}
 */
@SuppressWarnings("unchecked")
public class TopicProviderTest {

    private TopicCallbacks callbacks;
    private MockTopicService mockTopicService;
    private TopicProvider topicProvider;

    @Before
    public void setUp() throws Exception {
        mockTopicService = spy(new MockTopicService());
        topicProvider = new TopicProvider(mockTopicService);
        callbacks = mock(TopicCallbacks.class);
    }

    @Test
    public void getTopicsDoesNotPassBrandId() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                anyString(),
                anyBoolean(),
                isNull(Integer.class), // testing that the brand id is null
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getTopicsDoesPassBrandId() throws Exception {
        final int brandId = 1;
        topicProvider.getTopics(brandId, callbacks);
        verify(mockTopicService).getTopics(
                anyString(),
                anyBoolean(),
                eq(brandId), // testing that this brand id is the same
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getTopicsUsesCorrectLanguage() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                eq(Desk.getLanguage()), // testing that this is correct
                anyBoolean(),
                anyInt(),
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getTopicsOnlyGetsTopicsInSupportCenter() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                anyString(),
                eq(true), // testing that this is true
                anyInt(),
                anyString(),
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getTopicsSortsByPosition() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                anyString(),
                anyBoolean(),
                anyInt(),
                eq(TopicService.FIELD_POSITION), // testing that this is the position field
                any(SortDirection.class),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getTopicsSortsAscending() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                anyString(),
                anyBoolean(),
                anyInt(),
                anyString(),
                eq(SortDirection.ASC), // testing that this is ascending
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getArticlesNotifiesCallbackOnSuccess() throws Exception {
        mockTopicService.setError(false);
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(callbacks).onTopicsLoaded(anyListOf(Topic.class));
    }

    @Test
    public void getArticlesNotifiesCallbackOnError() throws Exception {
        mockTopicService.setError(true);
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(callbacks).onTopicsLoadError(any(ErrorResponse.class));
    }

    private class MockTopicService implements TopicService {

        private boolean error = false;

        public void setError(boolean error) {
            this.error = error;
        }

        @Override
        public void getTopics(String s, Boolean aBoolean, Integer integer, String s1, SortDirection sortDirection, Callback<ApiResponse<Topic>> callback) {
            if (error) {
                callback.failure(RetrofitError.unexpectedError("", new RuntimeException()));
            } else {
                ApiResponse<Topic> response = TestUtils.readMockJsonFile(new TypeToken<ApiResponse<Topic>>() {}.getType(), "/mock_topic_response.json");
                callback.success(response, null);
            }
        }

        @Override
        public ApiResponse<Topic> getTopics(String s, Boolean aBoolean, Integer integer, String s1, SortDirection sortDirection) {
            return null;
        }

        @Override
        public void getArticlesOfTopic(String s, int i, Boolean aBoolean, Callback<ApiResponse<Article>> callback) {

        }

        @Override
        public ApiResponse<Article> getArticlesOfTopic(String s, int i, Boolean aBoolean) {
            return null;
        }
    }
}