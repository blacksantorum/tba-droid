package com.tba.theboxingapp.Model;

/**
 * Created by christibbs on 9/16/14.
 */
public class Comment extends BaseModel {
    public String content;
    public User user;
    public int[] taggedUsers;
    public int fightId;

    //<editor-fold desc="Accessors">
    public int getFightId() {
        return fightId;
    }

    public void setFightId(int fightId) {
        this.fightId = fightId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
