package com.tba.theboxingapp.Model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by christibbs on 9/16/14.
 */
public class Fight extends BaseModel {
    public enum State {
        UPCOMING, IN_PROGRESS, PAST
    }

    public Boxer boxerA;
    public Boxer boxerB;
    public Date date;
    public State state;
    public int winnerId;
    public int currentUserPickedWinnerId;
    public boolean stoppage;
    public String weightClass;
    public int rounds;
    public String location;

    public int commentCount;

    public Fight (int fightId)
    {
        id = fightId;
    }

    public Fight (JSONObject object) {
        Log.v("Fight", object.toString());
        try {
            this.id = object.getInt("id");
            this.location = object.getString("location");
            this.winnerId = object.getInt("winner_id");

            if (object.has("comments_count")) {
                if (!object.isNull("comments_count")) {
                    this.commentCount = object.getInt("comments_count");
                }
            }

            if (object.has("users_pick")) {
                if (!object.isNull("users_pick")) {
                    this.currentUserPickedWinnerId = object.getInt("users_pick");
                }
            }

            if (object.has("weight")) {
                if (!object.isNull("weight")) {
                    this.weightClass = object.getString("weight");
                }
            }

            String dateString = object.getString("date");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            this.date = sdf.parse(dateString);
            /*
            if (object.get("users_pick") != null) {
                this.currentUserPickedWinnerId = object.getInt("users_pick");
            } */

            JSONArray boxersArray = object.getJSONArray("boxers");

            boxerA = new Boxer(boxersArray.getJSONObject(0));
            boxerB = new Boxer(boxersArray.getJSONObject(1));

            if (winnerId >= 0) {
                this.state = State.PAST;
                if (winnerId > 0) {
                    this.stoppage = object.getBoolean("stoppage");
                    if (winnerId == boxerB.id) {
                        Boxer tmp = boxerA;
                        boxerA = boxerB;
                        boxerB = tmp;
                    }
                }
            }
            else if (winnerId == -1){
                this.state = State.IN_PROGRESS;
            } else {
                this.state = State.UPCOMING;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        // Include a hash for each field.
        result = 31 * result + id;
        result = 31 * result + (boxerA == null ? 0 : boxerA.hashCode());
        result = 31 * result + (boxerB == null ? 0 : boxerB.hashCode());
        result = 31 * result + (date == null ? 0 : date.hashCode());
        result = 31 * result + state.hashCode();
        result = 31 * result + winnerId;
        result = 31 * result + currentUserPickedWinnerId;
        result = 31 * result + (stoppage ? 1 : 0);
        result = 31 * result + (weightClass == null ? 0 : weightClass.hashCode());
        result = 31 * result + rounds;
        result = 31 * result + (location == null ? 0 : location.hashCode());

        return result;
    }

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
