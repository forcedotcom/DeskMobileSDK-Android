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

import com.desk.android.sdk.error.ErrorResponse;
import com.desk.android.sdk.model.CreateCaseRequest;
import com.desk.java.apiclient.model.Case;
import com.desk.java.apiclient.model.Message;
import com.desk.java.apiclient.model.MessageDirection;
import com.desk.java.apiclient.service.CaseService;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Wraps a {@link CaseService} to provide a higher level of abstraction.
 */
public class CaseProvider {

    /**
     * Callbacks for creating a case
     */
    public interface CreateCaseCallback {

        /**
         * Called when the case has been created successfully
         * @param deskCase the new case
         */
        void onCaseCreated(Case deskCase);

        /**
         * Called when there is an error creating the case
         * @param error the error response
         */
        void onCreateCaseError(ErrorResponse error);
    }

    private CaseService caseService;

    public CaseProvider(CaseService caseService) {
        this.caseService = caseService;
    }

    /**
     * Creates a case
     * @param request the request object to build the case
     * @param callback the callback to notify on success or failure
     */
    public void createCase(@NonNull CreateCaseRequest request, @NonNull final CreateCaseCallback callback) {

        // create case object
        Case newCase = new Case();
        newCase.setType(request.getType());
        newCase.setCustomerName(request.getName());
        newCase.setCustomFields(request.getCustomFields());

        // create message object
        Message message = new Message();
        message.setFrom(request.getFrom());
        message.setTo(request.getTo());
        message.setSubject(request.getSubject());
        message.setBody(request.getBody());
        message.setDirection(MessageDirection.IN);
        newCase.setMessage(message);

        // create the case
        caseService.createCase(
                newCase,
                null,
                null)
                .enqueue(
                        new Callback<Case>() {
                            @Override
                            public void onResponse(Response<Case> response, Retrofit retrofit) {
                                callback.onCaseCreated(response.body());
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                callback.onCreateCaseError(new ErrorResponse(throwable));
                            }
                        }
                );
    }
}
