package com.tba.theboxingapp;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.internal.id;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;
import com.tba.theboxingapp.Model.Comment;
import com.tba.theboxingapp.Model.User;
import com.tba.theboxingapp.Networking.TBAVolley;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONException;
import org.json.JSONObject;


public class TBAActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        FightListFragment.OnFragmentInteractionListener, FightDetailFragment.OnFragmentInteractionListener, UserDetailFragment.OnFragmentInteractionListener,
        Response.ErrorListener    {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    public static final String PREFS_NAME = "TBAPref";

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpDrawer();

        if (User.currentUser().id == 0) {
            showLogin();
        }
    }

    private void showLogin()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
            Log.i("pop", "this pop");
        }
        // Default action on back pressed
        else {
            Log.i("pop", "super pop");
            super.onBackPressed();
        }
    }

    private void setUpDrawer()
    {
        setContentView(R.layout.activity_tba);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                if (data.getBooleanExtra("login", false)) {
                    Log.i("Result","User logged in");
                    mNavigationDrawerFragment.mSlideoutProfileTextView.setText(User.currentUser().getName());
                    mNavigationDrawerFragment.mSlideoutProfileImageView.
                            setImageUrl(User.currentUser().profileImageUrl, TBAVolley.getInstance(this).getImageLoader());
                    mNavigationDrawerFragment.selectItem(0);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == 2) {
            FightDetailFragment detailFragment = (FightDetailFragment)getFragmentManager().
                    findFragmentByTag("FIGHT_DETAIL");
            if (detailFragment.isVisible()) {
                detailFragment.mAddCommentEditText.clearFocus();
            }
            if (resultCode == RESULT_OK) {
                try {
                    JSONObject commentObj = new JSONObject(data.getStringExtra("comment"));
                    Comment comment = new Comment(commentObj);

                    Log.i("Comment date after init", comment.createdAt.toString());

                    if (detailFragment.isVisible()) {
                        detailFragment.mCommentArrayAdapter.comments.add(comment);
                        detailFragment.mCommentArrayAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (position == 0) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FightListFragment.newInstance(FightListFragment.ListType.FEATURED)).commit();
            mTitle = "Featured";
        }
        else if (position == 1) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, FightListFragment.newInstance(FightListFragment.ListType.PAST)).commit();
            mTitle = "Recent";
        }
        else if (position == 2) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlacesFragment.newInstance()).commit();
            mTitle = "Places";
        } else if (position == 3) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container,
                            UserDetailFragment.newInstance(User.currentUser().id,
                                    User.currentUser().name, User.currentUser().profileImageUrl)).commit();
            mTitle = User.currentUser().getName();
        } else {
            // update the main content by replacing fragments
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = "Places";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.tba, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_log_out) {
            User.clear();

           // Twitter.logOut();

            SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("Handle");
            editor.remove("Name");
            editor.remove("ImgUrl");
            editor.remove("TwitterId");
            editor.remove("SessionToken");
            editor.commit();

            showLogin();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        if (User.currentUser().isLoggedIn) {
            // Log.i("Error", volleyError.getLocalizedMessage());
            new AlertDialog.Builder(this).setTitle("Network error").setMessage("Sorry, but your request failed")
                    .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Do nothing;
                        }
                    }).show();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tba, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((TBAActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
