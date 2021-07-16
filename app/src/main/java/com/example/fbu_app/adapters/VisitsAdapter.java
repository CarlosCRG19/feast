package com.example.fbu_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Adapter for both visits screen (NextVisits and PastVisits)
public class VisitsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int NEXT_VISITS_CODE = 0;
    public static final int PAST_VISITS_CODE = 1;

    // FIELDS
    Context context;
    List<Visit> visits;
    int fragmentCode; // NextVisits = 0, PastVisits = 1;

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

    public class NextVisitsViewHolder extends RecyclerView.ViewHolder {

        // VIEWS
        private ImageView ivBusinessImage;
        private Button btnLike;
        private TextView tvName, tvRating, tvDate;


        public NextVisitsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            // Get views from layout
            ivBusinessImage = itemView.findViewById(R.id.ivBusinessImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnLike = itemView.findViewById(R.id.btnLike);
        }

        public void bind(Visit visit) {
            // Unite info from visit
            Business visitBusiness = visit.getBusiness();
            Glide.with(context)
                    .load(visitBusiness.getImageUrl())
                    .into(ivBusinessImage);
            tvName.setText(visitBusiness.getName());
            tvRating.setText("Rating: " + visitBusiness.getRating());
            tvDate.setText(visit.getDateStr());

            if (fragmentCode == NEXT_VISITS_CODE) {
                btnLike.setVisibility(View.GONE);
            } else {
            }


        }
    }

}



