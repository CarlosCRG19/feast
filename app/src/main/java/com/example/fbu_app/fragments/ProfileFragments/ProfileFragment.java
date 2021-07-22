package com.example.fbu_app.fragments.ProfileFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.adapters.BusinessAdapter;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Like;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment"; // TAG for log messages
    public static final String USER_TAG = "user"; // tag to pass user

    // User object
    ParseUser profileUser;

    // Views
    ImageView ivProfile;
    TextView tvUsername, tvEmail;
    RecyclerView rvBusinesses; // View group to display user's favorited restaurants

    BusinessAdapter adapter;
    List<Business> likedBusinesses;

    // Required empty public constructor
    public ProfileFragment() {}

    // REQUIRED METHODS

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get profile user
        profileUser = getArguments().getParcelable(USER_TAG);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init liked businesses list
        likedBusinesses = new ArrayList<>();

        // Set views from specified layout
        setViews(view);
        // Bind profile info
        populateViews();

        // Setup RV
        adapter = new BusinessAdapter(getContext(), likedBusinesses);
        rvBusinesses.setAdapter(adapter);
        rvBusinesses.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get liked businesses
        queryLikedBusinesses();

    }

    // VIEWS METHODS

    protected void setViews(View view) {
        // User info
        ivProfile = view.findViewById(R.id.ivProfile);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        // Recycler view
        rvBusinesses = view.findViewById(R.id.rvBusinesses);
    }

    // Binds the views with the users data
    protected void populateViews() {
        // Set image
        ParseFile profileImage = (ParseFile) profileUser.get("profileImage");
        Glide.with(getContext())
                .load(profileImage.getUrl())
                .circleCrop()
                .into(ivProfile);
        // Fill TVs with username and email
        tvUsername.setText(profileUser.getUsername());
        tvEmail.setText((String) profileUser.get("email"));
    }

    // LISTENERS AND FEATURES

    // QUERY METHODS
    public void queryLikedBusinesses() {
        // Specify what type of data we want to query - Comment.class
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        // Limit query to latest 20 items and include the user
        query.include("business");
        // Limit query to only those comments that belong to this post
        query.whereEqualTo("user", profileUser);
        // Start async call for comments
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }
                // Clear the list of liked businesses
                likedBusinesses.clear();
                // Get businesses from like objects
                getBusinessesFromLikes(likes);
            }
        });
    }

    // Adds businesses objects referenced in likes
    public void getBusinessesFromLikes(List<Like> likes) {
        // For each like, add its respective business
        for (Like like : likes) {
            likedBusinesses.add(like.getBusiness());
        }
        // Notify adapter of change
        adapter.notifyDataSetChanged();
    }
}
