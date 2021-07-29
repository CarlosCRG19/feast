package com.example.fbu_app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.controllers.DatePickerController;
import com.example.fbu_app.fragments.DetailsFragments.DetailsFragmentCreate;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Like;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Adapter for both visits screen (NextVisits and PastVisits)
public class VisitsAdapter extends RecyclerView.Adapter<VisitsAdapter.ViewHolder>{

    // FIELDS
    private Context context;
    private List<Visit> visits; //

    // Constructor
    public VisitsAdapter(Context context, List<Visit> visits) {
        this.context = context;
        this.visits = visits;
        // Assign currentUser value with getMethod
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.visits_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(visits.get(position));
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // Visit for this holder
        Visit visit;

        // VIEWS
        private ImageView ivBusinessImage, ivCreatorImage;
        private TextView tvName, tvAddress, tvLocation, tvAttendees, tvDate;
        private TextView tvOptions; // tv to display cancel visit option

        // Business for this visit
        private Business visitBusiness;

        // Object to retreive like from database or create a new like
        private Like userLike;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // Get views from layout
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            ivCreatorImage = itemView.findViewById(R.id.ivCreatorImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAttendees = itemView.findViewById(R.id.tvAttendees);
            tvDate = itemView.findViewById(R.id.tvDate);
            // TV that serves as menu button
            tvOptions = itemView.findViewById(R.id.tvOptions);
            // Set listener
            itemView.setOnClickListener(this);
        }

        public void bind(Visit visitToBind) {
            // Assign visit value
            visit = visitToBind;
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
            visitBusiness = visit.getBusiness();
            // Bind business info to respective views
            Glide.with(context)
                    .load(visitBusiness.getImageUrl())
                    .into(ivBusinessImage);
            // Set name text
            tvName.setText(visitBusiness.getName());
            // Set address text
            tvAddress.setText(visitBusiness.getAddress());
            // Create text for location
            String locationText = visitBusiness.getCity() + ", " + visitBusiness.getCountry();
            // Set text into view
            tvLocation.setText(locationText);

            // Get attendees from visit
            ParseUser visitCreator = visit.getUser();
            // Get creator's profile picture
            ParseFile creatorImage = (ParseFile) visitCreator.get("profileImage");
            // Embbed creator pp into IV
            Glide.with(context)
                    .load(creatorImage.getUrl())
                    .circleCrop()
                    .into(ivCreatorImage);

            // Check if list of attendees exists
            List<ParseUser> attendees = visit.getAttendees();
            // Check if list is empty
            if (attendees == null || attendees.size() <= 1) {
                tvAttendees.setVisibility(View.INVISIBLE);
            } else {
                // Create text for attendees bubble
                String attendeesText = "+" + String.valueOf(attendees.size() - 1);
                tvAttendees.setText(attendeesText);
            }

            // Set menu click listener
            tvOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Setup delete option
                    enableDeleteMenu();
                }
            });
        }

        // LISTENERS

        // Launches details screen when a row is clicked
        @Override
        public void onClick(View v) {
            // Create new bundle to pass args
            Bundle bundle = new Bundle();
            bundle.putParcelable(Business.TAG, visitBusiness);

            // Create new instance of detailsFragment (the user can create a new visit from this details screen)
            DetailsFragmentCreate detailsFragmentCreate = new DetailsFragmentCreate();
            detailsFragmentCreate.setArguments(bundle);

            // Make fragment transaction adding to back stack
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, detailsFragmentCreate)
                    .addToBackStack(null)
                    .commit();
        }

        // Creates and sets up the delete menu
        private void enableDeleteMenu() {
            // Create popup menu
            PopupMenu popupMenu = new PopupMenu(context, tvOptions);
            // inflate menu from R.menu
            popupMenu.inflate(R.menu.menu_delete_visit);
            // add click listener
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.cancel_visit) {
                        // Call method to cancel visit
                        cancelVisit();
                        return true;
                    }
                    return false;
                }
            });
            // display popup menu
            popupMenu.show();
        }

        // Private method to cancel a visit
        private void cancelVisit(){
            // Get visit creator and current user
            ParseUser currentUser = ParseUser.getCurrentUser();
            ParseUser visitCreator = visit.getUser();
            // Check if current user is the creator of the visit
            if(currentUser.getObjectId().equals(visitCreator.getObjectId())) {
                // if current user is the creator, delete the visit
                visit.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        // Check for errors
                        if(e != null) {
                            Toast.makeText(context, "Error deleting visit!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // Display success message
                        Toast.makeText(context, "Visit deleted!" , Toast.LENGTH_SHORT).show();
                        // Eliminate row
                        visits.remove(getAdapterPosition());
                        // Notify item removed
                        notifyItemRemoved(getAdapterPosition());
                        // Check for visit invites using this visit and delete them
                        deleteVisitInvitations(visit);
                    }
                });
            } else {
                // if current user is not the creator, just remove current user from attendees
                visit.removeAttendee(currentUser);
                // Save visit
                visit.saveInBackground();
                // Eliminate row
                visits.remove(getAdapterPosition());
                // Notify item removed
                notifyItemRemoved(getAdapterPosition());
                // Display success message
                Toast.makeText(context, "You will not attend this visit" , Toast.LENGTH_SHORT).show();
            }
        }

    }

    // Checks for pending invitations that were made using this visit
    // Deletes them if they exist
    private void deleteVisitInvitations(Visit visit) {
        // Create query for visit invitations
        ParseQuery<VisitInvitation> query = ParseQuery.getQuery(VisitInvitation.class);
        // Check that invitation uses visit
        query.whereEqualTo("visit", visit);
        // Make query
        query.findInBackground(new FindCallback<VisitInvitation>() {
            @Override
            public void done(List<VisitInvitation> visitInvitations, ParseException e) {
                // Check if visits where found
                if (e == null) {
                    // Make a for loop and delete the invitations
                    for (VisitInvitation visitInvitation : visitInvitations) {
                        visitInvitation.deleteInBackground();
                    }
                }
            }
        });
    }

}





