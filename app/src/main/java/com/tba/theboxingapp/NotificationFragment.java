package com.tba.theboxingapp;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.tba.theboxingapp.Adapters.NotificationListAdapter;
import com.tba.theboxingapp.Model.Notification;
import com.tba.theboxingapp.Networking.TBAVolley;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

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

    public static NotificationFragment newInstance(String param1, String param2) {
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
                        v = savedView;
                    } else {
                        v = savedView;
                    }
                }
            }
        } else {

            mViewReference = new SoftReference<View>(v);

            v = inflater.inflate(R.layout.fragment_notification, container, false);

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

            if (mNotificationsAdapter == null) {
                setNotificationListAdapter();
            }

            mSaveView = true;
        }

        return v;
    }

    private void loadNotifications()
    {

    }
}
