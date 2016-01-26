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

import android.support.annotation.VisibleForTesting;

import com.desk.android.sdk.error.ErrorResponse;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.InboundMailbox;
import com.desk.java.apiclient.service.InboundMailboxService;

import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

/**
 * <p>Wraps an {@link InboundMailboxService} to provide a higher level of abstraction.</p>
 *
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class InboundMailboxProvider {

    /**
     * Callbacks for loading inbound mailboxes
     */
    public interface InboundMailboxCallbacks {

        /**
         * Called when inbound mailboxes have loaded successfully
         * @param page the page loaded
         * @param mailboxes the mailboxes
         */
        void onInboundMailboxesLoaded(int page, List<InboundMailbox> mailboxes);

        /**
         * Called when there is an error loading inbound mailboxes
         * @param error the error response
         */
        void onInboundMailboxLoadError(ErrorResponse error);
    }

    @VisibleForTesting
    static final int PER_PAGE = 1;

    private InboundMailboxService mInboundMailboxService;

    public InboundMailboxProvider(InboundMailboxService inboundMailboxService) {
        mInboundMailboxService = inboundMailboxService;
    }

    /**
     * Retrieves {@link InboundMailbox}es for the given page.
     *
     * @param page the current page
     * @param cb the callback upon success or failure
     */
    public void getMailboxes(int page, InboundMailboxCallbacks cb) {
        mInboundMailboxService.getInboundMailboxes(
                PER_PAGE,
                page)
                .enqueue(new RetrofitCallback(cb));
    }

    static class RetrofitCallback implements Callback<ApiResponse<InboundMailbox>> {

        InboundMailboxCallbacks callbacks;

        public RetrofitCallback(InboundMailboxCallbacks callbacks) {
            this.callbacks = callbacks;
        }

        @Override public void onResponse(Response<ApiResponse<InboundMailbox>> response) {
            ApiResponse<InboundMailbox> apiResponse = response.body();
            callbacks.onInboundMailboxesLoaded(apiResponse.getPage(), apiResponse.getEntriesAsList());
        }

        @Override public void onFailure(Throwable throwable) {
            callbacks.onInboundMailboxLoadError(new ErrorResponse(throwable));
        }
    }
}
