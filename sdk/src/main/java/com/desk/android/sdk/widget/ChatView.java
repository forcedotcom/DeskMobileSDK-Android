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

package com.desk.android.sdk.widget;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desk.android.sdk.R;
import com.desk.android.sdk.mvp.model.ChatMessage;
import com.desk.android.sdk.mvp.presenter.IChatPresenter;
import com.desk.android.sdk.mvp.presenter.provider.PresenterProvider;
import com.desk.android.sdk.mvp.view.IChatView;

import java.util.List;

/**
 * Created by Matt Kranzler on 12/3/15.
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class ChatView extends LinearLayout implements IChatView {

    private IChatPresenter presenter;

    private EditText chatInput;
    private ImageButton sendButton;
    private RecyclerView recycler;

    public ChatView(Context context) {
        this(context, null);
    }

    public ChatView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.attach(this);
    }

    @Override public void destroy() {
        presenter.destroy();
    }

    @Override public void onNewMessages(List<ChatMessage> messages) {

    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.chat_view, this, true);
        chatInput = (EditText) findViewById(R.id.chat_input);
        sendButton = (ImageButton) findViewById(R.id.btn_send);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        presenter = PresenterProvider.getChatPresenter();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        recycler.setLayoutManager(layoutManager);
    }

    private class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

        static final int TYPE_INCOMING_MESSAGE = 0;
        static final int TYPE_OUTGOING_MESSAGE = 1;

        private LayoutInflater inflater;
        private SortedList<ChatMessage> items;

        public ChatMessageAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            items = new SortedList<>(ChatMessage.class, new SortedListAdapterCallback<ChatMessage>(this) {
                @Override public int compare(ChatMessage o1, ChatMessage o2) {
                    return o1.getTime().compareTo(o2.getTime());
                }

                @Override
                public boolean areContentsTheSame(ChatMessage oldItem, ChatMessage newItem) {
                    return oldItem.equals(newItem);
                }

                @Override public boolean areItemsTheSame(ChatMessage item1, ChatMessage item2) {
                    return item1.equals(item2);
                }
            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int chatMessageLayoutResId;
            if (TYPE_INCOMING_MESSAGE == viewType) {
                chatMessageLayoutResId = R.layout.chat_message_incoming;
            } else if (TYPE_OUTGOING_MESSAGE == viewType) {
                chatMessageLayoutResId = R.layout.chat_message_outgoing;
            } else {
                throw new IllegalStateException("viewType " + viewType + " is invalid.");
            }
            return new ViewHolder(inflater.inflate(chatMessageLayoutResId, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // TODO
        }

        @Override public int getItemCount() {
            return items.size();
        }

        @Override public int getItemViewType(int position) {
            ChatMessage message = getItem(position);
            return message.isIncoming() ? TYPE_INCOMING_MESSAGE : TYPE_OUTGOING_MESSAGE;
        }

        public void addAll(List<ChatMessage> messages) {
            items.addAll(messages);
        }

        public ChatMessage getItem(int position) {
            return items.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView chatMessage;
            TextView chatTimestamp;

            public ViewHolder(View itemView) {
                super(itemView);
                chatMessage = (TextView) itemView.findViewById(R.id.chat_message);
                chatTimestamp = (TextView) itemView.findViewById(R.id.chat_timestamp);
            }
        }

    }
}
