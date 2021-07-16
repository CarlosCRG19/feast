package com.example.fbu_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Hour;
import com.parse.ParseFile;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Comment;

import java.util.List;

public class HoursAdapter extends RecyclerView.Adapter<HoursAdapter.ViewHolder> {

    // FIELDS
    private Context context;
    private List<Hour> hours;

    public HoursAdapter(Context context, List<Hour> hours) {
        this.context = context;
        this.hours = hours;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.hour_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(hours.get(position));
    }

    @Override
    public int getItemCount() {
        return hours.size();
    }

    // Add an entire array of hours
    public void addAll(List<Hour> hourList) {
        hours.addAll(hourList);
        notifyDataSetChanged();
    }

    // Removes all businesses from the adapter
    public void clear() {
        hours.clear();
        notifyDataSetChanged();
    }


    public class ViewHolder extends  RecyclerView.ViewHolder{
        // VIEWS
        private TextView tvDay, tvStart, tvEnd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Get views from layout
            tvDay = itemView.findViewById(R.id.tvDay);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);
        }

        // Connect hour data to the views
        public void bind(Hour hour) {
            tvDay.setText(hour.getDay());
            tvStart.setText(hour.getStart() + " - ");
            tvEnd.setText(hour.getEnd());
        }

    }
}
