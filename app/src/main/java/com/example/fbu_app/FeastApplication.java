package com.example.fbu_app;

import android.app.Application;

import com.example.fbu_app.models.Business;
import com.example.fbu_app.models.Hour;
import com.example.fbu_app.models.Like;
import com.example.fbu_app.models.Visit;
import com.example.fbu_app.models.VisitInvitation;
import com.parse.Parse;
import com.parse.ParseObject;

import org.w3c.dom.Comment;

// 
public class FeastApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Register all classes for objects before initializing parse
        ParseObject.registerSubclass(Business.class);
        ParseObject.registerSubclass(Hour.class);
        ParseObject.registerSubclass(Like.class);
        ParseObject.registerSubclass(Visit.class);
        ParseObject.registerSubclass(VisitInvitation.class);


        // Connect to database using secret keys
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(BuildConfig.APPLICATION_ID) // Get keys from secrets
                .clientKey(BuildConfig.CLIENT_KEY)
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
