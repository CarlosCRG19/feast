package com.example.fbu_app.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("FriendRequest")
public class FriendRequest extends ParseObject {

    // SETTERS

    public void setFromUser(ParseUser fromUser) {
        put("fromUser", fromUser); }

    public void setToUser(ParseUser toUser) { // Business selected by the user
        put("toUser", toUser);
    }

    public void setStatus(String status) { // User that created the visit
        put("status", status);
    }


    // GETTERS

    public ParseUser getFromUser() { return (ParseUser) get("fromUser"); }

    public ParseUser getToUser() { return (ParseUser) get("toUser"); }

    public String getStatus() { return (String) get("status"); }
}


