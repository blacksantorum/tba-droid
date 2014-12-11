package com.tba.theboxingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import android.content.Intent;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class LoginActivity extends Activity implements Response.Listener<JSONObject>, Response.ErrorListener {

    public static final String PREFS_NAME = "TBAPref";

    // private RequestQueue mRequestQueue = RailsClient.getInstance(this).getRequestQueue();
    private TwitterLoginButton loginButton;
    private ProgressBar mProgressBar;

    private void ThrowTwitterError(TwitterException exception)
    {
        new AlertDialog.Builder(this).setTitle("Connection error").setMessage(exception.getLocalizedMessage())
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing;
                    }
                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_login);

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                JSONObject TBAuser = new JSONObject();
                try {
                    TBAuser.put("id", result.data.getUserId());
                    TBAuser.put("screen_name", result.data.getUserName());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                signInWithRails(TBAuser);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.i("Twitter fail", exception.getLocalizedMessage());
                ThrowTwitterError(exception);
            }
        });

        mProgressBar = ((ProgressBar) findViewById(R.id.progressBar));
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signInWithRails(JSONObject user)
    {
        final JSONObject finalUser = user;

        mProgressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(TBARequestFactory.LoginRequest(user,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the first 500 characters of the response string.
                mProgressBar.setVisibility(View.INVISIBLE);
                Log.i("Response","Response is: "+ response.toString());
                User.currentUser().updateWithLoginResponse(response);

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("Handle",User.currentUser().handle);
                editor.putString("Name",User.currentUser().name);
                editor.putString("ImgUrl",User.currentUser().profileImageUrl);
                editor.putString("TwitterId",User.currentUser().twitterId);
                editor.putString("SessionToken",User.currentUser().sessionToken);
                editor.putInt("Id",User.currentUser().id);
                editor.commit();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("login",true);
                setResult(RESULT_OK,returnIntent);
                finish();
            }
        }, this));
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        new AlertDialog.Builder(this).setTitle("Network error").setMessage("Sorry, but we could not log you in.")
                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing;
                    }
                }).show();
    }

    @Override
    public void onResponse(JSONObject response) {

    }

}
