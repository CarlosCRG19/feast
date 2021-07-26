package com.example.fbu_app.models;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("Business")
public class Business extends ParseObject{

    // DISTANCE TEMPORAL FIELD
    private int distance;
    private boolean closed;

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    // STATIC METHODS FOR OBJECT CONSTRUCTION

    // Creates a new Business directly from a JSON object and returns it
    public static Business fromJson(JSONObject jsonObject) {

        // Create new ParseObject
        Business business = new Business();

        // Business info
        business.setYelpId(jsonObject.optString("id"));
        business.setName(jsonObject.optString("name"));
        business.setImageUrl(jsonObject.optString("image_url"));
        business.setTelephone(jsonObject.optString("display_phone"));

        /*
         * NOTE: using optString instead of getString allows us to define a callback (which by default is "" for strings) in case
         * there is no value for the specified name. getString, throws a JSONException Error
         */

        // Metrics
        business.setRating(jsonObject.optInt("rating"));
        business.setPrice(jsonObject.optString("price"));
        business.setPriceInt(jsonObject.optString("price"));
        business.setDistance((int) jsonObject.optDouble("distance"));
        business.setClosed((boolean) jsonObject.optBoolean("is_closed"));

        // Coordinates
        JSONObject coordinates = jsonObject.optJSONObject("coordinates");
        business.setCoordLatitude(coordinates.optDouble("latitude"));
        business.setCoordLongitude(coordinates.optDouble("longitude"));

        // Location
        JSONObject location = jsonObject.optJSONObject("location");
        business.setAddress(location.optString("address1"));
        business.setCity(location.optString("city"));
        business.setCountry(location.optString("country"));

        // Categories

        // Create empty list
        List<String> categoriesList = new ArrayList<>();
        // Get categories JSONArray
        JSONArray categories = jsonObject.optJSONArray("categories");
        // Iterate through array and get category title for each object
        for (int i=0 ; i < categories.length() ; i++) {
            categoriesList.add(categories.optJSONObject(i).optString("title"));
        }
        // Save categories
        business.setCategories(categoriesList);

        // Return newly created business
        return business;

    }

    // Applies fromJson method to the objects inside a JSON Array and returns a new list of Business objects
    public static List<Business> fromJsonArray(JSONArray jsonArray) throws JSONException {
        // Create empty list
        List<Business> businesses = new ArrayList<>();
        // Iterate through the array and apply fromJson to each object
        for(int i=0 ; i < jsonArray.length() ; i++) {
            businesses.add(fromJson(jsonArray.getJSONObject(i))); // add objects to businesses list
        }
        return businesses;
    }

    // Method to format distance
    public static  String formatDistance(int distance) {
        // Convert distance to kilometers
        float distanceKm = ((float) distance) / 1000;
        // Create distance in String
        String formattedDistance = String.format("%.1f km.", distanceKm);
        // Return formatted distance
        return formattedDistance;
    }

    // Method to format categories
    public static String formatCategories(List<String> categories) {
        // Concatenate all strings
        String concatenated = String.join(", ", categories);
        // Remove last two characters
        concatenated.substring(0, concatenated.length() - 2);
        // Return concatenated
        return concatenated;
    }

    // SETTERS (use put method from ParseObject)

    public void setYelpId(String yelpId) { // Unique Yelp ID of this business.
        put("yelpId", yelpId);
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setImageUrl(String imageUrl) { // URL of photo for this business.
        put("imageUrl", imageUrl);
    }

    public void setRating(int rating) { // Rating for this business (value ranges from 1, 1.5, ... 4.5, 5).
        put("rating", rating);
    }

    public void setPrice(String price) { // Price level of the business. Value is one of $, $$, $$$ and $$$$.
        put("price", price);
    }

    public void setPriceInt(String price) { // Price level of the business. Value is one of $, $$, $$$ and $$$$.
        put("priceInt", formatPriceToInt(price));
    }

    public void setTelephone(String telephone) { // Phone number of the business formatted nicely to be displayed to users.
        put("telephone", telephone);
    }

    public void setCoordLatitude(double latitude) {
        put("coordLatitude", latitude);
    }

    public void setCoordLongitude(double longitude) {
        put("coordLongitude", longitude);
    }

    public void setAddress(String address) { // Street address of this business.
        put("address", address);
    }

    public void setCity(String city) {
        put("city", city);
    }

    public void setCountry(String country) {
        put("country", country);
    }

    public void setCategories(List<String> categories) {
        put("categories", categories);
    }


    // GETTERS (get methods from ParseObject)

    public String getYelpId() {
        return getString("yelpId");
    }

    public String getName() {
        return getString("name");
    }

    public String getImageUrl() {
        return getString("imageUrl");
    }

    public int getRating() {
        return getInt("rating");
    }

    public String getPrice() {
        return getString("price");
    }

    public int getPriceInt() {
        return getInt("priceInt");
    }

    public String getTelephone() {
        return getString("telephone");
    }

    public double getCoordLatitude() {
        return getDouble("coordLatitude");
    }

    public double getCoordLongitude() {
        return getDouble("coordLongitude");
    }

    public String getAddress() {
        return getString("address");
    }

    public String getCity() {
        return getString("city");
    }

    public String getCountry() {
        return getString("country");
    }

    public List<String> getCategories() {
        return getList("categories");
    }

    private int formatPriceToInt(String price) {
        return price.length();
    }

}
