package com.siyam.travelschedulemanager.ui.plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.data.firebase.AuthRepository;
import com.siyam.travelschedulemanager.ui.plan.adapter.PlanAdapter;
import com.siyam.travelschedulemanager.viewmodel.PlanViewModel;

public class PlansFragment extends Fragment {
    private PlanViewModel planViewModel;
    private RecyclerView recyclerView;
    private PlanAdapter adapter;
    private AuthRepository authRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plans, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        authRepository = new AuthRepository();
        planViewModel = new ViewModelProvider(requireActivity()).get(PlanViewModel.class);

        setupRecyclerView(view);
        setupObservers();
        loadPlans();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_plan);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                // Navigate to create plan
                Toast.makeText(requireContext(), "Create Plan - Coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.plans_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PlanAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        planViewModel.getPlans().observe(getViewLifecycleOwner(), plans -> {
            if (plans != null) {
                adapter.setPlans(plans);
            }
        });

        planViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        planViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPlans() {
        String userId = authRepository.getCurrentUserId();
        if (userId != null) {
            planViewModel.loadUserPlans(userId);
        }
    }
}
