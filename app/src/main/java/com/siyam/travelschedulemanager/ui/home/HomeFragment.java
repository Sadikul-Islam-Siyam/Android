package com.siyam.travelschedulemanager.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.User;
import com.siyam.travelschedulemanager.ui.auth.LoginActivity;
import com.siyam.travelschedulemanager.util.Constants;
import com.siyam.travelschedulemanager.viewmodel.AuthViewModel;

public class HomeFragment extends Fragment {
    private AuthViewModel authViewModel;
    private User currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        authViewModel.loadCurrentUser();

        setupObservers();
        setupClickListeners(view);
    }

    private void setupObservers() {
        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                updateUIBasedOnRole();
            }
        });
    }

    private void setupClickListeners(View view) {
        // Common cards for all users
        view.findViewById(R.id.card_create_plan).setOnClickListener(v -> {
            // Navigate to create plan
        });

        view.findViewById(R.id.card_saved_plans).setOnClickListener(v -> {
            // Navigate to saved plans
        });

        view.findViewById(R.id.card_route_finder).setOnClickListener(v -> {
            // Navigate to route finder
        });

        view.findViewById(R.id.card_logout).setOnClickListener(v -> {
            authViewModel.signOut();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });

        // Developer/Master cards
        MaterialCardView manageSchedules = view.findViewById(R.id.card_manage_schedules);
        if (manageSchedules != null) {
            manageSchedules.setOnClickListener(v -> {
                // Navigate to manage schedules
            });
        }

        // Master only cards
        MaterialCardView accountApprovals = view.findViewById(R.id.card_account_approvals);
        if (accountApprovals != null) {
            accountApprovals.setOnClickListener(v -> {
                // Navigate to account approvals
            });
        }
    }

    private void updateUIBasedOnRole() {
        if (currentUser == null) return;

        View view = getView();
        if (view == null) return;

        String role = currentUser.getRole();

        // Show/hide developer features
        View developerSection = view.findViewById(R.id.developer_section);
        if (developerSection != null) {
            developerSection.setVisibility(
                    role.equals(Constants.ROLE_DEVELOPER) || role.equals(Constants.ROLE_MASTER)
                            ? View.VISIBLE : View.GONE
            );
        }

        // Show/hide master features
        View masterSection = view.findViewById(R.id.master_section);
        if (masterSection != null) {
            masterSection.setVisibility(
                    role.equals(Constants.ROLE_MASTER) ? View.VISIBLE : View.GONE
            );
        }
    }
}
