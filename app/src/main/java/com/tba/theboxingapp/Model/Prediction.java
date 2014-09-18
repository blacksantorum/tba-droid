package com.tba.theboxingapp.Model;

/**
 * Created by christibbs on 9/16/14.
 */
public class Prediction extends BaseModel {
    public int fightId;
    public int winnerId;
    public boolean stoppage;
    public User user;

    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + id;
        result = 31 * result + winnerId;
        result = 31 * result + (stoppage ? 1 : 0);
        result = 31 * result + (user == null ? 0 : user.hashCode());

        return result;
    }

    //<editor-fold desc="Accessors">
    public int getFightId() {
        return fightId;
    }

    public void setFightId(int fightId) {
        this.fightId = fightId;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public boolean isStoppage() {
        return stoppage;
    }

    public void setStoppage(boolean stoppage) {
        this.stoppage = stoppage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    //</editor-fold>
}
