package com.example.fbu_app.fragments;

import android.opengl.ETC1;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

public class ConfirmationFragment extends Fragment {

    Visit visit;
    ImageView ivBusinessImage;
    TextView tvName, tvDate;

    Button btnConfirm;

    // TEST VIEWS
    ImageButton btnInvite;
    EditText etUsername;

    public ConfirmationFragment() {}

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Get visit from arguments
        visit = getArguments().getParcelable("visit");
        return inflater.inflate(R.layout.fragment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Business visitBusiness = visit.getBusiness();

        ivBusinessImage = view.findViewById(R.id.ivBusinessImage);
        tvName = view.findViewById(R.id.tvName);
        tvDate = view.findViewById(R.id.tvDate);
        btnConfirm = view.findViewById(R.id.btnConfirm);

        // TEST VIEWS
        etUsername = view.findViewById(R.id.etUsername);
        btnInvite = view.findViewById(R.id.btnInvite);

        tvName.setText(visitBusiness.getName());
        tvDate.setText(visit.getDateStr());

        Glide.with(getContext())
                .load(visitBusiness.getImageUrl())
                .into(ivBusinessImage);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Transaction to new fragment
                NextVisitsFragment nextVisitsFragment = new NextVisitsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, nextVisitsFragment)
                        .commit();
                // Change selected item in bottom nav bar
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigation);
                bottomNavigationView.setSelectedItemId(R.id.action_history);
            }
        });

        // TEST BUTTON LISTENER
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get username from edittext
                String invitedUsername = etUsername.getText().toString();
                searchUserAndSendInvitation(invitedUsername);
            }
        });


    }

    private void searchUserAndSendInvitation(String username) {
        // Specify type of query
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        // Search for business in database based on yelpId
        query.whereEqualTo("username", username);
        // Use getFirstInBackground to finish the search if it has found one matching business
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (object != null) {
                    // Check if searched user is not the same as current user
                    if (object.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                        Toast.makeText(getContext(), "You can't send an invitation to yourself.", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        sendInvitation(object);
                    }
                    return;
                }
                Toast.makeText(getContext(), "User not found.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendInvitation(ParseUser user) {
        // Create new invitation object
        VisitInvitation visitInvitation = new VisitInvitation();
        // Set the visit for this invite
        visitInvitation.setVisit(visit);
        // Assign users
        visitInvitation.setFromUser(ParseUser.getCurrentUser());
        visitInvitation.setToUser(user);
        // Set status
        visitInvitation.setStatus("pending");
        // Save invitation using background thread
        visitInvitation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Display success message
                    Toast.makeText(getContext(), user.getUsername() + " invited to visit!", Toast.LENGTH_SHORT).show();
                    // Change text in edit text
                    etUsername.setText("");
                }
            }
        });
    }



}
