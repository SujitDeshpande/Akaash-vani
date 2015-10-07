package com.akaashvani.akaashvani;

import android.app.Application;

import com.akaashvani.akaashvani.preference.PreferenceUtil;
import com.pubnub.api.Pubnub;

/**
 * Created by agoel on 04/10/15.
 */
public class AkaashVaniApplication extends Application {

    private static final String TAG = AkaashVaniApplication.class.getSimpleName();
    private static AkaashVaniApplication mApplication;
    Pubnub pubnub;
    private String userId;


    @Override
    public void onCreate() {
        super.onCreate();

        init();

        // initialize a Shared Preference
        PreferenceUtil.init(this, TAG);

    }

    /**
     * create a application instance
     */
    private void init() {
        if (mApplication == null) {
            mApplication = AkaashVaniApplication.this;
        }
    }

    /**
     * get Application context from Application class
     *
     * @return
     */
    public static AkaashVaniApplication getApplication() {
        return mApplication;
    }


    public Pubnub getPubNub() {
        return pubnub;
    }

    public void setPubNub(Pubnub pubnub) {
        this.pubnub = pubnub;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        pubnub.setUUID(userId);
    }
}
