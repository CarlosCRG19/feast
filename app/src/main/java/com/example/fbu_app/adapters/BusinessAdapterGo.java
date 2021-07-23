package com.example.fbu_app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.fragments.ConfirmationFragment;
import com.example.fbu_app.fragments.DetailsFragments.DetailsFragmentCreate;
import com.example.fbu_app.fragments.DetailsFragments.DetailsFragmentGo;
import com.example.fbu_app.fragments.NextVisitsFragment;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

// Adapter for businesses in the Compare Screen (includes a Go! button to instantly create a visit)
public class BusinessAdapterGo extends BusinessAdapter{

    public static final String VISIT_TAG = "visit";

    // FIELDS
    private String visitDateStr; // Date values for visit creation
    private Date visitDate;

    // CONSTRUCTOR. User super class constructor but includes new fields for visit creation
    public BusinessAdapterGo(Context context, List<Business> businesses, String visitDateStr, Date visitDate) {
        super(context, businesses);
        this.visitDateStr = visitDateStr; // visit info required to create new visit
        this.visitDate = visitDate;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Change layout used by super class
        View view = LayoutInflater.from(context).inflate(R.layout.businesses_compare_layout, parent, false);
        return new ViewHolder(view);
    }

    // Override inner ViewHolder class
    public class ViewHolder extends BusinessAdapter.ViewHolder {

        // VIEWS
        private TextView tvPrice, tvDistance;
        private Button btnGo; // new view for visit creation


        public ViewHolder(@NonNull @NotNull View itemView) {
            // Use super class method and assign button value
            super(itemView);
            // Assign new views
            btnGo = itemView.findViewById(R.id.btnGo);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }

        @Override
        public void bind(Business businessToBind) {
            // Call super class bind method
            super.bind(businessToBind);
            // Set texts
            tvPrice.setText("Price: " + businessToBind.getPrice());
            tvDistance.setText("Distance: " + (int)businessToBind.getDistance() + " m.");
            // Add listener to btnGo for visit creation
            btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Verify if business already exists in database
                    // and create visit with current info
                    verifyBusinessExistsAndCreateVisit();
                }
            });
        }

        // Overrides super class click listener to open a different details view
        @Override
        public void onClick(View v) {
            // Create bundle to pass busines as argument
            Bundle bundle = new Bundle();
            bundle.putParcelable(BUSINESS_TAG, business);

            // Create new detailsFragmentGo instance
            DetailsFragmentGo detailsFragmentGo = new DetailsFragmentGo();
            detailsFragmentGo.setArguments(bundle);

            // Make fragment transaction adding to back stack to return when back clicked
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, detailsFragmentGo)
                    .addToBackStack(null)
                    .commit();
        }

        // Checks if local business is already in database, if it is, the value of business is changed to match that one
        // after that, it creates a new visit
        private void verifyBusinessExistsAndCreateVisit() {
            // Specify type of query
            ParseQuery<Business> query = ParseQuery.getQuery(Business.class);
            // Search for business in database based on yelpId
            query.whereEqualTo("yelpId", business.getYelpId());
            // Use getFirstInBackground to finish the search if it has found one matching business
            query.getFirstInBackground(new GetCallback<Business>() {
                @Override
                public void done(Business object, ParseException e) {
                    if(e != null) {
                        Log.i("ParseSave", "Search for business", e);
                    }
                    // if the business exists, change value of member variable and create the new visit
                    if (object != null) {
                        business = object;
                    }
                    createVisit();
                }
            });

        }

        // Creates a visit with the specified business and visit date
        private void createVisit() {
            // Create new visit
            Visit newVisit = new Visit();
            newVisit.setBusiness(business);
            // Add fields
            newVisit.setUser(ParseUser.getCurrentUser());
            newVisit.addAttendee(ParseUser.getCurrentUser());
            newVisit.setDate(visitDate);
            newVisit.setDateStr(visitDateStr);
            // Save visit using background thread
            newVisit.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.i("ParseSave", "Failed to save visit", e);
                        return;
                    }
                    // Display success message
                    Toast.makeText(context, "Succesfully created visit!", Toast.LENGTH_SHORT).show();
                    // Create bundle to pass busines as argument
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(VISIT_TAG, newVisit);
                    // Transaction to new fragment
                    ConfirmationFragment confirmationFragment = new ConfirmationFragment();
                    confirmationFragment.setArguments(bundle);
                    // Make fragment transaction
                    ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.flContainer, confirmationFragment)
                            .commit();

                }
            });
        }

    }
}