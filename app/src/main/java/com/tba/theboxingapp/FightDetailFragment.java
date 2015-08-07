package com.tba.theboxingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.internal.de;
import com.google.android.gms.internal.pu;
import com.pubnub.api.PnGcmMessage;
import com.pubnub.api.PnMessage;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.tba.theboxingapp.Model.Boxer;
import com.tba.theboxingapp.Model.Comment;
import com.tba.theboxingapp.Model.Fight;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.format.DateUtils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;


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

    private boolean mSaveView = false;
    private SoftReference<View> mViewReference;

    public enum CommentMode { SHOW_TOP, SHOW_NEW};

    private static final String FIGHT_PARAM = "fight_param";
    private static final String BOXER_A_PARAM = "boxer_a_param";
    private static final String BOXER_B_PARAM = "boxer_b_param";
    private static final String NOTIFIED_COMMENT_ID = "notified_comment_id";

    private int mFightId;
    private String mBoxerAName;
    private String mBoxerBName;

    private Fight mFight;

    private Handler mPercentageCountHandler;

    private TextView mBoxerAPercentageLabel;
    private TextView mBoxerANameLabel;

    private TextView mBoxerBPercentageLabel;
    private TextView mBoxerBNameLabel;

    private int mNotifiedCommentId;

    private TextView mWeightClassLabel;
    private ListView mCommentsListView;
    public CommentArrayAdapter mCommentArrayAdapter;

    private RelativeLayout mCommentsLayout;
    private NetworkImageView mCommentToolbarImageView;
    public EditText mAddCommentEditText;

    private ProgressBar mCommentsProgressBar;

    private RequestQueue mRequestQueue;

    private List<Comment> mComments;
    private CommentMode mCommentMode;

    private boolean shouldUpdatePickNumbers = true;

    private int mBoxerAPickPercentage;
    private int mBoxerBPickPercentage;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static FightDetailFragment newInstance(Fight fight, int notifiedCommentId) {
        FightDetailFragment fragment = new FightDetailFragment();
        Bundle args = new Bundle();
        args.putInt(FIGHT_PARAM,fight.id);
        args.putInt(NOTIFIED_COMMENT_ID, notifiedCommentId);

        if (fight.boxerA != null) {
            args.putString(BOXER_A_PARAM, fight.boxerA.fullName);
            args.putString(BOXER_B_PARAM, fight.boxerB.fullName);
        } else {
            args.putString(BOXER_A_PARAM, "");
            args.putString(BOXER_B_PARAM, "");
        }
        fragment.setArguments(args);
        return fragment;
    }
    public FightDetailFragment() {
        mComments = new ArrayList<Comment>();
        mCommentMode = CommentMode.SHOW_TOP;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mFightId = getArguments().getInt(FIGHT_PARAM);
            this.mBoxerAName = getArguments().getString(BOXER_A_PARAM);
            this.mBoxerBName = getArguments().getString(BOXER_B_PARAM);

            this.mNotifiedCommentId = getArguments().getInt(NOTIFIED_COMMENT_ID);
            mRequestQueue = TBAVolley.getInstance(getActivity()).getRequestQueue();
        }
    }

    private void updateComments(JSONArray object)
    {
        int notifiedLocation = -1;

        mComments.clear();
        for (int i = 0; i < object.length() ; i++) {
            try {
                Comment c = new Comment(object.getJSONObject(i).getJSONObject("comment"));
                mComments.add(c);

                if (c.id == mNotifiedCommentId) {
                    notifiedLocation = i;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (mCommentMode == CommentMode.SHOW_TOP) {
            Collections.sort(mComments, new LikesComparator());
        } else {
            Collections.sort(mComments, new DateComparator());
        }

        // if (mCommentArrayAdapter == null) {
            mCommentArrayAdapter = new CommentArrayAdapter(getActivity(),mComments);
            mCommentsListView.setAdapter(mCommentArrayAdapter);
       //  }
        mCommentArrayAdapter.comments = mComments;
        mCommentArrayAdapter.notifyDataSetChanged();

        if (notifiedLocation > 0) {
            mCommentsListView.smoothScrollToPosition(notifiedLocation);
        }

        mCommentsLayout.setVisibility(View.VISIBLE);
        mCommentsProgressBar.setVisibility(View.INVISIBLE);
    }

    public void showCommentPage()
    {
        Log.i("EditText","Edit text tapped!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = null;

        if (mSaveView) {
            if (mViewReference != null) {
                final View savedView = mViewReference.get();
                if (savedView != null) {
                    if (savedView.getParent() != null) {
                        ((ViewGroup) savedView.getParent()).removeView(savedView);
                        v = savedView;
                    } else {
                        v = savedView;
                    }
                }
            }
        } else {

            // Inflate the layout for this fragment
            v = inflater.inflate(R.layout.fragment_fight_detail, container, false);
            mViewReference = new SoftReference<View>(v);

            mBoxerANameLabel = (TextView) v.findViewById(R.id.boxerANameLabel);
            mBoxerANameLabel.setText(mBoxerAName);
            mBoxerBNameLabel = (TextView) v.findViewById(R.id.boxerBNameLabel);
            mBoxerBNameLabel.setText(mBoxerBName);
            mBoxerAPercentageLabel = (TextView) v.findViewById(R.id.boxerAPickPercentageLabel);
            mBoxerAPercentageLabel.setText("0%");
            mBoxerBPercentageLabel = (TextView) v.findViewById(R.id.boxerBPickPercentageLabel);
            mBoxerBPercentageLabel.setText("0%");
            mWeightClassLabel = (TextView) v.findViewById(R.id.weightClassLabel);
            // mShadowView = (View)v.findViewById(R.id.addCommentShadow);

            mCommentsProgressBar = (ProgressBar) v.findViewById(R.id.loadCommentsProgress);

            mCommentToolbarImageView = (NetworkImageView) v.findViewById(R.id.commentToolbarUserImageView);
            mCommentToolbarImageView.setImageUrl(User.currentUser().profileImageUrl,
                    TBAVolley.getInstance(getActivity()).getImageLoader());

            mCommentsListView = (ListView) v.findViewById(R.id.comments_list_view);
            mCommentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override

                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.i("Comments", "Clicked");
                    Comment comment = mCommentArrayAdapter.comments.get(i);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, UserDetailFragment.newInstance(comment.user.id, comment.user.name, comment.user.profileImageUrl)).
                            addToBackStack(null).commit();
                }
            });


            //mCommentsListView.setEmptyView(v.findViewById(R.id.emptyView));
            mAddCommentEditText = (EditText) v.findViewById(R.id.addACommentEditTextView);
            mAddCommentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (b) {
                        Intent intent = new Intent(getActivity(), AddCommentActivity.class);
                        intent.putExtra("FIGHT_ID", mFightId);
                        getActivity().startActivityForResult(intent, 2);
                        if (mAddCommentEditText.hasFocus()) {

                        }
                    }
                }
            });

            mCommentsLayout = (RelativeLayout) v.findViewById(R.id.commentsLayout);

            fetchFight();

            mSaveView = true;
        }
        return v;
    }

    private void updatePickPercentages() {
        mBoxerAPercentageLabel.setText(mBoxerAPickPercentage + "%");
        mBoxerBPercentageLabel.setText(mBoxerBPickPercentage + "%");
    }

    private void fetchFight()
    {
        mRequestQueue.add(TBARequestFactory.FightRequest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                Log.i("fight",object.toString());
                try {
                    JSONObject fightObject = object.getJSONObject("fight");
                    mFight = new Fight(fightObject);
                    mBoxerANameLabel.setText(mFight.boxerA.fullName);
                    mBoxerBNameLabel.setText(mFight.boxerB.fullName);
                    mWeightClassLabel.setText(mFight.weightClass);
                    JSONArray boxerArray = object.getJSONObject("fight").getJSONArray("boxers");
                    for (int i = 0; i < boxerArray.length() ; i++ ) {
                        JSONObject boxerObject = boxerArray.getJSONObject(i);
                        Boxer b = new Boxer(boxerObject);
                        if (b.equals(mFight.boxerA)) {
                            mBoxerAPickPercentage = boxerObject.getInt("percent_pick");
                        }
                        else if (b.equals(mFight.boxerB)) {
                            mBoxerBPickPercentage = boxerObject.getInt("percent_pick");
                        }
                    }
                    Log.i("percentageA", "" + mBoxerAPickPercentage);
                    Log.i("percentageB", "" + mBoxerBPickPercentage);
                    updatePickPercentages();
                    fetchComments(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },mFightId, (TBAActivity)getActivity()));
    }

    private void fetchComments(final boolean onMainQueue)
    {
        RequestQueue queue;

        if (onMainQueue) {
            queue = mRequestQueue;
        } else {
            queue = Volley.newRequestQueue(getActivity());
        }

        mCommentsLayout.setVisibility(View.INVISIBLE);
        mCommentsProgressBar.setVisibility(View.VISIBLE);
        queue.add(TBARequestFactory.CommentsRequest(new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray object) {
                if (!onMainQueue) {
                    Log.i("Recomment",object.toString());
                }
                updateComments(object);

            }
        },mFightId, (TBAActivity)getActivity()));
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void sendTestMessage()
    {
        PnGcmMessage gcmMessage = new PnGcmMessage();

        JSONObject jso = new JSONObject();
        try {
            jso.put("summary", "Game update 49ers touchdown");
            jso.put("lastplay", "5yd run up the middle");
        } catch (JSONException e) {

        }

        gcmMessage.setData(jso);

        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                System.out.println(response);
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                System.out.println(error);
            }
        };

        PnMessage message = null;

        Pubnub pubnub = TBAApp.pubnub;;

        message = new PnMessage(pubnub, User.currentUser().handle, callback, gcmMessage);
        /*
        if (message == null)
            message = new PnMessage(pubnub, User.currentUser().handle, callback);
        */
        try {
            message.publish();
        } catch (PubnubException e) {
            switch (e.getPubnubError().errorCode) {
                case PubnubError.PNERR_CHANNEL_MISSING:
                    System.out.println("Channel name not set");
                    break;
                case PubnubError.PNERR_CONNECTION_NOT_SET:
                    System.out.println("Pubnub object not set");
                    break;
            }

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // sendTestMessage();

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
        shouldUpdatePickNumbers = false;
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



    public class LikesComparator implements Comparator<Comment> {
        @Override
        public int compare(Comment o1, Comment o2) {
            return o2.likes - o1.likes;
        }
    }

    public class DateComparator implements Comparator<Comment> {
        @Override
        public int compare(Comment o1, Comment o2) {
            return o1.createdAt.compareTo(o2.createdAt);
        }
    }

    public class CommentArrayAdapter extends ArrayAdapter<Comment> {
        private final Context context;
        public List<Comment> comments;

        public CommentArrayAdapter(Context context, List<Comment> comments) {
            super(context, R.layout.fight_comment_detail, comments );
            this.comments = comments;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Comment comment = comments.get(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.fight_comment_detail, parent, false);
            NetworkImageView userImageView = (NetworkImageView)v.findViewById(R.id.commentUserImageView);

            if (mNotifiedCommentId == comment.id) {

                // getResources().getColor(R.color.tw__light_gray)
                v.setBackgroundColor(getResources().getColor(R.color.tw__light_gray));
                mNotifiedCommentId = 0;
            } else {
                v.setBackgroundColor(getResources().getColor(R.color.white));
            }

            userImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, UserDetailFragment.newInstance(comment.user.id, comment.user.name, comment.user.profileImageUrl)).
                            addToBackStack(null).commit();
                }
            });

            TextView userHandleLabel = (TextView)v.findViewById(R.id.commentUserHandleTextView);
            TextView userPickLabel = (TextView)v.findViewById(R.id.commentPickTextView);
            if (comment.prediction == null) {
                userPickLabel.setVisibility(View.INVISIBLE);
            }
            else {
                if (comment.prediction.winnerId == mFight.boxerA.id) {
                    userPickLabel.setText("picked " + mFight.boxerA.fullName);
                } else {
                    userPickLabel.setText("picked " + mFight.boxerB.fullName);
                }
                userPickLabel.setVisibility(View.VISIBLE);
            }

            TextView commentContentLabel = (TextView)v.findViewById(R.id.commentContentTextView);
            TextView timeAgoLabel = (TextView)v.findViewById(R.id.timeAgoLabel);
            ImageButton jabButton = (ImageButton)v.findViewById(R.id.jabButton);
            final TextView likesLabel = (TextView)v.findViewById(R.id.likesLabel);
            TextView deleteButton = (TextView)v.findViewById(R.id.deleteButton);

            if (comment.user.profileImageUrl != null) {
                userImageView.setImageUrl(comment.user.profileImageUrl,
                        TBAVolley.getInstance(getActivity().getApplicationContext()).getImageLoader());
            } else {
                userImageView.setImageDrawable(getResources().getDrawable(R.drawable.places));
            }


            userHandleLabel.setText(comment.user.handle);
            commentContentLabel.setText(comment.body);

            Log.i("Comment date in cell", comment.createdAt.toString());

            timeAgoLabel.setText(prettyTimeAgo(comment.createdAt));
            likesLabel.setText(new String("" +comment.likes));

            if (User.currentUser().id == comment.user.id) {
                deleteButton.setText("Delete");
            } else {
                deleteButton.setText("Reply");
            }

            deleteButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (User.currentUser().id == comment.user.id) {

                        new AlertDialog.Builder(getActivity()).setTitle("Confirm").setMessage("Are you sure you want to delete this comment?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mRequestQueue.add(TBARequestFactory.DeleteCommentRequest(comment.id, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        for (int i = 0; i < mCommentArrayAdapter.comments.size() ; i++) {
                                            Comment c = mCommentArrayAdapter.getItem(i);
                                            if (c.id == comment.id) {
                                                mCommentArrayAdapter.comments.remove(c);
                                                mCommentArrayAdapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        new AlertDialog.Builder(getActivity()).setTitle("Couldn't delete comment").setMessage(volleyError.getLocalizedMessage())
                                                .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        // Do nothing;
                                                    }
                                                }).show();
                                    }
                                }));
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Do nothing;
                            }
                        }).show();
                    } else {
                        Intent intent = new Intent(getActivity(), AddCommentActivity.class);
                        intent.putExtra("FIGHT_ID", mFightId);
                        intent.putExtra("TAGGED_USER", comment.user.handle);
                        getActivity().startActivityForResult(intent, 2);
                    }
                }
            });

            if (comment.likedByCurrentUser) {
                jabButton.setEnabled(false);
            }
            else {
                jabButton.setEnabled(true);
            }

            jabButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mRequestQueue.add(TBARequestFactory.LikeRequest(comment, new Response.Listener<String>() {
                        // private final TextView likesLabelCopy = likesLabel;

                        @Override
                        public void onResponse(String string) {
                            likesLabel.setText(new String("" + (comment.likes + 1)));
                            comment.likedByCurrentUser = true;
                        }
                    }, (TBAActivity)getActivity()));
                }
            });

            return v;
        }

        private String prettyTimeAgo(Date date)
        {
            Date currentDate = new Date();

            Log.i("Current date", currentDate.toString());

            long currentDateLong = currentDate.getTime();
            Log.i("Current date long", String.valueOf(currentDateLong));

            long oldDate = date.getTime();
            Log.i("Old date long", String.valueOf(oldDate));

            return PrettyTime.getTimeAgo(oldDate + 21600000);
        }
    }
}
