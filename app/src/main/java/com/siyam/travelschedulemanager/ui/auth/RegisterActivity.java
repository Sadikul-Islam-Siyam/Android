package com.siyam.travelschedulemanager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.util.Constants;
import com.siyam.travelschedulemanager.util.ValidationUtils;
import com.siyam.travelschedulemanager.viewmodel.RestAuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput, fullNameInput;
    private AutoCompleteTextView roleSpinner;
    private Button registerButton;
    private ImageButton backButton;
    private TextView loginLink;
    private ProgressBar progressBar;
    private RestAuthViewModel authViewModel;
    private String selectedRole = Constants.ROLE_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(RestAuthViewModel.class);

        initViews();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        fullNameInput = findViewById(R.id.full_name_input);
        roleSpinner = findViewById(R.id.role_spinner);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
        backButton = findViewById(R.id.back_button);
        loginLink = findViewById(R.id.login_link);
        progressBar = findViewById(R.id.progress_bar);
        
        // Setup role spinner
        String[] roles = {"User", "Developer"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        roleSpinner.setAdapter(adapter);
        roleSpinner.setText("User", false);
        roleSpinner.setOnItemClickListener((parent, view, position, id) -> {
            selectedRole = position == 0 ? Constants.ROLE_USER : Constants.ROLE_DEVELOPER;
        });
    }

    private void setupObservers() {
        authViewModel.getRegisterSuccess().observe(this, success -> {
            if (success != null && success) {
                showSuccessDialog();
            }
        });

        authViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            registerButton.setEnabled(!isLoading);
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (!TextUtils.isEmpty(error)) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                authViewModel.clearError();
            }
        });
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Registration Submitted")
                .setMessage("Your registration has been submitted successfully!\n\n" +
                        "Your account is pending approval from the Master Admin.\n\n" +
                        "You will be able to login once your account is approved.")
                .setPositiveButton("Check Status", (dialog, which) -> {
                    showStatusCheckDialog();
                })
                .setNegativeButton("Close", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showStatusCheckDialog() {
        EditText input = new EditText(this);
        input.setHint("Enter your username");
        
        new AlertDialog.Builder(this)
                .setTitle("Check Account Status")
                .setView(input)
                .setPositiveButton("Check", (dialog, which) -> {
                    String username = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(username)) {
                        checkStatus(username);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkStatus(String username) {
        authViewModel.checkAccountStatus(username);
        authViewModel.getStatusMessage().observe(this, status -> {
            if (!TextUtils.isEmpty(status)) {
                new AlertDialog.Builder(this)
                        .setTitle("Account Status")
                        .setMessage(status)
                        .setPositiveButton("OK", (dialog, which) -> finish())
                        .show();
            }
        });
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleRegister());

        loginLink.setOnClickListener(v -> finish());
        
        backButton.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String fullName = fullNameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        String usernameError = ValidationUtils.getUsernameError(username);
        if (usernameError != null) {
            usernameInput.setError(usernameError);
            return;
        }

        if (TextUtils.isEmpty(fullName) || fullName.length() < 3) {
            fullNameInput.setError("Full name must be at least 3 characters");
            return;
        }

        String emailError = ValidationUtils.getEmailError(email);
        if (emailError != null) {
            emailInput.setError(emailError);
            return;
        }

        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null) {
            passwordInput.setError(passwordError);
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        authViewModel.register(username, email, password, fullName, selectedRole);
    }
}
