package com.tba.theboxingapp;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.parse.Parse;
import com.parse.ParseTwitterUtils;
import com.parse.PushService;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Networking.TBAVolley;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.io.IOException;

import io.fabric.sdk.android.Fabric;

/**
 * Created by christibbs on 8/16/14.
 */
public class TBAApp extends  Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "GhMnf7prLg17Tluc4Yy1eZfgz";
    private static final String TWITTER_SECRET = "4lFAs1eP2uvyIh3c2CMUCi5VNOjPvKAHFRIPa8IvRF8uMtO9xa";

    private static final String PUBLISH_KEY = "pub-c-873f5905-5d05-4a92-9829-3ae6e2fa9273";
    private static final String SUBSCRIBE_KEY = "sub-c-2d21be14-6570-11e4-90a5-02ee2ddab7fe";
    private static final String SENDER_ID = "605438060906";
    private static String REG_ID = "";

    private static final Pubnub pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY, null, null, false);
    private static GoogleCloudMessaging gcm;

    public static final String PREFS_NAME = "TBAPref";

    private void registerPush()
    {
        pubnub.setAuthKey("AIzaSyDZoWfec-4ZVjEIk0rHIU8AUgThluemiRI");

        pubnub.setCacheBusting(false);
        pubnub.setOrigin("pubsub.pubnub.com");

        registerInBackground();
    }

    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    REG_ID = gcm.register(SENDER_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + REG_ID);
                    msg = "Device registered, registration ID=" + REG_ID;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i("PubNub", "Registered with GCM Server." + msg);
                try {
                    pubnub.subscribe(User.currentUser().handle, new Callback() {
                        @Override
                        public void successCallback(String channel, Object message) {
                            super.successCallback(channel, message);
                            Log.i("PubNub", message.toString());
                            pubnub.enablePushNotificationsOnChannel(User.currentUser().getHandle(), REG_ID, new Callback() {
                                @Override
                                public void successCallback(String channel, Object message) {
                                    super.successCallback(channel, message);
                                    Log.i("PubNub", message.toString());
                                }

                                @Override
                                public void errorCallback(String channel, PubnubError error) {
                                    super.errorCallback(channel, error);
                                    Log.i("PubNub", error.toString());
                                }
                            });
                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            super.errorCallback(channel, error);
                            Log.i("PubNub", error.toString());
                        }
                    });
                } catch (PubnubException e) {
                    e.printStackTrace();
                }

            }
        }.execute(null, null, null);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

        Fabric.with(this, new Twitter(authConfig), new Crashlytics());

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getInt("Id", 0) != 0) {
            User.currentUser().updateWithSharedPreferences(settings);
            registerPush();
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
