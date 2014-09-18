package com.tba.theboxingapp.Model;

/**
 * Created by christibbs on 9/16/14.
 */
public class Prediction {
    public int fightId;
    public int winnerId;
    public boolean stoppage;
    public User user;

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
