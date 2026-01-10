package com.siyam.travelschedulemanager.ui.schedule;

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
import com.siyam.travelschedulemanager.ui.schedule.adapter.ScheduleAdapter;
import com.siyam.travelschedulemanager.viewmodel.ScheduleViewModel;

public class SchedulesFragment extends Fragment {
    private ScheduleViewModel scheduleViewModel;
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedules, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scheduleViewModel = new ViewModelProvider(requireActivity()).get(ScheduleViewModel.class);

        setupRecyclerView(view);
        setupObservers();
        loadSchedules();

        FloatingActionButton fab = view.findViewById(R.id.fab_add_schedule);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Toast.makeText(requireContext(), "Add Schedule - Coming soon", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.schedules_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ScheduleAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        scheduleViewModel.getSchedules().observe(getViewLifecycleOwner(), schedules -> {
            if (schedules != null) {
                adapter.setSchedules(schedules);
            }
        });

        scheduleViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSchedules() {
        scheduleViewModel.loadAllSchedules();
    }
}
