package com.akaashvani.akaashvani.tabs;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.akaashvani.akaashvani.R;
import com.akaashvani.akaashvani.adapters.PagerModelManager;
import com.akaashvani.akaashvani.fragments.ChatFragment;
import com.akaashvani.akaashvani.fragments.GuideFragment;
import com.akaashvani.akaashvani.fragments.LocationFragment;
import com.akaashvani.akaashvani.screens.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.springindicator.SpringIndicator;
import github.chenupt.springindicator.viewpager.ScrollerViewPager;

public class TabActivity extends BaseActivity {

    protected static final String TAG = "AkaashVani";
    protected final static String KEY_LOCATION = "location";
    protected Location mCurrentLocation;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private boolean showOverFlowMenuBool = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);

        setToolBarComponents(showOverFlowMenuBool);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        //TextView toolbarTextView = (TextView) toolbar.findViewById(R.id.toolbar_heading);
        //toolbarTextView.setText(getIntent().getStringExtra("groupName"));
//        setSupportActionBar(toolbar);
//        toolbar.showOverflowMenu();

        //String strGroupObjId = getIntent().getStringExtra("groupObjId");

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        //ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ScrollerViewPager viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);
        SpringIndicator springIndicator = (SpringIndicator) findViewById(R.id.indicator);
        //viewPager.setAdapter(new TabPagerAdapter(getSupportFragmentManager(), TabActivity.this));

        PagerModelManager manager = new PagerModelManager();
        manager.addCommonFragment(GuideFragment.class, getBgRes(), getTitles());
        ModelPagerAdapter adapter = new ModelPagerAdapter(getSupportFragmentManager(), manager){
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return LocationFragment.newInstance("" + position, "");
                    case 1:
                        return ChatFragment.newInstance("" + position, "");
                    default:
                        return LocationFragment.newInstance("" + position, "");
                }
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.fixScrollSpeed();

        // just set viewPager
        springIndicator.setViewPager(viewPager);

        // Give the TabLayout the ViewPager
        /*TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);*/

        updateValuesFromBundle(savedInstanceState);
    }

    private List<String> getTitles(){
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        return list;
    }

    private List<Integer> getBgRes(){
        List<Integer> list = new ArrayList<Integer>();
        list.add(R.drawable.bg1);
        list.add(R.drawable.bg2);
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.new_geofence) {
            return true;
        } else if (id == R.id.help) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "User agreed to make required location settings changes.");
                        //startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "User chose not to make required location settings changes.");
                        break;
                }
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    // If Google Play services resolved the problem
                    case Activity.RESULT_OK:
                        Log.d(TAG, "Connected to Google Play services");
                        break;
                    default:
                        Log.d(TAG, "Could not connect to Google Play services");
                        break;
                }
                break;
        }
    }
}
