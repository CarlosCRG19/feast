package com.example.fbu_app.fragments.ProfileFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.fbu_app.R;
import com.example.fbu_app.controllers.MediaController;
import com.example.fbu_app.fragments.DialogFragments.NotificationsFragment;
import com.example.fbu_app.helpers.BitmapScaler;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class OwnProfileFragment extends ProfileFragment {

    // VIEWS
    ImageButton btnUpload, btnLogout, btnNotifications;

    // MEDIA FILE VARIABLES
    private File file;

    // Codes for GET_CONTENT action
    public final static int PICK_PHOTO_CODE = 1046;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

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
                selectImage();
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

    // ALERT DIALOG FRAGMENT

    // Creates an AlertDialogFragment with options to set a profile picture
    private void selectImage() {
        // Create options for alert dialog
        final CharSequence[] options = {"Take photo", "Choose from gallery", "Cancel"};
        // Create dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // Set title for builder
        builder.setTitle("Change profile picture");
        // Setup options and listener
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle the different options
                switch (which){
                    case 0:
                        launchCamera();
                        break;
                    case 1:
                        onPickPhoto();
                    default:
                        dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    // MEDIA METHODS

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        String photoFileName = "photo.jpg";
        file = MediaController.getPhotoFileUri(getContext(), photoFileName);
        // Set value for photoFile so it can be saved into database
        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.FeastApplication", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        // Start the image capture intent to take photo
        // TODO: intent.resolveActivity is null
        if(intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Trigger gallery selection for a photo
    private void onPickPhoto() {
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

    // RESULT METHOD

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_CODE) {
            if(data == null) {
                Toast.makeText(getContext(), "Picture wasn't selected", Toast.LENGTH_SHORT).show();
            } else {
                // Get image from intent
                Uri photoUri = data.getData();
                // Load the image located at photoUri into selectedImage
                Bitmap selectedImage = MediaController.loadFromUri(getActivity(), photoUri);
                // Use method to save image
                saveProfileImage(selectedImage);
            }
        } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // By this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(file.getAbsolutePath());
                // Use method to save image
                saveProfileImage(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // OTHER METHODS

    // Uses a bitmap to create a new parse file
    // Saves it into the database and changes the IV
    private void saveProfileImage(Bitmap bitmap) {
        // Transform to byte array
        byte[] byteArray = MediaController.byteArrayFromBitmap(bitmap);
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

    // Calls logout method from ParseUser to forget current credentials
    public void logout() {
        ParseUser.logOut();
    }

}
