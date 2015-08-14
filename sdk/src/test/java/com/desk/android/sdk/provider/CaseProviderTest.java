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
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.Attachment;
import com.desk.java.apiclient.model.Case;
import com.desk.java.apiclient.model.CaseLock;
import com.desk.java.apiclient.model.CaseType;
import com.desk.java.apiclient.model.Embed;
import com.desk.java.apiclient.model.Fields;
import com.desk.java.apiclient.model.MacroResponse;
import com.desk.java.apiclient.model.Message;
import com.desk.java.apiclient.model.SortDirection;
import com.desk.java.apiclient.service.CaseService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import retrofit.Callback;
import retrofit.RetrofitError;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link CaseProvider}
 */
@RunWith(MockitoJUnitRunner.class)
public class CaseProviderTest {

    private MockCaseService mockCaseService;
    private CaseProvider provider;

    private CreateCaseRequest request;
    private CaseProvider.CreateCaseCallback callback;

    @Before
    public void setUp() throws Exception {
        mockCaseService = new MockCaseService();
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
        mockCaseService.setError(false);
        CreateCaseRequest requestSpy = spy(request);
        CaseProvider.CreateCaseCallback callbackSpy = spy(callback);
        provider.createCase(requestSpy, callbackSpy);
        verify(callbackSpy).onCaseCreated(Mockito.<Case>any());
    }

    @Test
    public void createCaseCallsOnCreateCaseErrorOnError() throws Exception {
        mockCaseService.setError(true);
        CreateCaseRequest requestSpy = spy(request);
        CaseProvider.CreateCaseCallback callbackSpy = spy(callback);
        provider.createCase(requestSpy, callbackSpy);
        verify(callbackSpy).onCreateCaseError(Mockito.<ErrorResponse>any());
    }

    /**
     * Mock CaseService for unit testing purposes
     */
    private class MockCaseService implements CaseService {

        private boolean error = false;

        public void setError(boolean error) {
            this.error = error;
        }

        @Override
        public void getCasesByFilter(int i, int i1, int i2, String s, SortDirection sortDirection, Embed embed, Fields fields, Callback<ApiResponse<Case>> callback) {

        }

        @Override
        public ApiResponse<Case> getCasesByFilter(int i, int i1, int i2, String s, SortDirection sortDirection, Embed embed, Fields fields) {
            return null;
        }

        @Override
        public void searchCases(String s, int i, int i1, String s1, SortDirection sortDirection, Embed embed, Fields fields, Callback<ApiResponse<Case>> callback) {

        }

        @Override
        public ApiResponse<Case> searchCases(String s, int i, int i1, String s1, SortDirection sortDirection, Embed embed, Fields fields) {
            return null;
        }

        @Override
        public void getCaseById(int i, Embed embed, Fields fields, Callback<Case> callback) {

        }

        @Override
        public Case getCaseById(int i, Embed embed, Fields fields) {
            return null;
        }

        @Override
        public void updateCaseLock(int i, CaseLock caseLock, Callback<Case> callback) {

        }

        @Override
        public Case updateCaseLock(int i, CaseLock caseLock) {
            return null;
        }

        @Override
        public void updateCase(int i, Case aCase, Callback<Case> callback) {

        }

        @Override
        public Case updateCase(int i, Case aCase) {
            return null;
        }

        @Override
        public void updateCase(int i, Case aCase, Embed embed, Fields fields, Callback<Case> callback) {

        }

        @Override
        public Case updateCase(int i, Case aCase, Embed embed, Fields fields) {
            return null;
        }

        @Override
        public void createCase(Case aCase, Embed embed, Fields fields, Callback<Case> callback) {
            if (error) {
                callback.failure(RetrofitError.unexpectedError("", new RuntimeException()));
            } else {
                callback.success(new Case(), null);
            }
        }

        @Override
        public Case createCase(Case aCase, Embed embed, Fields fields) {
            return null;
        }

        @Override
        public void updateCaseMessage(int i, Message message, Callback<Message> callback) {

        }

        @Override
        public Message updateCaseMessage(int i, Message message) {
            return null;
        }

        @Override
        public void updateCaseReply(int i, int i1, Message message, Callback<Message> callback) {

        }

        @Override
        public Message updateCaseReply(int i, int i1, Message message) {
            return null;
        }

        @Override
        public void getCaseFeed(int i, int i1, int i2, SortDirection sortDirection, Callback<ApiResponse<Message>> callback) {

        }

        @Override
        public ApiResponse<Message> getCaseFeed(int i, int i1, int i2, SortDirection sortDirection) {
            return null;
        }

        @Override
        public void getDraft(int i, Embed embed, Callback<Message> callback) {

        }

        @Override
        public Message getDraft(int i, Embed embed) {
            return null;
        }

        @Override
        public void createDraft(int i, Callback<Message> callback) {

        }

        @Override
        public Message createDraft(int i) {
            return null;
        }

        @Override
        public void updateDraft(int i, Message message, Callback<Message> callback) {

        }

        @Override
        public Message updateDraft(int i, Message message) {
            return null;
        }

        @Override
        public void createNote(int i, Message message, Callback<Message> callback) {

        }

        @Override
        public Message createNote(int i, Message message) {
            return null;
        }

        @Override
        public void previewMacro(int i, Case aCase, Callback<MacroResponse> callback) {

        }

        @Override
        public MacroResponse previewMacro(int i, Case aCase) {
            return null;
        }

        @Override
        public void getAttachments(int i, int i1, int i2, Callback<ApiResponse<Attachment>> callback) {

        }

        @Override
        public ApiResponse<Attachment> getAttachments(int i, int i1, int i2) {
            return null;
        }
    }
}