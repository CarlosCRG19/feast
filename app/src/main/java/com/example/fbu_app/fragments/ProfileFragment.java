package com.example.fbu_app.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.adapters.BusinessAdapter;
import com.example.fbu_app.helpers.BitmapScaler;
import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Like;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment"; // TAG for log messages

    // Codes for GET_CONTENT action
    public final static int PICK_PHOTO_CODE = 1046;

    // User object
    ParseUser profileUser;

    // Views
    ImageView ivProfile;
    TextView tvUsername, tvEmail;
    Button btnUpload, btnLogout;
    RecyclerView rvBusinesses; // View group to display user's favorited restaurants

    BusinessAdapter adapter;
    List<Business> likedBusinesses;

    // Required empty public constructor
    public ProfileFragment() {}

    // REQUIRED METHODS

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current user
        profileUser = ParseUser.getCurrentUser();
        // Init liked businesses list
        likedBusinesses = new ArrayList<>();

        // Set views from specified layout
        setViews(view);
        // Bind profile info
        populateViews();
        // Set listeners for upload and logout buttons
        setClickListeners();

        // Setup RV
        adapter = new BusinessAdapter(getContext(), likedBusinesses);
        rvBusinesses.setAdapter(adapter);
        rvBusinesses.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get liked businesses
        queryLikedBusinesses();

    }

    // VIEWS METHODS

    private void setViews(View view) {
        // User info
        ivProfile = view.findViewById(R.id.ivProfile);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        // Interactions
        btnUpload = view.findViewById(R.id.btnUpload);
        btnLogout = view.findViewById(R.id.btnLogout);
        // Recycler view
        rvBusinesses = view.findViewById(R.id.rvBusinesses);
    }

    // Binds the views with the users data
    private void populateViews() {
        // Set image
        ParseFile profileImage = (ParseFile) profileUser.get("profileImage");
        Glide.with(getContext())
                .load(profileImage.getUrl())
                .circleCrop()
                .into(ivProfile);
        // Fill TVs with username and email
        tvUsername.setText(profileUser.getUsername());
        tvEmail.setText((String) profileUser.get("email"));
    }

    // LISTENERS AND FEATURES

    // Set listeners for each buttons (these are only available if profileUser is the same as current user)
    private void setClickListeners() {

        // Calls onPickPhoto to access media storage and select a photo
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });

        // Logout current user and close the app
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                getActivity().finish();
            }
        });
    }

    // QUERY METHODS
    public void queryLikedBusinesses() {
        // Specify what type of data we want to query - Comment.class
        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        // Limit query to latest 20 items and include the user
        query.include("business");
        // Limit query to only those comments that belong to this post
        query.whereEqualTo("user", profileUser);
        // Start async call for comments
        query.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> likes, ParseException e) {
                // Check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting comments", e);
                    return;
                }
                // Clear the list of liked businesses
                likedBusinesses.clear();
                // Get businesses from like objects
                getBusinessesFromLikes(likes);
            }
        });
    }

    // Adds businesses objects referenced in likes
    public void getBusinessesFromLikes(List<Like> likes) {
        // For each like, add its respective business
        for (Like like : likes) {
            likedBusinesses.add(like.getBusiness());
        }
        // Notify adapter of change
        adapter.notifyDataSetChanged();
    }

    // MEDIA METHODS

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    // RESULT METHOD

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && requestCode == PICK_PHOTO_CODE) {
            // Get image from intent
            Uri photoUri = data.getData();
            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);
            Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(selectedImage, 500); // resize image using helper
            // Configure byte output stream
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            // Transform to byte array
            byte[] byteArray = stream.toByteArray();
            // Create new parseFile to save image
            ParseFile newProfileImage = new ParseFile("profile.png", byteArray);
            // Set new image on profileUser object
            profileUser.put("profileImage", newProfileImage);
            // Save profile user in background thread
            profileUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    // Check for errors
                    if (e != null) {
                        Log.e(TAG, "Profile image wasn't saved", e);
                        return;
                    }
                    Toast.makeText(getContext(), "Profile image changed!", Toast.LENGTH_SHORT).show(); // display success message
                }
            });
            // Change image on view
            Glide.with(getContext())
                    .load(byteArray)
                    .circleCrop()
                    .into(ivProfile);
        }
    }

    // OTHER METHODS

    // Calls logout method from ParseUser to forget current credentials
    public void logout() {
        ParseUser.logOut();
    }

}
