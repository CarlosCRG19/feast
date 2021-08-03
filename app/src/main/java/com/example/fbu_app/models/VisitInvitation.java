package com.example.fbu_app.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

// Object for saving invitations
@ParseClassName("VisitInvitation")
public class VisitInvitation extends ParseObject {

    // SETTERS

    // Assigns a visit for the invitation
    public void setVisit(Visit visit) { put("visit", visit); }

    // Assigns the creator of the invite
    public void setFromUser(ParseUser fromUser) { put("fromUser", fromUser); }

    // Assigns the user that is being invited
    public void setToUser(ParseUser toUser) { // Business selected by the user
        put("toUser", toUser);
    }

    // Assigns the current status of the  visit. Can be pending, accepted or declined
    public void setStatus(String status) { // User that created the visit
        put("status", status);
    }

    // Scheduled date for the visit
    public void setVisitDate(Date visitDate) { put("visitDate", visitDate); }

    // GETTERS

    // Returns visit for this invitations
    public Visit getVisit(){ return (Visit) get("visit"); }

    // Returns invite creator
    public ParseUser getFromUser() { return (ParseUser) get("fromUser"); }

    public ParseUser getToUser() { return (ParseUser) get("toUser"); }

    // Returns status of the visit
    public String getStatus() { return (String) get("status"); }

    // Returns the date of the visit
    public Date getVisitDate() { return (Date) get("visitDate"); }
}