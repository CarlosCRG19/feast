package com.example.fbu_app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.fragments.DetailsFragments.DetailsFragmentGo;
import com.example.fbu_app.models.Business;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

// Adapter for CardStackView from the yuyakaido library
public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    // Fields
    private Context context; // handle to environment
    private List<Business> businesses; // list of business objects

    // GETTERS FOR FIELDS

    public Context getContext() {
        return context;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public CardStackAdapter(Context context, List<Business> businesses) {
        this.context = context;
        this.businesses = businesses;
    }

    // MANDATORY METHODS from RecyclerView.Adapter

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.business_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // Bind business to Vh
        holder.bind(businesses.get(position));
    }

    @Override
    public int getItemCount() { return businesses.size(); } // Size of the businesses array

    // OTHER METHODS

    // Add an entire array of businesses
    public void addAll(List<Business> businessesList) {
        businesses.addAll(businessesList);
        notifyDataSetChanged();
    }

    // Removes all businesses from the adapter
    public void clear() {
        businesses.clear();
        notifyDataSetChanged();
    }

    // Inner ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {

        public static final String BUSINESS_TAG  = "business"; // identifier for passing busines with bundle

        // Layout views as member variables
        private ImageView ivBusinessImage;
        private TextView tvName, tvPrice, tvDistance, tvAddress, tvCategories, tvOpen;
        private ImageButton btnInfo;
        private RatingBar rbRating;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // View assignments
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            tvName = itemView.findViewById(R.id.tvName);
            rbRating = itemView.findViewById(R.id.rbRating);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategories = itemView.findViewById(R.id.tvCategories);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            btnInfo = itemView.findViewById(R.id.btnInfo);
            tvOpen = itemView.findViewById(R.id.tvOpen);

        }

        public void bind(Business business) {
            // Use glide to populate ImageView
            Glide.with(context)
                    .load(business.getImageUrl())
                    .into(ivBusinessImage);

            // Set TVs with info from the business
            tvName.setText(business.getName());
            // Set Price text
            if(business.getPrice().length() > 0) {
                tvPrice.setText(business.getPrice() + " · ");
            }
            // Set categories text
            tvCategories.setText(Business.formatCategories(business.getCategories()));
            // Set distance text
            if (!Objects.isNull(business.getDistance())) {
                tvDistance.setText(Business.formatDistance(business.getDistance()) +  "km.  · ");
            }
            // Set address text
            tvAddress.setText(business.getAddress());
            // Check if business is now closed
            if(business.isClosed()){
                // Set tv text
                tvOpen.setText("Closed");
                // Change tv color
                tvOpen.setTextColor(ContextCompat.getColor(context, R.color.color_alert));
            } else {
                // Set tv text to open
                tvOpen.setText("Open");
                // Change tv color to green
                tvOpen.setTextColor(ContextCompat.getColor(context, R.color.color_success));
            }
            tvOpen.setText(business.isClosed() ? "Closed" : "Open");
            rbRating.setRating(business.getRating());

            // Set button listener for details screen
            btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new bundle
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(BUSINESS_TAG, business);

                    // Create new instance for detailsFragmentGo
                    DetailsFragmentGo detailsFragmentGo = new DetailsFragmentGo();
                    detailsFragmentGo.setArguments(bundle);

                    // Make fragment transaction adding to back stack to return when back clicked
                    ((MainActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.flContainer, detailsFragmentGo)
                            .addToBackStack(null)
                            .commit();
                }
            });
        }
    }
}
