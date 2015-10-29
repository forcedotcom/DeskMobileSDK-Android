package com.desk.android.sdk.activity;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.desk.android.sdk.Desk;
import com.desk.android.sdk.R;
import com.desk.android.sdk.config.ContactUsPropertyConfig;
import com.desk.android.sdk.provider.TopicProvider;
import com.desk.android.sdk.provider.TopicProvider.TopicCallbacks;
import com.desk.android.sdk.util.DeskDefaultsRule;
import com.desk.android.sdk.util.ViewActions;
import com.desk.java.apiclient.model.ApiResponse;
import com.desk.java.apiclient.model.Topic;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static android.support.test.InstrumentationRegistry.getContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static com.desk.android.sdk.activity.ArticleListActivity.EXTRA_MODE;
import static com.desk.android.sdk.activity.ArticleListActivity.EXTRA_QUERY;
import static com.desk.android.sdk.activity.ArticleListActivity.EXTRA_TOPIC;
import static com.desk.android.sdk.activity.ArticleListActivity.MODE_SEARCH;
import static com.desk.android.sdk.activity.ArticleListActivity.MODE_TOPIC;
import static com.desk.android.sdk.helper.DeskThemeHelper.EXTRA_THEME_RES_ID;
import static com.desk.android.sdk.helper.DeskThemeHelper.NO_THEME_RES_ID;
import static com.desk.android.sdk.util.InstrumentationTestUtils.getContactUsComponentName;
import static com.desk.android.sdk.util.InstrumentationTestUtils.getMockedTopicResponse;
import static com.desk.android.sdk.util.InstrumentationTestUtils.matchToolbarTitle;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * UI Test for {@link TopicListActivity}.
 * <p/>
 * Created by Jerrell Mardis
 * Copyright (c) 2015 Desk.com. All rights reserved.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TopicListActivityTest {

    @Mock static TopicProvider mockTopicProvider = mock(TopicProvider.class);

    @ClassRule
    public static DeskDefaultsRule resetRule = new DeskDefaultsRule();

    private static ApiResponse<Topic> mockedTopicResponse;

    @Rule public IntentsTestRule<TopicListActivity> activityRule = new IntentsTestRule<>(TopicListActivity.class);

    @SuppressWarnings("unchecked")
    @BeforeClass
    public static void setUpClass() {
        Desk desk = Desk.with(InstrumentationRegistry.getContext());
        desk.setTopicProvider(mockTopicProvider);
        desk.setContactUsConfig(new ContactUsPropertyConfig(getContext()) {
            @Override
            public boolean isWebFormEnabled() {
                return false;
            }
        });

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                mockedTopicResponse = getMockedTopicResponse();
                ((TopicCallbacks) invocation.getArguments()[1]).onTopicsLoaded(mockedTopicResponse.getEntriesAsList());
                return null;
            }
        }).when(mockTopicProvider).getTopics(anyInt(), Matchers.<TopicCallbacks>any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void clickingOnTopicOpensArticleListActivity() {
        onView(ViewMatchers.withId(R.id.topics)).check(matches(isDisplayed()));

        int pos = 0;
        // click on item in the ListView
        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(pos).perform(click());

        // verify that the toolbar title in the ArticleListActivity matches the selected Topic's name
        Topic topic = mockedTopicResponse.getEntriesAsList().get(pos);
        matchToolbarTitle(topic.getName());

        // verify that the ArticleListActivity was launched
        intended(hasComponent(ArticleListActivity.class.getName()));

        // verify that the ArticleListActivity is launched with the correct Intent extras
        intended(allOf(
                hasExtras(allOf(
                        hasEntry(equalTo(EXTRA_MODE), equalTo(MODE_TOPIC)),
                        hasEntry(equalTo(EXTRA_TOPIC), equalTo(topic)),
                        hasEntry(equalTo(EXTRA_THEME_RES_ID), equalTo(NO_THEME_RES_ID))))));
    }

//    @SuppressWarnings("unchecked")
//    @Test
//    public void searchOpensArticleListActivity() {
//        // click the search button and type "test"
//        onView(withId(R.id.search)).perform(click());
//        String query = "test";
//        onView(withId(R.id.search_src_text)).perform(typeText(query), pressKey(KEYCODE_ENTER));
//
//        // verify that the ArticleListActivity was launched
//        intended(hasComponent(ArticleListActivity.class.getName()));
//
//        // verify that the ArticleListActivity is launched with the correct Intent extras
//        intended(allOf(
//                hasExtras(allOf(
//                        hasEntry(equalTo(EXTRA_MODE), equalTo(MODE_SEARCH)),
//                        hasEntry(equalTo(EXTRA_QUERY), equalTo(query)),
//                        hasEntry(equalTo(EXTRA_THEME_RES_ID), equalTo(NO_THEME_RES_ID))))));
//    }
//
//    @Test
//    public void clickContactUsMenuLaunchesContactUsActivity() {
//        // click the contact us button
//        onView(withId(R.id.contact_us)).perform(click());
//
//        // verify that the ContactUsWebActivity was launched
//        intended(hasComponent(getContactUsComponentName()));
//    }
}
