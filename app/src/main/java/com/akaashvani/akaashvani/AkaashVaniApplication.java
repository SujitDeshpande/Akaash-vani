package com.akaashvani.akaashvani;

import android.app.Application;

import com.akaashvani.akaashvani.preference.PreferenceUtil;
import com.digits.sdk.android.Digits;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.pubnub.api.Pubnub;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;

/**
 * Created by agoel on 04/10/15.
 */
public class AkaashVaniApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "CKEihbEh3LgzDuaQov81oIKP7";
    private static final String TWITTER_SECRET = "emKomVQZTmzTRMBaG0axVoK7JRYhIMB5wpz4HxhT9Li4xAlM48";


    private static final String TAG = AkaashVaniApplication.class.getSimpleName();
    private static AkaashVaniApplication mApplication;
    Pubnub pubnub;
    private String userId;


    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Parse
        Parse.initialize(this);
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);
        ParseObject.registerSubclass(ParseUser.class);
        // Initialize Fabric
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());

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
