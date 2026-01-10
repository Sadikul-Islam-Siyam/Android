package com.siyam.travelschedulemanager.ui.profile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.User;
import com.siyam.travelschedulemanager.util.ThemeManager;
import com.siyam.travelschedulemanager.viewmodel.AuthViewModel;

public class ProfileFragment extends Fragment {
    private AuthViewModel authViewModel;
    private MaterialTextView usernameText, emailText, currentThemeText;
    private Chip roleChip;
    private EditText editUsername, editCurrentPassword, editNewPassword, editConfirmPassword;
    private MaterialButton updateUsernameButton, changePasswordButton;
    private LinearLayout themeSetting;
    private ImageButton backButton;
    private ProgressBar progressBar;
    private ThemeManager themeManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        themeManager = new ThemeManager(requireContext());

        initViews(view);
        setupObservers();
        setupListeners();

        authViewModel.loadCurrentUser();
    }

    private void initViews(View view) {
        usernameText = view.findViewById(R.id.profile_username);
        emailText = view.findViewById(R.id.profile_email);
        roleChip = view.findViewById(R.id.profile_role);
        editUsername = view.findViewById(R.id.edit_username);
        editCurrentPassword = view.findViewById(R.id.edit_current_password);
        editNewPassword = view.findViewById(R.id.edit_new_password);
        editConfirmPassword = view.findViewById(R.id.edit_confirm_password);
        updateUsernameButton = view.findViewById(R.id.button_update_username);
        changePasswordButton = view.findViewById(R.id.button_change_password);
        themeSetting = view.findViewById(R.id.theme_setting);
        currentThemeText = view.findViewById(R.id.text_current_theme);
        backButton = view.findViewById(R.id.back_button);
        progressBar = view.findViewById(R.id.progress_bar);

        // Set current theme text
        currentThemeText.setText(themeManager.getThemeName());
    }

    private void setupObservers() {
        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUI(user);
            }
        });
    }

    private void setupListeners() {
        backButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });

        themeSetting.setOnClickListener(v -> showThemeDialog());

        updateUsernameButton.setOnClickListener(v -> updateUsername());

        changePasswordButton.setOnClickListener(v -> changePassword());
    }

    private void updateUI(User user) {
        usernameText.setText(user.getUsername());
        emailText.setText(user.getEmail());
        roleChip.setText(user.getRole());
        editUsername.setText(user.getUsername());
    }

    private void showThemeDialog() {
        String[] themes = {"Light", "Dark", "System Default"};
        int currentTheme = themeManager.getThemeMode();

        new AlertDialog.Builder(requireContext())
                .setTitle("Choose Theme")
                .setSingleChoiceItems(themes, currentTheme, (dialog, which) -> {
                    themeManager.setThemeMode(which);
                    currentThemeText.setText(themeManager.getThemeName());
                    dialog.dismiss();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateUsername() {
        String newUsername = editUsername.getText().toString().trim();
        
        if (TextUtils.isEmpty(newUsername)) {
            editUsername.setError("Username cannot be empty");
            return;
        }

        if (newUsername.length() < 3) {
            editUsername.setError("Username must be at least 3 characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.getUid())
                    .update("username", newUsername)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        usernameText.setText(newUsername);
                        Toast.makeText(requireContext(), "Username updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void changePassword() {
        String currentPassword = editCurrentPassword.getText().toString();
        String newPassword = editNewPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(currentPassword)) {
            editCurrentPassword.setError("Enter current password");
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            editNewPassword.setError("Enter new password");
            return;
        }

        if (newPassword.length() < 6) {
            editNewPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            editConfirmPassword.setError("Passwords do not match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
            
            user.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        user.updatePassword(newPassword)
                                .addOnSuccessListener(aVoid1 -> {
                                    progressBar.setVisibility(View.GONE);
                                    editCurrentPassword.setText("");
                                    editNewPassword.setText("");
                                    editConfirmPassword.setText("");
                                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(requireContext(), "Failed to change password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        editCurrentPassword.setError("Incorrect current password");
                    });
        }
    }
}
