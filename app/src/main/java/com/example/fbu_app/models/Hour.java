package com.example.fbu_app.models;

import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// Stores the open hours information for a specific business (one-to-many relationship)
@ParseClassName("Hour")
public class Hour extends ParseObject{

    // STATIC METHODS FOR OBJECT CREATION

    // Creates a new Hour directly from a JSON object and returns it
    public static Hour fromJson(JSONObject jsonObject) {

        // Create new ParseObject
        Hour hour = new Hour();

        // Set info
        hour.setDay(jsonObject.optInt("day"));
        hour.setStart(jsonObject.optString("start"));
        hour.setEnd(jsonObject.optString("end"));

        // Return newly created object
        return hour;

    }

    // Applies fromJson method to the objects inside a JSON Array and returns a new list of hour objects (linked to the same business)
    public static List<Hour> fromJsonArray(JSONArray jsonArray, Business business) throws JSONException {
        // Create new empty list
        List<Hour> hours = new ArrayList<>();
        // Iterate through JSON Array and apply fromJson to each object
        for(int i=0 ; i < jsonArray.length() ; i++) {
            Hour hour = fromJson(jsonArray.getJSONObject(i));
            // Reference the same business to create relationship
            hour.setBusiness(business);
            // Add new object to the list
            hours.add(hour);
        }
        return  hours;
    }

    // SETTERS

    public void setDay(int day) { // String representing the day of the week for the opening hours
        put("day", formatDay(day));
    }

    public void setStart(String start) { // Start of the opening hours in a day, in 24-hour clock notation. Formatted to HH:MM
        put("start", formatHour(start));
    }

    public void setEnd(String end) { // End of the opening hours in a day, in 24-hour clock notation. Formatted to HH:MM
        put("end", formatHour(end));
    }

    public void setBusiness(Business business) { // Business linked to the Hour
        put("business", business);
    }

    // GETTERS

    public String getDay() {
        return getString("day");
    }

    public String getStart() {
        return getString("start") ;
    }

    public String getEnd() {
        return getString("end");
    }

    public Business getBusiness() {
        return (Business) get("business");
    }

    // HELPER METHODS

    // Day is received as an integer from 0 to 6 (0 being Monday and 6 Sunday). This method transforms the data into redable day names
    private String formatDay(int day) {
        switch (day) {
            case 0:
                return "Monday";
            case 1:
                return "Tuesday";
            case 2:
                return "Wednesday";
            case 3:
                return "Thursday";
            case 4:
                return "Friday";
            case 5:
                return "Saturday";
            case 6:
                return "Sunday";
            default:
                return "";
        }
    }

    // Returns hour formatted as HH:MM
    private String formatHour(String hour) {

        return hour.substring(0,2) + ":" + hour.substring(2);
    }

}
