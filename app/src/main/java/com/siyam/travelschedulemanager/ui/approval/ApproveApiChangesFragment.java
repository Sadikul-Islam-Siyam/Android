package com.siyam.travelschedulemanager.ui.approval;

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
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.ui.approval.adapter.PendingRouteAdapter;
import com.siyam.travelschedulemanager.viewmodel.ApprovalViewModel;
import com.siyam.travelschedulemanager.viewmodel.AuthViewModel;

public class ApproveApiChangesFragment extends Fragment {
    private ApprovalViewModel approvalViewModel;
    private AuthViewModel authViewModel;
    private PendingRouteAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        approvalViewModel = new ViewModelProvider(this).get(ApprovalViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_approve_api_changes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_pending_routes);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new PendingRouteAdapter((pendingRoute, action) -> {
            authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    if ("approve".equals(action)) {
                        approvalViewModel.approvePendingRoute(
                            pendingRoute.getId(),
                            user.getUid(),
                            user.getUsername(),
                            user.getRole(),
                            "Approved by " + user.getUsername()
                        );
                    } else if ("reject".equals(action)) {
                        approvalViewModel.rejectPendingRoute(
                            pendingRoute.getId(),
                            user.getUid(),
                            user.getUsername(),
                            user.getRole(),
                            "Rejected by " + user.getUsername()
                        );
                    }
                }
            });
        });

        recyclerView.setAdapter(adapter);

        approvalViewModel.loadPendingRoutes();

        approvalViewModel.getPendingRoutes().observe(getViewLifecycleOwner(), routes -> {
            adapter.setPendingRoutes(routes);
        });

        approvalViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        approvalViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
