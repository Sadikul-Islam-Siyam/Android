package com.siyam.travelschedulemanager.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.util.Constants;
import com.siyam.travelschedulemanager.util.ValidationUtils;
import com.siyam.travelschedulemanager.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    private EditText usernameInput, emailInput, passwordInput, confirmPasswordInput;
    private AutoCompleteTextView roleSpinner;
    private Button registerButton;
    private TextView loginLink;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;
    private String selectedRole = Constants.ROLE_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        setupObservers();
        setupListeners();
    }

    private void initViews() {
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        roleSpinner = findViewById(R.id.role_spinner);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        registerButton = findViewById(R.id.register_button);
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
        authViewModel.getAuthResult().observe(this, result -> {
            progressBar.setVisibility(View.GONE);
            if (result.success) {
                Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
                finish();
            }
        });

        authViewModel.getError().observe(this, error -> {
            progressBar.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(error)) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> handleRegister());

        loginLink.setOnClickListener(v -> finish());
    }

    private void handleRegister() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        String usernameError = ValidationUtils.getUsernameError(username);
        if (usernameError != null) {
            usernameInput.setError(usernameError);
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

        progressBar.setVisibility(View.VISIBLE);
        authViewModel.registerWithRole(username, email, password, selectedRole);
    }
}
