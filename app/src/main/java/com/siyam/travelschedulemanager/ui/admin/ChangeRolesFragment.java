package com.siyam.travelschedulemanager.ui.admin;

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
import com.siyam.travelschedulemanager.ui.admin.adapter.UserRoleAdapter;
import com.siyam.travelschedulemanager.viewmodel.AdminViewModel;

public class ChangeRolesFragment extends Fragment {
    private AdminViewModel adminViewModel;
    private UserRoleAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_roles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_roles);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new UserRoleAdapter((user, newRole) -> {
            adminViewModel.changeUserRole(user.getUid(), newRole);
        });

        recyclerView.setAdapter(adapter);

        adminViewModel.loadApprovedUsers();

        adminViewModel.getAllUsers().observe(getViewLifecycleOwner(), users -> {
            adapter.setUsers(users);
        });

        adminViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        adminViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
