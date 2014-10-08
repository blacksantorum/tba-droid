package com.tba.theboxingapp;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.internal.de;
import com.tba.theboxingapp.Model.Boxer;
import com.tba.theboxingapp.Model.Comment;
import com.tba.theboxingapp.Model.Fight;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.format.DateUtils;

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

    public enum CommentMode { SHOW_TOP, SHOW_NEW};

    private static final String FIGHT_PARAM = "fight_param";
    private static final String BOXER_A_PARAM = "boxer_a_param";
    private static final String BOXER_B_PARAM = "boxer_b_param";

    private int mFightId;
    private String mBoxerAName;
    private String mBoxerBName;

    private Fight mFight;

    private Handler mPercentageCountHandler;

    private TextView mBoxerAPercentageLabel;
    private TextView mBoxerANameLabel;

    private TextView mBoxerBPercentageLabel;
    private TextView mBoxerBNameLabel;

   // private View mShadowView;

    private TextView mWeightClassLabel;
    private ListView mCommentsListView;
    private CommentArrayAdapter mCommentArrayAdapter;

    private RelativeLayout mCommentsLayout;
    private NetworkImageView mCommentToolbarImageView;
    private EditText mAddCommentEditText;
    private ImageButton mSendCommentButton;

    private Button mChangeSortLabel;

    private ProgressBar mCommentsProgressBar;
    private TextView mCommentsLoadingTextView;

    private RequestQueue mRequestQueue;

    private List<Comment> mComments;
    private CommentMode mCommentMode;

    private boolean shouldUpdatePickNumbers = true;

    private int mBoxerAPickPercentage;
    private int mBoxerBPickPercentage;

    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static FightDetailFragment newInstance(Fight fight) {
        FightDetailFragment fragment = new FightDetailFragment();
        Bundle args = new Bundle();
        args.putInt(FIGHT_PARAM,fight.id);
        args.putString(BOXER_A_PARAM, fight.boxerA.fullName);
        args.putString(BOXER_B_PARAM, fight.boxerB.fullName);
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

            Log.i("fight_id", "Fight id " + mFightId);
            mRequestQueue = TBAVolley.getInstance(getActivity()).getRequestQueue();
            /*mRequestQueue.add(TBARequestFactory.FightRequest(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject object) {
                    Log.i("fight",object.toString());
                }
            },mFightId));
            */
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

        if (mCommentMode == CommentMode.SHOW_TOP) {
            Collections.sort(mComments, new LikesComparator());
        } else {
            Collections.sort(mComments, new DateComparator());
        }

        Comment[] commentArray = new Comment[mComments.size()];
        for (int i = 0; i < mComments.size(); i ++) {
            commentArray[i] = mComments.get(i);
        }

        // if (mCommentArrayAdapter == null) {
            mCommentArrayAdapter = new CommentArrayAdapter(getActivity(),commentArray);
            mCommentsListView.setAdapter(mCommentArrayAdapter);
       //  }
        mCommentArrayAdapter.comments = commentArray;
        mCommentArrayAdapter.notifyDataSetChanged();
        mCommentsLayout.setVisibility(View.VISIBLE);
        mCommentsProgressBar.setVisibility(View.INVISIBLE);
        mCommentsLoadingTextView.setVisibility(View.INVISIBLE);
    }

    public void showCommentPage()
    {
        Log.i("EditText","Edit text tapped!");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fight_detail, container, false);
        mBoxerANameLabel = (TextView)v.findViewById(R.id.boxerANameLabel);
        mBoxerANameLabel.setText(mBoxerAName);
        mBoxerBNameLabel = (TextView)v.findViewById(R.id.boxerBNameLabel);
        mBoxerBNameLabel.setText(mBoxerBName);
        mBoxerAPercentageLabel = (TextView)v.findViewById(R.id.boxerAPickPercentageLabel);
        mBoxerAPercentageLabel.setText("0%");
        mBoxerBPercentageLabel = (TextView)v.findViewById(R.id.boxerBPickPercentageLabel);
        mBoxerBPercentageLabel.setText("0%");
        mWeightClassLabel = (TextView)v.findViewById(R.id.weightClassLabel);
        // mShadowView = (View)v.findViewById(R.id.addCommentShadow);

        mCommentsProgressBar = (ProgressBar)v.findViewById(R.id.loadCommentsProgress);
        mCommentsLoadingTextView = (TextView)v.findViewById(R.id.loadCommentsTextView);

        mCommentToolbarImageView = (NetworkImageView)v.findViewById(R.id.commentToolbarUserImageView);
        mCommentToolbarImageView.setImageUrl(User.currentUser().profileImageUrl,
                TBAVolley.getInstance(getActivity()).getImageLoader());

        mSendCommentButton = (ImageButton)v.findViewById(R.id.sendCommentButton);

        mSendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAddCommentEditText.getText().length() > 0) {
                    TBAVolley.getInstance(getActivity()).getRequestQueue().add(
                        TBARequestFactory.PostCommentRequest(new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject object) {
                                mAddCommentEditText.setText("");
                                mAddCommentEditText.clearFocus();
                                fetchComments(false);
                                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(mAddCommentEditText.getWindowToken(), 0);
                            }
                        },mFightId,mAddCommentEditText.getText().toString(), (TBAActivity)getActivity())
                    );
                }
            }
        });

        mCommentsListView = (ListView)v.findViewById(R.id.comments_list_view);
        mCommentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Comments","Clicked");
                Comment comment = mCommentArrayAdapter.comments[i];
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, UserDetailFragment.newInstance(comment.user.id, comment.user.name, comment.user.profileImageUrl)).
                        addToBackStack(null).commit();
            }
        });


        //mCommentsListView.setEmptyView(v.findViewById(R.id.emptyView));
        mAddCommentEditText = (EditText)v.findViewById(R.id.addACommentEditTextView);
        /*
        mAddCommentEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCommentPage();
            }
        });
        */
        /*
        mAddCommentEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    mShadowView.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        mAddCommentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    mShadowView.setVisibility(View.VISIBLE);
                } else {
                    mShadowView.setVisibility(View.INVISIBLE);
                }
            }
        });
        */

        mCommentsLayout = (RelativeLayout)v.findViewById(R.id.commentsLayout);

        mChangeSortLabel = (Button)v.findViewById(R.id.changeSortLabel);
        mChangeSortLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMode();
            }
        });

        fetchFight();
        return v;
    }

    private void updatePickPercentages() {
        Thread th = new Thread(new Runnable() {
            int i = 0;
            public void run() {
                while (i <= Math.max(mBoxerAPickPercentage, mBoxerBPickPercentage) && shouldUpdatePickNumbers) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (i <= mBoxerAPickPercentage) {
                                mBoxerAPercentageLabel.setText(i+"%");
                            }
                            if(i <= mBoxerBPickPercentage) {
                                mBoxerBPercentageLabel.setText(i+"%");
                            }
                        }
                    });
                    i++;
                    try {
                        Thread.sleep(15);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        th.start();
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
                    // mBoxerANameLabel.setText(mFight.boxerA.fullName);
                    // mBoxerBNameLabel.setText(mFight.boxerB.fullName);
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
                    /*
                    int i = 0;

                    while (i <= Math.max(mBoxerAPickPercentage, mBoxerBPickPercentage)) {
                        if (i <= mBoxerAPickPercentage) {
                            mBoxerAPercentageLabel.setText(i+"%");
                        }
                        if(i <= mBoxerBPickPercentage) {
                            mBoxerBPercentageLabel.setText(i+"%");
                        }
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        i++;
                    }
                    */
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },mFightId, (TBAActivity)getActivity()));
    }



    private void toggleMode() {
        if (mCommentMode == CommentMode.SHOW_NEW) {
            mCommentMode = CommentMode.SHOW_TOP;
            mChangeSortLabel.setText("Show new");
        }
        else {
            mCommentMode = CommentMode.SHOW_NEW;
            mChangeSortLabel.setText("Show top");
        }
        fetchComments(true);
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
        mCommentsLoadingTextView.setVisibility(View.VISIBLE);
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
        public Comment[] comments;

        public CommentArrayAdapter(Context context, Comment[] comments) {
            super(context, R.layout.fight_comment_detail, comments );
            this.comments = comments;
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Comment comment = comments[position];
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.fight_comment_detail, parent, false);
            NetworkImageView userImageView = (NetworkImageView)v.findViewById(R.id.commentUserImageView);

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

            userImageView.setImageUrl(comment.user.profileImageUrl,
                    TBAVolley.getInstance(getActivity().getApplicationContext()).getImageLoader());
            userHandleLabel.setText(comment.user.handle);
            commentContentLabel.setText(comment.body);
            timeAgoLabel.setText(prettyTimeAgo(comment.createdAt));
            likesLabel.setText(new String("" +comment.likes));
            if (User.currentUser() == comment.user) {
                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.INVISIBLE);
            }

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
            long currentDateLong = currentDate.getTime();
            long oldDate = date.getTime();

            return PrettyTime.getTimeAgo(oldDate);
        }
    }
}
