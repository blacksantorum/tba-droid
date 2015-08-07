package com.tba.theboxingapp.Model;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

import java.util.Arrays;

/**
 * Created by christibbs on 9/13/14.
 */
public class User extends BaseModel {
    public String handle;
    public String name;
    public String profileImageUrl;
    public String twitterId;
    public String sessionToken;
    public boolean isLoggedIn;

    private static User current;

    public static void clear()
    {
        current = null;
    }

    private User() {

    }

    public static User currentUser()
    {
        if (current == null) {
            current = new User();
        }
        return  current;
    }

    public void updateWithSharedPreferences(SharedPreferences preferences)
    {
        this.handle = preferences.getString("Handle","");
        this.name = preferences.getString("Name","");
        this.profileImageUrl = preferences.getString("ImgUrl","");
        this.twitterId = preferences.getString("TwitterId","");
        this.sessionToken =  preferences.getString("SessionToken","");
        this.id = preferences.getInt("Id",0);
        this.currentUser().isLoggedIn = true;
    }

    public void updateWithLoginResponse(JSONObject object)
    {
        try {
            JSONObject userObject = object.getJSONObject("user");
            this.id = userObject.getInt("id");
            this.handle = userObject.getString("screen_name");
            this.sessionToken = userObject.getString("session_token");
            this.profileImageUrl = userObject.getString("img");
            this.name = userObject.getString("name");
            this.isLoggedIn = true;
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public User(JSONObject object)
    {
        try {
           id = object.getInt("id");
           profileImageUrl = object.getString("img");
           handle = object.getString("screen_name");
           name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + id;
        result = 31 * result + (handle == null ? 0 : handle.hashCode());
        result = 31 * result + (name == null ? 0 : name.hashCode());
        result = 31 * result + (profileImageUrl == null ? 0 : profileImageUrl.hashCode());
        result = 31 * result + (twitterId == null ? 0 : twitterId.hashCode());
        result = 31 * result + (sessionToken == null ? 0 : sessionToken.hashCode());

        return result;
    }

    //<editor-fold desc="Accessors">
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
    //</editor-fold>
   
}
