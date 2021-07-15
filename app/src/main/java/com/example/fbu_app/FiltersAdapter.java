package com.example.fbu_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbu_app.Fragments.FiltersFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Adapter to TEST that the filters are being passed and applied (this class is only temporary)
public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.ViewHolder> {

    // Fields
    private Context context;
    private List<Pair<String, String>> filters;

    public FiltersAdapter(Context context, List<Pair<String, String>> filters) {
        this.context = context;
        this.filters = filters;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.filters_layout, parent, false);
        return new FiltersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Pair<String, String> filter = filters.get(position);
        holder.bind(filter);
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder {
        private TextView tvFilter, tvValue;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            tvFilter = itemView.findViewById(R.id.tvFilter);
            tvValue = itemView.findViewById(R.id.tvValue);
        }

        public void bind(Pair<String, String> filter) {
            tvFilter.setText(filter.first);
            tvValue.setText(filter.second);
        }

    }

}
