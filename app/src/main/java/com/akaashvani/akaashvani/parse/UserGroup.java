package com.akaashvani.akaashvani.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserGroup")
public class UserGroup extends ParseObject {

    // User
    public ParseUser getUser() {
        return (ParseUser)getParseObject("user");
    }
    public void setUser(ParseUser value) {
        put("user", value);
    }

    // Group
    public Group getGroup() {
        return (Group)getParseObject("group");
    }
    public void setGroup(Group value) {
        put("group", value);
    }


}
