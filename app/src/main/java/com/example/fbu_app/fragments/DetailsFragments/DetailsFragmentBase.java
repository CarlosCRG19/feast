package com.example.fbu_app.fragments.DetailsFragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.adapters.HoursAdapter;
import com.example.fbu_app.fragments.ConfirmationFragment;
import com.example.fbu_app.helpers.YelpClient;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Hour;
import com.example.fbu_app.models.Like;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Headers;

public class DetailsFragmentBase extends Fragment {

    public static final String VISIT_TAG = "visit"; // Simple tag to save objects in Parse

    protected Business business;

    // Layout views as member variables
    private ImageView ivBusinessImage;
    private TextView tvName, tvPrice, tvAddress, tvTelephone, tvCategories, tvHours;
    private RatingBar rbRating;

    HoursAdapter adapter;
    List<Hour> hours;
    RecyclerView rvHours;

    YelpClient yelpClient;

    // Like button
    ImageButton btnLike;

    // Object to retreive like from database or create a new like
    private Like userLike;

    // Current user from parse
    ParseUser currentUser;

    public DetailsFragmentBase(){}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
       // Hide Activity's bottomAppBar
        ((MainActivity) getActivity()).hideBottomNavBar();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current user
        currentUser = ParseUser.getCurrentUser();

        yelpClient = new YelpClient();

        business = getArguments().getParcelable("business");

        // Use private method to assign views values
        setViews(view);
        populateViews();

        hours = new ArrayList<>();
        adapter = new HoursAdapter(getContext(), hours);

        rvHours = view.findViewById(R.id.rvHours);
        rvHours.setAdapter(adapter);
        rvHours.setLayoutManager(new LinearLayoutManager(getContext()));

        // check if the business is already in database and setup listeners
        verifyBusinessAndSetListeners();

        yelpClient.getBusinessDetails(business.getYelpId(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {

                    JSONObject hoursJSON = json.jsonObject.getJSONArray("hours").getJSONObject(0);
                    hours = Hour.fromJsonArray(hoursJSON.getJSONArray("open"), business);
                    // Check if hours where received
                    if(hours.size() > 0) {
                        // Add hours to adapter
                        adapter.addAll(hours);
                        // Change Hours text visibility
                        tvHours.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.i("DetailsFragmentBase", "Failure getting details: " + response, throwable);
            }
        });

    }

    // Assigns views to member variables
    private void setViews(View view) {
        // ImageView for business photo
        ivBusinessImage = view.findViewById(R.id.ivBusinessImage);
        // Text views
        tvName = view.findViewById(R.id.tvName);
        tvPrice =view.findViewById(R.id.tvPrice);
        tvCategories = view.findViewById(R.id.tvCategories);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvTelephone = view.findViewById(R.id.tvTelephone);
        tvHours = view.findViewById(R.id.tvHours);
        // Assign yelp rating bar
        rbRating = view.findViewById(R.id.rbRating);
        // Like button
        btnLike = view.findViewById(R.id.btnLike);
    }

    // Binds the business data with the views
    private void populateViews() {
        // Use glide to populate ImageView
        Glide.with(getContext())
                .load(business.getImageUrl())
                .centerCrop()
                .into(ivBusinessImage);

        // Set TVs with info from the business
        tvName.setText(business.getName());
        // Check if price value exists
        if(business.getPrice().length() > 0) {
            // Set price text
            String textPrice = business.getPrice() + " · ";
            tvPrice.setText(textPrice);
        }
        // Set categories text
        tvCategories.setText(Business.formatCategories(business.getCategories()));
        // Set address text
        tvAddress.setText(business.getAddress());
        // Set telephone text
        if(business.getTelephone().length() > 0) {
            tvTelephone.setText(business.getTelephone());
        } else {
            tvTelephone.setVisibility(View.INVISIBLE);
        }
        // Use business rating to setup rating bar
        rbRating.setRating(business.getRating());
    }

