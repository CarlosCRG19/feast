package com.example.fbu_app.fragments.ProfileFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fbu_app.R;
import com.example.fbu_app.models.FriendRequest;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OtherProfileFragment extends ProfileFragment{

    // Button for sending friend requests
    Button btnFriendStatus;

    // Boolean value to check if users are friends
    boolean areFriends;

    // Current user seeing this profile
    ParseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current user
        currentUser = ParseUser.getCurrentUser();

        // Assign value for button
        btnFriendStatus = view.findViewById(R.id.btnFriendStatus);

        // Assign button click listener to send a request
        btnFriendStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new friend request
                FriendRequest friendRequest = new FriendRequest();
                // Set arguments
                friendRequest.setFromUser(currentUser);
                friendRequest.setToUser(profileUser);
                friendRequest.setStatus("pending");
                // Save friend request using background thread
                friendRequest.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Check for errors
                        if (e != null) {
                            Log.i("FriendRequest", "Error sending friend request");
                            return;
                        }
                        // Display success message
                        Toast.makeText(getContext(), "Friend request sent!", Toast.LENGTH_SHORT).show();
                        // Disable button
                        btnFriendStatus.setEnabled(false);
                        // Change text
                        btnFriendStatus.setText("Pending");
                    }
                });
            }
        });
        // The button starts without being able to click on it, after verifying if there already
        // exist a request or that both users are friends, the state might change
        btnFriendStatus.setEnabled(false);
        // Verify friendship status
        verifyFriendship();

    }

    // Verifies if current user and profile user are friends
    private void verifyFriendship() {
        // Get current user friends
        List<ParseUser> currentUserFriends = (List) currentUser.get("friends");
        if(currentUserFriends != null && currentUserFriends.contains(profileUser)){
            // Change btnText
            btnFriendStatus.setText("Friends ✓");
            // Enable button
            btnFriendStatus.setEnabled(false);
        } else {
            checkFriendRequest();
        }
    }

    // Check for pending friend request
    private void checkFriendRequest() {
        // Specify query type
        ParseQuery<FriendRequest> query = new ParseQuery<>(FriendRequest.class);
        // Include both users in query
        query.include("fromUser");
        query.include("toUser");
        // Make query
        query.getFirstInBackground(new GetCallback<FriendRequest>() {
            @Override
            public void done(FriendRequest friendRequest, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.i("FriendRequest", "Error getting friend request");
                    return;
                }
                // Check if friendRequest is null
                if (friendRequest == null) {
                    // Change text of button
                    btnFriendStatus.setText("Send friend request");
                    // Enable button
                    btnFriendStatus.setEnabled(true);
                    return;
                }

                // Check if status is pending
                if (friendRequest.getStatus().equals("pending")) {
                    // Change text of button
                    btnFriendStatus.setText("Pending");
                    // Enable button
                    btnFriendStatus.setEnabled(false);
                    return;
                }

                // If status is not pending, user can send another invite
                btnFriendStatus.setText("Send friend request");
                // Enable button
                btnFriendStatus.setEnabled(true);
            }
        });

    }

}
