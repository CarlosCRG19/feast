package com.example.fbu_app.controllers;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

// Contains most of the code required for embedding images
// into views using Glide
public class ImagesController {

    public static void simpleImageLoad(Context context, String imageUrl, ImageView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }

    public static void loadCircleImage(Context context, String imageUrl, ImageView imageView){
        Glide.with(context)
                .load(imageUrl)
                .circleCrop()
                .into(imageView);
    }

    public static void loadCircleImage(Context context, byte[] byteArray, ImageView imageView){
        Glide.with(context)
                .load(byteArray)
                .circleCrop()
                .into(imageView);
    }

    public static void loadImageAsBitmap(Context context, String imageUrl, Target target){
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(target);
    }

}
