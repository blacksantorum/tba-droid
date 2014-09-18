package com.tba.theboxingapp.Model;

/**
 * Created by christibbs on 9/16/14.
 */
public class Boxer extends BaseModel {
    public String firstName;
    public String lastName;
    public String photoUrl;

    //<editor-fold desc="Accessors">
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

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
    //</editor-fold>
}
