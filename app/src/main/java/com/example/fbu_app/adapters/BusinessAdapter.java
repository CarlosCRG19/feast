package com.example.fbu_app.adapters;

import android.content.Context;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.R;
import com.example.fbu_app.controllers.ImagesController;
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

// Class to bind data to compare recyclerview
public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder>{

    // FIELDS
    protected Context context;
    protected List<Business> businesses; // list that contains businesses to be displayed

    // CONSTRUCTOR
    public BusinessAdapter(Context context, List<Business> businesses) {
        this.context = context;
        this.businesses = businesses;
    }

    // MANDATORY METHODS

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.businesses_favorite_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // Call inner class method to bind business data with the views
        holder.bind(businesses.get(position));
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }


    // VIEW HOLDER

    // Class responsible of binding each business data with their respective row
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // VIEWS
        protected Business business;
        protected ImageView ivBusinessImage;
        protected TextView tvName;

        // Constructor
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // Get views from layout
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            tvName = itemView.findViewById(R.id.tvName);
            // Set an onClick listener
            itemView.setOnClickListener(this);

        }

        // Populates the views with business data
        public void bind(Business businessToBind) {
            // assign value to business
            business = businessToBind;

            // Check if passed business already exists in database
            verifyBusinessExists();

            // Use static method to load image
            ImagesController.simpleImageLoad(context, business.getImageUrl(), ivBusinessImage);
            // Set text for name
            tvName.setText(business.getName());
        }

        // If a row is clicked, a new fragment displays more information about that business
        @Override
        public void onClick(View v) {
            // Create bundle to pass busines as argument
            Bundle bundle = new Bundle();
            bundle.putParcelable(Business.TAG, business);

            // Create new detailsFragmentCreate instance
            DetailsFragmentCreate detailsFragmentCreate = new DetailsFragmentCreate();
            // Pass bundle as args
            detailsFragmentCreate.setArguments(bundle);

            // Make fragment transaction adding to back stack to return when back clicked
            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, detailsFragmentCreate)
                    .addToBackStack(null)
                    .commit();
        }

        // Checks if business already exists in database
        // if it exists, changes the value of the current business object
        private void verifyBusinessExists() {
            // Specify type of query
            ParseQuery<Business> query = ParseQuery.getQuery(Business.class);
            // Search for business in database based on yelpId
            query.whereEqualTo("yelpId", business.getYelpId());
            // Use getFirstInBackground to finish the search if it has found one matching business
            query.getFirstInBackground(new GetCallback<Business>() {
                @Override
                public void done(Business object, ParseException e) {
                    if(e != null) {
                    }
                    // if the business exists, change value of member variable
                    if (object != null) {
                        business = object;
                    }
                }
            });
        }

    }
}
