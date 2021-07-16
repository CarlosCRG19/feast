package com.example.fbu_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fbu_app.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    public static String TAG = "SignupActivity"; // TAG for log messages

    // VIEWS
    Button  btnSignup, btnGoLogin;
    EditText etUsername,  etPassword, etEmail;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_signup);

        // Check whether user is already logged in
        if (ParseUser.getCurrentSessionToken() != null) {
            goMainActivity();
        }

        // Get views from layout
        setViews();
        // Set click listener for btnSignup
        setSignUpListener();
        // Set listener for returning to login
        setGoLoginListener();
    }

    // VIEWS METHODS

    // Assign views on layout to variables
    private void setViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        btnSignup = findViewById(R.id.btnSignup);
        btnGoLogin = findViewById(R.id.btnGoLogin);
    }

    // Set click listener for signup
    private void setSignUpListener(){
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Signup Button clicked");
                // Get content on both EditTexts
                String username = etUsername.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                // Signup user
                signupUser(username, email, password);
            }
        });
    }

    // Set click listener to return to login
    private void setGoLoginListener(){
        btnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Go login button clicked");
                goLoginActivity();
            }
        });
    }

    // Register new user with new credentials
    private void signupUser(String username, String email, String password) {
        // Create new user instance
        ParseUser newUser = new ParseUser();
        newUser.put("username", username);
        newUser.put("email", email);
        newUser.put("password", password);
        // Execute login async call
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // Check for errors
                if(e != null) {
                    Log.e(TAG, "Issue trying to sign up user", e);
                    Toast.makeText(SignupActivity.this, "Issue with signing up!", Toast.LENGTH_LONG ).show();
                    return;
                }
                // If signup was successful go to main activity
                Toast.makeText(SignupActivity.this, "Successfully signed up!", Toast.LENGTH_SHORT).show();
                goMainActivity();
            }
        });
    }

    // Uses intent to pass to MainActivity and ends LoginActivity
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    // Uses intent to pass to return to LoginActivity
    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
