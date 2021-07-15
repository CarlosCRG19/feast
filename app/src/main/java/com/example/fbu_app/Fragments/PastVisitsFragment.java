package com.example.fbu_app.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fbu_app.R;

import org.jetbrains.annotations.NotNull;

public class PastVisitsFragment extends Fragment {

    Button btnNextVisits;

    public PastVisitsFragment() {};

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_past_visits, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnNextVisits = view.findViewById(R.id.btnNextVisits);
        btnNextVisits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NextVisitsFragment nextVisitsFragment = new NextVisitsFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, nextVisitsFragment)
                        .commit();
            }
        });

    }
}