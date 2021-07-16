package com.example.fbu_app.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Visit")
public class Visit extends ParseObject {

    // SETTERS

    public void setDateStr(String dateStr) { put("dateStr", dateStr); }

    public void setDate(Date date) { // Scheduled date for the visit
        put("date", date);
    }

    public void setBusiness(Business business) { // Business selected by the user
        put("business", business);
    }

    public void setUser(ParseUser user) { // User that created the visit
        put("user", user);
    }

    // GETTERS

    public String getDateStr() { return (String) get("dateStr"); }

    public Date getDate() {
        return getDate("date");
    }

    public Business getBusiness() {
        return (Business) get("business");
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }
}