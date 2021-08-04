package com.example.fbu_app.adapters;


import android.content.Context;
import android.os.Bundle;
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
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.controllers.DatePickerController;
import com.example.fbu_app.controllers.ImagesController;
import com.example.fbu_app.fragments.VisitDetailsFragment;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Adapter for invitations displayed in next visits screen
public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder>{

    // FIELDS
    private Context context;
    private List<VisitInvitation> invitations; //

    // Current user
    private ParseUser currentUser;

    // Constructor
    public InvitationAdapter(Context context, List<VisitInvitation> invitations) {
        this.context = context;
        this.invitations = invitations;
        // Assign currentUser value with Get method
        this.currentUser = ParseUser.getCurrentUser();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.invitation_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(invitations.get(position));
    }

    @Override
    public int getItemCount() {
        return invitations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public static final String INVITATION_TAG = "invitation"; // Simple tag to send invitation with bundle

        // VIEWS
        private ImageView ivBusinessImage, ivCreatorImage;
        private Button btnAccept;
        private TextView tvName, tvAddress, tvLocation, tvCreatorUsername, tvDate, tvDecline;

        // Invitation object
        VisitInvitation invitation;

        // Fields of invitation
        Visit visit;
        ParseUser fromUser;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // Get views from layout
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            ivCreatorImage = itemView.findViewById(R.id.ivCreatorImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvCreatorUsername = itemView.findViewById(R.id.tvCreatorUsername);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            tvDecline = itemView.findViewById(R.id.tvDecline);
            // Set listener
            itemView.setOnClickListener(this);
        }



        public void bind(VisitInvitation visitInvitation) {

            // Assign invitation value
            invitation = visitInvitation;

            // Get visit for this invitation
            visit = visitInvitation.getVisit();
            // Get todays date
            String todaysDateStr = DatePickerController.getTodaysDate().second;
            // Check if visit date is today
            if (visit.getDateStr().equals(todaysDateStr)) {
                // Set tvDate text
                tvDate.setText("Today");
            } else {
                tvDate.setText(visit.getDateStr());
            }
            // Get business from visit
            Business visitBusiness = visit.getBusiness();
            // Bind business info to respective views
            ImagesController.simpleImageLoad(context, visitBusiness.getImageUrl(), ivBusinessImage);
            // Set name text
            tvName.setText(visitBusiness.getName());
            // Set address text
            tvAddress.setText(visitBusiness.getAddress());
            // Create text for location
            String locationText = visitBusiness.getCity() + ", " + visitBusiness.getCountry();
            // Set text into view
            tvLocation.setText(locationText);

            // Get creator of invitation
            ParseUser visitCreator = visitInvitation.getFromUser();
            // Get creator's profile picture
            ParseFile creatorImage = (ParseFile) visitCreator.get("profileImage");
            // Embbed creator pp into IV
            ImagesController.loadCircleImage(context, creatorImage.getUrl(), ivCreatorImage);
            // Populate TV with creator username
            tvCreatorUsername.setText(visitCreator.getUsername());

            // Button to accept the invitation
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Display a message of acceptance
                    Toast.makeText(context, "Invitation accepted!", Toast.LENGTH_SHORT).show();
                    // Change invitation status to accepted
                    invitation.setStatus("accepted");
                    // Save confirmation
                    saveConfirmation(visit, currentUser);
                    // Save invitation with new changes
                    invitation.saveInBackground();
                    // Remove row
                    invitations.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

            // Button to decline an invitation
            tvDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Display a message of success
                    Toast.makeText(context, "Invitation declined!", Toast.LENGTH_SHORT).show();
                    // Change invitation status to declined
                    invitation.setStatus("declined");
                    // Save invitation with new changes
                    invitation.saveInBackground();
                    // Remove row
                    invitations.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });

        }

        // SAVE POST METHODS

        // Save confirmation to a visit with
        private void saveConfirmation(Visit visit, ParseUser newAttendee) {
            // Add attendee to visit
            visit.addAttendee(newAttendee);
            // save current visit
            visit.saveInBackground();
        }

        // LISTENERS

        // Launches details screen when a row is clicked
        @Override
        public void onClick(View v) {
            // Create new bundle to pass args
            Bundle bundle = new Bundle();
            bundle.putParcelable(Visit.TAG, visit);

            // Create new instance of visit details, the user can see who is attending the visit
            VisitDetailsFragment visitDetailsFragment = new VisitDetailsFragment();
            visitDetailsFragment.setArguments(bundle);

            // Make fragment transaction adding to back stack
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, visitDetailsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

}


