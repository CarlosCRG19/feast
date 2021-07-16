package com.example.fbu_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.fbu_app.fragments.CreateFragment;
import com.example.fbu_app.fragments.NextVisitsFragment;
import com.example.fbu_app.fragments.ProfileFragment;
import com.example.fbu_app.helpers.YelpClient;
import com.example.fbu_app.models.Business;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public YelpClient yelpClient;

    BottomNavigationView bottomNavigationView;

    List<Business> businessList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Object responsible of adding, removing or replacing Fragments in the stack
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // Assign bottom navigation bar from layout
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        // Create listener for bottomNavigationView items
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                Fragment fragment;
                // Check which item was selected using ids
                switch (item.getItemId()) {
                    case R.id.action_history: // Launch compose fragment to create a post
                        fragment = new NextVisitsFragment();
                        break;
                    case R.id.action_profile: // Launch profile fragment (for current user)
                        // Assign fragment with new args
                        fragment = new ProfileFragment();
                        break;
                    default: // By default, go to main feed
                        fragment = new CreateFragment();
                        break;
                }
                // Change to selected fragment using manager
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set home option as default
        bottomNavigationView.setSelectedItemId(R.id.action_explore);

//        yelpClient = new YelpClient();
//
//        Pair<String, String> location = new Pair<>("location", "Seattle");
//        Log.i(TAG, "New query with params: " + location.first + " -> " + location.second);
//        yelpClient.getMatchingBusinesses(new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                JSONObject response = json.jsonObject;
//                try {
//                    JSONArray jsonArray = response.getJSONArray("businesses");
//                    Log.i(TAG, jsonArray.toString());
//                    businessList = Business.fromJsonArray(jsonArray);
//                    Log.i(TAG, "List of Businesses created with size of: " + String.valueOf(businessList.size()));
//
//                    // Test details
//                    Business business = businessList.get(0);
//                    Log.i(TAG, "EXAMPLE BUSINESS");
//                    Log.i(TAG, "YelpID: " + business.getYelpId());
//                    Log.i(TAG, "Name: " + business.getName());
//                    Log.i(TAG, "Address: " + business.getAddress());
//                    Log.i(TAG, "City: " + business.getCity());
//                    Log.i(TAG, "Country: " + business.getCountry());
//                    Log.i(TAG, "Rating: " + String.valueOf(business.getRating()));
//                    business.saveInBackground();
//
//                    String testId = business.getYelpId();
//                    yelpClient.getBusinessDetails(testId, new JsonHttpResponseHandler() {
//                        @Override
//                        public void onSuccess(int statusCode, Headers headers, JSON json) {
//                            try {
//                                JSONObject hours = json.jsonObject.getJSONArray("hours").getJSONObject(0);
//                                List<Hour> hoursList = Hour.fromJsonArray(hours.getJSONArray("open"), business);
//                                hoursList.get(0).saveInBackground();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//                        }
//                    });
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//            }
//        }, location);

    }
}