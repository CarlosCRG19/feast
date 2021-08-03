package com.example.fbu_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fbu_app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static String TAG = "LoginActivity"; // TAG for log messages

    // VIEWS
    private Button btnLogin;
    private TextInputEditText etUsername,  etPassword; // ETs from material design
    private TextView tvSignup;


    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_login);

        // Check whether user is already logged in
        if (ParseUser.getCurrentSessionToken() != null) {
            goMainActivity();
        }

        // Get views from layout
        setViews();
        // Set listeners for the login button and for the clickable text
        setListeners();
    }

    //-- VIEWS METHODS --//

    // Assign views on layout to variables
    private void setViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvSignup = findViewById(R.id.tvSignup);
    }

    // Set click listeners for the views
    private void setListeners(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Login Button clicked");
                // Get content on both EditTexts
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                // Login user
                loginUser(username, password);
            }
        });

        // Setup clickable text view
        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Go signup button clicked");
                goSignupActivity();
            }
        });
    }

    //-- LOGIN METHOD --//

    // Tries to login user with username and password
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username + password);
        // Execute login async call
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // Check for errors
                if(e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Issue with login!", Toast.LENGTH_LONG ).show();
                    return;
                }
                // If login was successful go to main activity
                Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
                goMainActivity();
            }
        });
    }

    //-- METHODS TO REDIRECT USER --//

    // Uses intent to pass to MainActivity and ends LoginActivity
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    // Uses intent to go to Signup (used when user does not have an account yet)
    private void goSignupActivity() {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
        finish();
    }
}
