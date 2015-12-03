package com.desk.android.sdk.mvp.usecase;

import com.desk.java.apiclient.model.chat.CustomerInfo;
import com.desk.java.apiclient.model.chat.SessionInfo;
import com.desk.java.apiclient.service.RxChatService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <p>
 * TODO - Add class comments
 * </p>
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class StartChatSession {

    private RxChatService chatService;
    private long guestCustomerId;
    private String chatToken;
    private String customerToken;

    public StartChatSession(RxChatService chatService, long guestCustomerId, String chatToken, String customerToken) {
        this.chatService = chatService;
        this.guestCustomerId = guestCustomerId;
        this.chatToken = chatToken;
        this.customerToken = customerToken;
    }

    public Observable<SessionInfo> execute() {
        return chatService
                .startSession(guestCustomerId, chatToken, customerToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
