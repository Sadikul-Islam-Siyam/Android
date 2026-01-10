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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.RouteStop;
import com.siyam.travelschedulemanager.model.TrainRoute;
import com.siyam.travelschedulemanager.ui.adapter.IntermediateStopAdapter;
import com.siyam.travelschedulemanager.viewmodel.RouteManagementViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Fragment for adding a new train route with separate start/destination and intermediate stops.
 */
public class AddTrainRouteFragment extends Fragment implements IntermediateStopAdapter.OnStopActionListener {

    private RouteManagementViewModel viewModel;
    private String userRole;

    // UI Components - Train Info
    private MaterialToolbar toolbar;
    private TextInputEditText trainNameInput;
    private TextInputEditText trainCodeInput;
    private AutoCompleteTextView offDayDropdown;
    
    // UI Components - Start Station
    private AutoCompleteTextView startStationInput;
    private TextInputEditText startDepTimeInput;
    
    // UI Components - Destination Station
    private AutoCompleteTextView destStationInput;
    private TextInputEditText destArrTimeInput;
    private TextInputEditText destFareInput;
    
    // UI Components - Intermediate Stops
    private RecyclerView stopsRecyclerView;
    private MaterialButton addStopButton;
    
    // UI Components - Message & Actions
    private TextInputEditText messageInput;
    private MaterialButton saveDraftButton;
    private MaterialButton submitButton;
    private FrameLayout loadingOverlay;

    // Adapter
    private IntermediateStopAdapter stopAdapter;
    private List<RouteStop> intermediateStops = new ArrayList<>();

    // Station suggestions
    private final String[] stationSuggestions = {
        "Dhaka", "Kamalapur", "Airport", "Chittagong", "Sylhet", "Rajshahi", 
        "Khulna", "Barisal", "Rangpur", "Mymensingh", "Comilla", "Gazipur", 
        "Narayanganj", "Jamalpur", "Dinajpur", "Bogra", "Cox's Bazar", 
        "Jessore", "Brahmanbaria", "Tangail", "Narsingdi", "Tongi",
        "Banani", "Uttara", "Mirpur", "Savar", "Cumilla", "Feni",
        "Noakhali", "Laksam", "Chandpur", "Akhaura"
    };

    private final String[] offDays = {
        "None", "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"
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
        return inflater.inflate(R.layout.fragment_add_train_route_v2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        viewModel = new ViewModelProvider(this).get(RouteManagementViewModel.class);
        
        initViews(view);
        setupToolbar();
        setupDropdowns();
        setupTimePickers();
        setupStopsRecyclerView();
        setupButtons();
        observeViewModel();
    }

    private void initViews(View view) {
        // Toolbar
        toolbar = view.findViewById(R.id.toolbar);
        
        // Train Info
        trainNameInput = view.findViewById(R.id.trainNameInput);
        trainCodeInput = view.findViewById(R.id.trainCodeInput);
        offDayDropdown = view.findViewById(R.id.offDayDropdown);
        
        // Start Station
        startStationInput = view.findViewById(R.id.startStationInput);
        startDepTimeInput = view.findViewById(R.id.startDepTimeInput);
        
        // Destination Station
        destStationInput = view.findViewById(R.id.destStationInput);
        destArrTimeInput = view.findViewById(R.id.destArrTimeInput);
        destFareInput = view.findViewById(R.id.destFareInput);
        
        // Intermediate Stops
        stopsRecyclerView = view.findViewById(R.id.stopsRecyclerView);
        addStopButton = view.findViewById(R.id.addStopButton);
        
        // Message & Actions
        messageInput = view.findViewById(R.id.messageInput);
        saveDraftButton = view.findViewById(R.id.saveDraftButton);
        submitButton = view.findViewById(R.id.submitButton);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void setupDropdowns() {
        // Off Day dropdown
        ArrayAdapter<String> offDayAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                offDays
        );
        offDayDropdown.setAdapter(offDayAdapter);
        offDayDropdown.setText("Friday", false);

        // Station autocompletes
        ArrayAdapter<String> stationAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                stationSuggestions
        );
        startStationInput.setAdapter(stationAdapter);
        destStationInput.setAdapter(stationAdapter);
    }

    private void setupTimePickers() {
        startDepTimeInput.setOnClickListener(v -> showTimePicker(startDepTimeInput));
        destArrTimeInput.setOnClickListener(v -> showTimePicker(destArrTimeInput));
    }

    private void showTimePicker(TextInputEditText input) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String existingTime = getText(input);
        if (!existingTime.isEmpty() && existingTime.contains(":")) {
            try {
                String[] parts = existingTime.split(":");
                hour = Integer.parseInt(parts[0]);
                minute = Integer.parseInt(parts[1]);
            } catch (Exception e) {
                // Use current time
            }
        }

