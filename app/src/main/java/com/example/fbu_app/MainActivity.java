package com.example.fbu_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fbu_app.helpers.YelpClient;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Hour;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public YelpClient yelpClient;

    List<Business> businessList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        yelpClient = new YelpClient();

        Pair<String, String> location = new Pair<>("location", "Seattle");
        yelpClient.getMatchingBusinesses(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                JSONObject response = json.jsonObject;
                try {
                    JSONArray jsonArray = response.getJSONArray("businesses");
                    Log.i(TAG, jsonArray.toString());
                    businessList = Business.fromJsonArray(jsonArray);
                    Log.i(TAG, String.valueOf(businessList.size()));

                    // Test details
                    Business business = businessList.get(0);
                    String testId = business.getYelpId();
                    yelpClient.getBusinessDetails(testId, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i(TAG, json.toString());
                            try {
                                JSONObject hours = json.jsonObject.getJSONArray("hours").getJSONObject(0);
                                List<Hour> hoursList = Hour.fromJsonArray(hours.getJSONArray("open"), business);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        }, location);

    }
}