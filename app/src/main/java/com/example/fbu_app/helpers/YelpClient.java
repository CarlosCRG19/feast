package com.example.fbu_app.helpers;

import androidx.core.util.Pair;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fbu_app.BuildConfig;

import java.util.List;

/*
 * Lightweight asynchronous HTTP client that inherits from Codepath's AsyncHttpClient:
 * https://guides.codepath.org/android/Using-Codepath-Async-Http-Client
 */
public class YelpClient extends AsyncHttpClient {

    public static final String YELP_API_KEY = BuildConfig.YELP_API_KEY; // API secured in BuildConfig
    public static final String SEARCH_END_POINT = "https://api.yelp.com/v3/businesses/search"; // This endpoint returns up to 1000 businesses based on the provided search criteria. It has some basic information about the business.
    public static final String DETAILS_END_POINT = "https://api.yelp.com/v3/businesses/"; // This endpoint returns detailed business content like telephone, open hours and more photos

    // Gets businesses according to the criteria specified by the user
    public void getMatchingBusinesses(JsonHttpResponseHandler handler, List<Pair<String, String>> filters) {

        // Set Authorization header with the API Key
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + YELP_API_KEY);

        // Create new params
        RequestParams params = new RequestParams();

        // Get params from varargs
        for (Pair <String, String> filter : filters) {
            params.put(filter.first, filter.second);
        }

        // Set request limit to 50 businesses
        params.put("limit", 50);

        // Make request
        get(SEARCH_END_POINT, headers, params, handler);

    }

    // Gets detailed information for the restaurants that have been selected
    public void getBusinessDetails(String businessId, JsonHttpResponseHandler handler) {

        // Add the id of the business to the end of end point
        String DETAILS_URL = DETAILS_END_POINT + businessId;

        // Set Authorization header with API key
        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + YELP_API_KEY);

        // Params object required to make the request
        RequestParams params = new RequestParams();

        // Make request
        get(DETAILS_URL, headers, params, handler);

    }

}
