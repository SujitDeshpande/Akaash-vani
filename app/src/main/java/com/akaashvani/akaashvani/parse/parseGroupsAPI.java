package com.akaashvani.akaashvani.parse;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class parseGroupsAPI {
    public interface Callback {
        public void getMyGroupsSuccess(List<ParseObject> groupList);
        public void getMyGroupsFailed(String s);
    }

    Callback callback;

    public parseGroupsAPI(Callback callback) {
        this.callback = callback;

    }

    public void getMyGroups() {
        final ParseUser user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserGroup");
        query.include("group");
        query.whereEqualTo("user", user);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> userGroupList, ParseException e) {
                if (e == null) {
                    Log.d("Groups", "Retrieved " + userGroupList.size() + " groups");
                    callback.getMyGroupsSuccess(userGroupList);
                } else {
                    Log.d("Groups", "Error: " + e.getMessage());
                    callback.getMyGroupsFailed(e.getMessage());
                }
            }
        });
    }
}
