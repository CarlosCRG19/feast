package com.example.fbu_app.adapters;

import android.content.Context;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.fragments.DetailsFragment;
import com.example.fbu_app.models.Business;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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

        // Layout views as member variables
        ImageView ivBusinessImage;
        TextView tvName, tvRating, tvPrice;
        Button btnInfo;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            // View assignments
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnInfo = itemView.findViewById(R.id.btnInfo);

        }

        public void bind(Business business) {
            // Use glide to populate ImageView
            Glide.with(context)
                    .load(business.getImageUrl())
                    .into(ivBusinessImage);

            // Set TVs with info from the business
            tvName.setText(business.getName());
            tvPrice.setText("Price: " + business.getPrice());
            tvRating.setText("Rating: " + business.getRating() + "/5");

            ivBusinessImage.setTransitionName("business_image");
            tvName.setTransitionName("business_name");
            tvRating.setTransitionName("business_rating");
            tvPrice.setTransitionName("business_price");

            btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("business", business);

                    DetailsFragment detailsFragment = new DetailsFragment();
                    detailsFragment.setArguments(bundle);

                    ((MainActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.flContainer, detailsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            });


        }
    }
}
