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

    public static Business fromJson(JSONObject jsonObject) {

        Business business = new Business();
        business.setYelpId(jsonObject.optString("id"));
        business.setName(jsonObject.optString("name"));
        business.setImageUrl(jsonObject.optString("image_url"));
        business.setRating(jsonObject.optInt("rating"));
        business.setPrice(jsonObject.optString("price"));
        business.setTelephone(jsonObject.optString("display_phone"));

        JSONObject coordinates = jsonObject.optJSONObject("coordinates");
        business.setCoordLatitude(coordinates.optDouble("latitude"));
        business.setCoordLongitude(coordinates.optDouble("longitude"));

        JSONObject location = jsonObject.optJSONObject("location");
        business.setAddress(location.optString("address1"));
        business.setCity(location.optString("city"));
        business.setCountry(location.optString("country"));

        return business;

    }

    public void setYelpId(String yelpId) {
        put("yelpId", yelpId);
    }

    public String getYelpId() {
        return getString("yelpId");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getName() {
        return getString("name");
    }

    public void setImageUrl(String imageUrl) {
        put("imageUrl", imageUrl);
    }

    public String getImageUrl() {
        return getString("imageUrl");
    }

    public void setRating(int rating) {
        put("rating", rating);
    }

    public int getRating() {
        return getInt("rating");
    }

    public void setPrice(String price) {
        put("price", price);
    }

    public String getPrice() {
        return getString("price");
    }

    public void setTelephone(String telephone) {
        put("telephone", telephone);
    }

    public String getTelephone() {
        return getString("telephone");
    }

    public void setCoordLatitude(double latitude) {
        put("coordLatitude", latitude);
    }

    public double getCoordLatitude() {
        return getDouble("coordLatitude");
    }

    public void setCoordLongitude(double longitude) {
        put("coordLongitude", longitude);
    }

    public double getCoordLongitude() {
        return getDouble("coordLongitude");
    }

    public String getAddress() {
        return getString("address");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public String getCity() {
        return getString("city");
    }

    public void setCity(String city) {
        put("city", city);
    }

    public String getCountry() {
        return getString("country");
    }

    public void setCountry(String country) {
        put("country", country);
    }

    public static List<Business> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Business> businesses = new ArrayList<>();
        for(int i=0 ; i < jsonArray.length() ; i++) {
            businesses.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return businesses;
    }
}
