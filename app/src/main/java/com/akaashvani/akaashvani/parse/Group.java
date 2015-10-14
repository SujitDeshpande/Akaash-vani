package com.akaashvani.akaashvani.parse;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Sujit on 13/10/15.
 */
public class Group {
    private String userName;
    private double latitude, longitude;
    private LatLng myLatLng = new LatLng(0,0);

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LatLng getMyLatLng() {
        return myLatLng;
    }

    public void setMyLatLng(LatLng myLatLng) {
        this.myLatLng = myLatLng;
    }
}