        TimePickerDialog dialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                    input.setText(time);
                }, hour, minute, true);
        dialog.show();
    }

    private void setupStopsRecyclerView() {
        stopAdapter = new IntermediateStopAdapter(requireContext(), intermediateStops, this);
        stopAdapter.setStationSuggestions(Arrays.asList(stationSuggestions));
        
        stopsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        stopsRecyclerView.setAdapter(stopAdapter);
        stopsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setupButtons() {
        addStopButton.setOnClickListener(v -> {
            RouteStop newStop = new RouteStop("", "", "", 0);
            stopAdapter.addStop(newStop);
            stopsRecyclerView.smoothScrollToPosition(intermediateStops.size() - 1);
        });

        saveDraftButton.setOnClickListener(v -> {
            if (validateForm()) {
                saveRoute("DRAFT");
            }
        });

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
        String trainName = getText(trainNameInput);
        String startStation = startStationInput.getText().toString().trim();
        String destStation = destStationInput.getText().toString().trim();

        if (trainName.isEmpty()) {
            trainNameInput.setError("Train name is required");
            trainNameInput.requestFocus();
            return false;
        }

        if (startStation.isEmpty()) {
            startStationInput.setError("Start station is required");
            startStationInput.requestFocus();
            return false;
        }

        if (destStation.isEmpty()) {
            destStationInput.setError("Destination is required");
            destStationInput.requestFocus();
            return false;
        }

        // Validate intermediate stops have station names
        List<RouteStop> currentStops = stopAdapter.getStops();
        for (int i = 0; i < currentStops.size(); i++) {
            RouteStop stop = currentStops.get(i);
            if (stop.getStation() == null || stop.getStation().trim().isEmpty()) {
                Toast.makeText(requireContext(), "Intermediate stop " + (i + 1) + " needs a station name", 
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void saveRoute(String status) {
        TrainRoute route = new TrainRoute();
        
        String trainName = getText(trainNameInput);
        String trainCode = getText(trainCodeInput);
        String offDay = offDayDropdown.getText().toString();
        
        // Basic info
        route.setTrainName(trainName);
        route.setTrainNumber(trainCode);
        route.setOffDay("None".equals(offDay) ? null : offDay);
        
        // Start and destination
        String startStation = startStationInput.getText().toString().trim();
        String destStation = destStationInput.getText().toString().trim();
        String startTime = getText(startDepTimeInput);
        String arrivalTime = getText(destArrTimeInput);
        
        route.setStart(startStation);
        route.setDestination(destStation);
        route.setStartTime(startTime);
        route.setArrivalTime(arrivalTime);
        
        // Fare from destination
        try {
            route.setFare(Double.parseDouble(getText(destFareInput)));
        } catch (NumberFormatException e) {
            route.setFare(0);
        }
        
        // Build complete stops list: Start + Intermediate + Destination
        List<RouteStop> allStops = new ArrayList<>();
        
        // Start stop
        RouteStop startStop = new RouteStop(startStation, null, startTime, 0);
        allStops.add(startStop);
        
        // Intermediate stops
        allStops.addAll(stopAdapter.getStops());
        
        // Destination stop
        double destFare = 0;
        try {
            destFare = Double.parseDouble(getText(destFareInput));
        } catch (NumberFormatException e) {
            // ignore
        }
        RouteStop destStop = new RouteStop(destStation, arrivalTime, null, destFare);
        allStops.add(destStop);
        
        route.setStops(allStops);
        
        // Calculate duration
        route.setDuration(calculateDuration(startTime, arrivalTime));
        
        // Metadata
        route.setStatus(status);
        route.setCreatedBy(FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "unknown");
        route.setCreatedAt(Timestamp.now());
        route.setUpdatedAt(Timestamp.now());

        // Get message
        String message = getText(messageInput);
        if (message.isEmpty()) {
            message = "New train route: " + trainName;
        }

        if ("master".equals(userRole) || "DRAFT".equals(status)) {
            viewModel.createTrainRouteDirect(route);
        } else {
            viewModel.submitTrainRouteForApproval(route, "CREATE", message);
        }
    }

    private String calculateDuration(String startTime, String endTime) {
        if (startTime == null || endTime == null || 
            startTime.isEmpty() || endTime.isEmpty()) {
            return "";
        }
        
        try {
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");
            
            int startMinutes = Integer.parseInt(startParts[0]) * 60 + Integer.parseInt(startParts[1]);
            int endMinutes = Integer.parseInt(endParts[0]) * 60 + Integer.parseInt(endParts[1]);
            
            // Handle overnight journeys
            if (endMinutes < startMinutes) {
                endMinutes += 24 * 60;
            }
            
            int durationMinutes = endMinutes - startMinutes;
            int hours = durationMinutes / 60;
            int minutes = durationMinutes % 60;
            
            return String.format("%d:%02dh", hours, minutes);
        } catch (Exception e) {
            return "";
        }
    }

    private String getText(TextInputEditText input) {
        return input.getText() != null ? input.getText().toString().trim() : "";
    }

    // IntermediateStopAdapter.OnStopActionListener implementations
    @Override
    public void onStopChanged(int position, RouteStop stop) {
        if (position >= 0 && position < intermediateStops.size()) {
            intermediateStops.set(position, stop);
        }
    }

    @Override
    public void onStopDeleted(int position) {
        if (position >= 0 && position < intermediateStops.size()) {
            intermediateStops.remove(position);
        }
    }
}
