package com.desk.android.sdk.util;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.Toolbar;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.activity.ContactUsActivity;
import com.desk.android.sdk.activity.ContactUsWebActivity;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.Article;
import com.desk.java.apiclient.model.Topic;
import com.google.gson.reflect.TypeToken;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static com.desk.android.sdk.util.TestUtils.readMockJsonFile;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.hamcrest.core.Is.is;

/**
 * A collection of utility methods to aid in writing Instrumentation tests.
 * <p/>
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class InstrumentationTestUtils {

    public static ApiResponse<Article> getMockedArticleResponse() {
        ApiResponse<Article> response = readMockJsonFile(
                new TypeToken<ApiResponse<Article>>() {}.getType(),
                "mock/mock_article_response.json"
        );
        return response;
    }

    public static ApiResponse<Topic> getMockedTopicResponse() {
        ApiResponse<Topic> response = readMockJsonFile(
                new TypeToken<ApiResponse<Topic>>() {}.getType(),
                "mock/mock_topic_response.json"
        );
        return response;
    }

    @NonNull
    public static String getContactUsComponentName() {
        Desk desk = Desk.with(InstrumentationRegistry.getContext());
        boolean isWebFormEnabled = desk.getContactUsConfig().isWebFormEnabled();
        return isWebFormEnabled ? ContactUsWebActivity.class.getName() : ContactUsActivity.class.getName();
    }

    @NonNull
    public static IdlingResource createIdleResourceAndWait(long waitingTime) {
        // set Espresso timeout policies
        IdlingPolicies.setMasterPolicyTimeout(waitingTime * 2, MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(waitingTime * 2, MILLISECONDS);

        // wait
        IdlingResource idlingResource = new ElapsedTimeIdlingResource(waitingTime);
        Espresso.registerIdlingResources(idlingResource);
        return idlingResource;
    }

    public static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    public static Matcher<Object> withToolbarTitle(final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }
}
