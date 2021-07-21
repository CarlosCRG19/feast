package com.example.fbu_app.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

// Object for saving invitations
@ParseClassName("VisitInvitation")
public class VisitInvitation extends ParseObject {

    // SETTERS

    public void setVisit(Visit visit) { put("visit", visit); }

    public void setFromUser(ParseUser fromUser) { // Scheduled date for the visit
        put("fromUser", fromUser);
    }

    public void setToUser(ParseUser toUser) { // Business selected by the user
        put("toUser", toUser);
    }

    public void setStatus(String status) { // User that created the visit
        put("status", status);
    }

    // GETTERS

    public Visit getVisit(){ return (Visit) get("visit"); }

    public ParseUser getFromUser() { return (ParseUser) get("fromUser"); }

    public ParseUser getToUser() { return (ParseUser) get("toUser"); }

    public String getStatus() { return (String) get("status"); }

}