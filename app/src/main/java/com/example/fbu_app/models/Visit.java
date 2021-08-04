package com.example.fbu_app.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
  ParseObject corresponding to a visit scheduled to a restaurant
  Its basic fields are the date of the visit, the restaurant, the visit
  creator and the attendees
 */
@ParseClassName("Visit")
public class Visit extends ParseObject {

    // TAG FOR REFERENCE
    public static final String TAG = "visit";

    // ATTENDEES METHODS
    public void addAttendee(ParseUser user) { add("attendees", user); }

    public void removeAttendee(ParseUser user) { removeAll("attendees", Arrays.asList(user)); }

    // SETTERS

    // Scheduled date for the visit in displayable format
    public void setDateStr(String dateStr) { put("dateStr", dateStr); }

    // Date object referring to the date of the visit
    public void setDate(Date date) { put("date", date); }

    // Business selected by the user
    public void setBusiness(Business business) { put("business", business); }

    // User that created the visit
    public void setUser(ParseUser user) { put("user", user); }

    // GETTERS

    public String getDateStr() { return (String) get("dateStr"); }

    public Date getDate() { return getDate("date"); }

    public Business getBusiness() { return (Business) get("business"); }

    public ParseUser getUser() { return getParseUser("user"); }

    // Returns a list of the users that will attend this visit
    public List<ParseUser> getAttendees() {return (List<ParseUser>) get("attendees"); }

}