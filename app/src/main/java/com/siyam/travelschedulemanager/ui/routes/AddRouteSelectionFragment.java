package com.siyam.travelschedulemanager.ui.routes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.siyam.travelschedulemanager.R;

public class AddRouteSelectionFragment extends Fragment {

    private MaterialToolbar toolbar;
    private MaterialCardView cardAddBusRoute;
    private MaterialCardView cardAddTrainRoute;
    private String userRole = "master";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_route_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get user role from arguments
        if (getArguments() != null) {
            userRole = getArguments().getString("userRole", "master");
        }

        initViews(view);
        setupToolbar();
        setupClickListeners();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        cardAddBusRoute = view.findViewById(R.id.cardAddBusRoute);
        cardAddTrainRoute = view.findViewById(R.id.cardAddTrainRoute);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void setupClickListeners() {
        cardAddBusRoute.setOnClickListener(v -> {
            try {
                Bundle args = new Bundle();
                args.putString("userRole", userRole);
                args.putString("mode", "add"); // Add mode, not edit
                Navigation.findNavController(v).navigate(R.id.action_addRouteSelection_to_addBusRoute, args);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Navigation error", Toast.LENGTH_SHORT).show();
            }
        });

        cardAddTrainRoute.setOnClickListener(v -> {
            try {
                Bundle args = new Bundle();
                args.putString("userRole", userRole);
                args.putString("mode", "add"); // Add mode, not edit
                Navigation.findNavController(v).navigate(R.id.action_addRouteSelection_to_addTrainRoute, args);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Navigation error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
