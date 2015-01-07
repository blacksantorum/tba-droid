package com.tba.theboxingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.tba.theboxingapp.Model.Fight;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FightListFragment extends Fragment implements Response.ErrorListener {

    private boolean mSaveView = false;
    private SoftReference<View> mViewReference;

    private static final String LOGIN_TYPE_PARAM = "login_type_param";

    public enum ListType { FEATURED, PAST };

    private ListType mListType;
    private FightListAdapter mFightListAdapter;
    private RequestQueue mRequestQueue;
    private ExpandableListView mExpandableListView;
    private ProgressBar mLoadingFightsProgress;

    private View mFooterView;

    private int page = 1;
    private boolean hasNext = true;

    private boolean isLoading = false;

    private void setLoading(boolean loading) {
        isLoading = loading;

        if (isLoading) {
            mFooterView.setVisibility(View.VISIBLE);
        } else {
            mFooterView.setVisibility(View.INVISIBLE);
        }
    }

    private OnFragmentInteractionListener mListener;

    public static FightListFragment newInstance(ListType listType) {
        FightListFragment fragment = new FightListFragment();
        Bundle args = new Bundle();
        args.putSerializable(LOGIN_TYPE_PARAM,listType);
        fragment.setArguments(args);
        return fragment;
    }
    public FightListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mListType = (ListType)getArguments().getSerializable(LOGIN_TYPE_PARAM);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setFightListAdapter()
    {
        mFightListAdapter = new FightListAdapter(getActivity());
        mExpandableListView.setAdapter(mFightListAdapter);

        mRequestQueue = TBAVolley.getInstance(getActivity()).getRequestQueue();

        loadFights();
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

            Log.e("View", "Creating view!");

            // Inflate the layout for this fragment
            v = inflater.inflate(R.layout.fragment_fight_list, container, false);
            mViewReference = new SoftReference<View>(v);

            mExpandableListView = (ExpandableListView) v.findViewById(R.id.fight_list);

            mFooterView = inflater.inflate(R.layout.bottom_spinner_layout, null, false);
            mExpandableListView.addFooterView(mFooterView);

            mExpandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int first, int visible, int total) {
                    float scrollPercentage = ((float) first + (float) visible) / (float) total;

                    if (scrollPercentage > 0.6 && hasNext & !isLoading) {
                        loadFights();
                    }
                }
            });

            mLoadingFightsProgress = (ProgressBar) v.findViewById(R.id.loadFightsProgress);

            mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                    return true;
                }
            });

            if (mFightListAdapter == null) {
                setFightListAdapter();
            }

            mSaveView = true;
        }

        Log.e("View", "There's a view!");

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

    private void loadFights()
    {
        if (page == 1) {
            mExpandableListView.setVisibility(View.INVISIBLE);
            mLoadingFightsProgress.setVisibility(View.VISIBLE);
        }

        boolean featured = (mListType == ListType.FEATURED);

        setLoading(true);

        mRequestQueue.add(TBARequestFactory.FightsRequest(page++, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                int currentFights = 0;
                if (mFightListAdapter != null) {
                    if (mFightListAdapter.fights != null) {
                        Date[] dates =  mFightListAdapter.fights.keySet().toArray(new Date[mFightListAdapter.fights.size()]);
                        for (int i = 0; i < dates.length; i++) {
                            currentFights += mFightListAdapter.fights.get(dates[i]).size();
                        }
                    }
                }

                try {
                    JSONArray fights = jsonObject.getJSONArray("fights");
                    int fightsCount = jsonObject.getInt("fights_count");

                    hasNext = currentFights + fights.length() < fightsCount;

                    for (int i = 0; i < fights.length(); i++) {
                        try {
                            JSONObject fightObject = fights.getJSONObject(i);
                            Fight f = new Fight(fightObject);
                            // Fight f = new Fight(fightObject.getJSONObject("fight"));
                            if (!mFightListAdapter.dates.contains(f.date)) {
                                mFightListAdapter.dates.add(f.date);
                                List<Fight> fightsForDate = new ArrayList<Fight>();
                                fightsForDate.add(f);
                                mFightListAdapter.fights.put(f.date,fightsForDate);
                            }
                            else {
                                List<Fight> fightsForDate = mFightListAdapter.fights.get(f.date);
                                fightsForDate.add(f);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    for(int i=0; i < mFightListAdapter.getGroupCount(); i++) {
                        mExpandableListView.expandGroup(i);
                    }
                    mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                                                                    @Override
                                                                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                                                                        Date date = mFightListAdapter.dates.get(groupPosition);
                                                                        Fight fight = mFightListAdapter.fights.get(date).get(childPosition);
                                                                        FragmentManager fragmentManager = getFragmentManager();


                                                                        fragmentManager.beginTransaction()
                                                                                .replace(R.id.container, FightDetailFragment.newInstance(fight), "FIGHT_DETAIL").
                                                                                addToBackStack(null).commit();
                                                                        /*
                                                                        fragmentManager.beginTransaction()
                                                                                .add(FightDetailFragment.newInstance(fight), "Detail")
                                                                                .addToBackStack(null).commit();
                                                                                */

                                                                        return true;
                                                                    }
                                                                }
                    );

                    if (page == 2) {
                        mExpandableListView.setVisibility(View.VISIBLE);
                        mLoadingFightsProgress.setVisibility(View.INVISIBLE);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setLoading(false);
            }
        }, featured, this));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        setLoading(false);
        if (User.currentUser().isLoggedIn) {
            // Log.i("Error", volleyError.getLocalizedMessage());
            new AlertDialog.Builder(getActivity()).setTitle("Network error").setMessage("Sorry, but your request failed")
                    .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do nothing;
                        }
                    }).show();
        }
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

    public class FightListAdapter extends BaseExpandableListAdapter {

        public List<Date> dates;
        public Map<Date,List<Fight>> fights;

        private Context mContext;

        public FightListAdapter(Context context) {
            mContext = context;
            dates = new ArrayList<Date>();
            fights = new HashMap<Date,List<Fight>>();
        }

        @Override
        public int getGroupCount() {
            return dates.size();
        }

        @Override
        public int getChildrenCount(int i) {
           return fights.get(dates.get(i)).size();
        }

        @Override
        public Object getGroup(int i) {
            return dates.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return fights.get(dates.get(i)).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.fight_list_header, viewGroup, false);
                TextView textView = (TextView) rowView.findViewById(R.id.dateTextView);
                textView.setText(headerDateString(dates.get(i)));
                return rowView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
                final Fight fight = fights.get(dates.get(i)).get(i1);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.fight_list_child, viewGroup, false);
                NetworkImageView boxerAImageView = (NetworkImageView) rowView.findViewById(R.id.boxerAImageView);
                boxerAImageView.setImageUrl(fight.boxerA.imgUrl,
                        TBAVolley.getInstance(getActivity()).getImageLoader());

                final TextView boxerATextView = (TextView) rowView.findViewById(R.id.boxerATextView);
                boxerATextView.setText(fight.boxerA.fullName);
                NetworkImageView boxerBImageView = (NetworkImageView) rowView.findViewById(R.id.boxerBImageView);
                boxerBImageView.setImageUrl(fight.boxerB.imgUrl,
                        TBAVolley.getInstance(getActivity()).getImageLoader());
                final TextView boxerBTextView = (TextView) rowView.findViewById(R.id.boxerBTextView);
                boxerBTextView.setText(fight.boxerB.fullName);

                ImageView commentsIcon = (ImageView)rowView.findViewById(R.id.commentsIcon);
                TextView commentsCountTextView = (TextView)rowView.findViewById(R.id.commentsCountTextView);

                Log.i("Fight comment count", String.valueOf(fight.commentCount));

                if (fight.commentCount > 0) {
                    commentsIcon.setVisibility(View.VISIBLE);
                    commentsCountTextView.setVisibility(View.VISIBLE);
                    commentsCountTextView.setText(String.valueOf(fight.commentCount));
                } else {
                    commentsIcon.setVisibility(View.INVISIBLE);
                    commentsCountTextView.setVisibility(View.INVISIBLE);
                }

                TextView resultTextView = (TextView) rowView.findViewById(R.id.resultTextView);
                if (fight.state == Fight.State.PAST) {
                    if (fight.winnerId != 0) {
                        if (fight.stoppage) {
                            resultTextView.setText("KO");
                        } else {
                            resultTextView.setText("def");
                        }
                    } else {
                        resultTextView.setText("drew");
                    }
                }

                final Button pickButton = (Button) rowView.findViewById(R.id.pickButton);
                final SeekBar pickBar = (SeekBar) rowView.findViewById(R.id.pickBar);

                if (fight.state == Fight.State.UPCOMING) {

                    if (fight.currentUserPickedWinnerId == fight.boxerA.id) {
                        pickButton.setText("Change your pick?");
                        pickBar.setProgress(0);
                        boxerATextView.setTypeface(Typeface.DEFAULT_BOLD);
                        boxerBTextView.setTypeface(Typeface.DEFAULT);
                    } else if (fight.currentUserPickedWinnerId == fight.boxerB.id) {
                        pickBar.setProgress(100);
                        pickButton.setText("Change your pick?");
                        boxerBTextView.setTypeface(Typeface.DEFAULT_BOLD);
                        boxerATextView.setTypeface(Typeface.DEFAULT);
                    } else {
                        pickBar.setProgress(50);
                        boxerATextView.setTypeface(Typeface.DEFAULT);
                        boxerBTextView.setTypeface(Typeface.DEFAULT);
                    }

                    pickButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pickButton.setVisibility(View.INVISIBLE);
                            pickBar.setVisibility(View.VISIBLE);
                        }
                    });

                    pickBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(final SeekBar seekBar, int i, boolean b) {
                            if (i == 0) {
                                seekBar.setEnabled(false);
                                mRequestQueue.add(TBARequestFactory.PickFightRequest(fight.id, fight.boxerA.id, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject object) {
                                        fight.currentUserPickedWinnerId = fight.boxerA.id;
                                        seekBar.setEnabled(true);
                                        notifyDataSetChanged();
                                    }
                                }, (TBAActivity) getActivity()));
                            }

                            if (i == 100) {
                                seekBar.setEnabled(false);
                                mRequestQueue.add(TBARequestFactory.PickFightRequest(fight.id, fight.boxerB.id, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject object) {
                                        fight.currentUserPickedWinnerId = fight.boxerB.id;
                                        seekBar.setEnabled(true);
                                        notifyDataSetChanged();
                                    }
                                }, (TBAActivity) getActivity()));
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                } else {
                    pickBar.setVisibility(View.GONE);
                    pickButton.setVisibility(View.GONE);
                }

                return rowView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

        private String headerDateString(Date date) {
            DateFormat df = new SimpleDateFormat("MMMM d");
            return df.format(date);
        }
    }

}
