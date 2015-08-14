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

package com.desk.android.sdk.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.error.ErrorResponse;
import com.desk.android.sdk.model.CreateCaseRequest;
import com.desk.android.sdk.provider.CaseProvider;
import com.desk.java.apiclient.model.Case;

/**
 * Headless fragment which aids in creating a case. The instance of this fragment will be retained
 * across Activity configuration changes in order to retain the result of creating a case. To listen
 * for callbacks implement {@link com.desk.android.sdk.fragment.CreateCaseHelper.CreateCaseListener}.
 * To create an instance use {@link #attach(Activity)}. To create a case call {@link #createCase(CreateCaseRequest)}.
 */
public class CreateCaseHelper extends Fragment {

    private static final String FRAG_TAG = CreateCaseHelper.class.getCanonicalName();

    /**
     * Listener to be notified of the result of an attempt to create a case
     */
    public interface CreateCaseListener {

        /**
         * The case was created successfully
         * @param deskCase the created case
         */
        void onCaseCreated(Case deskCase);

        /**
         * An error occurred in the process of creating a case
         * @param error the error response
         */
        void onCreateCaseError(ErrorResponse error);
    }

    /**
     * Either creates or returns the retained {@link CreateCaseHelper} instance.
     * @param parent the activity which implements {@link com.desk.android.sdk.fragment.CreateCaseHelper.CreateCaseListener}
     * @return the {@link CreateCaseHelper} instance
     */
    public static <ParentActivity extends Activity & CreateCaseListener> CreateCaseHelper attach(ParentActivity parent) {
        return attach(parent.getFragmentManager());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Create a case with the provided request.
     * @param request the request to create the case
     */
    public void createCase(CreateCaseRequest request) {
        Desk.with(getActivity())
                .getCaseProvider()
                .createCase(request, new CaseProvider.CreateCaseCallback() {
                    @Override
                    public void onCaseCreated(Case deskCase) {
                        if (getParent() != null) {
                            getParent().onCaseCreated(deskCase);
                        }
                    }

                    @Override
                    public void onCreateCaseError(ErrorResponse error) {
                        if (getParent() != null) {
                            getParent().onCreateCaseError(error);
                        }
                    }
                });
    }

    private static CreateCaseHelper attach(FragmentManager fragmentManager) {
        CreateCaseHelper frag = (CreateCaseHelper) fragmentManager.findFragmentByTag(FRAG_TAG);
        if (frag == null) {
            frag = new CreateCaseHelper();
            fragmentManager.beginTransaction().add(frag, FRAG_TAG).commit();
        }
        return frag;
    }

    private CreateCaseListener getParent() {
        Activity activity = getActivity();
        if (activity instanceof CreateCaseListener) {
            return (CreateCaseListener) activity;
        }
        return null;
    }
}