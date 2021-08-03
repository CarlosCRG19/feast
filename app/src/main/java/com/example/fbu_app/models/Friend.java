package com.example.fbu_app.models;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

// Local class that represents a friend. This class is used only for invite purposes
// and stores a user (the friend) and a checked value that represents whether the user has been selected for invitation
public class Friend {

    // FIELDS
    private boolean checked; // flag that indicates if the friend is currently being selected for invitation
    private ParseUser friendUser; // user that is friends with the current user

    // CONSTRUCTOR
    public Friend(ParseUser friendUser) {
        this.checked = false;
        this.friendUser = friendUser;
    }

    // SETTERS
    public void setFriendUser(ParseUser friendUser) {
        this.friendUser = friendUser;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    // GETTERS
    public ParseUser getFriendUser() { return friendUser; }

    public boolean isChecked() {
        return checked;
    }
}
