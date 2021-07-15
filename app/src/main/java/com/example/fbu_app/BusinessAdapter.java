package com.example.fbu_app;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.Fragments.CompareFragment;
import com.example.fbu_app.Fragments.NextVisitsFragment;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Comment;

import java.util.Date;
import java.util.List;

// Class to bind data to compare recyclerview
public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.ViewHolder>{

    // FIELDS
    Context context;
    List<Business> businesses;
    String visitDateStr;
    Date visitDate;

    public BusinessAdapter(Context context, List<Business> businesses, String visitDateStr, Date visitDate) {
        this.context = context;
        this.businesses = businesses;
        this.visitDateStr = visitDateStr; // visit info required to create new visit
        this.visitDate = visitDate;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.businesses_compare_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(businesses.get(position));
    }

    @Override
    public int getItemCount() {
        return businesses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // VIEWS
        private ImageView ivBusinessImage;
        private TextView tvName, tvRating;
        private Button btnGo;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // Get views from layout
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvRating = itemView.findViewById(R.id.tvRating);
            btnGo = itemView.findViewById(R.id.btnGo);
        }

        public void bind(Business business) {
            // Bind data to views
            Glide.with(context)
                    .load(business.getImageUrl())
                    .into(ivBusinessImage);
            tvName.setText(business.getName());
            tvRating.setText("Rating: " + business.getRating());

            // Button to create a new visit
            btnGo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Create new visit
                    Visit newVisit = new Visit();
                    newVisit.setBusiness(business);
                    // Add fields
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
                            // Transaction to new fragment
                            NextVisitsFragment nextVisitsFragment = new NextVisitsFragment();
                            ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.flContainer, nextVisitsFragment)
                                    .commit();
                            // Change selected item in bottom nav bar
                            BottomNavigationView bottomNavigationView = ((MainActivity) context).findViewById(R.id.bottomNavigation);
                            bottomNavigationView.setSelectedItemId(R.id.action_history);
                        }
                    });
                }
            });
        }
    }
}