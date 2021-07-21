package com.example.fbu_app.adapters;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.fragments.DetailsFragments.DetailsFragmentCreate;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Like;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

        public static final String BUSINESS_TAG  = "business"; // identifier for passing busines with bundle

        // VIEWS
        private ImageView ivBusinessImage;
        private Button btnAccept;
        private TextView tvName, tvInvitedBy, tvDate;

        // Fields of invitation
        Visit visit;
        ParseUser fromUser;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // Get views from layout
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvInvitedBy = itemView.findViewById(R.id.tvInvitedBy);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            // Set listener
            itemView.setOnClickListener(this);
        }

        public void bind(VisitInvitation invitation) {
            // Get visit and fromUser fields
            visit = invitation.getVisit();
            fromUser = invitation.getFromUser();
            // Unite info from invitation
            Glide.with(context)
                    .load(visit.getBusiness().getImageUrl())
                    .into(ivBusinessImage);
            tvName.setText(visit.getBusiness().getName());
            tvInvitedBy.setText("Invited by: " + fromUser.getUsername());
            tvDate.setText(visit.getDateStr());

            // Button to accept the invitation
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Display a message of acceptance
                    Toast.makeText(context, "Accepted invitation!", Toast.LENGTH_SHORT).show();
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
            bundle.putParcelable(BUSINESS_TAG, visit.getBusiness());

            // Create new instance of detailsFragment (the user can create a new visit from this details screen)
            DetailsFragmentCreate detailsFragmentCreate = new DetailsFragmentCreate();
            detailsFragmentCreate.setArguments(bundle);

            // Make fragment transaction adding to back stack
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flContainer, detailsFragmentCreate)
                    .addToBackStack(null)
                    .commit();
        }
    }

}


