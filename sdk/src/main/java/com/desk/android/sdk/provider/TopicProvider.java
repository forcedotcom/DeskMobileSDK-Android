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
import com.desk.java.apiclient.model.Topic;
import com.desk.java.apiclient.service.TopicService;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.desk.java.apiclient.model.SortDirection.ASC;
import static com.desk.java.apiclient.service.TopicService.FIELD_POSITION;

/**
 * <p>Wraps a {@link TopicService} to provide a higher level of abstraction.</p>
 *
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class TopicProvider {

    /**
     * Callbacks for loading topics
     */
    public interface TopicCallbacks {

        /**
         * Called when topics have loaded successfully
         * @param topics the topics
         */
        void onTopicsLoaded(List<Topic> topics);

        /**
         * Called when there is an error loading topics
         * @param error the error response
         */
        void onTopicsLoadError(ErrorResponse error);
    }

    public static final int ALL_BRANDS = 0;

    private TopicService mTopicService;

    public TopicProvider(TopicService topicService) {
        mTopicService = topicService;
    }

    /**
     * Retrieves the {@link Topic}s based on the {@code brandId} provided.
     *
     * @param brandId the brand Id
     * @param cb the callback upon success or failure
     */
    public void getTopics(int brandId, TopicCallbacks cb) {
        mTopicService.getTopics(
                Desk.getLanguage(),
                true,
                brandId == ALL_BRANDS ? null : brandId,
                FIELD_POSITION,
                ASC,
                new RetrofitCallback(cb)
        );
    }

    static class RetrofitCallback implements Callback<ApiResponse<Topic>> {

        TopicCallbacks callbacks;

        public RetrofitCallback(TopicCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        @Override
        public void success(ApiResponse<Topic> topicApiResponse, Response response) {
            callbacks.onTopicsLoaded(topicApiResponse.getEntriesAsList());
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            callbacks.onTopicsLoadError(new ErrorResponse(retrofitError));
        }
    }
}
