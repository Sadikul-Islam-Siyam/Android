package com.siyam.travelschedulemanager.ui.route;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.siyam.travelschedulemanager.data.remote.dto.ScheduleDTO;
import com.siyam.travelschedulemanager.util.Constants;
import com.siyam.travelschedulemanager.viewmodel.RestScheduleViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RouteFinderFragment extends Fragment {
    private RestScheduleViewModel scheduleViewModel;
    private AutoCompleteTextView editTextOrigin;
    private AutoCompleteTextView editTextDestination;
    private TextInputEditText editTextDate;
    private ChipGroup chipGroupTransport;
    private Chip chipAll, chipBus, chipTrain;
    private Button buttonFindRoutes;
    private RecyclerView recyclerViewResults;
    private ProgressBar progressBar;
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;
    private RouteResultAdapter adapter;

    // Station suggestions for autocomplete
    private static final String[] STATION_SUGGESTIONS = {
        "Dhaka", "Kamalapur", "Airport", "Chittagong", "Sylhet", "Rajshahi", 
        "Khulna", "Barisal", "Rangpur", "Mymensingh", "Comilla", "Gazipur",
        "Narayanganj", "Tongi", "Narsingdi", "Brahmanbaria", "Kishoreganj",
        "Cox's Bazar", "Jessore", "Dinajpur", "Bogra", "Saidpur", "Pabna",
        "Tangail", "Jamalpur", "Netrokona", "Sherpur", "Habiganj", "Moulvibazar",
        "Sunamganj", "Nawabganj", "Naogaon", "Natore", "Sirajganj", "Kushtia",
        "Meherpur", "Chuadanga", "Jhenaidah", "Magura", "Narail", "Satkhira",
        "Bagerhat", "Pirojpur", "Patuakhali", "Bhola", "Barguna", "Jhalokati",
        "Lakshmipur", "Noakhali", "Feni", "Chandpur", "Shariatpur", "Madaripur",
        "Gopalganj", "Faridpur", "Manikganj", "Munshiganj", "Rajbari",
        "Bandarban", "Rangamati", "Khagrachhari", "Thakurgaon", "Panchagarh",
        "Nilphamari", "Lalmonirhat", "Kurigram", "Gaibandha", "Joypurhat"
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleViewModel = new ViewModelProvider(this).get(RestScheduleViewModel.class);
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
        setupRecyclerView();
        setupAutoComplete();
        setupDatePicker();
        setupTransportFilter();
        setupFindButton();
        setupObservers();
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
        recyclerViewResults = view.findViewById(R.id.recycler_view_results);
        progressBar = view.findViewById(R.id.progress_bar);

        // Set default date to today
        editTextDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void setupRecyclerView() {
        adapter = new RouteResultAdapter(new ArrayList<>());
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewResults.setAdapter(adapter);
    }

    private void setupObservers() {
        scheduleViewModel.getSearchResults().observe(getViewLifecycleOwner(), schedules -> {
            if (schedules != null) {
                // Filter by transport type if needed
                List<ScheduleDTO> filteredSchedules = filterByTransportType(schedules);
                adapter.updateSchedules(filteredSchedules);
                
                if (filteredSchedules.isEmpty()) {
                    Toast.makeText(requireContext(), "No routes found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), 
                            "Found " + filteredSchedules.size() + " routes", 
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        scheduleViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            buttonFindRoutes.setEnabled(!isLoading);
        });

        scheduleViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (!TextUtils.isEmpty(error)) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private List<ScheduleDTO> filterByTransportType(List<ScheduleDTO> schedules) {
        if (chipAll.isChecked()) {
            return schedules;
        }

        List<ScheduleDTO> filtered = new ArrayList<>();
        for (ScheduleDTO schedule : schedules) {
            if (chipBus.isChecked() && "BUS".equals(schedule.getType())) {
                filtered.add(schedule);
            } else if (chipTrain.isChecked() && "TRAIN".equals(schedule.getType())) {
                filtered.add(schedule);
            }
        }
        return filtered;
    }

    private void setupAutoComplete() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                STATION_SUGGESTIONS
        );
        
        editTextOrigin.setAdapter(adapter);
        editTextDestination.setAdapter(adapter);
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

        // Call REST API to search routes
        scheduleViewModel.searchRoutes(origin, destination);
    }
}
