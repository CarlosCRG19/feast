package com.example.fbu_app.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

// Stores favorite interactions with restaurants
@ParseClassName("Like")
public class Like extends ParseObject {

    // SETTERS

    public void setBusiness(Business business) {
        put("business", business);
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    // GETTERS

    public Business getBusiness() {
        return (Business) get("business");
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }
}

