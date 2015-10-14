package com.akaashvani.akaashvani.fragments;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.akaashvani.akaashvani.AkaashVaniApplication;
import com.akaashvani.akaashvani.R;
import com.akaashvani.akaashvani.geofence.Constants;
import com.akaashvani.akaashvani.geofence.GeofenceTransitionsIntentService;
import com.akaashvani.akaashvani.parse.Group;
import com.akaashvani.akaashvani.pubnub.PubNubManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseUser;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.internal.zzid.runOnUiThread;

public class LocationFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        ResultCallback<LocationSettingsResult> {

    private static final String TAG = "Location-Fragment";
    MapView mMapView;
    private GoogleMap googleMap;
    private CameraPosition cameraPosition;
    private int counter = 1;
    private BitmapDrawable d, e;
    private Bitmap bmp1, bmp2;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected LocationSettingsRequest mLocationSettingsRequest;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected final static String KEY_LOCATION = "location";

    // latitude and longitude
    double latitude = 12.8963272;
    double longitude = 77.5557918;
    MarkerOptions marker;

    protected ArrayList<Geofence> mGeofenceList;
    private boolean mGeofencesAdded;
    private PendingIntent mGeofencePendingIntent;
    private SharedPreferences mSharedPreferences;
    private Circle mapCircle;
    private LatLng myLatLng;

    // PubNub
    private Pubnub mPubnub;
    private static String groupName;
    private static String groupID;
    AkaashVaniApplication akaashVaniApplication;

    Group groupOthers, groupMe;
    Map<String, Group> groupMap = new HashMap<>();

    public static LocationFragment newInstance(String param1, String param2, String param3) {
        groupName = param2;
        groupID = param3;
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    public LocationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        updateValuesFromBundle(savedInstanceState);

        mPubnub = akaashVaniApplication.startPubnub();
        //groupOthers = new Group();

        mGeofenceList = new ArrayList<Geofence>();
        mGeofencePendingIntent = null;
        mSharedPreferences = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                getActivity().MODE_PRIVATE);
        mGeofencesAdded = mSharedPreferences.getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        initializeIcons();
        populateGeofenceList();

        buildGoogleApiClient();
        createLocationRequest();

        buildLocationSettingsRequest();
        checkLocationSettings();

        mGoogleApiClient.connect();

