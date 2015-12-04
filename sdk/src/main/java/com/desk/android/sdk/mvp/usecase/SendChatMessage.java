package com.desk.android.sdk.mvp.usecase;

import com.desk.java.apiclient.model.chat.ChatMessage;
import com.desk.java.apiclient.service.RxChatService;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <p>
 *     Use case for sending a chat message.
 * </p>
 *
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class SendChatMessage {

    private RxChatService chatService;
    private String message;
    private long guestCustomerId;
    private String chatToken;
    private String customerToken;

    public SendChatMessage(RxChatService chatService, String message, long guestCustomerId, String chatToken, String customerToken) {
        this.chatService = chatService;
        this.message = message;
        this.guestCustomerId = guestCustomerId;
        this.chatToken = chatToken;
        this.customerToken = customerToken;
    }

    public Observable<ChatMessage> execute() {
        return chatService
                .sendMessage(guestCustomerId, message, chatToken, customerToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
