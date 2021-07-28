package com.example.fbu_app.activities;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.fbu_app.R;

import org.jetbrains.annotations.NotNull;

// Simple activity to display the profile image in full size
// and be able to expand it
public class ShowImageActivity extends AppCompatActivity {

    // Use library's image view
    SubsamplingScaleImageView ivProfileImage;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout for the activity
        setContentView(R.layout.activty_show_image);

        // Get url of photo
        String photoUrl = getIntent().getStringExtra("photoUrl");

        // Assign value of image view
        ivProfileImage = findViewById(R.id.ivProfileImage);

        // Load image into ScaleImageView
        Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                        ivProfileImage.setImage(ImageSource.bitmap(resource));
                    }
                });

    }
}
