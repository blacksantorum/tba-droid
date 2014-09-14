package com.tba.theboxingapp.Model;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by christibbs on 9/14/14.
 */
public class Place {
    public int id;
    public String name;
    public String address;
    public String city;
    public String zip;
    public String country;
    public Location location;
    public String phoneNumber;

    public Place(JSONObject object)
    {
        try {
            this.id = object.getInt("id");
            this.name = object.getString("name");

            double latitude = object.getDouble("latitude");
            double longitude = object.getDouble("longitude");

            this.location = new Location("");
            this.location.setLatitude(latitude);
            this.location.setLongitude(longitude);

            this.address = object.getString("address");
            this.city = object.getString("city");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //<editor-fold desc="Accessors">
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    //</editor-fold>
}
