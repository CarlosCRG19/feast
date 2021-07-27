package com.example.fbu_app.fragments;

import android.opengl.ETC1;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.activities.MainActivity;
import com.example.fbu_app.fragments.DialogFragments.InviteFragment;
import com.example.fbu_app.fragments.DialogFragments.NotificationsFragment;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

public class ConfirmationFragment extends Fragment {

    Visit visit;
    ImageView ivBusinessImage;
    TextView tvName, tvDate;

    Button btnConfirm;

    // TEST VIEWS
    Button btnInvite;

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

        // TEST VIEWS
        btnInvite = view.findViewById(R.id.btnInvite);

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
            }
        });

        // TEST BUTTON LISTENER
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create bundle to pass visit
                Bundle bundle = new Bundle();
                bundle.putParcelable("visit", visit);
                // Create new instance of fragment
                InviteFragment inviteFragment = new InviteFragment();
                // Add arguments to fragment
                inviteFragment.setArguments(bundle);
                // Launch fragment
                inviteFragment.show(getChildFragmentManager(), "fragment_invite");
            }
        });


    }


}
