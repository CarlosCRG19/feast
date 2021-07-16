package com.example.fbu_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static String TAG = "LoginActivity"; // TAG for log messages

    // VIEWS
    Button btnLogin, btnSignup;
    EditText etUsername,  etPassword;


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
        // Set click listener for btnLogin
        setLoginListener();
        // Set click listener for btnSignup
        setSignUpListener();
    }

    // VIEWS METHODS

    // Assign views on layout to variables
    private void setViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
    }

    // Set click listener for login
    private void setLoginListener(){
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
    }

    // Set click listener for login
    private void setSignUpListener(){
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Signup Button clicked");
                // Get content on both EditTexts
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                // Login user
                signupUser(username, password);
            }
        });
    }


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
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                goMainActivity();
            }
        });
    }

    // Register new user with new credentials
    private void signupUser(String username, String password) {
        // Create new user instance
        ParseUser newUser = new ParseUser();
        newUser.put("username", username);
        newUser.put("password", password);
        // Execute login async call
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // Check for errors
                if(e != null) {
                    Log.e(TAG, "Issue trying to sign up user", e);
                    Toast.makeText(LoginActivity.this, "Issue with signing up!", Toast.LENGTH_LONG ).show();
                    return;
                }
                // If signup was successful go to main activity
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
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
}
