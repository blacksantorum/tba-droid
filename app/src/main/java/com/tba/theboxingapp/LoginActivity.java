package com.tba.theboxingapp;

import android.app.Activity;
import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    // private RequestQueue mRequestQueue = RailsClient.getInstance(this).getRequestQueue();
    private Button mLoginButton;

    private String runThroughSuperSecretHash(String screenname)
    {
        Map <String,String> map = new HashMap<String, String>();
        map.put("f","9");map.put("x","k");map.put("k","1");map.put("o","5");map.put("m","f");
        map.put("w","x");map.put("u","d");map.put("b","w");map.put("z","7");map.put("a","r");
        map.put("v","2");map.put("i","v");map.put("y","z");map.put("e","u");map.put("c","h");
        map.put("d","t");map.put("h","s");map.put("q","i");map.put("j","g");map.put("p","e");
        map.put("r","p");map.put("s","6");map.put("g","o");map.put("t","q");map.put("n","a");
        map.put("l","b");map.put("0","3");map.put("1","4");map.put("2","m");map.put("3","j");
        map.put("4","8");map.put("5","c");map.put("6","0");map.put("7","y");map.put("8","n");
        map.put("9","l");map.put("_","_");

        String superSecretHashed = new String();

        for (int i = 0; i < screenname.length(); i++) {
            String character = new String();
            character += screenname.charAt(i);
            superSecretHashed += map.get(character);
        }
        return superSecretHashed;
    }

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

    private void signInWithRails()
    {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://www.theboxingapp.com/api/signin";

        JSONObject user = new JSONObject();
        try {
            user.put("id",ParseTwitterUtils.getTwitter().getUserId());
            user.put("screen_name", ParseTwitterUtils.getTwitter().getScreenName());
            user.put("password",
                    runThroughSuperSecretHash(ParseTwitterUtils.getTwitter().getScreenName()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(url, user, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Display the first 500 characters of the response string.
                Log.i("Response","Response is: "+ response.toString());
                User.currentUser().updateWithLoginResponse(response);
                // Save user
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error", "Response is: " + error.toString());
            }
        });
        queue.add(request);
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
                    signInWithRails();
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
