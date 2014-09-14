package com.tba.theboxingapp.Requests;

import android.location.GpsStatus;
import android.net.sip.SipSession;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tba.theboxingapp.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by christibbs on 9/14/14.
 */
public class TBARequestFactory {

    private static JSONObject authObject()
    {
        JSONObject auth = new JSONObject();
        try {
            auth.put("session_token", User.currentUser().sessionToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return auth;
    }

    public static JsonObjectRequest LoginRequest
            (JSONObject user, Response.Listener<JSONObject> listener, Response.ErrorListener errorHandler)
    {
        String url ="http://www.theboxingapp.com/api/signin";
        return new JsonObjectRequest(url,user,listener, errorHandler);
    }

    public static JsonObjectRequest PlacesRequest(Response.Listener<JSONObject> listener, Response.ErrorListener errorHandler)
    {
        String url ="http://www.theboxingapp.com/api/places";

        return new JsonObjectRequest(url,TBARequestFactory.authObject(),listener, errorHandler);
    }

}
