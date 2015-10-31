package com.akaashvani.akaashvani.parse;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sujit on 13/10/15.
 */
public class Group {
    private String userName;
    private double latitude, longitude;
    private LatLng myLatLng = new LatLng(0,0);
    private List<LatLng> myLocation = new ArrayList<LatLng>();

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

    public void setMyLocation(List<LatLng> myLocation) {
        this.myLocation = myLocation;
    }

    public List<LatLng> getMyLocation() {
        return myLocation;
    }
}
