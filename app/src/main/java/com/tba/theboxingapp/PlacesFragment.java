package com.tba.theboxingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tba.theboxingapp.Model.Place;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Requests.TBARequestFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by christibbs on 9/14/14.
 */
public class PlacesFragment extends MapFragment implements LocationListener
{
    public LocationManager locationManager;
    public List<Place> places = new ArrayList<Place>();

    public static PlacesFragment newInstance() {
        return new PlacesFragment();
    }

    private void initializeLocationManager()
    {
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        String best = locationManager.getBestProvider(criteria, true);

        if (locationManager.isProviderEnabled(best)) {
            locationManager.requestSingleUpdate(best,this,null);
        } else {
            Toast.makeText(getActivity(), "Cannot find your location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initializeLocationManager();
        pollPlaces();
    }

    private void pollPlaces()
    {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        queue.add(TBARequestFactory.PlacesRequest(new Response.Listener<JSONArray>() {
                                                      @Override
                                                      public void onResponse(JSONArray response) {
                                                          Log.i("Respone", "Response is: " + response.toString());
                                                          GoogleMap map = getMap();
                                                          for (int i = 0; i < response.length(); i++) {
                                                              try {
                                                                  JSONObject o = response.getJSONObject(i);
                                                                  JSONObject p = o.getJSONObject("place");
                                                                  Place place = new Place(p);
                                                                  places.add(place);
                                                                  if (map != null) {
                                                                     map.addMarker(new MarkerOptions()
                                                                     .position(new LatLng(place.location.getLatitude(),
                                                                                          place.location.getLongitude()))
                                                                     .title(place.name));
                                                                  }
                                                              } catch (JSONException e) {
                                                                  e.printStackTrace();
                                                              }
                                                          }


                                                      }
                                                  }
                , (TBAActivity)getActivity()));
    }

    public  PlacesFragment()
    {
    }

    //<editor-fold desc="LocationListener">
    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        GoogleMap map = getMap();
        if (map != null) {
            getMap().animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(
                                    location.getLatitude(), location.getLongitude()), 12
                    ));
        }
    }

    @Override
    public void onStatusChanged(String provider, int something, Bundle bundle)
    {

    }
    //</editor-fold>
}
