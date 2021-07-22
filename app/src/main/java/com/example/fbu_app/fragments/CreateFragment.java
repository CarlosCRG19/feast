package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_app.R;
import com.example.fbu_app.adapters.UserAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

// Simple fragment to create a new visit
public class CreateFragment extends Fragment {

    // Views from layout
    Button btnCreate;

    // Search users views
    SearchView svPeople;
    RecyclerView rvSearch;

    // List of obtained users
    List<ParseUser> userList;
    // Adapter for search RV
    UserAdapter adapter;

    public CreateFragment() {};

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize list and adapter
        userList = new ArrayList<>();
        adapter = new UserAdapter(getContext(), userList);

        // Initialize RV
        rvSearch = view.findViewById(R.id.rvSearch);
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearch.setAdapter(adapter);

        // Set searchView
        svPeople = view.findViewById(R.id.svPeople);
        svPeople.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() < 3) {
                    userList.clear();
                    adapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        // Set listener for button
        btnCreate = view.findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new location fragment
                LocationFragment locationFragment = new LocationFragment();
                // Make fragment transaction
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, locationFragment)
                        .commit();
                // Change navbar visibility
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
                // Change visibility of toolbar
                bottomNavigationView.setVisibility(View.GONE);
            }
        });
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
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

}
