package com.tba.theboxingapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.tba.theboxingapp.Adapters.TagCandidateListAdapter;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Model.UserActivityComment;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class AddCommentActivity extends Activity {

    int mFightId;
    ImageButton mBackButton;
    NetworkImageView mUserImageView;
    TextView mNameTextView;
    TextView mHandleTextView;
    Button mAddCommentButton;
    EditText mCommentContentTextView;
    ListView mTagCandidatesList;

    public void setTaggingMode(boolean taggingMode) {
        this.taggingMode = taggingMode;
        if (this.taggingMode) {
            Log.i("Tag","Tag on!");
            mTagCandidatesList.setVisibility(View.VISIBLE);
        } else {
            Log.i("Tag","Tag off!");
            mTagCandidatesList.setVisibility(View.INVISIBLE);
        }
    }

    boolean taggingMode;
    String partialCandidate = "";

    TagCandidateListAdapter mAdapter;

    RequestQueue mRequestQueue = TBAVolley.getInstance(this).getRequestQueue();
    ImageLoader mImageLoader = TBAVolley.getInstance(this).getImageLoader();


    List<User> allUsers = new ArrayList<User>();
    List<User> taggedUsers = new ArrayList<User>();

    @Override
    protected  void onResume() {
        super.onResume();
        mCommentContentTextView.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);

        getActionBar().hide();

        mBackButton = (ImageButton)findViewById(R.id.back_button);
        mUserImageView = (NetworkImageView)findViewById(R.id.add_comment_user_image);
        mNameTextView = (TextView)findViewById(R.id.add_comment_user_name);
        mHandleTextView = (TextView)findViewById(R.id.add_comment_user_screen_name);
        mAddCommentButton = (Button)findViewById(R.id.add_comment_button);


        mRequestQueue.add(TBARequestFactory.GetUsers(new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject userDict = (JSONObject) jsonArray.get(i);
                        User u = new User(userDict.getJSONObject("user"));
                        allUsers.add(u);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

                mAdapter = new TagCandidateListAdapter(getApplicationContext(), allUsers);
                mTagCandidatesList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                // mTagCandidatesList.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }));


        mAddCommentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String[] words = mCommentContentTextView.getText().toString().split(" ");

                for (String word : words) {
                    if (word.charAt(0) == '@') {
                        for (User user : allUsers) {
                            if (word.substring(1) == user.handle) {
                                taggedUsers.add(user);
                                break;
                            }
                        }
                    }
                }

                JSONObject[] tagged = new JSONObject[taggedUsers.size()];

                for (int i = 0; i < taggedUsers.size(); i++) {
                    tagged[i] = new JSONObject();
                    try {
                        tagged[i].put("id", String.valueOf(taggedUsers.get(i).id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mRequestQueue.add(TBARequestFactory.PostCommentRequest(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                    }
                }, mFightId, tagged, mCommentContentTextView.getText().toString(), new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }));
            }
        });

        mCommentContentTextView = (EditText)findViewById(R.id.add_comment_edit_text);
        mCommentContentTextView.addTextChangedListener(new AddCommentTextWatcher());

        mTagCandidatesList = (ListView)findViewById(R.id.tag_users);
        mTagCandidatesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                
            }
        });

        mNameTextView.setText(User.currentUser().getName());

        mUserImageView.setImageUrl(User.currentUser().profileImageUrl,
                mImageLoader);

        mHandleTextView.setText("@" + User.currentUser().getHandle());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_comment, menu);
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

    public class AddCommentTextWatcher implements TextWatcher {

        char deletedCharacter;
        String priorText;

        public void afterTextChanged (Editable s) {
           //  Log.i("After text changed", String.valueOf(priorText));
            if (s.toString().length() > 0) {
                mAddCommentButton.setEnabled(true);
            } else {
                mAddCommentButton.setEnabled(false);
            }
        }

        public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            priorText = s.toString();
           //  Log.i("Before text changed", "Called!");
            // Log.i("Before prior text", String.valueOf(priorText));

            if (after == 0 && count == 1) {
                deletedCharacter = s.charAt(start);
            }
            // Log.i("Text watch", "S: " + s + ", Start: " + String.valueOf(start) + ", After: " + String.valueOf(after) + ", Count: " + String.valueOf(count));
        }

        public void onTextChanged (CharSequence s, int start, int before, int count) {

            Log.i("onTextChanged Prior text", String.valueOf(priorText));
            Log.i("S", String.valueOf(s));

            // Log.i("On text changed", "Called!");

            if (priorText.length() < s.toString().length()) {
                // Log.i("Added character", String.valueOf(s.charAt(start)));
                if (count == 1) { // Inserting one character
                    if (!taggingMode && s.charAt(start) == '@') {
                        setTaggingMode(true);
                    } else if (taggingMode) {
                        if (s.charAt(start) == ' ') {
                            setTaggingMode(false);
                        } else {
                            partialCandidate += s.charAt(start);
                        }
                    }
                }
            } else if (priorText.length() > s.length()) {
                // Log.i("Deleted character", String.valueOf(deletedCharacter));
                if (deletedCharacter == '@' && partialCandidate.length() == 0) {
                    setTaggingMode(false);
                } else {
                    partialCandidate = partialCandidate.substring(0,partialCandidate.length() - 1);
                }
            }
        }
    }
}
