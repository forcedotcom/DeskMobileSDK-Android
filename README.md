# Introduction
The DeskKit Android SDK is an easy way to get customer support into your app. With just a few lines of code you will be able incorporate your Desk site's support center and allow your users to submit tickets / create cases natively within your app.

## Features
Currently, the DeskKit Android SDK supports the following features:
* Support Center - View and search support topics & articles
* Contact Us - Submit feedback or issues (create cases)
* Multiple Brands - Filter topics & articles by brand and provide brand specific customizations

# Getting Started

## Minimum Requirements
The DeskKit Android SDK supports Android API level 16 (Jelly Bean, 4.1) and up.

## Installation
[![Download](https://api.bintray.com/packages/desk/public/sdk/images/download.svg)](https://bintray.com/desk/public/sdk/_latestVersion)

Place code in your `build.gradle`

```gradle
repositories {
  jcenter()
}

dependencies {
  compile 'com.desk:sdk:1.3.1-SNAPSHOT'
}
```

## Configuration
The main class you will interface with in the SDK is the `Desk` class. You can get the `Desk` singleton by calling `Desk.with(Context context)`. In order for the `Desk` class to work properly you will first need to configure it to work with your site and authenticate with the Desk.com API.

To configure the SDK so it can communicate with the Desk.com API you will first need an API token. You can obtain an API token in your site’s Admin console by visiting the Settings > API page. You can set up an API application, and then click on the link for “Your Mobile SDK Token” to obtain the token.

Once you have obtained your API token you can configure the `Desk` instance for your site statically, via a properties file or programmatically in Java code.
* Properties based configuration
    1. Create a `desk.properties` file in your app's `assets` directory.
    2. Add the `desk.api.token` & `desk.hostname` properties as follows:

        ```
        desk.api.token = <your_api_token>
        desk.hostname = <your_site_hostname>
        # example hostname: mysite.desk.com
        ```
* Java based configuration
    1. Create a `DeskConfig` object and call `setConfig(DeskConfig config)` on your `Desk` instance, passing your `DeskConfig` object, as follows:

    ```
    Desk.with(getApplicationContext()).setConfig(new DeskConfig() {
            @Override
            public String getApiToken() {
                return "your_api_token";
            }

            @Override
            public String getHostname() {
                // ex: mysite.desk.com
                return "your_site_hostname";
            }
        });
    ```

You can configure your `Desk` instance wherever you see fit, we recommend you do so in your `Application` class or your main `Activity`.

Once you have completed one of the options above your `Desk` instance will be ready to communicate with the Desk.com API.

## Basic Usage
Once the above configuration is completed there are only two more things to do to get the Support Center & Contact Us running within your app. First you will need to define the following activities in the `<application>` section of your `AndroidManifest.xml` file, like this:
```
<application>
	...
	<activity
	    android:name="com.desk.android.sdk.activity.TopicListActivity"
	    android:label="@string/topics_activity_title"/>
	<activity
	    android:name="com.desk.android.sdk.activity.ArticleListActivity"/>
	<activity
	    android:name="com.desk.android.sdk.activity.ArticleActivity"/>
	<activity
	    android:name="com.desk.android.sdk.activity.ContactUsActivity"
	    android:label="@string/contact_us_form_activity_title"/>
    ...
</application>
```
If you would like to use the Contact Us web form instead of the native form, you will also need to declare the following activity in your manifest (more on how to use this later):
```
<activity
    android:name="com.desk.android.sdk.activity.ContactUsWebActivity"
    android:label="@string/contact_us_form_activity_title"/>
```
`TopicListActivity`, `ContactUsActivity`, and `ContactUsWebActivity` all support an optional `android:label` which will be used as the activity's title in the ActionBar. The title can also be set programmatically when launching the activity. `ArticleListActivity` will use the topic name or search query as the title, and `ArticleActivity` will use the article subject as the title.

Once you've defined the activities in your manifest, you will need to launch the `TopicListActivity` however you see fit in your app, like this:
```
TopicListActivity.start(this);
```
Launching `TopicListActivity` will display a list of topics for the user to choose from. Once they select a topic, `ArticleListActivity` will be launched and they will see a list of articles within the topic. Selecting an article will launch `ArticleActivity` which will display the article for them to read.

Your users' device locale will be used to determine which language the topics & articles will be translated to as long as your Support Center supports the language.

The user can also search across all articles while looking at topics within the `TopicListActivity`, or search articles within a topic after they have selected a topic and are looking at articles in the `ArticleListActivity`.

Finally, a Contact Us help icon will be available in the ActionBar for the user to tap on if they need additional help. Selecting this will launch the `ContactUsActivity` which displays a native form for the user to leave feedback or submit an issue, which will in return create a case within Desk. The form will ask the user for an *optional* name, *required* email address, and *required* message. Tapping the submit ActionBar icon will create a case within Desk and return the user to the activity they were at prior to launching the `ContactUsActivity`.

This is all you need to do to get a basic Support Center and Contact Us option within your app.

## Sample Apps
If you would like to see the SDK in action you can check out the sample apps in the `basic/` & `multi-brand/` folders. The samples will show you the flow between each activity as described above, as well as give you examples on some of the more advanced topics like contact us configuration, custom styles & themes, and supporting multiple brands.

## Advanced Topics
The SDK also supports further configuration and customization such as enabling and disabling various features, theming, multiple brands and more. To find out how to further customize the SDK please refer to the [Wiki][1].

[1]: https://github.com/forcedotcom/DeskMobileSDK-Android/wiki
