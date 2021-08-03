package com.example.fbu_app.fragments.DetailsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.example.fbu_app.R;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.example.fbu_app.models.VisitViewModel;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

// Details screen to answer to invites, there is a confirm and a decline button
public class DetailsFragmentInvitation extends DetailsFragmentBase {

    public static final String INVITATION_TAG = "invitation"; // Simple tag to get invitation from bundle

    // VIEWS
    private Button btnAccept, btnDecline;

    // VISIT OBJECTS
    VisitInvitation invitation;
    Visit visit;
    ParseUser fromUser;

    public DetailsFragmentInvitation(){}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_details_invitation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get visit from arguments
        invitation = (VisitInvitation) getArguments().get(INVITATION_TAG);
        // Get info from invitation
        visit = invitation.getVisit();
        fromUser = invitation.getFromUser();

    }

    @Override
    protected void setViews(View view) {
        super.setViews(view);

        // Accept button
        btnAccept = view.findViewById(R.id.btnAccept);

        // Decline button
        btnDecline = view.findViewById(R.id.btnDecline);
    }

    // Save confirmation to a visit with
    private void saveConfirmation(Visit visit, ParseUser newAttendee) {
        // Add attendee to visit
        visit.addAttendee(newAttendee);
        // save current visit
        visit.saveInBackground();
    }

    @Override
    protected void setClickListeners() {
        super.setClickListeners();

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a message of acceptance
                Toast.makeText(getContext(), "Invitation accepted!", Toast.LENGTH_SHORT).show();
                // Change invitation status to accepted
                invitation.setStatus("accepted");
                // Save confirmation
                saveConfirmation(visit, ParseUser.getCurrentUser());
                // Save invitation with new changes
                invitation.saveInBackground();
                // Return to previous screen
                getActivity().onBackPressed();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a message of success
                Toast.makeText(getContext(), "Invitation declined!", Toast.LENGTH_SHORT).show();
                // Change invitation status to declined
                invitation.setStatus("declined");
                // Save invitation with new changes
                invitation.saveInBackground();
                // Return to previous screen
                getActivity().onBackPressed();
            }
        });

    }
}
