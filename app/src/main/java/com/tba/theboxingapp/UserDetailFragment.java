package com.tba.theboxingapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;
import com.tba.theboxingapp.Model.Comment;
import com.tba.theboxingapp.Model.Prediction;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Model.UserActivityComment;
import com.tba.theboxingapp.Model.UserActivityPrediction;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class UserDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_ID_PARAM = "user_id_param";
    private static final String USER_FULL_NAME_PARAM = "user_name_param";
    private static final String USER_PROFILE_URL_PARAM = "user_profile_url_param";

    // TODO: Rename and change types of parameters
    private int mUserId;
    private String mUserFullName;
    private String mUserProfileUrl;

    private boolean picksHasNext;
    private boolean commentsHasnext;

    private boolean isLoading;

    private int picksPage = 1;
    private int commentsPage = 1;

    private NetworkImageView mUserProfileImageView;
    private TextView mUserProfileNameTextView;
    private Button mUserPicksButton;
    private Button mUserCommentsButton;
    private ListView mUserActivityListView;

    private RequestQueue mRequestQueue;

    private ProgressBar mLoadActivityProgressBar;
    private TextView mLoadActivityTextView;

    private UserDetailCommentsAdapter mCommentsAdapter;
    private UserDetailPicksAdapter mPicksAdapter;

    private DisplayedActivity mDisplayedActivity;

    private OnFragmentInteractionListener mListener;

    private enum DisplayedActivity { DISPLAY_PICKS, DISPLAY_COMMENTS };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDetailFragment newInstance(int userId, String fullName, String profileUrl) {
        UserDetailFragment fragment = new UserDetailFragment();
        Bundle args = new Bundle();
        args.putInt(USER_ID_PARAM, userId);
        args.putString(USER_FULL_NAME_PARAM, fullName);
        args.putString(USER_PROFILE_URL_PARAM, profileUrl);
        fragment.setArguments(args);
        return fragment;
    }
    public UserDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getInt(USER_ID_PARAM);
            mUserFullName = getArguments().getString(USER_FULL_NAME_PARAM);
            mUserProfileUrl = getArguments().getString(USER_PROFILE_URL_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_detail, container, false);

        mUserProfileImageView = (NetworkImageView)v.findViewById(R.id.profileScreenImageView);
        mUserProfileImageView.setImageUrl(mUserProfileUrl, TBAVolley.getInstance(getActivity()).getImageLoader());
        mUserProfileNameTextView = (TextView)v.findViewById(R.id.profileScreenNameLabel);
        mUserProfileNameTextView.setText(mUserFullName);

        mUserCommentsButton = (Button)v.findViewById(R.id.userCommentsButton);
        mUserCommentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadComments();
            }
        });

        mUserPicksButton = (Button)v.findViewById(R.id.userPicksButton);
        mUserPicksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPicks();
            }
        });

        mLoadActivityProgressBar = (ProgressBar)v.findViewById(R.id.loadActivityProgress);
        mLoadActivityTextView = (TextView)v.findViewById(R.id.loadActivityTextView);

        mUserActivityListView = (ListView)v.findViewById(R.id.userActivityListView);
        mUserActivityListView.setVisibility(View.INVISIBLE);

        loadPicks();

        return v;
    }

    private void loadPicks()
    {
        mUserActivityListView.setVisibility(View.INVISIBLE);
        mLoadActivityProgressBar.setVisibility(View.VISIBLE);
        mLoadActivityTextView.setText("Loading picks...");
        mLoadActivityTextView.setVisibility(View.VISIBLE);
        mDisplayedActivity = DisplayedActivity.DISPLAY_PICKS;

        mRequestQueue.add(TBARequestFactory.UserPicksRequest(picksPage, mUserId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONArray array = null;
                try {
                    array = jsonObject.getJSONArray("picks");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                UserActivityPrediction[] predictions = new UserActivityPrediction[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    try {
                        predictions[i] = new UserActivityPrediction(array.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (mPicksAdapter == null) {
                    mPicksAdapter = new UserDetailPicksAdapter(getActivity(),predictions);
                } else {
                    mPicksAdapter.picks = predictions;
                }
                mUserActivityListView.setAdapter(mPicksAdapter);
                mPicksAdapter.notifyDataSetChanged();
                mLoadActivityProgressBar.setVisibility(View.INVISIBLE);
                mLoadActivityTextView.setVisibility(View.INVISIBLE);
                mUserActivityListView.setVisibility(View.VISIBLE);

                try {
                    picksHasNext = jsonObject.getBoolean("picks_count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, (TBAActivity)getActivity()));
    }

    private void loadComments()
    {
        mDisplayedActivity = DisplayedActivity.DISPLAY_COMMENTS;

        mUserActivityListView.setVisibility(View.INVISIBLE);
        mLoadActivityProgressBar.setVisibility(View.VISIBLE);
        mLoadActivityTextView.setText("Loading comments...");
        mLoadActivityTextView.setVisibility(View.VISIBLE);

        mRequestQueue.add(TBARequestFactory.UserCommentsRequest(commentsPage, mUserId, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                JSONArray array = null;
                try {
                    array = jsonObject.getJSONArray("comments");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("UserDetailComments", array.toString());
                UserActivityComment[] comments = new UserActivityComment[array.length()];

                for (int i = 0; i < array.length(); i++) {
                    try {
                        comments[i] = new UserActivityComment(array.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (mCommentsAdapter == null) {
                    mCommentsAdapter = new UserDetailCommentsAdapter(getActivity(), comments);
                } else {
                    mCommentsAdapter.comments = comments;
                }
                mUserActivityListView.setAdapter(mCommentsAdapter);
                mCommentsAdapter.notifyDataSetChanged();
                mLoadActivityProgressBar.setVisibility(View.INVISIBLE);
                mLoadActivityTextView.setVisibility(View.INVISIBLE);
                mUserActivityListView.setVisibility(View.VISIBLE);

                try {
                    commentsHasnext = jsonObject.getBoolean("comments_count");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, (TBAActivity)getActivity()));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mRequestQueue = TBAVolley.getInstance(getActivity()).getRequestQueue();
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public class UserDetailPicksAdapter extends ArrayAdapter<UserActivityPrediction> {
        private final Context context;
        public UserActivityPrediction[] picks;

        public UserDetailPicksAdapter(Context context, UserActivityPrediction[] picks) {
            super(context, R.layout.user_detail_picks_detail, picks);
            this.picks = picks;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserActivityPrediction prediction = picks[position];
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.user_detail_picks_detail, parent, false);
            NetworkImageView boxerImageView = (NetworkImageView)v.findViewById(R.id.userPredictionBoxerImageView);
            boxerImageView.setImageUrl(prediction.getBoxerImageUrl(),TBAVolley.getInstance(getActivity()).getImageLoader());

            TextView predictionTextView = (TextView)v.findViewById(R.id.userPredictionTextView);
            predictionTextView.setText(prediction.getContent());

            TextView predictionDateTextView = (TextView)v.findViewById(R.id.userPredictionTimestampLabel);
            predictionDateTextView.setText(prettyTimeAgo(prediction.createdAt));

            return v;
        }

        private String prettyTimeAgo(Date date)
        {
            Date currentDate = new Date();
            long currentDateLong = currentDate.getTime();
            long oldDate = date.getTime();

            return PrettyTime.getTimeAgo(oldDate);
        }
    }

    public class UserDetailCommentsAdapter extends ArrayAdapter<UserActivityComment> {
        private final Context context;
        public UserActivityComment[] comments;

        public UserDetailCommentsAdapter(Context context, UserActivityComment[] comments) {
            super(context, R.layout.user_detail_comments_detail, comments);
            this.comments = comments;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserActivityComment prediction = comments[position];
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.user_detail_comments_detail, parent, false);
            TextView fightTitleTextView = (TextView)v.findViewById(R.id.userDetailCommentFightTitleTextView);
            fightTitleTextView.setText(prediction.getFightTitle());

            TextView contentTextView = (TextView)v.findViewById(R.id.userDetailCommentContentTextView);
            contentTextView.setText(prediction.getContent());

            TextView commentDateTextView = (TextView)v.findViewById(R.id.userDetailCommentTimestampLabel);
            commentDateTextView.setText(prettyTimeAgo(prediction.createdAt));

            return v;
        }

        private String prettyTimeAgo(Date date)
        {
            Date currentDate = new Date();
            long currentDateLong = currentDate.getTime();
            long oldDate = date.getTime();

            return PrettyTime.getTimeAgo(oldDate);
        }
    }

}
