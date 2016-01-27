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
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.SortDirection;
import com.desk.java.apiclient.model.Topic;
import com.desk.java.apiclient.service.TopicService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.desk.android.sdk.provider.TopicProvider.ALL_BRANDS;
import static com.desk.android.sdk.provider.TopicProvider.TopicCallbacks;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link TopicProvider}
 */
@SuppressWarnings("unchecked")
public class TopicProviderTest {

    @Mock TopicService mockTopicService;

    private TopicCallbacks callbacks;
    private TopicProvider topicProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        topicProvider = new TopicProvider(mockTopicService);
        callbacks = mock(TopicCallbacks.class);

        when(mockTopicService.getTopics(
                anyString(),
                anyBoolean(),
                anyInt(), // testing that the brand id is null
                anyString(),
                any(SortDirection.class))).thenReturn(mock(Call.class));
    }

    @Test
    public void getTopicsDoesNotPassBrandId() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                anyString(),
                anyBoolean(),
                isNull(Integer.class), // testing that the brand id is null
                anyString(),
                any(SortDirection.class));
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
                any(SortDirection.class));
    }

    @Test
    public void getTopicsUsesCorrectLanguage() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                eq(Desk.getLanguage()), // testing that this is correct
                anyBoolean(),
                anyInt(),
                anyString(),
                any(SortDirection.class));
    }

    @Test
    public void getTopicsOnlyGetsTopicsInSupportCenter() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                anyString(),
                eq(true), // testing that this is true
                anyInt(),
                anyString(),
                any(SortDirection.class));
    }

    @Test
    public void getTopicsSortsByPosition() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                anyString(),
                anyBoolean(),
                anyInt(),
                eq(TopicService.FIELD_POSITION), // testing that this is the position field
                any(SortDirection.class));
    }

    @Test
    public void getTopicsSortsAscending() throws Exception {
        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(mockTopicService).getTopics(
                anyString(),
                anyBoolean(),
                anyInt(),
                anyString(),
                eq(SortDirection.ASC)); // testing that this is ascending
    }

    @Test
    public void getArticlesNotifiesCallbackOnSuccess() throws Exception {
        Call mockCall = mock(Call.class);

        when(mockTopicService.getTopics(
                anyString(),
                anyBoolean(),
                anyInt(),
                anyString(),
                any(SortDirection.class))).thenReturn(mockCall);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((TopicProvider.RetrofitCallback) invocation.getArguments()[0]).onResponse(Response.success(new ApiResponse<Topic>()));
                return null;
            }
        }).when(mockCall).enqueue(any(Callback.class));

        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(callbacks).onTopicsLoaded(anyListOf(Topic.class));
    }

    @Test
    public void getArticlesNotifiesCallbackOnError() throws Exception {
        Call mockCall = mock(Call.class);

        when(mockTopicService.getTopics(
                anyString(),
                anyBoolean(),
                anyInt(),
                anyString(),
                any(SortDirection.class))).thenReturn(mockCall);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((TopicProvider.RetrofitCallback) invocation.getArguments()[0]).onFailure(new RuntimeException());
                return null;
            }
        }).when(mockCall).enqueue(any(Callback.class));

        topicProvider.getTopics(ALL_BRANDS, callbacks);
        verify(callbacks).onTopicsLoadError(any(ErrorResponse.class));
    }
}