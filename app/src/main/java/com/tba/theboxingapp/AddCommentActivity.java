package com.tba.theboxingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

        mCommentContentTextView.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(mCommentContentTextView, 0);
            }
        },200);
    }

    private void ThrowVolleyError(VolleyError error)
    {
        new AlertDialog.Builder(this).setTitle("Network error").setMessage(error.getLocalizedMessage())
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

        mFightId =  getIntent().getIntExtra("FIGHT_ID", 0);

        setContentView(R.layout.activity_add_comment);

        getActionBar().hide();

        mBackButton = (ImageButton)findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity(null);
                InputMethodManager keyboard = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });

        mUserImageView = (NetworkImageView)findViewById(R.id.add_comment_user_image);
        mNameTextView = (TextView)findViewById(R.id.add_comment_user_name);
        mHandleTextView = (TextView)findViewById(R.id.add_comment_user_screen_name);
        mAddCommentButton = (Button)findViewById(R.id.add_comment_button);

        /*
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
        */


        mAddCommentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mAddCommentButton.setEnabled(false);
                /*
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
                */
                JSONObject[] tagged = new JSONObject[0];
                /*
                for (int i = 0; i < taggedUsers.size(); i++) {
                    tagged[i] = new JSONObject();
                    try {
                        tagged[i].put("id", String.valueOf(taggedUsers.get(i).id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                */

                mRequestQueue.add(TBARequestFactory.PostCommentRequest(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.i("Created comment obj", jsonObject.toString());
                        try {
                            finishActivity(jsonObject.getJSONObject("comment"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, mFightId, null, mCommentContentTextView.getText().toString(), new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        ThrowVolleyError(volleyError);
                        mAddCommentButton.setEnabled(true);
                    }
                }));
            }
        });

        mCommentContentTextView = (EditText)findViewById(R.id.add_comment_edit_text);
        mCommentContentTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mCommentContentTextView.getText().toString().isEmpty()) {
                    mAddCommentButton.setEnabled(false);
                } else {
                    mAddCommentButton.setEnabled(true);
                }
            }
        });

        mTagCandidatesList = (ListView)findViewById(R.id.tag_users);
        mTagCandidatesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        mNameTextView.setText(User.currentUser().name);

        mUserImageView.setImageUrl(User.currentUser().profileImageUrl,
                mImageLoader);

        mHandleTextView.setText("@" + User.currentUser().handle);
    }

    private void finishActivity(JSONObject object)
    {
        Intent returnIntent = new Intent();

        if (object != null) {
            returnIntent.putExtra("comment", object.toString());
            setResult(RESULT_OK, returnIntent);
        } else {
            setResult(RESULT_CANCELED, returnIntent);
        }
        finish();
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
}
