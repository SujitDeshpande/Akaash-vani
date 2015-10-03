package com.akaashvani.akaashvani;

import android.app.Application;

import com.akaashvani.akaashvani.preference.PreferenceUtil;

/**
 * Created by agoel on 04/10/15.
 */
public class AkaashVaniApplication extends Application {

    private static final String TAG = AkaashVaniApplication.class.getSimpleName();

    private static AkaashVaniApplication mApplication;


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

}
