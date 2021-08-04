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
import com.example.fbu_app.controllers.ImagesController;
import com.example.fbu_app.fragments.ProfileFragments.OtherProfileFragment;
import com.example.fbu_app.fragments.ProfileFragments.OwnProfileFragment;
import com.example.fbu_app.fragments.ProfileFragments.ProfileFragment;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Class to populate the User search on create screen
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    // Codes for ViewHolders
    public static final int BASIC_USER_CODE = 12;
    public static final int SEARCH_USER_CODE = 42;

    // FIELDS
    private Context context;
    private List<ParseUser> users;
    private int userType;

    // CONSTRUCTOR
    public UserAdapter(Context context, List<ParseUser> parseUsers, int userType) {
        this.context = context;
        this.users = parseUsers;
        this.userType = userType;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        // Assign layout and holder for specific user type
        if (viewType == BASIC_USER_CODE) {
            View view = inflater.inflate(R.layout.attendee_layout, parent, false);
            return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.user_layout, parent, false);
            return new SearchViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return userType;
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
        // USER FOR BINDING
        protected ParseUser user;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get views from layout
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
            // Set click listener to go to the users profile
            itemView.setOnClickListener(this);
        }

        // Connect hour data to the views
        public void bind(ParseUser userToBind) {
            // Assign user value
            user = userToBind;

            // Get profile picture
            ParseFile profileImage = (ParseFile) user.get("profileImage");
            // Load Profile Picture using static method
            ImagesController.loadCircleImage(context, profileImage.getUrl(), ivUserImage);

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

    public class SearchViewHolder extends ViewHolder {

        // Views
        private TextView tvUsername, tvEmail;

        public SearchViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // Find views in layout
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvName);
        }

        @Override
        public void bind(ParseUser userToBind) {
            super.bind(userToBind);

            // Set TVs with the users data
            tvUsername.setText(user.getUsername());
            tvEmail.setText((String) user.get("firstName") + " " + (String) user.get("lastName"));

        }
    }
}
