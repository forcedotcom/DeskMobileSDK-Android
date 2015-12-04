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

import android.os.Handler;
import android.os.Looper;

import com.desk.android.sdk.bus.BusProvider;
import com.desk.android.sdk.mvp.model.ChatMessageModel;
import com.desk.android.sdk.mvp.usecase.SendChatMessage;
import com.desk.java.apiclient.model.chat.ChatMessage;
import com.desk.java.apiclient.model.chat.GuestCustomer;
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

    private ChatMessageModel chatMessageModel;
    private RxChatService chatService;
    private String chatToken;
    private GuestCustomer guestCustomer;
    private long caseId;

    public PostChatMessage(ChatMessageModel chatMessageModel, RxChatService chatService, GuestCustomer guestCustomer, String chatToken, long caseId) {
        super(new Params(PRIORITY));
        this.chatMessageModel = chatMessageModel;
        this.chatService = chatService;
        this.chatToken = chatToken;
        this.guestCustomer = guestCustomer;
        this.caseId = caseId;
    }

    @Override
    public void onAdded() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                BusProvider.get().post(new JobEvent(ADDED));
            }
        });
    }

    @Override
    public void onRun() throws Throwable {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                BusProvider.get().post(new JobEvent(PROCESSING));
            }
        });

        SendChatMessage sendChatMessage = new SendChatMessage(chatService, chatMessageModel.getMessage(), caseId, chatToken, guestCustomer.token);
        sendChatMessage.execute()
                .subscribe(
                        new Action1<ChatMessage>() {
                            @Override
                            public void call(ChatMessage sessionInfo) {
                                BusProvider.get().post(new JobEvent(PROCESSED, chatMessageModel));
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                                // TODO handle
                            }
                        });
    }

    @Override
    protected void onCancel() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                BusProvider.get().post(new JobEvent(CANCELED));
            }
        });
    }
}
