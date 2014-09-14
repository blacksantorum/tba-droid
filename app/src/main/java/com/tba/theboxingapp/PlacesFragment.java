package com.tba.theboxingapp;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;

/**
 * Created by christibbs on 9/14/14.
 */
public class PlacesFragment extends MapFragment implements LocationListener
{
    public LocationManager locationManager;
    public List<Place> places;

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
