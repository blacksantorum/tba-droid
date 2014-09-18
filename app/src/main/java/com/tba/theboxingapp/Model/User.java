package com.tba.theboxingapp.Model;

import com.parse.ParseTwitterUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by christibbs on 9/13/14.
 */
public class User extends BaseModel {
    public String handle;
    public String name;
    public String profileImageUrl;
    public String twitterId;
    public String sessionToken;

    private static User current;

    private User() {

    }

    public static User currentUser()
    {
        if (current == null) {
            current = new User();
        }
        return  current;
    }

    public void updateWithLoginResponse(JSONObject object)
    {
        try {
            JSONObject userObject = object.getJSONObject("user");
            this.id = userObject.getInt("id");
            this.handle = ParseTwitterUtils.getTwitter().getScreenName();
            this.sessionToken = userObject.getString("session_token");
            this.twitterId = ParseTwitterUtils.getTwitter().getUserId();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public User(JSONObject object)
    {

    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }
   
}
