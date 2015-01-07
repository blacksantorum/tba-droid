package com.tba.theboxingapp.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by jackmiddlebrook on 12/20/14.
 */
public class Notification extends BaseModel {

    public int commentId;
    public String msg;
    public boolean seen;
    public int fightId;
    public String imgUrl;

    public Notification(JSONObject object) {

        try {
            commentId = object.getInt("comment_id");
            id = object.getInt("id");
            String createdAtDateString = object.getString("created_at");

            createdAtDateString = createdAtDateString.substring(0, createdAtDateString.length() - 5);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            createdAt = sdf.parse(createdAtDateString);

            msg = object.getString("message");
            seen = object.getBoolean("seen");
            fightId = object.getInt("fight_id");
            imgUrl = object.getString("commentator_img_url");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }
}