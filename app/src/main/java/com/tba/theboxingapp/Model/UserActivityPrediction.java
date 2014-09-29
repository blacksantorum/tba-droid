package com.tba.theboxingapp.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by christibbs on 9/28/14.
 */
public class UserActivityPrediction extends BaseModel {
    public String getBoxerImageUrl() {
        return boxerImageUrl;
    }

    public void setBoxerImageUrl(String boxerImageUrl) {
        this.boxerImageUrl = boxerImageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String boxerImageUrl;
    String content;

    public UserActivityPrediction(JSONObject object)
    {
        try {
            this.boxerImageUrl = object.getString("img_winner");
            this.content = object.getString("text");
            String createdAtDateString = object.getString("created_at");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
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
