package com.tba.theboxingapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
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

    TaggedUserAdapter mAdapter;

    RequestQueue mRequestQueue = TBAVolley.getInstance(this).getRequestQueue();
    ImageLoader mImageLoader = TBAVolley.getInstance(this).getImageLoader();


    List<User> allUsers = new ArrayList<User>();
    List<User> taggedUsers = new ArrayList<User>();

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
                User[] allUserArray = new User[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject userDict = (JSONObject) jsonArray.get(i);
                        User u = new User(userDict.getJSONObject("user"));
                        allUsers.add(u);
                        allUserArray[i] = u;
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }

                mAdapter = new TaggedUserAdapter(getApplicationContext(), allUserArray);
                mTagCandidatesList.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                mTagCandidatesList.setVisibility(View.VISIBLE);
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
        mTagCandidatesList = (ListView)findViewById(R.id.tag_users);

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


    public class TaggedUserAdapter extends ArrayAdapter<User> {
        private final Context context;
        public User[] users;

        public TaggedUserAdapter(Context context, User[] users){
            super(context, R.layout.tagged_user_cell, users);
            this.context = context;
            this.users = users;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            User u = users[position];

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.tagged_user_cell, parent, false);

            NetworkImageView mTagCandidateImageView = (NetworkImageView)v.findViewById(R.id.tag_users_image_view);
            TextView mTagCandidateNameLabel = (TextView)v.findViewById(R.id.tag_users_name_label);
            TextView mTagCandidateHandleLabel = (TextView)v.findViewById(R.id.tag_users_handle_label);

            mTagCandidateImageView.setImageUrl(u.profileImageUrl, mImageLoader);
            mTagCandidateNameLabel.setText(u.getName());
            mTagCandidateHandleLabel.setText("@" + u.getHandle());

            return v;
        }
    }
}
