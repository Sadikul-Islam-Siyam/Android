package com.siyam.travelschedulemanager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.FirebaseApp;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.dashboard.developer.DeveloperDashboardActivity;
import com.siyam.travelschedulemanager.dashboard.master.MasterDashboardActivity;
import com.siyam.travelschedulemanager.dashboard.user.UserDashboardActivity;
import com.siyam.travelschedulemanager.model.User;
import com.siyam.travelschedulemanager.util.Constants;
import com.siyam.travelschedulemanager.util.ValidationUtils;
import com.siyam.travelschedulemanager.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink, forgotPasswordLink;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        
        setContentView(R.layout.activity_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);
        registerLink = findViewById(R.id.register_link);
        forgotPasswordLink = findViewById(R.id.forgot_password_link);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupObservers() {
        authViewModel.getAuthResult().observe(this, result -> {
            if (result.success) {
                Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                // Load user to redirect based on role
                authViewModel.loadCurrentUser();
            }
        });

        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                redirectBasedOnRole(user);
            }
        });

        authViewModel.getError().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(error)) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void redirectBasedOnRole(User user) {
        progressBar.setVisibility(View.GONE);
        Intent intent;
        
        if (Constants.ROLE_MASTER.equals(user.getRole())) {
            intent = new Intent(this, MasterDashboardActivity.class);
        } else if (Constants.ROLE_DEVELOPER.equals(user.getRole())) {
            intent = new Intent(this, DeveloperDashboardActivity.class);
        } else {
            intent = new Intent(this, UserDashboardActivity.class);
        }
        
        startActivity(intent);
        finish();
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> handleLogin());

        registerLink.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        forgotPasswordLink.setOnClickListener(v -> {
            // Show forgot password dialog
            showForgotPasswordDialog();
        });
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate inputs
        if (!ValidationUtils.isValidEmail(email)) {
            emailInput.setError("Please enter a valid email");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        authViewModel.signIn(email, password);
    }

    private void showForgotPasswordDialog() {
        // Simple implementation - you can enhance with a custom dialog
        String email = emailInput.getText().toString().trim();
        if (ValidationUtils.isValidEmail(email)) {
            authViewModel.sendPasswordReset(email);
            Toast.makeText(this, "Password reset email sent to " + email, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Please enter your email first", Toast.LENGTH_SHORT).show();
        }
    }
}
