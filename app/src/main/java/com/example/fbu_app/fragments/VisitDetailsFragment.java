package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.adapters.UserAdapter;
import com.example.fbu_app.adapters.VisitsAdapter;
import com.example.fbu_app.controllers.DatePickerController;
import com.example.fbu_app.controllers.ImagesController;
import com.example.fbu_app.fragments.DetailsFragments.DetailsFragmentCreate;
import com.example.fbu_app.fragments.DialogFragments.InviteFragment;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VisitDetailsFragment extends Fragment {

    // VIEWS

    // Business views
    private RelativeLayout rlBusiness;
    private ImageView ivBusinessImage;
    private TextView tvName, tvAddress, tvLocation;

    // Date views
    private TextView tvDate;

    // Creator views
    private ImageView ivCreatorImage;
    private TextView tvCreatorUsername;

    // Attendees views
    private TextView tvAttendees;
    private RecyclerView rvAttendees;

    // Button to add attendees
    private ImageButton btnAddAttendee;

    // Delete visit
    private LinearLayout llDelete;
    private Button btnDeleteVisit;

    // Variables for RVs
    private List<ParseUser> attendees;
    private UserAdapter adapter;

    // Visit related to this invitation
    private Visit visit;

    // Empty constructor
    public VisitDetailsFragment() {}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Hide Activity's bottomAppBar
        ((MainActivity) getActivity()).hideBottomNavBar();
        return inflater.inflate(R.layout.fragment_visit_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get visit from arguments
        visit = (Visit) getArguments().get(Visit.TAG);

        // Set views
        setViews(view);
        // Populate view
        populateViews();

        // RV setup
        attendees = visit.getAttendees();
        adapter = new UserAdapter(getContext(), attendees, 12);
        rvAttendees.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvAttendees.setAdapter(adapter);

        // Verify if current user is owner of visit or visit is a past visit
        if(!ParseUser.getCurrentUser().getObjectId().equals(visit.getUser().getObjectId())
                || DatePickerController.getTodaysDate().first.compareTo(visit.getDate()) > 0) {
            // Hide edit views
            btnAddAttendee.setVisibility(View.GONE);
        }

        // Check if user is currently in the attendees list
        if (!attendees.contains(ParseUser.getCurrentUser())) {
            // Change visibility of delete button
            llDelete.setVisibility(View.GONE);
        }

        // Set click listeners for the views
        setListeners();

    }

    // VIEW METHODS

    private void setViews(View view) {
        // Business views
        rlBusiness = view.findViewById(R.id.rlBusiness);
        ivBusinessImage = view.findViewById(R.id.ivBusinessImage);
        tvName = view.findViewById(R.id.tvName);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvLocation = view.findViewById(R.id.tvLocation);
        // Date views
        tvDate = view.findViewById(R.id.tvDate);
        // Creator views
        ivCreatorImage = view.findViewById(R.id.ivCreatorImage);
        tvCreatorUsername = view.findViewById(R.id.tvCreatorUsername);
        // Attendees views
        tvAttendees = view.findViewById(R.id.tvAttendees);
        rvAttendees = view.findViewById(R.id.rvAttendees);
        // Button to add attendees
        btnAddAttendee = view.findViewById(R.id.btnAddAttendee);
        // Delete visit views
        llDelete = view.findViewById(R.id.llDelete);
        btnDeleteVisit = view.findViewById(R.id.btnDeleteVisit);
    }

    private void populateViews() {
        // Business views
        ImagesController.simpleImageLoad(getContext(), visit.getBusiness().getImageUrl(), ivBusinessImage);
        tvName.setText(visit.getBusiness().getName());
        tvAddress.setText(visit.getBusiness().getAddress());
        // Create text for location
        String locationText = visit.getBusiness().getCity() + ", " + visit.getBusiness().getCountry();
        // Set text into view
        tvLocation.setText(locationText);

        // Date views
        tvDate.setText(visit.getDateStr());

        // Creator views
        ParseFile creatorImage = (ParseFile) visit.getUser().get("profileImage");
        ImagesController.loadCircleImage(getContext(), creatorImage.getUrl(), ivCreatorImage);
        tvCreatorUsername.setText(visit.getUser().getUsername());

        // Attendees views
        String attendeesSize = String.valueOf(visit.getAttendees().size());
        tvAttendees.setText(attendeesSize);
    }

    private void setListeners() {
        // Listener to get to details of business
        rlBusiness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create new bundle to pass args
                Bundle bundle = new Bundle();
                bundle.putParcelable(Business.TAG, visit.getBusiness());

                // Create new instance of detailsFragment (the user can create a new visit from this details screen)
                DetailsFragmentCreate detailsFragmentCreate = new DetailsFragmentCreate();
                detailsFragmentCreate.setArguments(bundle);

                // Make fragment transaction adding to back stack
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flContainer, detailsFragmentCreate)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Button to invite other users
        btnAddAttendee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create bundle to pass visit
                Bundle bundle = new Bundle();
                bundle.putParcelable(Visit.TAG, visit);
                // Create new instance of fragment
                InviteFragment inviteFragment = new InviteFragment();
                // Add arguments to fragment
                inviteFragment.setArguments(bundle);
                // Launch fragment
                inviteFragment.show(getChildFragmentManager(), "fragment_invite");
            }
        });

        // Delete visit button
        btnDeleteVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelVisit();
            }
        });

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
                        Toast.makeText(getContext(), "Error deleting visit!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // Display success message
                    Toast.makeText(getContext(), "Visit deleted!" , Toast.LENGTH_SHORT).show();
                    // Check for visit invites using this visit and delete them
                    deleteVisitInvitations(visit);
                    // On back pressed
                    getActivity().onBackPressed();

                }
            });
        } else {
            // if current user is not the creator, just remove current user from attendees
            visit.removeAttendee(currentUser);
            // Save visit
            visit.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    getActivity().onBackPressed();
                }
            });
            // Display success message
            Toast.makeText(getContext(), "You will not attend this visit" , Toast.LENGTH_SHORT).show();
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
