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

package com.desk.android.sdk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.desk.android.sdk.config.ContactUsConfig;
import com.desk.android.sdk.config.ContactUsPropertyConfig;
import com.desk.android.sdk.config.DeskConfig;
import com.desk.android.sdk.config.DeskPropertyConfig;
import com.desk.android.sdk.identity.Identity;
import com.desk.android.sdk.identity.UserIdentity;
import com.desk.android.sdk.provider.ArticleProvider;
import com.desk.android.sdk.provider.CaseProvider;
import com.desk.android.sdk.provider.InboundMailboxProvider;
import com.desk.android.sdk.provider.TopicProvider;
import com.desk.java.apiclient.DeskClient;
import com.squareup.okhttp.Cache;

import java.util.Locale;

/**
 * <p>Main class to interact with in the Desk SDK. This class allows you to provide various configurations
 * which customize certain parts of the SDK.</p>
 *
 * <p>This class creates a {@link DeskClient} to interact with the Desk.com API. To configure which
 * API token and Hostname to use either provide them in your desk.properties file or call
 * {@link #setConfig(DeskConfig)} passing a config object which provides them. To see which properties
 * to set refer to {@link DeskPropertyConfig}. The constructed {@link DeskClient} will have a response
 * cache of 20 Mb which will live in the external cache directory of the device if available, or else
 * it will live in the internal cache directory.</p>
 *
 * <p>To configure options for 'Contact Us', either provide your options in your desk.properties object
 * or call {@link #setContactUsConfig(ContactUsConfig)} passing a config object which provides them.
 * To see what properties to set refer to {@link ContactUsPropertyConfig}.</p>
 *
 * <p>To set the user's identity which is used when creating a case via 'Contact Us' call {@link #setIdentity(Identity)}
 * passing an identity object (see {@link com.desk.android.sdk.identity.UserIdentity}). If
 * {@link UserIdentity#getEmail()} is provided the email address option will be hidden and the provided
 * email address will be used when creating a case. If {@link UserIdentity#getName()} is provided
 * the name option will be hidden and the name will be used when creating a case.</p>
 */
public final class Desk {

    @VisibleForTesting
    static final long CACHE_MAX_SIZE = 20 * 1024 * 1024; // 20 mb

    private static final boolean DEBUG = false;
    private static final String CONTACT_US_PATH = "/customer/portal/emails/new";

    private static Desk singleton;

    private Context context;
    private DeskClient client;

    private CaseProvider caseProvider;
    private ArticleProvider articleProvider;
    private TopicProvider topicProvider;
    private InboundMailboxProvider mInboundMailboxProvider;

    private Identity identity;
    private DeskConfig config;
    private ContactUsConfig contactUsConfig;

    private Desk(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Gets an instance of Desk, creating one if necessary
     * @param context the application context
     * @return the singleton Desk instance
     */
    public static Desk with(Context context) {
        if (singleton == null) {
            singleton = new Desk(context);
        }
        return singleton;
    }

    /**
     * Set the desk config which is used to configure the {@link DeskClient}.
     * @param config the desk config
     * @return the Desk instance
     */
    public Desk setConfig(@NonNull DeskConfig config) {
        //noinspection ConstantConditions
        if (config == null) {
            throw new NullPointerException("DeskConfig cannot be null.");
        }
        this.config = config;
        clearClient();
        return this;
    }

    /**
     * Gets the desk config or creates a new {@link DeskPropertyConfig}.
     * @return the desk config
     */
    @NonNull
    public DeskConfig getConfig() {
        if (config == null) {
            config = new DeskPropertyConfig(context);
        }
        return config;
    }

    /**
     * Set the identity to be used to identify the user when creating a case.
     * @param identity the identity
     * @return the Desk instance
     */
    public Desk setIdentity(@Nullable Identity identity) {
        this.identity = identity;
        return this;
    }

    /**
     * Get the identity
     * @return the identity
     */
    @Nullable
    public Identity getIdentity() {
        return identity;
    }

    /**
     * Set the config for contact us (case creation)
     * @param contactUsConfig the contact us config
     * @return the Desk instance
     */
    public Desk setContactUsConfig(@NonNull ContactUsConfig contactUsConfig) {
        //noinspection ConstantConditions
        if (contactUsConfig == null) {
            throw new NullPointerException("ContactUsConfig cannot be null.");
        }
        this.contactUsConfig = contactUsConfig;
        return this;
    }

    /**
     * Gets the contact us config or creates a new {@link ContactUsPropertyConfig}.
     * @return the config
     */
    @NonNull
    public ContactUsConfig getContactUsConfig() {
        if (contactUsConfig == null) {
            contactUsConfig = new ContactUsPropertyConfig(context);
        }
        return contactUsConfig;
    }

    /**
     * Releases the singleton instance for testing purposes
     */
    @VisibleForTesting
    static void release() {
        singleton = null;
    }

    /**
     * Gets or creates a {@link DeskClient} using the {@link DeskConfig}.
     * @return the Desk client
     */
    @NonNull
    public DeskClient getClient() {
        if (client == null) {
            client = new DeskClient
                    .Builder(getConfig().getHostname(), getConfig().getApiToken())
                    .responseCache(getResponseCache(context))
                    .create();
        }
        return client;
    }

    public void clearClient() {

        // clear client so it gets recreated
        this.client = null;
    }

    /**
     * Gets the language to be used when retrieving topics & articles from the api
     * @return the language
     */
    @NonNull
    public static String getLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * Gets the {@link CaseProvider}, creating one if necessary.
     * @return the case provider
     */
    @NonNull
    public CaseProvider getCaseProvider() {
        if (caseProvider == null) {
            caseProvider = new CaseProvider(getClient().cases());
        }
        return caseProvider;
    }

    /**
     * Gets the {@link ArticleProvider}, creating one if necessary.
     * @return the case provider
     */
    @NonNull
    public ArticleProvider getArticleProvider() {
        if (articleProvider == null) {
            articleProvider = new ArticleProvider(getClient().articles());
        }
        return articleProvider;
    }

    /**
     * Gets the {@link TopicProvider}, creating one if necessary.
     * @return the case provider
     */
    @NonNull
    public TopicProvider getTopicProvider() {
        if (topicProvider == null) {
            topicProvider = new TopicProvider(getClient().topics());
        }
        return topicProvider;
    }

    /**
     * Gets the {@link TopicProvider}, creating one if necessary.
     * @return the case provider
     */
    @NonNull
    public InboundMailboxProvider getInboundMailboxProvider() {
        if (mInboundMailboxProvider == null) {
            mInboundMailboxProvider = new InboundMailboxProvider(getClient().inboundMailboxes());
        }
        return mInboundMailboxProvider;
    }

    @VisibleForTesting
    public void setTopicProvider(@NonNull TopicProvider topicProvider) {
        this.topicProvider = topicProvider;
    }

    @VisibleForTesting
    public void setArticleProvider(@NonNull ArticleProvider articleProvider) {
        this.articleProvider = articleProvider;
    }

    /**
     * Gets the url to the contact us web page
     * @return the url
     */
    @NonNull
    public String getContactUsWebFormUrl() {
        return getClient().getUrl(CONTACT_US_PATH);
    }

    @VisibleForTesting
    Cache getResponseCache(Context context) {
        return new Cache(
                context.getExternalCacheDir() != null
                        ? context.getExternalCacheDir()
                        : context.getCacheDir(),
                CACHE_MAX_SIZE
        );
    }
}
