package com.example.fbu_app.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

// Object that represents a friend relations between to users
@ParseClassName("FriendRequest")
public class FriendRequest extends ParseObject {

    // SETTERS

    // Sets the user that sent the friend request
    public void setFromUser(ParseUser fromUser) { put("fromUser", fromUser); }

    // Sets the user that receives the friend request
    public void setToUser(ParseUser toUser) { // Business selected by the user
        put("toUser", toUser);
    }

    // Sets the status of the request. Can be pending, accepted or declined.
    public void setStatus(String status) { // User that created the visit
        put("status", status);
    }

    // GETTERS

    public ParseUser getFromUser() { return (ParseUser) get("fromUser"); }

    public ParseUser getToUser() { return (ParseUser) get("toUser"); }

    public String getStatus() { return (String) get("status"); }
}


