package com.siyam.travelschedulemanager.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.data.firebase.AuditLogRepository;
import com.siyam.travelschedulemanager.data.firebase.AuthRepository;
import com.siyam.travelschedulemanager.model.Schedule;
import com.siyam.travelschedulemanager.util.Constants;
import com.siyam.travelschedulemanager.viewmodel.AuthViewModel;
import com.siyam.travelschedulemanager.viewmodel.ScheduleViewModel;

public class AddScheduleFragment extends Fragment {
    private ScheduleViewModel scheduleViewModel;
    private AuthViewModel authViewModel;
    private AuditLogRepository auditLogRepository;

    private Spinner spinnerTransportType;
    private Spinner spinnerOrigin;
    private Spinner spinnerDestination;
    private TextInputEditText editDepartureTime;
    private TextInputEditText editArrivalTime;
    private TextInputEditText editFare;
    private TextInputEditText editOperator;
    private TextInputEditText editTrainNumber;
    private Button buttonSubmit;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        auditLogRepository = new AuditLogRepository();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerTransportType = view.findViewById(R.id.spinner_transport_type);
        spinnerOrigin = view.findViewById(R.id.spinner_origin);
        spinnerDestination = view.findViewById(R.id.spinner_destination);
        editDepartureTime = view.findViewById(R.id.edit_departure_time);
        editArrivalTime = view.findViewById(R.id.edit_arrival_time);
        editFare = view.findViewById(R.id.edit_fare);
        editOperator = view.findViewById(R.id.edit_operator);
        editTrainNumber = view.findViewById(R.id.edit_train_number);
        buttonSubmit = view.findViewById(R.id.button_submit);

        setupSpinners();
        setupObservers();

        buttonSubmit.setOnClickListener(v -> submitSchedule());
    }

    private void setupSpinners() {
        // Transport type
        String[] transportTypes = {Constants.TRANSPORT_BUS, Constants.TRANSPORT_TRAIN};
        ArrayAdapter<String> transportAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, transportTypes);
        transportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransportType.setAdapter(transportAdapter);

        // Cities
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, Constants.BANGLADESH_CITIES);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrigin.setAdapter(cityAdapter);
        spinnerDestination.setAdapter(cityAdapter);
    }

    private void setupObservers() {
        scheduleViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        scheduleViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitSchedule() {
        String transportType = spinnerTransportType.getSelectedItem().toString();
        String origin = spinnerOrigin.getSelectedItem().toString();
        String destination = spinnerDestination.getSelectedItem().toString();
        String departureTime = editDepartureTime.getText().toString().trim();
        String arrivalTime = editArrivalTime.getText().toString().trim();
        String fareStr = editFare.getText().toString().trim();
        String operator = editOperator.getText().toString().trim();
        String trainNumber = editTrainNumber.getText().toString().trim();

        // Validation
        if (origin.equals(destination)) {
            Toast.makeText(requireContext(), "Origin and destination cannot be the same", Toast.LENGTH_SHORT).show();
            return;
        }

        if (departureTime.isEmpty() || arrivalTime.isEmpty() || fareStr.isEmpty() || operator.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double fare;
        try {
            fare = Double.parseDouble(fareStr);
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid fare amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate duration (simplified - you can enhance this)
        int duration = 120; // Default 2 hours

        Schedule schedule = new Schedule();
        schedule.setTransportType(transportType);
        schedule.setOrigin(origin);
        schedule.setDestination(destination);
        schedule.setDepartureTime(departureTime);
        schedule.setArrivalTime(arrivalTime);
        schedule.setDuration(duration);
        schedule.setFare(fare);
        schedule.setOperatorName(operator);
        schedule.setTrainNumber(trainNumber.isEmpty() ? null : trainNumber);
        schedule.setCreatedAt(Timestamp.now());

        scheduleViewModel.createSchedule(schedule);

        // Create audit log
        authViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                auditLogRepository.createAuditLog(
                    user.getUid(),
                    user.getUsername(),
                    user.getRole(),
                    Constants.ACTION_CREATE,
                    "schedule",
                    "new",
                    "Created schedule: " + origin + " to " + destination
                );
            }
        });

        // Navigate back or clear fields
        Toast.makeText(requireContext(), "Schedule submitted successfully", Toast.LENGTH_SHORT).show();
    }
}
