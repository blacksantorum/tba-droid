package com.tba.theboxingapp.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by christibbs on 9/16/14.
 */
public class Prediction extends BaseModel {
    public int fightId;
    public int winnerId;
    public boolean stoppage;
    // ?
    public User user;
    public int userId;

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

    public Prediction (JSONObject object)
    {
        try {
            id = object.getInt("id");
            stoppage = object.getBoolean("ko");
            fightId = object.getInt("fight_id");
            userId = object.getInt("user_id");

            String updatedAtDateString = object.getString("updated_at");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            this.updatedAt = sdf.parse(updatedAtDateString);

            winnerId = object.getInt("winner_id");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
