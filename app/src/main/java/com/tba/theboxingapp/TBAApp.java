package com.tba.theboxingapp;

import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import com.parse.ParseTwitterUtils;
import com.parse.PushService;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Networking.TBAVolley;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by christibbs on 8/16/14.
 */
public class TBAApp extends  Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "GhMnf7prLg17Tluc4Yy1eZfgz";
    private static final String TWITTER_SECRET = "4lFAs1eP2uvyIh3c2CMUCi5VNOjPvKAHFRIPa8IvRF8uMtO9xa";

    public static final String PREFS_NAME = "TBAPref";

    @Override
    public void onCreate() {
        super.onCreate();

        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

        Fabric.with(this, new Twitter(authConfig), new Crashlytics());

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getInt("Id", 0) != 0) {
            User.currentUser().updateWithSharedPreferences(settings);
        }

        TBAVolley.getInstance(getApplicationContext());

        Parse.initialize(this, "37Wefeshbl6O27mkd3hsKwXCA3QhbOEdRDxvD0bk", "SLxLOpx4IinngjXa1q3IC80I9gLJ8RPZASfg7lHb");
        ParseTwitterUtils.initialize("5tq6ikua9WzjyvCfdahH9g", "EKk7fSHD49IkEQ2nOFG89X3XXNFTZ43xQJ0yzRpLuM");
        try {
            PushService.setDefaultPushCallback(this, TBAActivity.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
