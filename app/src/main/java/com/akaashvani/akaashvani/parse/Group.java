package com.akaashvani.akaashvani.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Group")
public class Group extends ParseObject {
    // name
    public String getName() {
        return getString("name");
    }
    public void setName(String value) {
        put("name", value);
    }

    // owner
    public ParseUser getOwner() {
        return (ParseUser)getParseObject("owner");
    }
    public void setOwner(ParseUser value) {
        put("owner", value);
    }

}
