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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.R;
import com.desk.android.sdk.activity.ContactUsActivity;
import com.desk.android.sdk.activity.ContactUsWebActivity;
import com.desk.android.sdk.config.ContactUsConfig;
import com.desk.android.sdk.error.ErrorResponse;
import com.desk.android.sdk.helper.DeskThemeHelper;
import com.desk.android.sdk.helper.MenuHelper;
import com.desk.android.sdk.provider.InboundMailboxProvider;
import com.desk.java.apiclient.model.InboundMailbox;
import com.desk.java.apiclient.util.StringUtils;

import java.util.List;

/**
 * <p>Headless fragment which handles adding contact us options to the overflow menu by getting attributes
 * out of the {@link Activity}'s theme. This fragment also handles the menu options.</p>
 *
 * Created by Matt Kranzler on 7/9/15.
 */
public class ContactUsHelper extends Fragment {

    private static final String FRAG_TAG = ContactUsHelper.class.getCanonicalName();

    private DeskThemeHelper mThemeHelper;
    private Desk mDesk;
    private ContactUsConfig mConfig;

    private boolean mContactUsEnabled;
    private boolean mUseWebForm;

    private String mEmailAddress;

    /**
     * Attaches the fragment to the activity
     * @param activity the activity to attach to
     */
    public static void attach(Activity activity) {
        attach(activity.getFragmentManager());
    }

    /**
     * Removes the fragment from the activity
     * @param activity the activity to detach from
     */
    public static void detach(Activity activity) {
        Fragment fragment = activity.getFragmentManager().findFragmentByTag(FRAG_TAG);
        if (fragment != null) {
            activity.getFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    /**
     * Launches the {@link ContactUsWebActivity} or {@link ContactUsActivity} depending on the configuration.
     * @param activity the activity used to find the {@link ContactUsHelper}
     */
    public static void launch(Activity activity) {
        Fragment fragment = activity.getFragmentManager().findFragmentByTag(FRAG_TAG);
        if (fragment != null && fragment instanceof ContactUsHelper) {
            ((ContactUsHelper) fragment).handleContactUs();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mThemeHelper = new DeskThemeHelper(activity);
        mDesk = Desk.with(activity);
        mConfig = mDesk.getContactUsConfig();
        initializeVariables();
    }

    private void initializeVariables() {
        if (mThemeHelper.hasBrandId()) {
            int brandId = mThemeHelper.getBrandId();
            mContactUsEnabled = mConfig.isContactUsEnabled(brandId);
            mEmailAddress = mConfig.getEmailAddress(brandId);
            mUseWebForm = mConfig.isWebFormEnabled(brandId);
        } else {
            mContactUsEnabled = mConfig.isContactUsEnabled();
            mEmailAddress = mConfig.getEmailAddress();
            mUseWebForm = mConfig.isWebFormEnabled();
        }

        // if there isn't an overridden email address lets load an email address
        if (mContactUsEnabled && StringUtils.isEmpty(mEmailAddress) && !mUseWebForm) {
            mContactUsEnabled = false;
            getActivity().invalidateOptionsMenu();
            loadInboundMailbox();
        }
    }

    private void loadInboundMailbox() {
        mDesk.getInboundMailboxProvider()
                .getMailboxes(1, new InboundMailboxProvider.InboundMailboxCallbacks() {
                    @Override
                    public void onInboundMailboxesLoaded(int page, List<InboundMailbox> mailboxes) {
                        if (mailboxes != null && mailboxes.size() > 0) {
                            for (InboundMailbox mailbox : mailboxes) {
                                if (mailbox.isEnabled()) {
                                    onInboundMailboxLoaded(mailbox);
                                    break;
                                }
                            }
                        } else {
                            onNoInboundMailboxAvailable();
                        }
                    }

                    @Override
                    public void onInboundMailboxLoadError(ErrorResponse error) {
                        onNoInboundMailboxAvailable();
                    }
                });
    }

    private void onNoInboundMailboxAvailable() {
        if (getActivity() != null) {

            // disable email us if we fail to load the mailbox
            mContactUsEnabled = false;
            getActivity().invalidateOptionsMenu();
        }
    }

    private void onInboundMailboxLoaded(InboundMailbox mailbox) {
        if (getActivity() != null) {
            mContactUsEnabled = true;
            mEmailAddress = mailbox.getEmail();
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contact_us_menu, menu);
        MenuHelper.tintIcons(menu, mThemeHelper.getColorControlNormal(), R.id.contact_us);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem contactUs = menu.findItem(R.id.contact_us);
        if (contactUs != null) {
            contactUs.setVisible(mContactUsEnabled);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (R.id.contact_us == id) {
            handleContactUs();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void handleContactUs() {
        if (mUseWebForm) {
            ContactUsWebActivity.start(getActivity(), mThemeHelper.getThemeResId());
        } else {
            ContactUsActivity.start(getActivity(), mEmailAddress, mThemeHelper.getThemeResId());
        }
    }

    private static ContactUsHelper attach(FragmentManager fragmentManager) {
        ContactUsHelper frag = (ContactUsHelper) fragmentManager.findFragmentByTag(FRAG_TAG);
        if (frag == null) {
            frag = new ContactUsHelper();
            fragmentManager.beginTransaction().add(frag, FRAG_TAG).commit();
        }
        return frag;
    }
}
