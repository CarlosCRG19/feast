package com.example.fbu_app.fragments.DialogFragments;

import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_app.R;
import com.example.fbu_app.adapters.FriendAdapter;
import com.example.fbu_app.adapters.FriendRequestAdapter;
import com.example.fbu_app.adapters.NotificationsAdapter;
import com.example.fbu_app.controllers.NotificationsController;
import com.example.fbu_app.models.Friend;
import com.example.fbu_app.models.FriendRequest;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InviteFragment extends DialogFragment {

    public static final String VISIT_TAG = "visit"; // Tag to get visits from arguments

    // RecyclerView to see Friends
    private RecyclerView rvFriends;
    private List<Friend> friends;
    private FriendAdapter friendAdapter;

    // Button to send invite
    private ImageButton btnInvite;

    // Current user
    private ParseUser currentUser;

    // Visit object obtained from bundle
    private Visit visit;

    // Required empty constructor
    public InviteFragment() {}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite, container);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get visit from arguments
        visit = (Visit) getArguments().get(VISIT_TAG);

        // Get current user from parse
        currentUser = ParseUser.getCurrentUser();

        // Initialize lists friends
        friends = new ArrayList<>();
        // Initialize adapter
        friendAdapter = new FriendAdapter(getContext(), friends);

        // Set views and listeners
        setViews(view);
        setListeners();

        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));
        rvFriends.setAdapter(friendAdapter);

        // Get friends
        queryFriends();
    }


    // VIEW METHODS

    private void setViews(View view) {
        // Set button to send invites to friends
        btnInvite = view.findViewById(R.id.btnInvite);
        // Setup friend requests rv
        rvFriends = view.findViewById(R.id.rvFriends);
    }

    private void setListeners() {
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected friends
                List<Friend> selectedFriends = friendAdapter.getSelected();
                // For each friend, send an invite
                for (int i=0 ; i < selectedFriends.size() ; i++) {
                    sendInvitation(selectedFriends.get(i).getFriendUser(), i == selectedFriends.size() - 1);
                }
            }
        });
    }


    // Query for friends via accepted friend requests
    private void queryFriends() {

        // FIRST QUERY (CHECK GET REQUESTS SENT BY USER)

        // Specify query type
        ParseQuery<FriendRequest> queryMadeRequest = new ParseQuery<>(FriendRequest.class);
        queryMadeRequest.whereEqualTo("fromUser", currentUser);
        // Check status of request
        queryMadeRequest.whereEqualTo("status", "accepted");

        // SECOND QUERY (REQUESTS RECEIVED BY USER)

        // Specify query type
        ParseQuery<FriendRequest> queryReceivedRequest = new ParseQuery<>(FriendRequest.class);
        queryReceivedRequest.whereEqualTo("toUser", currentUser);
        // Check status of request
        queryReceivedRequest.whereEqualTo("status", "accepted");

        // COMPOUND QUERY

        ParseQuery<FriendRequest> compoundQuery = new ParseQuery<>(FriendRequest.class).or(Arrays.asList(queryMadeRequest, queryReceivedRequest));
        // Include both users in query
        compoundQuery.include("fromUser");
        compoundQuery.include("toUser");

        // Make query
        compoundQuery.findInBackground(new FindCallback<FriendRequest>() {
            @Override
            public void done(List<FriendRequest> friendRequests, ParseException e) {
                // Check if objects werefound
                if (e != null) {
                    Log.i("FriendRequest", "No friends found");
                    return;
                }
                // Clear friends array
                friends.clear();
                // Check each request and get the friend user
                for (FriendRequest request : friendRequests) {
                    Friend friend;
                    // Compare toUser id with the current user id
                    if (request.getToUser().getObjectId().equals(currentUser.getObjectId())){
                        // Check if user is already in list of attendees
                        if(!visit.getAttendees().contains(request.getFromUser())) {
                            // If request was made by current user, get the receiver
                            friend = new Friend(request.getFromUser());
                            friends.add(friend);
                        }
                    } else {
                        // Check if user is already invited
                        if(!visit.getAttendees().contains(request.getToUser())) {
                            // In the other case, get the user that sent the request
                            friend = new Friend(request.getToUser());
                            friends.add(friend);
                        }
                    }
                }
                friendAdapter.notifyDataSetChanged();
            }
        });
    }

    // Sends invitation to the visit
    private void sendInvitation(ParseUser user, boolean isLast) {
        // Create new invitation object
        VisitInvitation visitInvitation = new VisitInvitation();
        // Set the visit for this invite
        visitInvitation.setVisit(visit);
        // Assign users
        visitInvitation.setFromUser(ParseUser.getCurrentUser());
        visitInvitation.setToUser(user);
        // Set status
        visitInvitation.setStatus("pending");
        // Save invitation using background thread
        visitInvitation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.i("FriendInvite", "Error sending invite");
                    return;
                }

                // Send push notification to invited user
                NotificationsController.sendVisitInvitationPush(visitInvitation);

                if (isLast) {
                    // Display success message
                    Toast.makeText(getContext(), "Friends invited to Feast!", Toast.LENGTH_SHORT).show();
                    // Return to previous fragment
                    dismiss();
                }
            }
        });
    }
}


