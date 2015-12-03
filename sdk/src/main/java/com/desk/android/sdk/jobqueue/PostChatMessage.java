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

package com.desk.android.sdk.jobqueue;

import com.desk.android.sdk.bus.BusProvider;
import com.desk.android.sdk.mvp.model.ChatMessage;
import com.desk.android.sdk.mvp.usecase.SendChatMessage;
import com.desk.java.apiclient.model.chat.CustomerInfo;
import com.desk.java.apiclient.model.chat.SessionInfo;
import com.desk.java.apiclient.service.RxChatService;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import rx.functions.Action1;

import static com.desk.android.sdk.jobqueue.JobEvent.Action.ADDED;
import static com.desk.android.sdk.jobqueue.JobEvent.Action.CANCELED;
import static com.desk.android.sdk.jobqueue.JobEvent.Action.PROCESSED;
import static com.desk.android.sdk.jobqueue.JobEvent.Action.PROCESSING;

/**
 * <p>
 *     A {@link Job} that post chat messages to the backend.
 * </p>
 *
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class PostChatMessage extends Job {

    public static final int PRIORITY = 1;

    private ChatMessage chatMessage;
    private RxChatService chatService;
    private String chatToken;
    private CustomerInfo customerInfo;

    public PostChatMessage(ChatMessage chatMessage, RxChatService chatService, CustomerInfo customerInfo, String chatToken) {
        super(new Params(PRIORITY).requireNetwork().persist());
        this.chatMessage = chatMessage;
        this.chatService = chatService;
        this.chatToken = chatToken;
        this.customerInfo = customerInfo;
    }

    @Override
    public void onAdded() {
        BusProvider.get().post(new JobEvent(ADDED));
    }

    @Override
    public void onRun() throws Throwable {
        BusProvider.get().post(new JobEvent(PROCESSING));
        SendChatMessage sendChatMessage = new SendChatMessage(chatService, chatMessage.getMessage(), customerInfo.guestCustomerId, chatToken, customerInfo.customerToken);
        sendChatMessage.execute()
                .subscribe(
                        new Action1<SessionInfo>() {
                            @Override
                            public void call(SessionInfo sessionInfo) {
                                BusProvider.get().post(new JobEvent(PROCESSED, chatMessage));
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                // TODO handle
                            }
                        });
    }

    @Override
    protected void onCancel() {
        BusProvider.get().post(new JobEvent(CANCELED));
    }
}
