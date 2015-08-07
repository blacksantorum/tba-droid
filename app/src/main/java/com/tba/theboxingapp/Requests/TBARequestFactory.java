package com.tba.theboxingapp.Requests;

import android.location.GpsStatus;
import android.net.Uri;
import android.net.sip.SipSession;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.tba.theboxingapp.Model.Comment;
import com.tba.theboxingapp.Model.Notification;
import com.tba.theboxingapp.Model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.http.POST;

/**
 * Created by christibbs on 9/14/14.
 */

public class TBARequestFactory {

    private static final String BASE_URL = "http://www.theboxingapp.com/api/v2/";

    private static String withSessionToken(String url)
    {
        return url + "?session_token=" + User.currentUser().sessionToken;
    }

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
            (JSONObject user, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "signin";

        Log.i("url",url);

        return new JsonObjectRequest(url,user,listener,errorListener);
    }

    public static JsonArrayRequest PlacesRequest(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "places?session_token=";
        url += User.currentUser().sessionToken;

        Log.i("url",url);

        return new JsonArrayRequest(url,listener, errorListener);
    }

    public static JsonObjectRequest UserPicksRequest(
                                                   int page,
                                                   int userId,
                                                   Response.Listener<JSONObject> listener,
                                                   Response.ErrorListener errorListener)

    {
        String url = BASE_URL + "users/" + userId + "/picks";
        url = withSessionToken(url);
        url += "&page=" + String.valueOf(page);

        return new JsonObjectRequest(withSessionToken(url),null,listener, errorListener);
    }

    public static JsonObjectRequest UserCommentsRequest(
            int page,
            int userId,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener)

    {
        String url = BASE_URL + "users/" + userId + "/comments";
        url = withSessionToken(url);
        url += "&page=" + String.valueOf(page);

        return new JsonObjectRequest(withSessionToken(url),null,listener, errorListener);
    }

    public static JsonObjectRequest FightsRequest(
                                                 int page,
                                                 Response.Listener<JSONObject> listener,
                                                 boolean featured,
                                                 Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "fights/";

        if (featured) {
            url += "future";
        } else {
            url += "past";
        }

        url += "?session_token=" + User.currentUser().sessionToken;
        url += "&page=" + String.valueOf(page);

        Log.d("page",url);

        return new JsonObjectRequest(url,null,listener, errorListener);
    }

    /*
    public static JsonArrayRequest FightsRequest(Response.Listener<JSONArray> listener,
                                                 boolean featured, Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "fights/";

        if (featured) {
            url += "future";
        } else {
            url += "past";
        }

        url += "?session_token=" + User.currentUser().sessionToken;

        Log.i("url",url);

        return new JsonArrayRequest(url,listener, errorListener);
    }
    */

    public static JsonObjectRequest PostCommentRequest(Response.Listener<JSONObject> listener, int fightId, JSONArray tagged ,String comment,
                                                       Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "fights/" + fightId + "/comments";

        JSONObject params = new JSONObject();

        try {
            JSONObject commentObject = new JSONObject();
            commentObject.put("users", tagged);
            commentObject.put("body", comment);
            params.put("comment", commentObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("Comment obj", params.toString());

        return new JsonObjectRequest(TBARequestFactory.withSessionToken(url),params,listener, errorListener);
    }

    public static JsonObjectRequest PostCommentRequest(Response.Listener<JSONObject> listener, int fightId, String comment,
                                                       Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "fights/" + fightId + "/comments";

        JSONObject params = new JSONObject();

        JSONObject commentObject = new JSONObject();
        try {
            commentObject.put("body", comment);
            params.put("comment", commentObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JsonObjectRequest(TBARequestFactory.withSessionToken(url),params,listener, errorListener);
    }

    public static JsonObjectRequest FightRequest(Response.Listener<JSONObject> listener, int fightId, Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "fights/" + fightId + "?session_token=" + User.currentUser().sessionToken;

        return new JsonObjectRequest(url,null,listener, errorListener);
    }

    public static JsonArrayRequest CommentsRequest(Response.Listener<JSONArray> listener,
                                                   int fightId, Response.ErrorListener errorListener) {
        String url = BASE_URL + "fights/";

        url += fightId + "/comments?session_token=";
        url += User.currentUser().sessionToken;

        return new JsonArrayRequest(url,listener, errorListener);
    }

    public static StringRequest LikeRequest(Comment comment, Response.Listener<String> listener, Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "comments/" + comment.id + "/like";

        return new StringRequest(Request.Method.POST, withSessionToken(url),listener, errorListener);
    }

    public static JsonObjectRequest PickFightRequest(int fightId, int winnerId, Response.Listener<JSONObject> listener,
                                                     Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "fights/" + fightId + "/picks";

        JSONObject params = new JSONObject();
        JSONObject pick = new JSONObject();
        try {
            pick.put("winner_id", "" + winnerId);
            pick.put("ko", "false");
            params.put("pick", pick);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JsonObjectRequest(withSessionToken(url),params,listener, errorListener);
    }

    public static JsonArrayRequest UserCommentsRequest(Response.Listener<JSONArray> listener,
                                                   int userId, Response.ErrorListener errorListener) {
        String url = BASE_URL + "users/" + userId + "/comments";

        return new JsonArrayRequest(withSessionToken(url),listener, errorListener);
    }

    public static JsonArrayRequest UserPicksRequest(Response.Listener<JSONArray> listener,
                                                       int userId, Response.ErrorListener errorListener) {
        String url = BASE_URL + "users/" + userId + "/picks";

        return new JsonArrayRequest(withSessionToken(url),listener, errorListener);
    }

    public static JsonArrayRequest GetUsers(Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        String url = BASE_URL + "users";
        return new JsonArrayRequest(withSessionToken(url), listener, errorListener);
    }

    public static JsonObjectRequest FetchUnpickedFightsRequest(Response.Listener<JSONObject> listener,
                                                        Response.ErrorListener errorListener) {

        String url = BASE_URL + "unpicked_fights";
        return new JsonObjectRequest(Request.Method.GET, withSessionToken(url),null, listener, errorListener);

    }

    public static JsonObjectRequest NotificationRequest(int page, Response.Listener<JSONObject> listener,
                                                        Response.ErrorListener errorListener) {

        String url = BASE_URL + "users/" + User.currentUser().getId() + "/notifications";

        Log.i("Notifications url", url);

        return new JsonObjectRequest(Request.Method.GET, withSessionToken(url) + "&page=" +page ,null, listener, errorListener);

    }

    public static StringRequest DeleteCommentRequest(int commentId, Response.Listener<String> listener,
                                                     Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "comments/" + commentId;

        return new StringRequest(Request.Method.DELETE, withSessionToken(url), listener, errorListener);
    }

    public static JsonObjectRequest MarkNotificationsRequest(List<Notification> notifications,
                                                         Response.Listener<JSONObject> listener,
                                                         Response.ErrorListener errorListener)
    {
        String url = BASE_URL + "users/" + User.currentUser().getId() + "/notifications/seen";

        final JSONArray notifs = new JSONArray();

        for (int i = 0; i < notifications.size() ; i++) {
            Notification n = notifications.get(i);
            notifs.put(n.id);
        }

        JSONObject params = new JSONObject();
        try {
            params.put("notification_ids", notifs);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("Parameters", params.toString());

        return new JsonObjectRequest(Request.Method.POST,withSessionToken(url),params,listener, errorListener) {
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("notification_ids",notifs.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
    }
}
