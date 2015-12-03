package com.desk.android.sdk.mvp.usecase;

import com.desk.java.apiclient.model.chat.CustomerInfo;
import com.desk.java.apiclient.service.RxChatService;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * <p>
 * TODO - Add class comments
 * </p>
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class CreateGuestCustomer {

    private static final String FIELDS = "token,id";

    private RxChatService chatService;
    private String firstName;
    private String chatToken;

    public CreateGuestCustomer(RxChatService chatService, String firstName, String chatToken) {
        this.chatService = chatService;
        this.firstName = firstName;
        this.chatToken = chatToken;
    }

    public Observable<CustomerInfo> execute() {
        return chatService
                .createCustomer(firstName, chatToken, FIELDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
