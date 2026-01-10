package com.siyam.travelschedulemanager.ui.route;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.util.Constants;
import com.siyam.travelschedulemanager.viewmodel.ScheduleViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RouteFinderFragment extends Fragment {
    private ScheduleViewModel scheduleViewModel;
    private TextInputEditText editTextOrigin;
    private TextInputEditText editTextDestination;
    private TextInputEditText editTextDate;
    private ChipGroup chipGroupTransport;
    private Chip chipAll, chipBus, chipTrain;
    private Button buttonFindRoutes;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        selectedDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_finder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupDatePicker();
        setupTransportFilter();
        setupFindButton();

        scheduleViewModel.loadAllSchedules();
    }

    private void initViews(View view) {
        editTextOrigin = view.findViewById(R.id.edit_text_origin);
        editTextDestination = view.findViewById(R.id.edit_text_destination);
        editTextDate = view.findViewById(R.id.edit_text_date);
        chipGroupTransport = view.findViewById(R.id.chip_group_transport);
        chipAll = view.findViewById(R.id.chip_all);
        chipBus = view.findViewById(R.id.chip_bus);
        chipTrain = view.findViewById(R.id.chip_train);
        buttonFindRoutes = view.findViewById(R.id.button_find_routes);

        // Set default date to today
        editTextDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void setupDatePicker() {
        editTextDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    editTextDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        
        // Don't allow past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void setupTransportFilter() {
        chipGroupTransport.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // If All is checked, uncheck others
            if (checkedIds.contains(R.id.chip_all)) {
                if (chipBus.isChecked()) chipBus.setChecked(false);
                if (chipTrain.isChecked()) chipTrain.setChecked(false);
            } else {
                // If Bus or Train is checked, uncheck All
                if (checkedIds.contains(R.id.chip_bus) || checkedIds.contains(R.id.chip_train)) {
                    if (chipAll.isChecked()) chipAll.setChecked(false);
                }
                
                // If nothing is checked, check All
                if (checkedIds.isEmpty()) {
                    chipAll.setChecked(true);
                }
            }
        });
    }

    private void setupFindButton() {
        buttonFindRoutes.setOnClickListener(v -> findRoutes());
    }

    private void findRoutes() {
        String origin = editTextOrigin.getText().toString().trim();
        String destination = editTextDestination.getText().toString().trim();

        if (origin.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter origin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (destination.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter destination", Toast.LENGTH_SHORT).show();
            return;
        }

        if (origin.equalsIgnoreCase(destination)) {
            Toast.makeText(requireContext(), "Origin and destination cannot be the same", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected transport type
        String transportType = "ALL";
        if (chipBus.isChecked() && !chipTrain.isChecked()) {
            transportType = "BUS";
        } else if (chipTrain.isChecked() && !chipBus.isChecked()) {
            transportType = "TRAIN";
        }

        Date travelDate = selectedDate.getTime();
        int maxLegs = 3;

        // Here you would filter by transport type
        scheduleViewModel.findOptimalRoutes(origin, destination, travelDate, maxLegs);
        
        Toast.makeText(requireContext(), 
                "Finding " + transportType.toLowerCase() + " routes from " + origin + " to " + destination, 
                Toast.LENGTH_SHORT).show();
    }
}
