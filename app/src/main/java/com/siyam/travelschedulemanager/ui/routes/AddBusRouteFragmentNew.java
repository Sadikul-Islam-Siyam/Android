package com.siyam.travelschedulemanager.ui.routes;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.BusRoute;
import com.siyam.travelschedulemanager.viewmodel.RouteManagementViewModel;

import java.util.Calendar;
import java.util.Locale;

/**
 * Fragment for adding a new bus route.
 */
public class AddBusRouteFragmentNew extends Fragment {

    private RouteManagementViewModel viewModel;
    private String userRole;

    // UI Components
    private MaterialToolbar toolbar;
    private TextInputEditText busNameInput;
    private AutoCompleteTextView startAutoComplete;
    private AutoCompleteTextView destinationAutoComplete;
    private TextInputEditText startTimeInput;
    private TextInputEditText arrivalTimeInput;
    private TextInputEditText fareInput;
    private MaterialButton saveDraftButton;
    private MaterialButton submitButton;
    private FrameLayout loadingOverlay;

    // Station suggestions
    private final String[] stationSuggestions = {
        "Dhaka", "Chittagong", "Sylhet", "Rajshahi", "Khulna", "Barisal", 
        "Rangpur", "Mymensingh", "Comilla", "Gazipur", "Narayanganj",
        "Jamalpur", "Dinajpur", "Bogra", "Cox's Bazar", "Jessore",
        "Brahmanbaria", "Tangail", "Narsingdi", "Savar", "Tongi",
        "Kamalapur", "Airport", "Banani", "Uttara", "Mirpur"
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userRole = getArguments().getString("userRole", "developer");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_bus_route_new, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(RouteManagementViewModel.class);
        
        initViews(view);
        setupToolbar();
        setupAutocomplete();
        setupTimePickers();
        setupButtons();
        observeViewModel();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        busNameInput = view.findViewById(R.id.busNameInput);
        startAutoComplete = view.findViewById(R.id.startAutoComplete);
        destinationAutoComplete = view.findViewById(R.id.destinationAutoComplete);
        startTimeInput = view.findViewById(R.id.startTimeInput);
        arrivalTimeInput = view.findViewById(R.id.arrivalTimeInput);
        fareInput = view.findViewById(R.id.fareInput);
        saveDraftButton = view.findViewById(R.id.saveDraftButton);
        submitButton = view.findViewById(R.id.submitButton);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void setupAutocomplete() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                stationSuggestions
        );
        startAutoComplete.setAdapter(adapter);
        destinationAutoComplete.setAdapter(adapter);
    }

    private void setupTimePickers() {
        startTimeInput.setOnClickListener(v -> showTimePicker(startTimeInput));
        arrivalTimeInput.setOnClickListener(v -> showTimePicker(arrivalTimeInput));
    }

    private void showTimePicker(TextInputEditText input) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Try to parse existing time
        String existingTime = getText(input);
        if (!existingTime.isEmpty() && existingTime.contains(":")) {
            try {
                String[] parts = existingTime.split(":");
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                // Use current time as default
            }
        }

        TimePickerDialog dialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    input.setText(time);
                }, hour, minute, true);
        dialog.show();
    }

    private String calculateDuration() {
        String startTime = getText(startTimeInput);
        String arrivalTime = getText(arrivalTimeInput);
        
        if (!startTime.isEmpty() && !arrivalTime.isEmpty()) {
            try {
                String[] startParts = startTime.split(":");
                String[] arrivalParts = arrivalTime.split(":");
                
                int startMinutes = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
                int arrivalMinutes = Integer.parseInt(arrivalParts[0]) * 60 + Integer.parseInt(arrivalParts[1]);
                
                // Handle overnight journeys
                if (arrivalMinutes < startMinutes) {
                    arrivalMinutes += 24 * 60;
                }
                
                int durationMinutes = arrivalMinutes - startMinutes;
                int hours = durationMinutes / 60;
                int minutes = durationMinutes % 60;
                
                return String.format(Locale.getDefault(), "%d:%02dh", hours, minutes);
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    private void setupButtons() {
        // Save draft button
        saveDraftButton.setOnClickListener(v -> {
            if (validateForm()) {
                saveRoute("DRAFT");
            }
        });

        // Submit button
        submitButton.setOnClickListener(v -> {
            if (validateForm()) {
                String status = "master".equals(userRole) ? "APPROVED" : "PENDING";
                saveRoute(status);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }

    private boolean validateForm() {
        String busName = getText(busNameInput);
        String start = startAutoComplete.getText().toString().trim();
        String destination = destinationAutoComplete.getText().toString().trim();
        String fare = getText(fareInput);

        if (busName.isEmpty()) {
            busNameInput.setError("Bus name is required");
            busNameInput.requestFocus();
            return false;
        }

        if (start.isEmpty()) {
            startAutoComplete.setError("Start station is required");
            startAutoComplete.requestFocus();
            return false;
        }

        if (destination.isEmpty()) {
            destinationAutoComplete.setError("Destination is required");
            destinationAutoComplete.requestFocus();
            return false;
        }

        if (fare.isEmpty()) {
            fareInput.setError("Fare is required");
            fareInput.requestFocus();
            return false;
        }

        return true;
    }

    private void saveRoute(String status) {
        BusRoute route = new BusRoute();
        
        route.setBusName(getText(busNameInput));
        route.setStart(startAutoComplete.getText().toString().trim());
        route.setDestination(destinationAutoComplete.getText().toString().trim());
        route.setStartTime(getText(startTimeInput));
        route.setArrivalTime(getText(arrivalTimeInput));
        route.setDuration(calculateDuration());
        
        try {
            route.setFare(Double.parseDouble(getText(fareInput)));
        } catch (NumberFormatException e) {
            route.setFare(0);
        }
        
        route.setStatus(status);
        route.setCreatedBy(FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "unknown");
        route.setCreatedAt(Timestamp.now());
        route.setUpdatedAt(Timestamp.now());

        if ("master".equals(userRole) || "DRAFT".equals(status)) {
            viewModel.createBusRouteDirect(route);
        } else {
            viewModel.submitBusRouteForApproval(route, "CREATE", "New bus route: " + route.getBusName());
        }
    }

    private String getText(TextInputEditText input) {
        return input.getText() != null ? input.getText().toString().trim() : "";
    }
}
