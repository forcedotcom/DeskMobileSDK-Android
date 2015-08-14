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

import com.desk.android.sdk.error.ErrorResponse;
import com.desk.android.sdk.util.TestUtils;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.InboundMailbox;
import com.desk.java.apiclient.service.InboundMailboxService;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;

import retrofit.Callback;
import retrofit.RetrofitError;

import static com.desk.android.sdk.provider.InboundMailboxProvider.InboundMailboxCallbacks;
import static com.desk.android.sdk.provider.InboundMailboxProvider.PER_PAGE;
import static com.desk.android.sdk.provider.InboundMailboxProvider.RetrofitCallback;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link InboundMailboxProvider}
 */
public class InboundMailboxProviderTest {

    private InboundMailboxCallbacks callbacks;
    private MockInboundMailboxService mockInboundMailboxService;
    private InboundMailboxProvider inboundMailboxProvider;

    @Before
    public void setUp() throws Exception {
        mockInboundMailboxService = spy(new MockInboundMailboxService());
        inboundMailboxProvider = new InboundMailboxProvider(mockInboundMailboxService);
        callbacks = mock(InboundMailboxCallbacks.class);
    }

    @Test
    public void getMailboxesUsesCorrectPage() throws Exception {
        final int page = 2;
        inboundMailboxProvider.getMailboxes(page, callbacks);
        verify(mockInboundMailboxService).getInboundMailboxes(
                anyInt(),
                eq(page), // testing that this is the correct page
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getMailboxesUsesCorrectAmountPerPage() throws Exception {
        inboundMailboxProvider.getMailboxes(1, callbacks);
        verify(mockInboundMailboxService).getInboundMailboxes(
                eq(PER_PAGE), // testing that this is the correct amount
                anyInt(),
                any(RetrofitCallback.class)
        );
    }

    @Test
    public void getMailboxesNotifiesCallbacksOnSuccess() throws Exception {
        mockInboundMailboxService.setError(false);
        inboundMailboxProvider.getMailboxes(1, callbacks);
        verify(callbacks).onInboundMailboxesLoaded(anyInt(), anyListOf(InboundMailbox.class));
    }

    @Test
    public void getMailboxesNotifiesCallbacksOnError() throws Exception {
        mockInboundMailboxService.setError(true);
        inboundMailboxProvider.getMailboxes(1, callbacks);
        verify(callbacks).onInboundMailboxLoadError(any(ErrorResponse.class));
    }

    private class MockInboundMailboxService implements InboundMailboxService {

        private boolean error;

        public void setError(boolean error) {
            this.error = error;
        }

        @Override
        public void getInboundMailboxes(int i, int i1, Callback<ApiResponse<InboundMailbox>> callback) {
            if (error) {
                callback.failure(RetrofitError.unexpectedError("", new RuntimeException()));
            } else {
                ApiResponse<InboundMailbox> response = TestUtils.readMockJsonFile(
                        new TypeToken<ApiResponse<InboundMailbox>>(){}.getType(),
                        "/mock_inbound_mailbox_response.json"
                );
                callback.success(response, null);
            }
        }

        @Override
        public ApiResponse<InboundMailbox> getInboundMailboxes(int i, int i1) {
            return null;
        }
    }
}