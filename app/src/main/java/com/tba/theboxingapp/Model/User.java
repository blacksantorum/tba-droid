package com.tba.theboxingapp.Model;

import com.parse.ParseTwitterUtils;

import org.json.JSONException;
import org.json.JSONObject;

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
            this.profileImageUrl = userObject.getString("img");
            this.name = userObject.getString("name");
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
