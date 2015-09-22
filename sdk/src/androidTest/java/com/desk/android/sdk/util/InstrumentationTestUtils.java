package com.desk.android.sdk.util;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.v7.widget.Toolbar;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.activity.ContactUsActivity;
import com.desk.android.sdk.activity.ContactUsWebActivity;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.Article;
import com.desk.java.apiclient.model.Topic;
import com.google.gson.reflect.TypeToken;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static com.desk.android.sdk.util.TestUtils.readMockJsonFile;
import static org.hamcrest.core.Is.is;

/**
 * A collection of utility methods to aid in writing Instrumentation tests.
 * <p/>
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
public class InstrumentationTestUtils {

    public static ApiResponse<Article> getMockedArticleResponse() {
        return readMockJsonFile(
                new TypeToken<ApiResponse<Article>>() {}.getType(),
                "mock/mock_article_response.json"
        );
    }

    public static ApiResponse<Topic> getMockedTopicResponse() {
        return readMockJsonFile(
                new TypeToken<ApiResponse<Topic>>() {
                }.getType(),
                "mock/mock_topic_response.json"
        );
    }

    @NonNull
    public static String getContactUsComponentName() {
        Desk desk = Desk.with(InstrumentationRegistry.getContext());
        boolean isWebFormEnabled = desk.getContactUsConfig().isWebFormEnabled();
        return isWebFormEnabled ? ContactUsWebActivity.class.getName() : ContactUsActivity.class.getName();
    }

    public static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(ViewMatchers.withToolbarTitle(is(title))));
    }
}
