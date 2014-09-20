package com.tba.theboxingapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.internal.de;
import com.tba.theboxingapp.Model.Comment;
import com.tba.theboxingapp.Model.Fight;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FightDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FightDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FightDetailFragment extends Fragment {
    private static final String FIGHT_PARAM = "fight_param";

    private int mFightId;
    private Fight mFight;
    private TextView mBoxerAPercentageLabel;
    private TextView mBoxerANameLabel;

    private TextView mBoxerBPercentageLabel;
    private TextView mBoxerBNameLabel;

    private TextView mWeightClassLabel;
    private ListView mCommentsListView;
    private CommentArrayAdapter mCommentArrayAdapter;

    private RequestQueue mRequestQueue;

    private List<Comment> mComments;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static FightDetailFragment newInstance(Fight fight) {
        FightDetailFragment fragment = new FightDetailFragment();
        Bundle args = new Bundle();
        args.putInt(FIGHT_PARAM,fight.id);
        fragment.setArguments(args);
        return fragment;
    }
    public FightDetailFragment() {
         mComments = new ArrayList<Comment>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mFightId = getArguments().getInt(FIGHT_PARAM);

            Log.i("fight_id", "Fight id " + mFightId);
            mRequestQueue = Volley.newRequestQueue(getActivity());
            mRequestQueue.add(TBARequestFactory.FightRequest(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    Log.i("fight",object.toString());
                }
            },mFightId));
            mRequestQueue.add(TBARequestFactory.CommentsRequest(new Response.Listener<JSONArray>() {
                   @Override
                   public void onResponse(JSONArray object) {
                       updateComments(object);
                   }
                },mFightId));
        }
    }

    private void updateComments(JSONArray object)
    {
        mComments.clear();
        for (int i = 0; i < object.length() ; i++) {
            try {
                mComments.add(new Comment(object.getJSONObject(i).getJSONObject("comment")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Comment[] commentArray = new Comment[mComments.size()];
        for (int i = 0; i < mComments.size(); i ++) {
            commentArray[i] = mComments.get(i);
        }

        if (mCommentArrayAdapter == null) {
            mCommentArrayAdapter = new CommentArrayAdapter(getActivity(),commentArray);
            mCommentsListView.setAdapter(mCommentArrayAdapter);
        }
        mCommentArrayAdapter.comments = commentArray;
        mCommentArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fight_detail, container, false);
        mBoxerANameLabel = (TextView)v.findViewById(R.id.boxerANameLabel);
        mBoxerBNameLabel = (TextView)v.findViewById(R.id.boxerBNameLabel);
        mBoxerAPercentageLabel = (TextView)v.findViewById(R.id.boxerAPickPercentageLabel);
        mBoxerBPercentageLabel = (TextView)v.findViewById(R.id.boxerBPickPercentageLabel);
        mWeightClassLabel = (TextView)v.findViewById(R.id.weightClassLabel);

        mCommentsListView = (ListView)v.findViewById(R.id.comments_list_view);
        return v;
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

    public class CommentArrayAdapter extends ArrayAdapter<Comment> {
        private final Context context;
        public Comment[] comments;
        private ImageLoader mImageLoader;
        private RequestQueue mRequestQueue;

        public CommentArrayAdapter(Context context, Comment[] comments) {
            super(context, R.layout.fight_comment_detail, comments );
            this.context = context;
        }

        private RequestQueue getRequestQueue () {
            if (mRequestQueue == null) {
                mRequestQueue = Volley.newRequestQueue(context);
            }
            return mRequestQueue;
        }

        private ImageLoader getImageLoader() {
            getRequestQueue();
            if (mImageLoader == null) {
                mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
            }
            return this.mImageLoader;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Comment comment = comments[position];
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.fight_comment_detail, parent, false);
            NetworkImageView userImageView = (NetworkImageView)v.findViewById(R.id.commentUserImageView);
            TextView userHandleLabel = (TextView)v.findViewById(R.id.commentUserHandleTextView);
            TextView userPickLabel = (TextView)v.findViewById(R.id.commentUserHandleTextView);
            TextView commentContentLabel = (TextView)v.findViewById(R.id.commentContentTextView);
            TextView timeAgoLabel = (TextView)v.findViewById(R.id.timeAgoLabel);
            ImageButton jabButton = (ImageButton)v.findViewById(R.id.jabButton);
            TextView likesLabel = (TextView)v.findViewById(R.id.likesLabel);
            TextView deleteButton = (TextView)v.findViewById(R.id.deleteButton);

            userImageView.setImageUrl(comment.user.profileImageUrl, getImageLoader());
            userHandleLabel.setText(comment.user.handle);
            commentContentLabel.setText(comment.body);
            timeAgoLabel.setText(prettyTimeAgo(comment.createdAt));
            likesLabel.setText(new String("" +comment.likes));
            if (User.currentUser() == comment.user) {
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.INVISIBLE);
            }

            return v;
        }

        private CharSequence prettyTimeAgo(Date date)
        {
            Date currentDate = new Date();
            long currentDateLong = currentDate.getTime();
            long oldDate = date.getTime();

            return DateUtils
                    .getRelativeTimeSpanString(oldDate, currentDateLong, 0);
        }
    }
}
