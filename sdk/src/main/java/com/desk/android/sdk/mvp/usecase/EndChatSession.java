package com.desk.android.sdk.mvp.usecase;

import com.desk.java.apiclient.service.RxChatService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <p>
 *     Use case for ending a chat session.
 * </p>
 *
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class EndChatSession {

    private static final String REQUESTOR = "customer";

    private RxChatService chatService;
    private long guestCustomerId;
    private long chatSessionId;
    private String chatToken;
    private String customerToken;

    public EndChatSession(RxChatService chatService, long guestCustomerId, long chatSessionId, String chatToken, String customerToken) {
        this.chatService = chatService;
        this.guestCustomerId = guestCustomerId;
        this.chatSessionId = chatSessionId;
        this.chatToken = chatToken;
        this.customerToken = customerToken;
    }

    public Observable<Void> execute() {
        return chatService
                .endSession(guestCustomerId, chatSessionId, chatToken, customerToken, REQUESTOR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
