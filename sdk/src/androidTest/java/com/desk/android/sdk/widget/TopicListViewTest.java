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
import com.desk.android.sdk.provider.TopicProvider;
import com.desk.android.sdk.test.R;
import com.desk.android.sdk.util.DeskDefaultsRule;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.Topic;
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

import static com.desk.android.sdk.util.InstrumentationTestUtils.getMockedTopicResponse;
import static com.desk.android.sdk.util.TestUtils.getString;
import static com.desk.android.sdk.util.TestUtils.inflateView;
import static com.desk.android.sdk.util.TestUtils.readMockJsonFile;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.assertj.android.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for a {@link TopicListView}
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class TopicListViewTest {

    @Mock
    static TopicProvider mockTopicProvider = mock(TopicProvider.class);

    @ClassRule
    public static DeskDefaultsRule resetRule = new DeskDefaultsRule();

    private TopicListView.TopicSelectedListener listener;
    private List<Topic> mockTopics;
    private TopicListView topicListView;

    @BeforeClass
    public static void preSetup() {
        Desk.with(InstrumentationRegistry.getContext()).setTopicProvider(mockTopicProvider);
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
                        ApiResponse<Topic> mockedTopicResponse = getMockedTopicResponse();
                        ((TopicProvider.TopicCallbacks) invocation.getArguments()[1]).onTopicsLoaded(mockedTopicResponse.getEntriesAsList());
                    }
                }).start();
                return null;
            }
        }).when(mockTopicProvider).getTopics(anyInt(), any(TopicProvider.TopicCallbacks.class));
    }

    @Before
    public void setUp() throws Exception {
        listener = mock(TopicListView.TopicSelectedListener.class);
        mockTopics = getMockTopics();
        topicListView = new TopicListView(InstrumentationRegistry.getTargetContext());
        topicListView.setTopicSelectedListener(listener);
    }

    @Test
    public void emptyTextMatchesDefault() throws Exception {
        assertEquals(getString(R.string.def_topics_empty_text), topicListView.getEmptyText());
    }

    @Test
    public void errorTextMatchesDefault() throws Exception {
        assertEquals(getString(R.string.def_topics_error_text), topicListView.getErrorText());
    }

    @Test
    public void emptyTextMatchesValueFromLayoutAttrs() throws Exception {
        TopicListView topicListView = inflateView(R.layout.topic_list_view_with_attributes);
        assertEquals(topicListView.getEmptyText(), getString(R.string.test_attr_empty_text));
    }

    @Test
    public void errorTextMatchesValueFromLayoutAttrs() throws Exception {
        TopicListView topicListView = inflateView(R.layout.topic_list_view_with_attributes);
        assertEquals(topicListView.getErrorText(), getString(R.string.test_attr_error_text));
    }

    @Test
    public void emptyTextMatchesValueFromStyle() throws Exception {
        TopicListView topicListView = inflateView(R.layout.topic_list_view_with_style);
        assertEquals(topicListView.getEmptyText(), getString(R.string.test_style_empty_text));
    }

    @Test
    public void errorTextMatchesValueFromStyle() throws Exception {
        TopicListView topicListView = inflateView(R.layout.topic_list_view_with_style);
        assertEquals(topicListView.getErrorText(), getString(R.string.test_style_error_text));
    }

    @Test
    public void loadTopicsHidesList() throws Exception {
        ListView list = getList();
        assertThat(list).isVisible();
        topicListView.loadTopics();
        assertThat(list).isGone();
    }

    @Test
    public void loadTopicsShowsProgress() throws Exception {
        ProgressBar progress = getProgress();
        assertThat(progress).isGone();
        topicListView.loadTopics();
        assertThat(progress).isVisible();
    }

    @Test
    public void onTopicsLoadedHidesProgress() throws Exception {
        ProgressBar progress = getProgress();
        topicListView.onLoaded(Collections.<Topic>emptyList());
        assertThat(progress).isGone();
    }

    @Test
    public void onTopicsLoadedShowsList() throws Exception {
        ListView list = getList();
        topicListView.onLoaded(Collections.singletonList(new Topic()));
        assertThat(list).isVisible();
    }

    @Test
    public void onTopicsLoadedShowsEmptyWithEmptyText() throws Exception {
        TextView empty = getEmpty();
        topicListView.onLoaded(Collections.<Topic>emptyList());
        assertThat(empty).isVisible();
        assertThat(empty).hasText(topicListView.getEmptyText());
    }

    @Test
    public void onTopicsLoadErrorHidesProgress() throws Exception {
        ProgressBar progress = getProgress();
        topicListView.onLoadError();
        assertThat(progress).isGone();
    }

    @Test
    public void onTopicsLoadErrorShowsEmptyWithErrorText() throws Exception {
        TextView empty = getEmpty();
        topicListView.onLoadError();
        assertThat(empty).isVisible();
        assertThat(empty).hasText(topicListView.getErrorText());
    }

    @Test
    public void onSaveInstanceStateSavesTopics() throws Exception {
        topicListView.onLoaded(mockTopics);
        TopicListView.SavedState savedState = (TopicListView.SavedState) topicListView.onSaveInstanceState();
        assertEquals(mockTopics, savedState.topics);
        assertFalse(savedState.haveError);
    }

    @Test
    public void onSaveInstanceStateSavesError() throws Exception {
        topicListView.onLoadError();
        TopicListView.SavedState savedState = (TopicListView.SavedState) topicListView.onSaveInstanceState();
        assertTrue(savedState.haveError);
    }

    @Test
    public void onItemClickCallsListenerWithCorrectTopic() throws Exception {
        topicListView.onLoaded(mockTopics);
        ListView list = getList();
        for (int i = 0; i < mockTopics.size(); i++) {
            topicListView.onItemClick(list, list.getChildAt(i), i, 0);
            verify(listener).onTopicSelected(mockTopics.get(i));
        }
    }

    @Test
    public void listenerClearedInOnDetachedFromWindow() throws Exception {
        assertNotNull(topicListView.getTopicSelectedListener());
        topicListView.onDetachedFromWindow();
        assertNull(topicListView.getTopicSelectedListener());
    }

    private ListView getList() {
        return (ListView) topicListView.findViewById(android.R.id.list);
    }

    private ProgressBar getProgress() {
        return (ProgressBar) topicListView.findViewById(android.R.id.progress);
    }

    private TextView getEmpty() {
        return (TextView) topicListView.findViewById(android.R.id.empty);
    }

    private List<Topic> getMockTopics() {
        ApiResponse<Topic> response = readMockJsonFile(
                new TypeToken<ApiResponse<Topic>>() {}.getType(),
                "mock/mock_topic_response.json"
        );
        assertNotNull(response);
        return response.getEntriesAsList();
    }

}