    // Checks if local business is already in database, if it is, the value of business is changed to match that one
    // after that, enables all the listeners
    protected void verifyBusinessAndSetListeners() {
        // Specify type of query
        ParseQuery<Business> query = ParseQuery.getQuery(Business.class);
        // Search for business in database based on yelpId
        query.whereEqualTo("yelpId", business.getYelpId());
        // Use getFirstInBackground to finish the search if it has found one matching business
        query.getFirstInBackground(new GetCallback<Business>() {
            @Override
            public void done(Business object, ParseException e) {
                if(e != null) {
                    Log.i("ParseSave", "Search for business", e);
                }
                // if the business exists, change value of member variable and create the new visit
                if (object != null) {
                    business = object;
                    // if business exists verify if the business has been liked
                    verifyUserLiked();
                }
                // Set listeners
                setClickListeners();
            }
        });
    }


    // Sets listeners for the different views
    protected void setClickListeners() {
        // set listener for like button
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if user has previously liked the business, unlike it
                if (userLike != null) {
                    saveUnlike();
                } else {
                    // create new like if business has not been liked before
                    saveLike();
                }
            }
        });

        // Set listener to make call
        tvTelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel: " + business.getTelephone();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    // SAVE (POST) METHODS

    // Creates a visit with the specified business and visit date
    protected void createVisit(Date visitDate, String visitDateStr) {
        // Create new visit
        Visit newVisit = new Visit();
        newVisit.setBusiness(business);
        // Add fields
        newVisit.setUser(currentUser);
        newVisit.addAttendee(currentUser);
        newVisit.setDate(visitDate);
        newVisit.setDateStr(visitDateStr);
        // Save visit using background thread
        newVisit.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.i("ParseSave", "Failed to save visit", e);
                    return;
                }
                // Display success message
                Toast.makeText(getContext(), "Succesfully created visit!", Toast.LENGTH_SHORT).show();
                // Create bundle to pass busines as argument
                Bundle bundle = new Bundle();
                bundle.putParcelable(VISIT_TAG, newVisit);
                // Transaction to new fragment
                ConfirmationFragment confirmationFragment = new ConfirmationFragment();
                confirmationFragment.setArguments(bundle);
                // Make fragment transaction
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, confirmationFragment)
                        .commit();

            }
        });
    }


    // Posts a like, changes the button background and changes the count on databes
    protected void saveLike() {
        // Create new like
        Like like = new Like();
        // Set fields
        like.setBusiness(business);
        like.setUser(currentUser);
        // Save like in database using background thread
        like.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                // Check for errors
                if(e != null) {
                    Toast.makeText(getContext(), "Error liking business!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Display success message
                Toast.makeText(getContext(), "Business liked!" , Toast.LENGTH_SHORT).show();

                // Change button background
                btnLike.setImageResource(R.drawable.ic_round_favorite_red);

                // Change userLike (now it won't be null)
                userLike = like;
            }
        });
    }

    // Takes current userLike object and deletes it from database
    protected void saveUnlike() {
        // Delete current like from database
        userLike.deleteInBackground(new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                // Check for errors
                if(e != null) {
                    Toast.makeText(getContext(), "Error unliking business!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display success message
                Toast.makeText(getContext(), "Business unliked!" , Toast.LENGTH_SHORT).show();

                // Change button background
                btnLike.setImageResource(R.drawable.ic_round_favorite);

                userLike = null; // Even though we delete the object on the database, that does not mean that our local variable has been deleted

            }
        });
    }

    // Checks whether the current user has liked the post. If that is the case, it changes the button background and userLike stays as null
    private void verifyUserLiked() {
        // Create query
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        // Include business object in query
        query.include("business");
        // Define attributes to look for (like is on this post and by this user)
        query.whereEqualTo("business", business);
        query.whereEqualTo("user", currentUser);
        // Get the like object
        query.getFirstInBackground(new GetCallback<Like>() { // getFirstInBackground ends the query when it has found the first object that matches the attributes (instead of going through every object)
            @Override
            public void done(Like foundLike, ParseException e) {
                if(e != null) { // e == null when no matching object has been found
                    btnLike.setImageResource(R.drawable.ic_round_favorite); // set button icon to just the stroke
                }
                if(foundLike != null){
                    // if the user has liked the business, change button and save that business
                    btnLike.setImageResource(R.drawable.ic_round_favorite_red); // change icon to filled heart
                    userLike = foundLike;
                    // Save that business
                    business = foundLike.getBusiness();
                }
            }
        });
    }


}



