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

package com.desk.android.sdk.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.desk.java.apiclient.model.CaseType;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Encapsulates the fields necessary for creating a case. To create an instance use
 * the {@link com.desk.android.sdk.model.CreateCaseRequest.Builder} class.
 */
public class CreateCaseRequest implements Serializable {

    private CaseType type;
    private String body;
    private String to;
    private String from;
    private String subject;
    private String name;
    private HashMap<String, String> customFields;

    private CreateCaseRequest() {}

    private CreateCaseRequest(CaseType type, String body, String to, String from, String subject,
                              String name, HashMap<String, String> customFields) {
        this.type = type;
        this.body = body;
        this.to = to;
        this.from = from;
        this.subject = subject;
        this.name = name;
        this.customFields = customFields;
    }

    /**
     * Get the type of case to create
     * @return the case type
     */
    public CaseType getType() {
        return type;
    }

    /**
     * Get the body of the message attached to this case
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * Get the to email address for this case
     * @return the email address
     */
    public String getTo() {
        return to;
    }

    /**
     * Get the from email address for this case
     * @return the email address
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get the subject for this case
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get the customer name for this case
     * @return the customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the custom fields for this case
     * @return the custom fields
     */
    public HashMap<String, String> getCustomFields() {
        return customFields;
    }

    /**
     * Builder which aids in creating {@link CreateCaseRequest} instances.
     */
    public static class Builder {

        private CaseType type;
        private String body;
        private String to;
        private String from;
        private String subject;
        private String name;
        private HashMap<String, String> customFields;

        private Builder() {}

        /**
         * Default constructor with required fields.
         * @param type the case type
         * @param body the case body
         * @param to the case to email address
         * @param from the case from email address
         */
        @SuppressWarnings("ConstantConditions")
        public Builder(@NonNull CaseType type, @NonNull String body, @NonNull String to, @NonNull String from) {
            if (type == null) {
                throw new NullPointerException("type cannot be null.");
            } else if (body == null) {
                throw new NullPointerException("body cannot be null.");
            } else if (to == null) {
                throw new NullPointerException("to cannot be null.");
            } else if (from == null) {
                throw new NullPointerException("from cannot be null.");
            }
            this.type = type;
            this.body = body;
            this.to = to;
            this.from = from;
        }

        /**
         * Set the *optional* subject for the case.
         * @param subject the case subject
         * @return the builder instance
         */
        public Builder subject(@Nullable String subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Set the *optional* customer name for the case
         * @param name the customer name
         * @return the builder instance
         */
        public Builder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        /**
         * Add *optional* custom fields to the case
         * @param customFields the custom fields
         * @return the builder instance
         */
        public Builder customFields(HashMap<String, String> customFields) {
            this.customFields = customFields;
            return this;
        }

        /**
         * Add an *optional* custom field to the case
         * @param key the custom field key
         * @param value the custom field value
         * @return the builder instance
         */
        public Builder customField(String key, String value) {
            if (this.customFields == null) {
                this.customFields = new HashMap<>();
            }
            this.customFields.put(key, value);
            return this;
        }

        /**
         * Creates the {@link CreateCaseRequest} instance
         * @return the instance
         */
        public CreateCaseRequest create() {
            return new CreateCaseRequest(type, body, to, from, subject, name, customFields);
        }
    }
}
