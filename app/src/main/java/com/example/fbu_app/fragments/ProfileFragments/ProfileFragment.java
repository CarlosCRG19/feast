package com.example.fbu_app.fragments.ProfileFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.adapters.BusinessAdapter;
import com.example.fbu_app.adapters.UserAdapter;
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
    TextView tvName, tvUsername, tvDescription, tvFavorites;

    // Recycler Views Setup

    // Adapters
    BusinessAdapter adapter;
    UserAdapter userAdapter; // Adapter for search RV

    // RecyclerView
    RecyclerView rvBusinesses; // View group to display user's favorited restaurants
    RecyclerView rvSearch;

    // Lists for users and businesses
    List<Business> likedBusinesses;
    List<ParseUser> userList;

    // Search View
    SearchView svPeople;

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

        // Make sure that bottom nav bar is being displayed
        ((MainActivity) getActivity()).showBottomNavBar();

        // Init liked businesses list
        likedBusinesses = new ArrayList<>();
        // Init users
        userList = new ArrayList<>();

        // Adapters for the RVs
        adapter = new BusinessAdapter(getContext(), likedBusinesses); // adapter for liked businesses
        userAdapter = new UserAdapter(getContext(), userList);

        // Set views from specified layout
        setViews(view);
        // Bind profile info
        populateViews();

        // RV FOR LIKED BUSINESSES SETUP

        // Add adapter to RV
        rvBusinesses.setAdapter(adapter);
        // Set layout manager for recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2); // use 3 columns for grid
        rvBusinesses.setLayoutManager(gridLayoutManager);

        // Get liked businesses
        queryLikedBusinesses();

        // RV FOR USER SEARCH

        // Create LinearLayoutManager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // Initialize RV
        rvSearch.setLayoutManager(linearLayoutManager);
        rvSearch.setAdapter(userAdapter);
        // Set item decoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvSearch.getContext(),
                linearLayoutManager.getOrientation());
        rvSearch.addItemDecoration(dividerItemDecoration);

        // SV SETUP

        // Set searchView Listeners
        svPeople.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // When text is submitted, check for users that match that query
                searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() < 3) {
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        // Set complete SV click listener
        svPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                svPeople.setIconified(false);
            }
        });
    }

    // VIEWS METHODS

    protected void setViews(View view) {
        // User info
        ivProfile = view.findViewById(R.id.ivProfile);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvName = view.findViewById(R.id.tvName);
        tvDescription = view.findViewById(R.id.tvDescription);
        tvFavorites = view.findViewById(R.id.tvFavorites);
        // Recycler view
        rvBusinesses = view.findViewById(R.id.rvBusinesses);
        rvSearch = view.findViewById(R.id.rvSearch);
        // Search views
        svPeople = view.findViewById(R.id.svPeople);

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
        // Set complete name text
        String firstName = (String) profileUser.get("firstName");
        String lastName = (String) profileUser.get("lastName");
        String fullName = firstName != null && lastName != null ? firstName + " " + lastName : "";
        // Set text for name
        tvName.setText(fullName);
        // Set text for description
        tvDescription.setText(profileUser.get("description") != null ? (String) profileUser.get("description") : "");
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

    // Method to query users whose username match the search
    private void searchUsers(String queryText) {
        // Create query and specify class
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include("email");
        // Search for users that start with that text
        query.whereStartsWith("username", queryText);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.i("CreateFragment", "Error while searching users", e);
                    return;
                }
                // Check if objects is null
                if (users != null) {
                    Log.i("CreateFragment", "Total users obtained: " + users.size());
                    userList.clear();
                    userList.addAll(users);
                    userAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
