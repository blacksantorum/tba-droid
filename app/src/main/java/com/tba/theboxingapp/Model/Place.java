package com.tba.theboxingapp.Model;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by christibbs on 9/14/14.
 */
public class Place extends BaseModel {
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

    @Override public int hashCode() {
        // Start with a non-zero constant.
        int result = 17;

        result = 31 * result + id;
        result = 31 * result +(name == null ? 0 : name.hashCode());
        result = 31 * result +(address == null ? 0 : address.hashCode());
        result = 31 * result +(city == null ? 0 : city.hashCode());
        result = 31 * result +(zip == null ? 0 : zip.hashCode());
        result = 31 * result +(country == null ? 0 : country.hashCode());
        result = 31 * result +(location == null ? 0 : location.hashCode());
        result = 31 * result +(phoneNumber == null ? 0 : phoneNumber.hashCode());

        return result;
    }

    //<editor-fold desc="Accessors">

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
