package com.example.fbu_app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

public class ConfirmationFragment extends Fragment {

    Visit visit;
    ImageView ivBusinessImage;
    TextView tvName, tvDate;

    Button btnConfirm;

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
    }
}
