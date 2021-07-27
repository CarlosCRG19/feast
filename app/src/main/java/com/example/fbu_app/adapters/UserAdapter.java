package com.example.fbu_app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.fragments.ProfileFragments.OtherProfileFragment;
import com.example.fbu_app.fragments.ProfileFragments.OwnProfileFragment;
import com.example.fbu_app.fragments.ProfileFragments.ProfileFragment;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Class to populate the User search on create screen
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    // FIELDS
    private Context context;
    private List<ParseUser> users;

    // CONSTRUCTOR
    public UserAdapter(Context context, List<ParseUser> parseUsers) {
        this.context = context;
        this.users = parseUsers;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    // Add an entire array of hours
    public void addAll(List<ParseUser> userList) {
        users.addAll(userList);
        notifyDataSetChanged();
    }

    // Removes all businesses from the adapter
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }


    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        public static final String USER_TAG = "user"; // tag to pass user

        // VIEWS
        private ImageView ivUserImage;
        private TextView tvUsername, tvEmail;

        // USER FOR BINDING
        ParseUser user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get views from layout
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);

            // Set click listener to go to the users profile
            itemView.setOnClickListener(this);
        }

        // Connect hour data to the views
        public void bind(ParseUser userToBind) {
            // Assign user value
            user = userToBind;

            // Get profile picture
            ParseFile profileImage = (ParseFile) user.get("profileImage");
            // Use glide to load PP
            Glide.with(context)
                    .load(profileImage.getUrl())
                    .circleCrop()
                    .into(ivUserImage);

            // Set TVs with the users data
            tvUsername.setText(user.getUsername());
            tvEmail.setText((String) user.get("firstName") + " " + (String) user.get("lastName"));
        }


        @Override
        public void onClick(View v) {
            // Create new bundle to pass args
            Bundle bundle = new Bundle();
            bundle.putParcelable(USER_TAG, user);
            // Create fragment
            ProfileFragment profileFragment;
            // Verify if user is current user
            ParseUser currentUser = ParseUser.getCurrentUser();
            if(user.getObjectId().equals(currentUser.getObjectId())) {
                // if this user and current user user are the same,
                // launch OwnProfileFragment
                profileFragment = new OwnProfileFragment();
            } else {
                // if they are different, launch OtherProfile
                profileFragment = new OtherProfileFragment();
            }
            // Set fragment arguments
            profileFragment.setArguments(bundle);
            // Make fragment transaction adding to back stack
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, profileFragment)
                    .addToBackStack(null)
                    .commit();
        }




    }
}
