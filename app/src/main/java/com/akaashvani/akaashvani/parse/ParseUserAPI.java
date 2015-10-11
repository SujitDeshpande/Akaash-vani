package com.akaashvani.akaashvani.parse;

import android.util.Log;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Sujit on 11/10/15.
 */
public class ParseUserAPI {
    // The callback interface
    public interface Callback {
        void registerSuccess(ParseUser user);

        void registerFailed();

        void loginSuccess(ParseUser user);

        void loginFailed(String phone);

        void getUserSuccess(ParseUser user);

        void getUserFailed(String str);
    }

    Callback callback;

    public ParseUserAPI(Callback callback) {
        this.callback = callback;

    }

    public void registerParseUser(String phone, String password, String fullName) {
        final ParseUser user = new ParseUser();
        user.setUsername(phone);//"my name");
        user.setPassword(password);//"my pass");
        user.put("fullname", fullName);
        Log.i("registerUser", "User: " + phone);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    Log.d("k10", "registration success");
                    if (callback != null) {
                        callback.registerSuccess(user);
                    }
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.d("k10", "registration failed");
                    if (callback != null) {
                        callback.registerFailed();
                    }
                }
            }
        });
    }

    public void getParseUser(final String phone) {
        //ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", phone);
        Log.i("Inside Get User", "getUser ");

        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    Log.i("getFirstInBackground", "e==null ");
                    if (callback != null) {
                        callback.getUserSuccess(parseUser);
                    }
                } else {
                    Log.i("getFirstInBackground", "else ");
                    if (callback != null) {
                        callback.getUserFailed(phone);
                    }
                }
            }
        });
    }

    public void loginParse(final String phone, String password) {
        Log.i("Inside Login", "login ");
        ParseUser.logInInBackground(phone, password, new LogInCallback() {
            public void done(ParseUser parseUser, ParseException e) {
                ParseUser user = parseUser;
                Log.i("Parse User", "Parse User " + user.getUsername());
                if (user != null) {
                    // Hooray! The user is logged in.
                    if (callback != null) {
                        callback.loginSuccess(user);
                    }
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    if (callback != null) {
                        callback.loginFailed(phone);
                    }
                }
            }
        });
    }
}
