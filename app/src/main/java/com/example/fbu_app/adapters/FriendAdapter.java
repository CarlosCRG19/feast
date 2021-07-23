package com.example.fbu_app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.fragments.ProfileFragments.OtherProfileFragment;
import com.example.fbu_app.fragments.ProfileFragments.OwnProfileFragment;
import com.example.fbu_app.fragments.ProfileFragments.ProfileFragment;
import com.example.fbu_app.models.Friend;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{

    // FIELDS
    private Context context;
    private List<Friend> friends;

    // CONSTRUCTOR
    public FriendAdapter(Context context, List<Friend> parseUsers) {
        this.context = context;
        this.friends = parseUsers;
    }

    @NonNull
    @NotNull
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false);
        return new FriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    // Add an entire array of hours
    public void addAll(List<Friend> friendsList) {
        friends.addAll(friendsList);
        notifyDataSetChanged();
    }

    // Removes all businesses from the adapter
    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    // Gets selected friends
    public ArrayList<Friend> getSelected() {
        // Create array to return
        ArrayList<Friend> selected = new ArrayList<>();
        // Make for loop to pass through each user
        for (int i=0 ; i < getItemCount() ; i++) {
            // Check if users are checked
            if (friends.get(i).isChecked()) {
                // Add checked users
                selected.add(friends.get(i));
            }
        }
        return selected;
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        // VIEWS
        private RelativeLayout rlUser;
        private final ImageView ivUserImage;
        private final TextView tvUsername;
        private final TextView tvEmail;

        // USER FOR BINDING
        Friend friend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get views from layout
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            rlUser =  itemView.findViewById(R.id.rlUser);

            // Set click listener to go to the users profile
            itemView.setOnClickListener(this);
        }

        // Connect friend data to the views
        public void bind(Friend friendToBind) {
            // Assign friend value
            friend = friendToBind;

            // Get user of this friend
            ParseUser friendUser = friend.getFriendUser();
            // Get profile picture
            ParseFile profileImage = (ParseFile) friendUser.get("profileImage");
            // Use glide to load PP
            Glide.with(context)
                    .load(profileImage.getUrl())
                    .into(ivUserImage);

            // Set TVs with the friend's data
            tvUsername.setText(friendUser.getUsername());
            tvEmail.setText((String) friendUser.get("firstName") + " " + (String) friendUser.get("lastName"));
        }


        @Override
        public void onClick(View v) {
            // Define friend check state
            friend.setChecked(!friend.isChecked());
            rlUser.setBackgroundResource(friend.isChecked() ? R.color.light_gray : R.color.white);
        }
    }
}