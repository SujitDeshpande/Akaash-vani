package com.akaashvani.akaashvani.screens;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.akaashvani.akaashvani.R;
import com.akaashvani.akaashvani.parse.ParseUserAPI;
import com.akaashvani.akaashvani.view.LoginButton;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.parse.ParseUser;

public class LoginActivity extends BaseActivity implements ParseUserAPI.Callback {

    final ParseUserAPI parseUserAPI = new ParseUserAPI(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final LoginButton digitsButton = (LoginButton) findViewById(R.id.phone_button);
        digitsButton.setBackgroundColor(Color.BLUE);

        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // Do something with the session and phone number
                Toast.makeText(LoginActivity.this,
                        "Authentication Successful for " + phoneNumber, Toast.LENGTH_SHORT).show();
                showProgressDialog(LoginActivity.this, "Logging In");
                parseUserAPI.getParseUser(phoneNumber);
            }

            @Override
            public void failure(DigitsException exception) {
                // Do something on failure
                Toast.makeText(LoginActivity.this, "Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void registerSuccess(ParseUser user) {
        dismissProgressDialog();
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, DetailsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void registerFailed() {
        dismissProgressDialog();
        Toast.makeText(LoginActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginSuccess(ParseUser user) {
        dismissProgressDialog();
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, DetailsActivity.class);
            startActivity(intent);
            //finish();
        }
    }

    @Override
    public void loginFailed(String phone) {
        parseUserAPI.registerParseUser(phone, "1234", "NewUser");
    }

    @Override
    public void getUserSuccess(ParseUser user) {
        Log.i("Inside didRetrieve User", "didRetriveUser ");
        parseUserAPI.loginParse(user.getUsername(), "1234");
    }

    @Override
    public void getUserFailed(String phone) {
        parseUserAPI.registerParseUser(phone, "1234", "NewUser");
    }
}
