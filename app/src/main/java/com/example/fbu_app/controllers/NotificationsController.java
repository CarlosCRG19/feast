package com.example.fbu_app.controllers;

import android.util.Log;

import com.example.fbu_app.models.FriendRequest;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

//
public class NotificationsController {

    // Creates installation object with current user
    public static void setupPushNotifications() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "192956268173");
        // Save installation with current user
        installation.put("user", ParseUser.getCurrentUser());
        // Save installation in database
        installation.saveInBackground();
    }

    // Sends a push notification to a user that received a visit invitation
    public static void sendVisitInvitationPush(VisitInvitation visitInvitation) {
        // Create message for notification
        String message = visitInvitation.getFromUser().getUsername() + " is inviting you to " + visitInvitation.getVisit().getBusiness().getName() + "!";

        // Create query for installation
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", visitInvitation.getToUser());

        // Send push notification to installation
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // set installation query
        push.setMessage(message);
        push.sendInBackground();
    }

    // Sends a push notification to a user that received a friend request
    public static void sendFriendRequestPush(FriendRequest friendRequest) {
        // Create message for notification
        String message = friendRequest.getFromUser().getUsername() + " sent you a friend request!" ;

        // Create query for installation
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user", friendRequest.getToUser());

        // Send push notification to installation
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // set installation query
        push.setMessage(message);
        push.sendInBackground();
    }


}
