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
    private Button mLoginButton;
    private ProgressBar mProgressBar;

    public void connectWithTwitter(View view)
    {
        mLoginButton.setEnabled(false);
        connect();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_login);

        mLoginButton = (Button)findViewById(R.id.login_button);
        mProgressBar = ((ProgressBar) findViewById(R.id.progressBar));
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.INVISIBLE);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String userString = settings.getString("User","");
        if (userString != "") {
            try {
                JSONObject userObject = new JSONObject(userString);
                signInWithRails(userObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        int width = mLoginButton.getWidth();
        int height = mLoginButton.getHeight();

        String size = String.format("On start Width is %d, height is %d",width,height);

        Log.i("button",size);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        int width = mLoginButton.getWidth();
        int height = mLoginButton.getHeight();

        String size = String.format("On resume Width is %d, height is %d",width,height);

        Log.i("button",size);
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
                // Save user

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("User",finalUser.toString());
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

    private void connect()
    {
        ParseTwitterUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    mLoginButton.setEnabled(true);
                    Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                } else {
                    JSONObject TBAuser = new JSONObject();
                    try {
                        TBAuser.put("id", ParseTwitterUtils.getTwitter().getUserId());
                        TBAuser.put("screen_name", ParseTwitterUtils.getTwitter().getScreenName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    signInWithRails(TBAuser);
                    // new MakeTwitterRequestTask().execute();
                }
            }
        });
    }

    private class MakeTwitterRequestTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet verifyGet = new HttpGet(
                        "https://api.twitter.com/1.1/account/verify_credentials.json");
                ParseTwitterUtils.getTwitter().signRequest(verifyGet);

                HttpResponse response = client.execute(verifyGet);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                return reader.readLine();
                // return new JSONObject(tokener);
            } catch (IOException e) {
                e.printStackTrace();
                mLoginButton.setEnabled(true);
                return "Connect with Twitter failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONTokener tokener = new JSONTokener(result);
                JSONObject object = new JSONObject(tokener);
                Log.d("TwitterLogin",object.toString());
                mLoginButton.setEnabled(true);
            }catch (JSONException e) {
                mLoginButton.setEnabled(true);
                e.printStackTrace();
            }
        }

    }

}
