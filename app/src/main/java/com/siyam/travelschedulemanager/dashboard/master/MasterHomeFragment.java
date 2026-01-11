package com.siyam.travelschedulemanager.dashboard.master;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.card.MaterialCardView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.data.firebase.AuthRepository;
import com.siyam.travelschedulemanager.ui.auth.LoginActivity;

public class MasterHomeFragment extends Fragment {
    private AuthRepository authRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authRepository = new AuthRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_master_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialCardView cardApproveApiChanges = view.findViewById(R.id.card_approve_api_changes);
        MaterialCardView cardApproveAccounts = view.findViewById(R.id.card_approve_accounts);
        MaterialCardView cardChangeRoles = view.findViewById(R.id.card_change_roles);
        MaterialCardView cardLockUsers = view.findViewById(R.id.card_lock_users);
        MaterialCardView cardFullHistory = view.findViewById(R.id.card_full_history);
        MaterialCardView cardProfile = view.findViewById(R.id.card_profile);
        MaterialCardView cardLogout = view.findViewById(R.id.card_logout);
        ImageButton helpButton = view.findViewById(R.id.help_button);

        NavController navController = Navigation.findNavController(view);

        cardApproveApiChanges.setOnClickListener(v -> {
            navController.navigate(R.id.action_masterHome_to_approveApiChanges);
        });

        cardApproveAccounts.setOnClickListener(v -> {
            navController.navigate(R.id.action_masterHome_to_approveAccounts);
        });

        cardChangeRoles.setOnClickListener(v -> {
            navController.navigate(R.id.action_masterHome_to_changeRoles);
        });

        cardLockUsers.setOnClickListener(v -> {
            navController.navigate(R.id.action_masterHome_to_lockUsers);
        });

        cardFullHistory.setOnClickListener(v -> {
            navController.navigate(R.id.action_masterHome_to_fullHistory);
        });

        cardProfile.setOnClickListener(v -> {
            navController.navigate(R.id.action_masterHome_to_profile);
        });

        cardLogout.setOnClickListener(v -> {
            authRepository.signOut();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
        
        helpButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_masterHome_to_help);
        });
    }
}
