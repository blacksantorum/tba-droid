package com.tba.theboxingapp;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tba.theboxingapp.Adapters.NotificationListAdapter;
import com.tba.theboxingapp.Model.Fight;
import com.tba.theboxingapp.Model.Notification;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment implements Response.ErrorListener {

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */

    private RequestQueue mRequestQueue;

    private ListView mListView;
    private ProgressBar mLoadingNotificationsProgress;

    private boolean mSaveView = false;
    private SoftReference<View> mViewReference;

    private View mFooterView;

    private int page = 1;
    private boolean hasNext = true;
    private boolean isLoading = false;

    private List<Notification> unreadNotifications = new ArrayList<Notification>();

    private NotificationListAdapter mNotificationsAdapter;

    private void setLoading(boolean loading) {
        isLoading = loading;

        if (isLoading) {
            mFooterView.setVisibility(View.VISIBLE);
        } else {
            mFooterView.setVisibility(View.INVISIBLE);
        }
    }

    private void setNotificationListAdapter()
    {
        mNotificationsAdapter = new NotificationListAdapter(getActivity(), new ArrayList<Notification>());

        mListView.setAdapter(mNotificationsAdapter);

        mRequestQueue = TBAVolley.getInstance(getActivity()).getRequestQueue();

        loadNotifications();
    }

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = null;

        if (mSaveView) {
            if (mViewReference != null) {
                final View savedView = mViewReference.get();
                if (savedView != null) {
                    if (savedView.getParent() != null) {
                        ((ViewGroup) savedView.getParent()).removeView(savedView);

                        Log.e("View", "Loading saved view!");

                        v = savedView;
                    } else {
                        Log.e("View", "Loading saved view!");

                        v = savedView;
                    }
                }
            }
        } else {

            Log.e("View", "Creating view!");

            v = inflater.inflate(R.layout.fragment_notification, container, false);
            mViewReference = new SoftReference<View>(v);

            mLoadingNotificationsProgress = (ProgressBar) v.findViewById(R.id.loadNotificationProgress);
            mListView = (ListView) v.findViewById(R.id.notificationsList);

            mFooterView = inflater.inflate(R.layout.bottom_spinner_layout, null, false);
            mListView.addFooterView(mFooterView);

            mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {

                }

                @Override
                public void onScroll(AbsListView absListView, int first, int visible, int total) {
                    float scrollPercentage = ((float) first + (float) visible) / (float) total;

                    if (scrollPercentage > 0.6 && hasNext & !isLoading) {
                        loadNotifications();
                    }
                }
            });

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Notification n = mNotificationsAdapter.notifications.get(position);

                    FragmentManager fragmentManager = getFragmentManager();

                    fragmentManager.beginTransaction()
                            .replace(R.id.container, FightDetailFragment.newInstance(new Fight(n.fightId), n.commentId), "FIGHT_DETAIL").
                            addToBackStack(null).commit();
                }
            });

            if (mNotificationsAdapter == null) {
                setNotificationListAdapter();
            }

            mSaveView = true;
        }

        return v;
    }

    private void loadNotifications()
    {
        if (page == 1) {
            mListView.setVisibility(View.INVISIBLE);
            mLoadingNotificationsProgress.setVisibility(View.VISIBLE);
        }

        setLoading(true);

        mRequestQueue.add(TBARequestFactory.NotificationRequest(page++, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    int notificationsCount = jsonObject.getInt("notifications_count");
                    JSONArray notificationsArray = jsonObject.getJSONArray("notifications");

                    hasNext = mNotificationsAdapter.notifications.size() + notificationsArray.length() < notificationsCount;

                    unreadNotifications.clear();

                    for (int i = 0; i < notificationsArray.length(); i++) {
                        Notification n = new Notification(notificationsArray.getJSONObject(i));
                        mNotificationsAdapter.notifications.add(n);

                        if (!n.seen) {
                            unreadNotifications.add(n);
                        }
                    }

                    mNotificationsAdapter.notifyDataSetChanged();

                    Log.i("Unread notifications", String.valueOf(unreadNotifications.size()));

                    if (unreadNotifications.size() > 0) {
                        /*
                        mRequestQueue.add(TBARequestFactory.MarkNotificationsRequest(unreadNotifications, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String string) {
                                for (int i = 0; i < unreadNotifications.size(); i++) {
                                    for (int j = 0; j < mNotificationsAdapter.notifications.size(); j++) {
                                        if (mNotificationsAdapter.notifications.get(j).id == unreadNotifications.get(i).id) {
                                            mNotificationsAdapter.notifications.get(j).seen = true;
                                            break;
                                        }
                                    }
                                }
                                mNotificationsAdapter.notifyDataSetChanged();
                                unreadNotifications.clear();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                            }
                        }));
                        */
                    }

                    setLoading(false);

                    if (page == 2) {
                        mListView.setVisibility(View.VISIBLE);
                        mLoadingNotificationsProgress.setVisibility(View.INVISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, this));
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
}
