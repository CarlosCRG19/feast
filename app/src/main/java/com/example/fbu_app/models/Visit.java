package com.example.fbu_app.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;

@ParseClassName("Visit")
public class Visit extends ParseObject {

    public void setDate(Date date) {
        put("date", date);
    }

    public Date getDate() {
        return getDate("date");
    }

    public void setBusiness(Business business) {
        put("business", business);
    }

    public Business getBusiness() {
        return (Business) get("business");
    }

    public void setUser(ParseUser user) {
        put("user", user);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

}