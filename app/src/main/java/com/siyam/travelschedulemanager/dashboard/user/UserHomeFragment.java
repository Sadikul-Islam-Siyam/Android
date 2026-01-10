package com.siyam.travelschedulemanager.dashboard.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.google.android.material.card.MaterialCardView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.data.firebase.AuthRepository;
import com.siyam.travelschedulemanager.ui.auth.LoginActivity;

public class UserHomeFragment extends Fragment {
    private AuthRepository authRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authRepository = new AuthRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialCardView cardCreatePlan = view.findViewById(R.id.card_create_plan);
        MaterialCardView cardSavedPlans = view.findViewById(R.id.card_saved_plans);
        MaterialCardView cardRouteFinder = view.findViewById(R.id.card_route_finder);
        MaterialCardView cardLogout = view.findViewById(R.id.card_logout);

        NavController navController = Navigation.findNavController(view);

        cardCreatePlan.setOnClickListener(v -> {
            navController.navigate(R.id.action_userHome_to_createPlan);
        });

        cardSavedPlans.setOnClickListener(v -> {
            navController.navigate(R.id.action_userHome_to_savedPlans);
        });

        cardRouteFinder.setOnClickListener(v -> {
            navController.navigate(R.id.action_userHome_to_routeFinder);
        });

        cardLogout.setOnClickListener(v -> {
            authRepository.signOut();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }
}
