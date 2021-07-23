package com.example.fbu_app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.fragments.ProfileFragments.OtherProfileFragment;
import com.example.fbu_app.fragments.ProfileFragments.OwnProfileFragment;
import com.example.fbu_app.fragments.ProfileFragments.ProfileFragment;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.FriendRequest;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>{

    private static final int FRIEND_REQUEST_NOTIFICATION = 123;
    private static final int VISIT_INVITE_NOTIFICATION = 321;

    private Context context;
    private List<ParseObject> requests;

    public NotificationsAdapter(Context context, List<ParseObject> requests){
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @NotNull
    @Override
    public NotificationsAdapter.NotificationViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_layout, parent, false);
        // Delegate view holder depending on viewType
        if (viewType == VISIT_INVITE_NOTIFICATION)
            return new VisitInvitationViewHolder(view);
        else
            return new FriendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotificationsAdapter.NotificationViewHolder holder, int position) {
        holder.bindRequest(requests.get(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(requestType(position))
            return VISIT_INVITE_NOTIFICATION;
        else
            return FRIEND_REQUEST_NOTIFICATION;
    }

    // Returns true if it is a visit invitation, false if it is a friend request
    private boolean requestType(int position) {
        ParseObject request = requests.get(position);
        if (request instanceof VisitInvitation)
            return true;
        else
            return false;
    }

    // Create abstract class for both kind of notifications
    public abstract class NotificationViewHolder extends RecyclerView.ViewHolder {

        // VIEWS
        protected ImageView ivProfilePicture;
        protected TextView tvNotification;

        public NotificationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            // Assign value to views
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvNotification = itemView.findViewById(R.id.tvNotification);

        }

        abstract void bindRequest(ParseObject request);
    }

    // Create ViewHolder for VisitInvitation
    public class VisitInvitationViewHolder extends NotificationViewHolder {

        // Member variable to store the VisitInvitation
        VisitInvitation visitInvitation;

        public VisitInvitationViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        @Override
        void bindRequest(ParseObject request) {
            // Save visit invitation value
            visitInvitation = (VisitInvitation) request;
            // Get status
            String status = visitInvitation.getStatus();
            // Get the visit object
            Visit visit = visitInvitation.getVisit();
            // Get user that received request
            ParseUser user = visitInvitation.getToUser();
            // Get profile picture for the user
            ParseFile parseFile = (ParseFile) user.get("profileImage");
            // Bind data to views
            Glide.with(context)
                    .load(parseFile.getUrl())
                    .circleCrop()
                    .into(ivProfilePicture);
            // Create notification text
            String notificationText = user.getUsername() + " has " + status + " your Visit Invitation to " + visit.getBusiness().getName();
            tvNotification.setText(notificationText);
            // Delete VisitInvitation from database
            destroyVisitInvitation();

        }

        // Destroys the current VisitInvitation in database
        private void destroyVisitInvitation() {
            visitInvitation.deleteInBackground();
        }
    }

    // Create ViewHolder for FriendRequest
    public class FriendRequestViewHolder extends NotificationViewHolder {

        // Member Variable to store the request
        FriendRequest friendRequest;

        public FriendRequestViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        @Override
        void bindRequest(ParseObject request) {
            // Save request value
            friendRequest = (FriendRequest) request;
            // Get request status
            String status = friendRequest.getStatus();
            // Get user that received request
            ParseUser user = friendRequest.getToUser();
            // Get profile picture
            ParseFile profilePicture = (ParseFile) user.get("profileImage");
            // Bind data to views
            Glide.with(context)
                    .load(profilePicture.getUrl())
                    .circleCrop()
                    .into(ivProfilePicture);
            // Set text
            String notificationText = user.getUsername() + " has " + status + " your friend request.";
            tvNotification.setText(notificationText);
        }
    }
}