        mPubnub.hereNow(groupID, new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                super.successCallback(channel, message);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(getActivity(), "Users in list " + message.toString(), Toast.LENGTH_SHORT).show();
                        Log.d("users in list", message.toString());
                    }
                });


            }
        });

        PubNubManager.subscribe(mPubnub, groupID, new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                super.successCallback(channel, message);
                try {
                    JSONObject iob = (JSONObject) message;
                    final double lat = iob.getDouble("latitude");
                    final double lon = iob.getDouble("longitude");
                    final String user = iob.getString("userName");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!user.equals(ParseUser.getCurrentUser().getUsername())) {
                                Log.d(TAG, "User: " + user);
                                groupOthers = new Group();
                                groupOthers.setUserName(user);
                                groupOthers.setMyLatLng(new LatLng(lat, lon));
                                groupMap.put(user, groupOthers);
                            }
                        }
                    });
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                ;

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView ");
        // inflate and return the layout
        View v = inflater.inflate(R.layout.fragment_location, container,
                false);
        initializeMap(v, savedInstanceState);

        mGoogleApiClient.connect();
        return v;
    }

    protected void initializeIcons(){
        // Changing marker icon
        d = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_marker).getCurrent();
        bmp1 = Bitmap.createScaledBitmap(d.getBitmap(), d.getBitmap().getWidth() / 7, d.getBitmap().getHeight() / 7, false);
        e = (BitmapDrawable) getResources().getDrawable(R.drawable.person).getCurrent();
        bmp2 = Bitmap.createScaledBitmap(e.getBitmap(), (int) (e.getBitmap().getWidth() / 1), (int) (e.getBitmap().getHeight() / 1), false);
        bmp2 = getRoundedShape(bmp2);
    }

    protected void initializeMap(View v, Bundle savedInstanceState) {
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                addMarkers();
                setCamera();
                updateCircle(myLatLng);
                return true;
            }
        });
        addMarkers();
        setCamera();
        updateCircle(myLatLng);
    }

    protected void setCamera(){

        if (mCurrentLocation != null) {
            myLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        } else {
            myLatLng = new LatLng(latitude, longitude);
        }
        cameraPosition = new CameraPosition.Builder()
                .target(myLatLng).zoom(17).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    protected void addMarkers() {
        // create marker
        googleMap.clear();

        if (mCurrentLocation != null) {
            marker = new MarkerOptions().position(
                    new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude())).title("Hello Maps");
        } else {
            marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title("Hello Maps");
        }

        marker.icon(BitmapDescriptorFactory.fromBitmap(overlay(bmp1, bmp2)));

        // adding marker
        googleMap.addMarker(marker);

    }

    protected void reDrawMarkers(Map<String, Group> locationMap) {
        // create marker
        googleMap.clear();

        for (Map.Entry<String,Group> entry : locationMap.entrySet()) {
            String key = entry.getKey();
            Group userDetail = entry.getValue();
            Log.i(TAG, "Key: "+key+" |Value: "+userDetail.getUserName()+" |Lat: "+userDetail.getMyLatLng().latitude);
            Double lat = userDetail.getMyLatLng().latitude;
            Double lon = userDetail.getMyLatLng().longitude;
            marker = new MarkerOptions().position(
                    new LatLng(lat, lon)).title(key);
            marker.icon(BitmapDescriptorFactory.fromBitmap(overlay(bmp1, bmp2)));

            // adding marker
            googleMap.addMarker(marker);
        }
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {

        int targetWidth = 140;
        int targetHeight = 140;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

    private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawBitmap(bmp2, new Rect(0, 0, 200, 220), new Rect(48, 25, 228, 228), null);
        return bmOverlay;
    }

    /*GeoFencing Related Methods Below*/
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    public void addGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            );; // Result processed in onResult().
        } catch (SecurityException securityException) {
            logSecurityException(securityException);
        }

        mGeofencesAdded = !mGeofencesAdded;
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(Constants.GEOFENCES_ADDED_KEY, mGeofencesAdded);
        editor.commit();
    }

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.GEOFENCE.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(getActivity(), GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    private void updateCircle(LatLng latLng) {
            //googleMap.clear();
            mapCircle =
                    googleMap.addCircle(
                            new CircleOptions().center(myLatLng).radius(Constants.GEOFENCE_RADIUS_IN_METERS));
            int baseColor = Color.DKGRAY;
            mapCircle.setStrokeColor(baseColor);
            mapCircle.setStrokeWidth(2);
            mapCircle.setFillColor(Color.argb(50, Color.red(baseColor), Color.green(baseColor),
                    Color.blue(baseColor)));
        mapCircle.setCenter(myLatLng);
        mapCircle.setRadius(Constants.GEOFENCE_RADIUS_IN_METERS); // Convert radius in feet to meters.
    }
    /*End GeoFencing Related Methods*/

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this
        );
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                this
        );
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        result.setResultCallback(this);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }
            addMarkers();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected ");
        startLocationUpdates();
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        addGeofencesButtonHandler();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        groupMe = new Group();
        groupMe.setUserName(ParseUser.getCurrentUser().getUsername());
        groupMe.setMyLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        groupMap.put(ParseUser.getCurrentUser().getUsername(), groupMe);

        // Broadcast information on PubNub Channel
        PubNubManager.broadcastLocation(mPubnub, groupID, location.getLatitude(),
                location.getLongitude(), ParseUser.getCurrentUser().getUsername(), groupName, groupID);

        //addMarkers();
        reDrawMarkers(groupMap);
        while (counter < 2){
            setCamera();
            counter ++;
        }

        myLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        updateCircle(myLatLng);
        Log.i(TAG, "onLocationChanged " + "Latitude: " + mCurrentLocation.getLatitude() + " Longitude: " + mCurrentLocation.getLongitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                Log.i(TAG, "All location settings are satisfied.");
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                        "upgrade location settings ");

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "PendingIntent unable to execute request.");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created.");
                break;
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }
}

