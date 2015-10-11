package com.akaashvani.akaashvani.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.akaashvani.akaashvani.R;
import com.parse.ParseUser;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashscreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splashscreen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = null;
                ParseUser user = ParseUser.getCurrentUser();

                if (user != null) {
                    intent = new Intent(SplashscreenActivity.this, DetailsActivity.class);
                } else {
                    intent = new Intent(SplashscreenActivity.this, LoginActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
