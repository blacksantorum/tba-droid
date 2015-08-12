package com.tba.theboxingapp.Model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by christibbs on 9/16/14.
 */
public class Boxer extends BaseModel {
    private String fullName;
    private String firstName;
    private String lastName;
    private String imgId;
    private String imgUrl;
    private int wins;
    private int losses;
    private int draws;
    private int knockouts;
    private String weightClass;
    private String heightAndWeight;
    private String birthdate;
    private String nationality;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImgId() {
        return imgId;
    }

    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getKnockouts() {
        return knockouts;
    }

    public void setKnockouts(int knockouts) {
        this.knockouts = knockouts;
    }

    public String getWeightClass() {
        return weightClass;
    }

    public void setWeightClass(String weightClass) {
        this.weightClass = weightClass;
    }

    public String getHeightAndWeight() {
        return heightAndWeight;
    }

    public void setHeightAndWeight(String heightAndWeight) {
        this.heightAndWeight = heightAndWeight;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    //</editor-fold>
}
