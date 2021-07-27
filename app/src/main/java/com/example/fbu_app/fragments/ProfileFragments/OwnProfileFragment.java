package com.example.fbu_app.fragments.ProfileFragments;

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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.fragments.DialogFragments.NotificationsFragment;
import com.example.fbu_app.helpers.BitmapScaler;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class OwnProfileFragment extends ProfileFragment {

    // VIEWS
    ImageButton btnUpload, btnLogout, btnNotifications;

    // Codes for GET_CONTENT action
    public final static int PICK_PHOTO_CODE = 1046;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_own_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set text of tvFavorites
        tvFavorites.setText("Your favorite restaurants");
        // Set listeners for upload and logout buttons
        setClickListeners();
    }

    @Override
    protected void setViews(View view) {
        super.setViews(view);
        // Interaction views
        btnUpload = view.findViewById(R.id.btnUpload);
        btnLogout = view.findViewById(R.id.btnLogout);
        // Notifications
        btnNotifications = view.findViewById(R.id.btnNotifications);
    }

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

        // Show dialog fragment for notifications
        btnNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                notificationsFragment.show(getChildFragmentManager(), "fragment_notifications");
            }
        });

    }

    // MEDIA METHODS

    // Trigger gallery selection for a photo
    private void onPickPhoto(View view) {
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

    private Bitmap loadFromUri(Uri photoUri) {
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
