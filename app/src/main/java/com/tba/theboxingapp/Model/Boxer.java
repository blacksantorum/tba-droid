package com.tba.theboxingapp.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by christibbs on 9/16/14.
 */
public class Boxer extends BaseModel {
    public String fullName;
    public String imgUrl;

    public Boxer(JSONObject object) {
        try {
            this.id = object.getInt("id");
            this.fullName = object.getString("full_name");
            this.imgUrl = object.getString("img_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        result = 31 * result + id;
        result = 31 * result +(fullName == null ? 0 : fullName.hashCode());
        result = 31 * result +(imgUrl == null ? 0 : imgUrl.hashCode());

        return result;
    }

    //<editor-fold desc="Accessors">
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhotoUrl() {
        return imgUrl;
    }

    public void setPhotoUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    //</editor-fold>
}
