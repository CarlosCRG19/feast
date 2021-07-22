package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fbu_app.R;
import com.example.fbu_app.adapters.InvitationAdapter;
import com.example.fbu_app.adapters.VisitsAdapter;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NextVisitsFragment extends Fragment {

    // VIEWS
    TextView tvInvitations;
    RecyclerView rvVisits; // RV to display visits
    Button btnPastVisits; // Button to go to past visits

    // Model to store visits data
    List<Visit> visits;
    // Adapter for RecyclerView
    VisitsAdapter adapter;

    // TEST INVITATION
    InvitationAdapter invitationAdapter;
    List<VisitInvitation> invitations;
    RecyclerView rvInvitations;

    // Required empty constructor
    public NextVisitsFragment() {};

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_next_visits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init visits list and adapter
        visits = new ArrayList<>();
        adapter = new VisitsAdapter(getContext(), visits);

        // Init invitation list and adapter
        invitations = new ArrayList<>();
        invitationAdapter = new InvitationAdapter(getContext(), invitations);

        // Find tv invitations
        tvInvitations = view.findViewById(R.id.tvInvitations);

        // Setup RecyclerView for invitations
        rvInvitations = view.findViewById(R.id.rvVisitInvitations);
        rvInvitations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInvitations.setAdapter(invitationAdapter);

        // Setup RecyclerView for visits
        rvVisits = view.findViewById(R.id.rvNextVisits);
        rvVisits.setLayoutManager(new LinearLayoutManager(getContext()));
        rvVisits.setAdapter(adapter);

        // Button to pass to next visits
        btnPastVisits = view.findViewById(R.id.btnPastVisits);
        btnPastVisits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PastVisitsFragment pastVisitsFragment = new PastVisitsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, pastVisitsFragment)
                        .commit();
            }
        });

        // Make query for NextVisits
        queryNextVisits();
        queryInvitations();

//        // Enable refresh feature
//        setRefreshFeature(view);

    }

    // Makes a query to parse DataBase and gets visits whos date is greater or equal than todays
    private void queryNextVisits() {
        // Specify which class we want to query
        ParseQuery<Visit> query = ParseQuery.getQuery(Visit.class);
        // Include business object in query
        query.include("business");
        // Include visit creator
        query.include("user");
        // Include attendees list
        query.include("attendees");
        // Set max date as today
        query.whereGreaterThanOrEqualTo("date", Date.valueOf(LocalDate.now().toString()));
        // Add new attendee
        query.whereEqualTo("attendees", ParseUser.getCurrentUser());
        // order posts by date
        query.addDescendingOrder("date");
        // Make query using background thread
        query.findInBackground(new FindCallback<Visit>() {
            @Override
            public void done(List<Visit> visitsList, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.e("NextVisitsFragment", "Issue getting visits", e);
                    return;
                }
                // Clear list
                visits.clear();
                // Add values to visits
                visits.addAll(visitsList);
                // Notify adapter
                adapter.notifyDataSetChanged();
                // Hide refreshing icon
//                swipeContainer.setRefreshing(false);
//                return;
            }
        });
    }

    // Makes a query to parse DataBase and gets visits whos date is greater or equal than todays
    private void queryInvitations() {
        // Specify which class we want to query
        ParseQuery<VisitInvitation> query = ParseQuery.getQuery(VisitInvitation.class);
        // Include business object in query
        query.include("visit");
        query.include("visit.business");
        query.include("status");
        query.include("fromUser");
        query.include("fromUser.username");
        // Set max date as today
        query.whereEqualTo("status", "pending");
        query.whereEqualTo("toUser", ParseUser.getCurrentUser());
        // Make query using background thread
        query.findInBackground(new FindCallback<VisitInvitation>() {
            @Override
            public void done(List<VisitInvitation> invitationList, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.e("NextVisitsFragment", "Issue getting invitations", e);
                    return;
                }
                if(invitationList.size() > 0) {
                    tvInvitations.setVisibility(View.VISIBLE);
                    tvInvitations.setText("Your Invitations!");
                    // Clear list
                    invitations.clear();
                    // Add values to invitations
                    invitations.addAll(invitationList);
                    // Notify adapter
                    invitationAdapter.notifyDataSetChanged();
                    // Hide refreshing icon
//                    swipeContainer.setRefreshing(false);
                    return;
                }
            }
        });
    }
}

