package com.example.fbu_app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Adapter for both visits screen (NextVisits and PastVisits)
public class VisitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final String TAG = "VisitsAdapter";

    public static final int NEXT_VISITS_CODE = 0;
    public static final int PAST_VISITS_CODE = 1;

    // FIELDS
    Context context;
    List<Visit> visits;
    int fragmentCode; // NextVisits = 0, PastVisits = 1;

    // Current user
    ParseUser currentUser;

    // Constructor
    public VisitsAdapter(Context context, List<Visit> visits, int fragmentCode) {
        this.context = context;
        this.visits = visits;
        this.fragmentCode = fragmentCode;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        currentUser = ParseUser.getCurrentUser();
        View view = LayoutInflater.from(context).inflate(R.layout.visits_layout, parent, false);
        return new NextVisitsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        ((NextVisitsViewHolder) holder).bind(visits.get(position));
        return;
    }

    @Override
    public int getItemCount() {
        return visits.size();
    }

    public class NextVisitsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // VIEWS
        private ImageView ivBusinessImage;
        private ImageButton btnLike;
        private TextView tvName, tvRating, tvDate;

        Business visitBusiness;

        // USER LIKE
        Like userLike;

        public NextVisitsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // Get views from layout
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnLike = itemView.findViewById(R.id.btnLike);

            itemView.setOnClickListener(this);
        }

        public void bind(Visit visit) {
            // Unite info from visit
            visitBusiness = visit.getBusiness();
            Glide.with(context)
                    .load(visitBusiness.getImageUrl())
                    .into(ivBusinessImage);
            tvName.setText(visitBusiness.getName());
            tvRating.setText("Rating: " + visitBusiness.getRating());
            tvDate.setText(visit.getDateStr());

            if (fragmentCode == NEXT_VISITS_CODE) {
                btnLike.setVisibility(View.GONE);
            } else {
                verifyUserLiked(visitBusiness, currentUser);
                btnLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userLike != null) {
                            saveUnlike(visitBusiness, currentUser);
                        } else {
                            saveLike(visitBusiness, currentUser);
                        }
                    }
                });
            }
        }


        // SAVE (POST) METHODS

        // Posts a like, changes the button background and changes the count on databes
        private void saveLike(Business business, ParseUser currentUser) {
            // Create new like
            Like like = new Like();
            // Set fields
            like.setBusiness(business);
            like.setUser(currentUser);
            // Save like in database using background thread
            like.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    // Check for errors
                    if(e != null) {
                        Log.e(TAG, "Error while saving", e);
                        Toast.makeText(context, "Error liking business!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(context, "Business liked!" , Toast.LENGTH_SHORT).show(); // displays a success message

                    // Change button background
                    btnLike.setBackgroundResource(R.drawable.heart_icon);

                    // Change userLike (now it won't be null)
                    userLike = like;
                }
            });
        }

        // Takes current userLike object and deletes it from database
        private void saveUnlike(Business business, ParseUser currentUser) {
            // Delete current like from database
            userLike.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    // Check for errors
                    if(e != null) {
                        Log.e(TAG, "Error while saving", e);
                        Toast.makeText(context, "Error unliking business!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(context, "Business unliked!" , Toast.LENGTH_SHORT).show();

                    // Change button background
                    btnLike.setBackgroundResource(R.drawable.heart_icon_stroke);

                    userLike = null; // Even though we delete the object on the database, that does not mean that our local variable has been deleted

                }
            });
        }

        // Checks whether the current user has liked the post. If that is the case, it changes the button background and userLike stays as null
        private void verifyUserLiked(Business business, ParseUser currentUser) {
            // Create query
            ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
            // Define attributes to look for (like is on this post and by this user)
            query.whereEqualTo("business", business);
            query.whereEqualTo("user", currentUser);
            // Get the like object
            query.getFirstInBackground(new GetCallback<Like>() { // getFirstInBackground ends the query when it has found the first object that matches the attributes (instead of going through every object)
                @Override
                public void done(Like foundLike, ParseException e) {
                    if(e != null) { // e == null when no matching object has been found
                        btnLike.setBackgroundResource(R.drawable.heart_icon_stroke); // set button icon to just the stroke
                        return;
                    }
                    btnLike.setBackgroundResource(R.drawable.heart_icon); // change icon to filled heart
                    userLike = foundLike;
                }
            });
        }

        @Override
        public void onClick(View v) {
            Log.i("CLICKED", "Row clicked!!");
            Bundle bundle = new Bundle();
            bundle.putParcelable("business", visitBusiness);

            DetailsFragmentCreate detailsFragmentCreate = new DetailsFragmentCreate();
            detailsFragmentCreate.setArguments(bundle);

            ((MainActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.flContainer, detailsFragmentCreate)
                    .addToBackStack(null)
                    .commit();
        }
    }

}



