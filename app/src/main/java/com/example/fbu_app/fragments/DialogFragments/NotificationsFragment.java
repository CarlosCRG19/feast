package com.example.fbu_app.fragments.DialogFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_app.R;
import com.example.fbu_app.adapters.NotificationsAdapter;
import com.example.fbu_app.models.FriendRequest;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificationsFragment extends DialogFragment {

    private RecyclerView rvRequests, rvConfirmations;

    // RVs values
    private List<ParseObject> confirmations;
    private NotificationsAdapter adapter;

    // Current user
    ParseUser currentUser;

    // Required empty constructor
    public NotificationsFragment() {}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current user from parse
        currentUser = ParseUser.getCurrentUser();

        // Initialize data and adapter
        confirmations = new ArrayList<>();
        adapter = new NotificationsAdapter(getContext(), confirmations);

        // Setup confirmations rv
        rvConfirmations = view.findViewById(R.id.rvConfirmations);
        rvConfirmations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvConfirmations.setAdapter(adapter);

        // Query for confirmations
        queryFriendRequests();
        queryVisitInvitations();
    }

    // Query VisitInvitationConfirmations
    private void queryVisitInvitations() {
        // Specify query type
        ParseQuery<VisitInvitation> query = new ParseQuery<>(VisitInvitation.class);
        // Include users
        query.include("toUser");
        // Include visit and business
        query.include("visit");
        query.include("visit.business");
        // Check that user is owner of request
        query.whereEqualTo("fromUser", currentUser);
        // Make query using background thread
        query.findInBackground(new FindCallback<VisitInvitation>() {
            @Override
            public void done(List<VisitInvitation> visitInvitations, ParseException e) {
                // Check for errors
                if (visitInvitations == null && e!= null) {
                    Log.i("VisitInvitations", "No requests found");
                    return;
                }
                // Add objects to data model
                confirmations.addAll(visitInvitations);
                // Notify adapter of change
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Query for FriendRequests
    private void queryFriendRequests() {
        // Specify queryTypes
        ParseQuery<FriendRequest> query = new ParseQuery<>(FriendRequest.class);
        // Include users
        query.include("toUser");
        // Check that user is owner of request
        query.whereEqualTo("fromUser", currentUser);
        // Make query using background thread
        query.findInBackground(new FindCallback<FriendRequest>() {
            @Override
            public void done(List<FriendRequest> friendRequests, ParseException e) {
                // Check for errors
                if (friendRequests == null && e!= null) {
                    Log.i("FriendRequest", "No requests found");
                    return;
                }
                // Add objects to data model
                confirmations.addAll(friendRequests);
                // Notify adapter of change
                adapter.notifyDataSetChanged();
            }
        });
    }
}
