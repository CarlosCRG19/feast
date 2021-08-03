package com.example.fbu_app.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

// Stores like interactions with restaurants
// (establishes a one to many relationship)
@ParseClassName("Like")
public class Like extends ParseObject {

    // SETTERS

    // Business related to the like
    public void setBusiness(Business business) {
        put("business", business);
    }

    // User that liked this business
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

