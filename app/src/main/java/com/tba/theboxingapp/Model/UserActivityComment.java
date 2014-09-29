package com.tba.theboxingapp.Model;

import org.json.JSONObject;

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

    }
}
