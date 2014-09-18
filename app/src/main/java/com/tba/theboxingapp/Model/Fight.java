package com.tba.theboxingapp.Model;

import java.util.Date;

/**
 * Created by christibbs on 9/16/14.
 */
public class Fight {
    public enum State {
        UPCOMING, IN_PROGRESS, PAST
    }

    public Boxer boxerA;
    public Boxer boxerB;
    public Date date;
    public State state;
    public int winnerId;
    public boolean stoppage;
    public String weightClass;
    public int rounds;
    public String location;

    //<editor-fold desc="Accessors">
    public Boxer getBoxerA() {
        return boxerA;
    }

    public void setBoxerA(Boxer boxerA) {
        this.boxerA = boxerA;
    }

    public Boxer getBoxerB() {
        return boxerB;
    }

    public void setBoxerB(Boxer boxerB) {
        this.boxerB = boxerB;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
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

    public String getWeightClass() {
        return weightClass;
    }

    public void setWeightClass(String weightClass) {
        this.weightClass = weightClass;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    //</editor-fold>
}
