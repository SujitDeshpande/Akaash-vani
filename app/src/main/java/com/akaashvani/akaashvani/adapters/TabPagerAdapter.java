package com.akaashvani.akaashvani.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.akaashvani.akaashvani.fragments.ChatFragment;
import com.akaashvani.akaashvani.fragments.LocationFragment;


public class TabPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 2;
    //private final String mGroupObjId;
    private String tabTitles[] = new String[]{"Chat", "Location"};
    private Context context;

    public TabPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        //mGroupObjId = strGroupObjId;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

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

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}

