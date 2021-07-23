package com.example.fbu_app.models;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class Friend {

    private boolean checked;
    private ParseUser friendUser;

    public Friend(ParseUser friendUser) {
        this.checked = false;
        this.friendUser = friendUser;
    }

    public void setFriendUser(ParseUser friendUser) {
        this.friendUser = friendUser;
    }

    public ParseUser getFriendUser() {
        return friendUser; }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
}
