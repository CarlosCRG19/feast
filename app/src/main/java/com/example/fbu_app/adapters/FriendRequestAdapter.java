package com.example.fbu_app.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_app.R;
import com.example.fbu_app.controllers.ImagesController;
import com.example.fbu_app.models.FriendRequest;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>{
    // FIELDS
    private Context context;
    private List<FriendRequest> requests; //

    // Current user
    private ParseUser currentUser;

    // Constructor
    public FriendRequestAdapter(Context context, List<FriendRequest> friendRequests) {
        this.context = context;
        this.requests = friendRequests;
        // Assign currentUser value with Get method
        this.currentUser = ParseUser.getCurrentUser();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_request_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(requests.get(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // VIEWS
        private ImageView ivProfilePicture;
        private Button btnAccept;
        private TextView tvFriendRequest, tvDecline;

        // Friend request object for this VH
        FriendRequest request;

        // Fields of friendRequest
        ParseUser fromUser;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // Get views from layout
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvFriendRequest = itemView.findViewById(R.id.tvFriendRequest);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            tvDecline = itemView.findViewById(R.id.tvDecline);
        }

        public void bind(FriendRequest friendRequest) {
            // Set friend request value
            request = friendRequest;
            // Get visit and fromUser fields
            fromUser = request.getFromUser();
            // Get profile picture for fromUser
            ParseFile profilePicture = (ParseFile) fromUser.get("profileImage");
            // Unite info from invitation
            ImagesController.loadCircleImage(context, profilePicture.getUrl(), ivProfilePicture);
            // Create text for notification
            String requestText = fromUser.getUsername() + " has sent you a friend request!";
            tvFriendRequest.setText(requestText);

            // Button to accept the invitation
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Display a message of acceptance
                    Toast.makeText(context, fromUser.getUsername() + " and you are now friends!", Toast.LENGTH_SHORT).show();
                    // Change friend request status to accepted
                    request.setStatus("accepted");
//                    // Save confirmation
//                    saveConfirmation(fromUser, currentUser);
                    // Save request with new changes
                    request.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null)
                                Log.i("FriendRequest", "Failure saving request", e);
                            return;
                        }
                    });
                    // Remove row
                    requests.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            // Button to decline an invitation
            tvDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Display a message of success
                    Toast.makeText(context, "Friend request declined.", Toast.LENGTH_SHORT).show();
                    // Change invitation status to declined
                    request.setStatus("declined");
                    // Save request with new changes
                    request.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null)
                                Log.i("FriendRequest", "Failure saving request", e);
                                return;
                        }
                    });
                    // Remove row
                    requests.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

        }
    }
}


