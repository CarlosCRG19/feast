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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

// Creates new account for the user
public class SignupActivity extends AppCompatActivity {

    public static String TAG = "SignupActivity"; // TAG for log messages

    // VIEWS
    private Button  btnSignup;
    private TextInputEditText etUsername, etFirstName, etLastName, etPassword, etDescription, etEmail;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_signup);

        // Get views from layout
        setViews();
        // Set listeners to create account and go to login
        setListeners();
    }

    // Assign views on layout to variables
    private void setViews() {
        // ETs
        etUsername = findViewById(R.id.etUsername);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etDescription = findViewById(R.id.etDescription);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        // Buttons
        btnSignup = findViewById(R.id.btnSignup);
        // Clickable text views
        tvLogin = findViewById(R.id.tvLogin);
    }

    // Set listeners for button and clickable text
    private void setListeners() {
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Variable declaration
                String username, firstName, lastName, email, description, password;
                // Get content of EditTexts
                username = etUsername.getText().toString();
                email = etEmail.getText().toString();
                password = etPassword.getText().toString();
                // Optional texts
                firstName = etFirstName.getText().toString();
                lastName = etLastName.getText().toString();
                description = etDescription.getText().toString();
                // Try to sign up user
                signupUser(username, firstName, lastName, description, email, password);
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display log message
                Log.i(TAG, "Go login button clicked");
                // Launch LoginActivity
                goLoginActivity();
            }
        });
    }

    //-- SIGNUP METHODS --//

    // Register new user with new credentials
    private void signupUser(String username, String firstName, String lastName, String description, String email, String password) {
        // Create new user instance
        ParseUser newUser = new ParseUser();
        newUser.put("username", username);
        newUser.put("firstName", firstName);
        newUser.put("lastName", lastName);
        newUser.put("description", description);
        newUser.put("email", email);
        newUser.put("password", password);
        // Execute login async call
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                // Check for errors
                if(e != null) {
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
