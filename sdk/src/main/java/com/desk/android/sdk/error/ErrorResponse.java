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

package com.desk.android.sdk.error;

import java.io.IOException;

/**
 * Represents an error response from the desk api.
 */
public class ErrorResponse {

    private boolean isNetworkError;
    private String reason;
    private int status;

    private ErrorResponse() {}

    public ErrorResponse(boolean isNetworkError, String reason, int status) {
        this.isNetworkError = isNetworkError;
        this.reason = reason;
        this.status = status;
    }

    public ErrorResponse(Throwable throwable) {
        isNetworkError = throwable instanceof IOException;
        reason = throwable.getMessage();
        status = 500;
    }

    /**
     * Returns whether the error that occurred was network related
     * @return true if network related, false if not
     */
    public boolean isNetworkError() {
        return isNetworkError;
    }

    /**
     * Returns the human readable reason for the error
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns the http status code
     * @return the status code
     */
    public int getStatus() {
        return status;
    }

}
