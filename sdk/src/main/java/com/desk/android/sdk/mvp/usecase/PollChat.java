package com.desk.android.sdk.mvp.usecase;

import com.desk.java.apiclient.model.chat.ChatSessionPoll;
import com.desk.java.apiclient.model.chat.Requestor;
import com.desk.java.apiclient.service.RxChatService;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * <p>
 *     Use case for ending a chat session.
 * </p>
 *
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class PollChat {

    private RxChatService chatService;
    private long guestCustomerId;
    private long chatSessionId;
    private String chatToken;
    private String customerToken;

    public PollChat(RxChatService chatService, long guestCustomerId, long chatSessionId, String chatToken, String customerToken) {
        this.chatService = chatService;
        this.guestCustomerId = guestCustomerId;
        this.chatSessionId = chatSessionId;
        this.chatToken = chatToken;
        this.customerToken = customerToken;
    }

    public Observable<ChatSessionPoll> execute() {
        return Observable
                .interval(5, TimeUnit.SECONDS, Schedulers.io())
                .flatMap(new Func1<Long, Observable<ChatSessionPoll>>() {
                    @Override
                    public Observable<ChatSessionPoll> call(Long aLong) {
                        return chatService.poll(guestCustomerId, chatSessionId, chatToken, customerToken, Requestor.CUSTOMER);
                    }
                })
                .retry()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
