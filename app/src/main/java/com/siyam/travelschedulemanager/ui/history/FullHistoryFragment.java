package com.siyam.travelschedulemanager.ui.history;

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
import com.siyam.travelschedulemanager.ui.history.adapter.AuditLogAdapter;
import com.siyam.travelschedulemanager.viewmodel.ApprovalViewModel;

public class FullHistoryFragment extends Fragment {
    private ApprovalViewModel approvalViewModel;
    private AuditLogAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        approvalViewModel = new ViewModelProvider(this).get(ApprovalViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_full_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_audit_logs);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new AuditLogAdapter();
        recyclerView.setAdapter(adapter);

        approvalViewModel.loadRecentAuditLogs(100);

        approvalViewModel.getAuditLogs().observe(getViewLifecycleOwner(), logs -> {
            adapter.setAuditLogs(logs);
        });

        approvalViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
