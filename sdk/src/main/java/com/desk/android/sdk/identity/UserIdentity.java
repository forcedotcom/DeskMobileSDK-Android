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

package com.desk.android.sdk.identity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Represents user identification to be used when creating cases. To create an instance use the {@link com.desk.android.sdk.identity.UserIdentity.Builder}
 * class.
 */
public class UserIdentity implements Identity {

    private String name;
    private String email;

    private UserIdentity() {}

    private UserIdentity(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Returns the user's name
     * @return the user's name
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Returns the user's email address
     * @return the user's email address
     */
    @NonNull
    public String getEmail() {
        return email;
    }

    /**
     * Builder which aids in creating {@link UserIdentity} instances.
     */
    public static class Builder {

        private String name;
        private String email;

        private Builder() {}

        /**
         * Default constructor with required fields.
         * @param email the user's email address
         */
        @SuppressWarnings("ConstantConditions")
        public Builder(@NonNull String email) {
            if (email == null) {
                throw new NullPointerException("email cannot be null.");
            }
            this.email = email;
        }

        /**
         * Set the user's name
         * @param name the user's name
         * @return the builder instance
         */
        public Builder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        /**
         * Creates the {@link UserIdentity} instance
         * @return the instance
         */
        public UserIdentity create() {
            return new UserIdentity(name, email);
        }
    }
}
