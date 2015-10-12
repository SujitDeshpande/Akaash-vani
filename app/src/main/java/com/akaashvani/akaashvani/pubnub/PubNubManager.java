package com.akaashvani.akaashvani.pubnub;

import android.util.Log;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aditlal on 18/07/15.
 */
public class PubNubManager {

    public final static String TAG = "PUBNUB";

    public static Pubnub startPubnub() {
        Log.d(TAG, "Initializing PubNub");
        return new Pubnub("pub-c-704d1233-e6de-454f-9ab8-4c1dc966906e", "sub-c-a0ec95c8-2d8c-11e5-bda8-02ee2ddab7fe");
    }

    public static void subscribe(Pubnub mPubnub, String channelName, Callback subscribeCallback) {
        // Subscribe to channel
        try {
            mPubnub.subscribe(channelName, subscribeCallback);
            Log.d(TAG, "Subscribed to Channel");
        } catch (PubnubException e) {
            Log.e(TAG, e.toString());
        }
    }



    public static void broadcastLocation(Pubnub pubnub, String channelName, double latitude,
                                         double longitude, String user, String groupName) {
        JSONObject message = new JSONObject();
        try {
            message.put("lat", latitude);
            message.put("lon", longitude);
            message.put("user", user);
            message.put("group", groupName);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        Log.d(TAG, "Sending JSON Message: " + message.toString());
        Log.i(TAG, "Channel Name: "+channelName);

        pubnub.publish(channelName, message, publishCallback);
    }

    public static Callback publishCallback = new Callback() {

        @Override
        public void successCallback(String channel, Object response) {
            Log.d("PUBNUB", "Sent Message: " + response.toString());
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.d("PUBNUB", error.toString());
        }
    };
}

