package com.example.fbu_app.helpers;

import androidx.core.util.Pair;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestHeaders;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fbu_app.BuildConfig;

public class YelpClient extends AsyncHttpClient {

    public static final String YELP_API_KEY = BuildConfig.YELP_API_KEY;
    public static final String SEARCH_END_POINT = "https://api.yelp.com/v3/businesses/search"; // This endpoint returns up to 1000 businesses based on the provided search criteria. It has some basic information about the business.
    public static final String DETAILS_END_POINT = "https://api.yelp.com/v3/businesses/"; // This endpoint returns detailed business content like telephone, open hours and more photos

    // Gets businesses according to the criteria specified by the user
    public void getMatchingBusinesses(JsonHttpResponseHandler handler, Pair<String, String>... filters) {

        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + YELP_API_KEY);

        RequestParams params = new RequestParams();

        for (Pair <String, String> filter : filters) {
            params.put(filter.first, filter.second);
        }

        params.put("limit", 50);

        get(SEARCH_END_POINT, headers, params, handler);

    }

    // Gets detailed information for the restaurants that have been selected
    public void getBusinessDetails(String businessId, JsonHttpResponseHandler handler) {

        String requestUrl = DETAILS_END_POINT + businessId;

        RequestHeaders headers = new RequestHeaders();
        headers.put("Authorization", "Bearer " + YELP_API_KEY);

        RequestParams params = new RequestParams();

        get(requestUrl, headers, params, handler);

    }

}
