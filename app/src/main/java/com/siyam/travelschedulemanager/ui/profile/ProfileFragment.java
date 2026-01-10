package com.siyam.travelschedulemanager.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.User;
import com.siyam.travelschedulemanager.ui.auth.LoginActivity;
import com.siyam.travelschedulemanager.viewmodel.AuthViewModel;

public class ProfileFragment extends Fragment {
    private AuthViewModel authViewModel;
    private TextView username, email, role, status;
    private MaterialButton logoutButton;

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

        initViews(view);
        setupObservers();
        setupListeners();

        authViewModel.loadCurrentUser();
    }

    private void initViews(View view) {
        username = view.findViewById(R.id.profile_username);
        email = view.findViewById(R.id.profile_email);
        role = view.findViewById(R.id.profile_role);
        status = view.findViewById(R.id.profile_status);
        logoutButton = view.findViewById(R.id.logout_button);
    }

    private void setupObservers() {
        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                updateUI(user);
            }
        });
    }

    private void setupListeners() {
        logoutButton.setOnClickListener(v -> {
            authViewModel.signOut();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }

    private void updateUI(User user) {
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        role.setText(user.getRole());
        status.setText(user.getStatus());
    }
}
