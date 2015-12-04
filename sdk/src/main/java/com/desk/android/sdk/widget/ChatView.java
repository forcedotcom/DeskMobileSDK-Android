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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.R;
import com.desk.android.sdk.identity.Identity;
import com.desk.android.sdk.identity.UserIdentity;
import com.desk.android.sdk.mvp.model.ChatMessageModel;
import com.desk.android.sdk.mvp.presenter.IChatPresenter;
import com.desk.android.sdk.mvp.presenter.provider.PresenterProvider;
import com.desk.android.sdk.mvp.view.IChatView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.functions.Action1;

/**
 * Created by Matt Kranzler on 12/3/15.
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class ChatView extends LinearLayout implements IChatView {

    private static final String TAG = ChatView.class.getCanonicalName();
    private static final Pattern CONNECTED_WITH_PATTERN = Pattern.compile("^You are now connected with Agent\\s(.*)$");

    private IChatPresenter presenter;
    private EditText chatInput;
    private ImageButton sendButton;
    private RecyclerView recycler;
    private ChatMessageAdapter adapter;
    private String userName;
    private boolean typing;
    private ChatMessageModel agentTypingMessage;

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
        onWaitingForAgent();
        if (TextUtils.isEmpty(userName)) {
            Identity identity = Desk.with(getContext()).getIdentity();
            if (identity != null && identity instanceof UserIdentity) {
                userName = ((UserIdentity) identity).getName();
            }
            if (TextUtils.isEmpty(userName)) {
                showUserNameDialog();
            } else {
                presenter.startSession(userName);
            }
        }
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        presenter.detach(this);
    }

    @Override public void destroy() {
        presenter.destroy();
    }

    @Override public void onNewMessages(List<ChatMessageModel> messages) {
        adapter.addAll(messages);
        scrollRecycler();
    }

    @Override public void onPendingMessage(ChatMessageModel pendingMessage) {
        adapter.add(pendingMessage);
        scrollRecycler();
    }

    @Override public void onMessageSent(ChatMessageModel pendingMessage, ChatMessageModel sentMessage) {
        adapter.swap(pendingMessage, sentMessage);
        scrollRecycler();
    }

    private void scrollRecycler() {
        recycler.getLayoutManager().scrollToPosition(0);
    }

    @Override public void onAgentStartedTyping() {
        if (agentTypingMessage == null) {
            agentTypingMessage = new ChatMessageModel("...", true, false);
            adapter.add(agentTypingMessage);
            scrollRecycler();
        }
    }

    @Override public void onAgentStoppedTyping() {
        if (agentTypingMessage != null) {
            adapter.remove(agentTypingMessage);
            scrollRecycler();
            agentTypingMessage = null;
        }
    }

    @SuppressWarnings("ConstantConditions") @Override public void onWaitingForAgent() {
        setSubtitle(getContext().getString(R.string.chat_waiting_for_agent));
    }

    @Override public void onAgentConnected(String message) {
        Matcher matcher = CONNECTED_WITH_PATTERN.matcher(message);
        if (matcher.find()) {
            String agent = matcher.group(1);
            setSubtitle(getContext().getString(R.string.chat_connected_with_agent, agent));
        }
    }

    private void setSubtitle(String subtitle) {
        AppCompatActivity activity = (AppCompatActivity) getContext();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void showUserNameDialog() {

    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.chat_view, this, true);
        chatInput = (EditText) findViewById(R.id.chat_input);
        sendButton = (ImageButton) findViewById(R.id.btn_send);
        recycler = (RecyclerView) findViewById(R.id.recycler);
        presenter = PresenterProvider.getChatPresenter();
        disableSendButton();
        setupRecyclerView();
        setupSubscriptions();
    }

    private void setupSubscriptions() {

        // listen for changes to enable/disable the send button
        RxTextView.textChanges(chatInput)
                .subscribe(new Action1<CharSequence>() {
                    @Override public void call(CharSequence charSequence) {
                        if (charSequence.length() >= 1) {
                            enableSendButton();
                        } else {
                            disableSendButton();
                        }
                    }
                });

        // listen for changes to notify user has started & stopped typing
        RxTextView.textChanges(chatInput)
                .subscribe(new Action1<CharSequence>() {
                    @Override public void call(CharSequence charSequence) {
                        if (TextUtils.isEmpty(charSequence)) {
                            notifyTypingStopped();
                        } else {
                            notifyTypingStarted();
                        }
                    }
                });

        // listen for clicks to send a message
        RxView.clicks(sendButton)
                .subscribe(new Action1<Object>() {
                    @Override public void call(Object o) {
                        sendMessage();
                    }
                });
    }

    private void setupRecyclerView() {
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true));
        adapter = new ChatMessageAdapter(getContext());
        recycler.setAdapter(adapter);
    }

    private void enableSendButton() {
        sendButton.setEnabled(true);
        sendButton.setAlpha(1.0f);
    }

    private void disableSendButton() {
        sendButton.setEnabled(false);
        sendButton.setAlpha(.3f);
    }

    private void sendMessage() {
        presenter.handleNewMessage(chatInput.getText().toString());
        chatInput.getText().clear();
    }

    private void notifyTypingStarted() {
        if (!typing) {
            presenter.userStartedTyping();
            typing = true;
            Log.d(TAG, "notifyTypingStarted: user is typing");
        }
    }

    private void notifyTypingStopped() {
        if (typing) {
            presenter.userStoppedTyping();
            typing = false;
            Log.d(TAG, "notifyTypingStopped: user is not typing");
        }
    }

    private class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

        static final int TYPE_INCOMING_MESSAGE = 0;
        static final int TYPE_OUTGOING_MESSAGE = 1;

        private LayoutInflater inflater;
        private SortedList<ChatMessageModel> items;

        public ChatMessageAdapter(Context context) {
            inflater = LayoutInflater.from(context);
            items = new SortedList<>(ChatMessageModel.class, new SortedListAdapterCallback<ChatMessageModel>(this) {
                @Override public int compare(ChatMessageModel o1, ChatMessageModel o2) {
                    return o2.getTime().compareTo(o1.getTime());
                }

                @Override
                public boolean areContentsTheSame(ChatMessageModel oldItem, ChatMessageModel newItem) {
                    return oldItem.equals(newItem);
                }

                @Override public boolean areItemsTheSame(ChatMessageModel item1, ChatMessageModel item2) {
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
            ChatMessageModel message = getItem(holder.getAdapterPosition());
            holder.chatMessage.setText(message.getMessage());
            holder.chatTimestamp.setText(getTimestampString(message));
            if (message.isPending()) {
                holder.chatMessage.setAlpha(.3f);
            } else {
                holder.chatMessage.setAlpha(1.0f);
            }
        }

        @Override public int getItemCount() {
            return items.size();
        }

        @Override public int getItemViewType(int position) {
            ChatMessageModel message = getItem(position);
            return message.isIncoming() ? TYPE_INCOMING_MESSAGE : TYPE_OUTGOING_MESSAGE;
        }

        public void addAll(List<ChatMessageModel> messages) {
            items.addAll(messages);
        }

        public void add(ChatMessageModel message) {
            items.add(message);
        }

        public void remove(ChatMessageModel message) {
            items.remove(message);
        }

        public void swap(ChatMessageModel messageOne, ChatMessageModel messageTwo) {
            int index = items.indexOf(messageOne);
            items.updateItemAt(index, messageTwo);
        }

        public ChatMessageModel getItem(int position) {
            return items.get(position);
        }

        private String getTimestampString(ChatMessageModel message) {
            if (message.isPending()) {
                return message.isIncoming() ? getContext().getString(R.string.sending) : "";
            }
            Date date = message.getTime();
            long now = System.currentTimeMillis();

            if (now - date.getTime() < 1000) {
                return getContext().getString(R.string.just_now);
            }

            return DateUtils.getRelativeTimeSpanString(
                    date.getTime(),
                    now,
                    DateUtils.SECOND_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL
            ).toString();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            View itemView;
            TextView chatMessage;
            TextView chatTimestamp;

            public ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                chatMessage = (TextView) itemView.findViewById(R.id.chat_message);
                chatTimestamp = (TextView) itemView.findViewById(R.id.chat_timestamp);
            }
        }

    }
}
