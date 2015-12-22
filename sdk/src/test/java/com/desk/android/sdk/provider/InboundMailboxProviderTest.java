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
import com.desk.android.sdk.provider.InboundMailboxProvider.RetrofitCallback;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.InboundMailbox;
import com.desk.java.apiclient.service.InboundMailboxService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.desk.android.sdk.provider.InboundMailboxProvider.InboundMailboxCallbacks;
import static com.desk.android.sdk.provider.InboundMailboxProvider.PER_PAGE;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link InboundMailboxProvider}
 */
public class InboundMailboxProviderTest {

    @Mock InboundMailboxService mockInboundMailboxService;

    private InboundMailboxCallbacks callbacks;
    private InboundMailboxProvider inboundMailboxProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        inboundMailboxProvider = new InboundMailboxProvider(mockInboundMailboxService);
        callbacks = mock(InboundMailboxCallbacks.class);
    }

    @Test
    public void getMailboxesUsesCorrectPage() throws Exception {
        final int page = 2;

        when(mockInboundMailboxService.getInboundMailboxes(
                anyInt(),
                eq(page))).thenReturn(mock(Call.class));

        inboundMailboxProvider.getMailboxes(page, callbacks);
        verify(mockInboundMailboxService).getInboundMailboxes(
                anyInt(),
                eq(page)); // testing that this is the correct page
    }

    @Test
    public void getMailboxesUsesCorrectAmountPerPage() throws Exception {
        when(mockInboundMailboxService.getInboundMailboxes(
                anyInt(),
                anyInt())).thenReturn(mock(Call.class));

        inboundMailboxProvider.getMailboxes(1, callbacks);
        verify(mockInboundMailboxService).getInboundMailboxes(
                eq(PER_PAGE), // testing that this is the correct amount
                anyInt());
    }

    @Test
    public void getMailboxesNotifiesCallbacksOnSuccess() throws Exception {
        Call mockCall = mock(Call.class);

        when(mockInboundMailboxService.getInboundMailboxes(
                anyInt(),
                anyInt())).thenReturn(mockCall);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((RetrofitCallback) invocation.getArguments()[0]).onResponse(Response.success(new ApiResponse<InboundMailbox>()), null);
                return null;
            }
        }).when(mockCall).enqueue(any(Callback.class));

        inboundMailboxProvider.getMailboxes(1, callbacks);
        verify(callbacks).onInboundMailboxesLoaded(anyInt(), anyListOf(InboundMailbox.class));
    }

    @Test
    public void getMailboxesNotifiesCallbacksOnError() throws Exception {
        Call mockCall = mock(Call.class);

        when(mockInboundMailboxService.getInboundMailboxes(
                anyInt(),
                anyInt())).thenReturn(mockCall);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((RetrofitCallback) invocation.getArguments()[0]).onFailure(new RuntimeException());
                return null;
            }
        }).when(mockCall).enqueue(any(Callback.class));

        inboundMailboxProvider.getMailboxes(1, callbacks);
        verify(callbacks).onInboundMailboxLoadError(any(ErrorResponse.class));
    }
}