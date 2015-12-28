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
import com.desk.android.sdk.model.CreateCaseRequest;
import com.desk.java.apiclient.model.Case;
import com.desk.java.apiclient.model.CaseType;
import com.desk.java.apiclient.model.Embed;
import com.desk.java.apiclient.model.Fields;
import com.desk.java.apiclient.service.CaseService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CaseProvider}
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseProviderTest {

    @Mock CaseService mockCaseService;

    private CaseProvider provider;
    private CreateCaseRequest request;
    private CaseProvider.CreateCaseCallback callback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        provider = new CaseProvider(mockCaseService);
        request = new CreateCaseRequest.Builder(CaseType.EMAIL, "body", "to", "from").create();
        callback = new CaseProvider.CreateCaseCallback() {
            @Override
            public void onCaseCreated(Case deskCase) {}

            @Override
            public void onCreateCaseError(ErrorResponse error) {}
        };
    }

    @Test
    public void createCaseCallsOnCaseCreatedOnSuccess() throws Exception {
        Call mockCall = mock(Call.class);

        when(mockCaseService.createCase(
                any(Case.class),
                any(Embed.class),
                any(Fields.class))).thenReturn(mockCall);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Callback<Case>) invocation.getArguments()[0]).onResponse(Response.success(new Case()));
                return null;
            }
        }).when(mockCall).enqueue(any(Callback.class));

        CreateCaseRequest requestSpy = spy(request);
        CaseProvider.CreateCaseCallback callbackSpy = spy(callback);
        provider.createCase(requestSpy, callbackSpy);
        verify(callbackSpy).onCaseCreated(Mockito.<Case>any());
    }

    @Test
    public void createCaseCallsOnCreateCaseErrorOnError() throws Exception {
        Call mockCall = mock(Call.class);

        when(mockCaseService.createCase(
                any(Case.class),
                any(Embed.class),
                any(Fields.class))).thenReturn(mockCall);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Callback<Case>) invocation.getArguments()[0]).onFailure(new RuntimeException());
                return null;
            }
        }).when(mockCall).enqueue(any(Callback.class));

        CreateCaseRequest requestSpy = spy(request);
        CaseProvider.CreateCaseCallback callbackSpy = spy(callback);
        provider.createCase(requestSpy, callbackSpy);
        verify(callbackSpy).onCreateCaseError(Mockito.<ErrorResponse>any());
    }
}