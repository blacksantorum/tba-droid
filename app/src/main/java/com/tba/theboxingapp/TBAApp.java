package com.tba.theboxingapp;

import com.parse.Parse;
import com.parse.ParseTwitterUtils;
import com.parse.PushService;
import com.tba.theboxingapp.Networking.TBAVolley;

import android.app.Activity;
import android.app.Application;

/**
 * Created by christibbs on 8/16/14.
 */
public class TBAApp extends  Application {

    @Override
    public void onCreate() {
        super.onCreate();

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
