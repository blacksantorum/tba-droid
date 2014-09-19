package com.tba.theboxingapp.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Created by christibbs on 9/16/14.
 */
public class Comment extends BaseModel {
    public String body;
    public User user;
    public int[] taggedUsers;
    public int fightId;
    public boolean likedByCurrentUser;
    public int likes;
    public Prediction prediction;

    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        result = 31 * result + id;
        result = 31 * result +(body == null ? 0 : body.hashCode());
        result = 31 * Arrays.hashCode(taggedUsers);
        result = 31 * result + id;

        return result;
    }

    public Comment(JSONObject object)
    {
        try {
            id = object.getInt("id");
            body = object.getString("body");
            likedByCurrentUser = object.getBoolean("liked");
            likes = object.getInt("likes");

            String createdAtDateString = object.getString("created_at");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            this.createdAt = sdf.parse(createdAtDateString);

            prediction = new Prediction(object.getJSONObject("pick"));
            user = new User(object.getJSONObject("user"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //<editor-fold desc="Accessors">
    public int getFightId() {
        return fightId;
    }

    public void setFightId(int fightId) {
        this.fightId = fightId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int[] getTaggedUsers() {
        return taggedUsers;
    }

    public void setTaggedUsers(int[] taggedUsers) {
        this.taggedUsers = taggedUsers;
    }
    //</editor-fold>
}
