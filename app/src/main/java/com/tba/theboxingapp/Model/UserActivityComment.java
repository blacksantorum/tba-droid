package com.tba.theboxingapp.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by christibbs on 9/28/14.
 */
public class UserActivityComment extends BaseModel {
    public String getFightTitle() {
        return fightTitle;
    }

    public void setFightTitle(String fightTitle) {
        this.fightTitle = fightTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String fightTitle;
    String content;

    public UserActivityComment (JSONObject object)
    {
        try {
            this.content = object.getString("body");
            this.fightTitle = object.getString("fight_name");
            String createdAtDateString = object.getString("created_at");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(Calendar.getInstance().getTimeZone());
            try {
                this.createdAt = sdf.parse(createdAtDateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
