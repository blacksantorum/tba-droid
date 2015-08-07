package com.tba.theboxingapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.tba.theboxingapp.Model.Fight;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PickFightsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PickFightsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickFightsFragment extends Fragment {

    private RequestQueue mRequestQueue = TBAVolley.getInstance(getActivity()).getRequestQueue();
    private ImageLoader mImageLoader = TBAVolley.getInstance(getActivity()).getImageLoader();

    private FrameLayout canvasView;

    private Queue<Fight> unpickedFights;
    // queue of cards

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PickFightsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickFightsFragment newInstance() {
        PickFightsFragment fragment = new PickFightsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PickFightsFragment() {
        // Required empty public constructor
    }

    private void loadUnpickedFights()
    {
        mRequestQueue.add(TBARequestFactory.FetchUnpickedFightsRequest(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject object) {
                try {
                    JSONArray fightsArray = object.getJSONArray("fights");
                    unpickedFights = new LinkedBlockingQueue<Fight>(fightsArray.length());

                    for (int i = 0; i < fightsArray.length() ; i++) {
                        unpickedFights.add(new Fight(fightsArray.getJSONObject(i)));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initializeCards();

            }
        }, (TBAActivity) getActivity()));
    }

    private void initializeCards()
    {

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
        View v = inflater.inflate(R.layout.fragment_pick_fights, container, false);

        canvasView = (FrameLayout)v.findViewById(R.id.canvasView);

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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
