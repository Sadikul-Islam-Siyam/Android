package com.siyam.travelschedulemanager.ui.plan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.data.firebase.AuthRepository;
import com.siyam.travelschedulemanager.data.remote.ApiService;
import com.siyam.travelschedulemanager.data.remote.RetrofitClient;
import com.siyam.travelschedulemanager.data.remote.dto.ScheduleDTO;
import com.siyam.travelschedulemanager.data.remote.dto.ApiResponseWrapper;
import com.siyam.travelschedulemanager.model.Plan;
import com.siyam.travelschedulemanager.model.Schedule;
import com.siyam.travelschedulemanager.ui.plan.adapter.ScheduleSearchAdapter;
import com.siyam.travelschedulemanager.ui.plan.adapter.SelectedLegAdapter;
import com.siyam.travelschedulemanager.util.Constants;
import com.siyam.travelschedulemanager.viewmodel.PlanViewModel;
import com.siyam.travelschedulemanager.viewmodel.ScheduleViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreatePlanFragment extends Fragment {
    private ApiService apiService;
    private PlanViewModel planViewModel;
    private AuthRepository authRepository;
    
    private TextInputEditText editPlanName, editTextDate;
    private AutoCompleteTextView autocompleteOrigin, autocompleteDestination;
    private ChipGroup chipGroupTransport;
    private Chip chipAll, chipBus, chipTrain;
    private MaterialButton buttonSearch;
    private ExtendedFloatingActionButton fabSave;
    
    private TextView textSearchResults, textSelectedLegs;
    private TextView textTotalFare, textTotalDuration;
    private RecyclerView recyclerSearchResults, recyclerSelectedLegs;
    private MaterialCardView cardSummary;
    
    private ScheduleSearchAdapter searchAdapter;
    private SelectedLegAdapter selectedLegAdapter;
    
    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;
    private List<Schedule> selectedLegs = new ArrayList<>();
    private double totalFare = 0;
    private int totalDuration = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = RetrofitClient.getInstance(requireContext()).getApiService();
        planViewModel = new ViewModelProvider(requireActivity()).get(PlanViewModel.class);
        authRepository = new AuthRepository();
        selectedDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupAutoComplete();
        setupDatePicker();
        setupTransportFilter();
        setupAdapters();
        setupListeners();
        
        // Load all schedules from REST API with visible progress
        Toast.makeText(requireContext(), "Loading schedules from desktop...", Toast.LENGTH_SHORT).show();
        loadSchedulesFromAPI();
        
        // Check if editing existing plan
        if (getArguments() != null) {
            String planId = getArguments().getString("planId");
            boolean isEditing = getArguments().getBoolean("isEditing", false);
            
            if (isEditing && planId != null) {
                loadExistingPlan(planId);
            }
        }
    }

    private void initViews(View view) {
        editPlanName = view.findViewById(R.id.edit_plan_name);
        editTextDate = view.findViewById(R.id.edit_text_date);
        autocompleteOrigin = view.findViewById(R.id.autocomplete_origin);
        autocompleteDestination = view.findViewById(R.id.autocomplete_destination);
        chipGroupTransport = view.findViewById(R.id.chip_group_transport);
        chipAll = view.findViewById(R.id.chip_all);
        chipBus = view.findViewById(R.id.chip_bus);
        chipTrain = view.findViewById(R.id.chip_train);
        buttonSearch = view.findViewById(R.id.button_search_routes);
        fabSave = view.findViewById(R.id.fab_save_plan);
        
        textSearchResults = view.findViewById(R.id.text_search_results);
        textSelectedLegs = view.findViewById(R.id.text_selected_legs);
        textTotalFare = view.findViewById(R.id.text_total_fare);
        textTotalDuration = view.findViewById(R.id.text_total_duration);
        
        recyclerSearchResults = view.findViewById(R.id.recycler_search_results);
        recyclerSelectedLegs = view.findViewById(R.id.recycler_selected_legs);
        cardSummary = view.findViewById(R.id.card_summary);
        
        editTextDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void setupAutoComplete() {
        String[] cities = getResources().getStringArray(R.array.bangladesh_districts);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                cities
        );
        autocompleteOrigin.setAdapter(adapter);
        autocompleteDestination.setAdapter(adapter);
        autocompleteOrigin.setThreshold(1);
        autocompleteDestination.setThreshold(1);
    }

    private void setupDatePicker() {
        editTextDate.setOnClickListener(v -> {
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
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void setupTransportFilter() {
        chipGroupTransport.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chip_all)) {
                if (chipBus.isChecked()) chipBus.setChecked(false);
                if (chipTrain.isChecked()) chipTrain.setChecked(false);
            } else {
                if (checkedIds.contains(R.id.chip_bus) || checkedIds.contains(R.id.chip_train)) {
                    if (chipAll.isChecked()) chipAll.setChecked(false);
                }
                if (checkedIds.isEmpty()) {
                    chipAll.setChecked(true);
                }
            }
        });
    }

    private void setupAdapters() {
        searchAdapter = new ScheduleSearchAdapter(this::onScheduleSelected);
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSearchResults.setAdapter(searchAdapter);
        
        selectedLegAdapter = new SelectedLegAdapter(this::onLegRemoved);
        recyclerSelectedLegs.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerSelectedLegs.setAdapter(selectedLegAdapter);
    }

    private void setupListeners() {
        buttonSearch.setOnClickListener(v -> searchRoutes());
        fabSave.setOnClickListener(v -> savePlan());
        
        // Add long press on search button to reload schedules
        buttonSearch.setOnLongClickListener(v -> {
            Toast.makeText(requireContext(), "Reloading schedules from desktop...", Toast.LENGTH_SHORT).show();
            loadSchedulesFromAPI();
            return true;
        });
    }

    private List<Schedule> allSchedules = new ArrayList<>();
    
    private void loadSchedulesFromAPI() {
        android.util.Log.d("CreatePlanFragment", "Loading schedules from API...");
        
        apiService.getAllSchedules().enqueue(new retrofit2.Callback<java.util.List<com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO>> call, retrofit2.Response<java.util.List<com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO>> response) {
                android.util.Log.d("CreatePlanFragment", "API Response code: " + response.code());
                
                if (response.isSuccessful() && response.body() != null) {
                    allSchedules.clear();
                    List<com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO> dtoList = response.body();
                    
                    android.util.Log.d("CreatePlanFragment", "Received " + dtoList.size() + " schedules from API");
                    
                    // Convert UnifiedScheduleDTO to Schedule
                    for (com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO dto : dtoList) {
                        Schedule schedule = new Schedule();
                        schedule.setId(java.util.UUID.randomUUID().toString());
                        schedule.setTransportType(dto.getType().toUpperCase());
                        schedule.setOrigin(dto.getStart());
                        schedule.setDestination(dto.getDestination());
                        schedule.setDepartureTime(dto.getStartTime());
                        schedule.setArrivalTime(dto.getArrivalTime());
                        schedule.setFare(dto.getFare());
                        schedule.setOperatorName(dto.getName());
                        schedule.setTotalSeats(30); // Default value
                        
                        if (dto.isTrain()) {
                            schedule.setTrainNumber(dto.getName());
                        }
                        
                        allSchedules.add(schedule);
                    }
                    
                    android.util.Log.d("CreatePlanFragment", "Successfully loaded " + allSchedules.size() + " schedules");
                    Toast.makeText(requireContext(), "✓ Loaded " + allSchedules.size() + " routes from desktop", Toast.LENGTH_LONG).show();
                } else {
                    String errorMsg = "Failed to load routes. Response code: " + response.code();
                    android.util.Log.e("CreatePlanFragment", errorMsg);
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO>> call, Throwable t) {
                android.util.Log.e("CreatePlanFragment", "Failed to load schedules", t);
                Toast.makeText(requireContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchRoutes() {
        String origin = autocompleteOrigin.getText().toString().trim();
        String destination = autocompleteDestination.getText().toString().trim();

        if (origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter origin and destination", Toast.LENGTH_SHORT).show();
            return;
        }

        if (origin.equalsIgnoreCase(destination)) {
            Toast.makeText(requireContext(), "Origin and destination cannot be the same", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get transport filter
        String transportType = null;
        if (chipBus.isChecked() && !chipTrain.isChecked()) {
            transportType = "BUS";
        } else if (chipTrain.isChecked() && !chipBus.isChecked()) {
            transportType = "TRAIN";
        }

        // Filter schedules
        List<Schedule> filteredSchedules = new ArrayList<>();
        
        android.util.Log.d("CreatePlanFragment", "Searching for routes from '" + origin + "' to '" + destination + "'");
        android.util.Log.d("CreatePlanFragment", "Total schedules in memory: " + allSchedules.size());
        
        for (Schedule schedule : allSchedules) {
            boolean matchesRoute = schedule.getOrigin().equalsIgnoreCase(origin) && 
                                  schedule.getDestination().equalsIgnoreCase(destination);
            boolean matchesType = transportType == null || schedule.getTransportType().equals(transportType);
            
            if (matchesRoute && matchesType) {
                filteredSchedules.add(schedule);
                android.util.Log.d("CreatePlanFragment", "Match found: " + schedule.getOrigin() + " -> " + schedule.getDestination());
            }
        }
        
        android.util.Log.d("CreatePlanFragment", "Found " + filteredSchedules.size() + " matching routes");

        if (allSchedules.isEmpty()) {
            Toast.makeText(requireContext(), "⚠ No schedules loaded. Hold search button to reload from desktop.", Toast.LENGTH_LONG).show();
            textSearchResults.setVisibility(View.GONE);
            recyclerSearchResults.setVisibility(View.GONE);
        } else if (filteredSchedules.isEmpty()) {
            Toast.makeText(requireContext(), "No routes found from " + origin + " to " + destination, Toast.LENGTH_LONG).show();
            textSearchResults.setVisibility(View.GONE);
            recyclerSearchResults.setVisibility(View.GONE);
        } else {
            searchAdapter.setSchedules(filteredSchedules);
            textSearchResults.setVisibility(View.VISIBLE);
            textSearchResults.setText("✈️ Available Routes (" + filteredSchedules.size() + ")");
            recyclerSearchResults.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "Found " + filteredSchedules.size() + " routes", Toast.LENGTH_SHORT).show();
        }
    }

    private void onScheduleSelected(Schedule schedule) {
        // Validate 30-min connection time if there are existing legs
        if (!selectedLegs.isEmpty()) {
            Schedule lastLeg = selectedLegs.get(selectedLegs.size() - 1);
            String lastArrival = lastLeg.getArrivalTime();
            String newDeparture = schedule.getDepartureTime();
            
            // Check if destinations match (last leg destination should be same as new leg origin)
            if (!lastLeg.getDestination().equals(schedule.getOrigin())) {
                Toast.makeText(requireContext(), 
                    "Invalid connection: Destination of previous leg (" + lastLeg.getDestination() + 
                    ") doesn't match origin of new leg (" + schedule.getOrigin() + ")", 
                    Toast.LENGTH_LONG).show();
                return;
            }
            
            // Validate 30-min transfer time
            if (!com.siyam.travelschedulemanager.util.DateUtils.isValidTransferTime(lastArrival, newDeparture, 30)) {
                int gap = com.siyam.travelschedulemanager.util.DateUtils.calculateDuration(lastArrival, newDeparture);
                Toast.makeText(requireContext(), 
                    "Invalid connection: Only " + gap + " minutes between arrival (" + lastArrival + 
                    ") and departure (" + newDeparture + "). Minimum 30 minutes required.", 
                    Toast.LENGTH_LONG).show();
                return;
            }
        }
        
        selectedLegs.add(schedule);
        updateSelectedLegs();
        Toast.makeText(requireContext(), "Added to journey ✓", Toast.LENGTH_SHORT).show();
    }

    private void onLegRemoved(int position) {
        if (position >= 0 && position < selectedLegs.size()) {
            selectedLegs.remove(position);
            updateSelectedLegs();
            Toast.makeText(requireContext(), "Removed from journey", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSelectedLegs() {
        if (selectedLegs.isEmpty()) {
            textSelectedLegs.setVisibility(View.GONE);
            recyclerSelectedLegs.setVisibility(View.GONE);
            cardSummary.setVisibility(View.GONE);
            fabSave.setVisibility(View.GONE);
        } else {
            textSelectedLegs.setVisibility(View.VISIBLE);
            textSelectedLegs.setText("🎒 Your Journey (" + selectedLegs.size() + " legs)");
            recyclerSelectedLegs.setVisibility(View.VISIBLE);
            cardSummary.setVisibility(View.VISIBLE);
            fabSave.setVisibility(View.VISIBLE);
            
            selectedLegAdapter.setSelectedLegs(selectedLegs);
            calculateTotals();
        }
    }

    private void calculateTotals() {
        totalFare = 0;
        totalDuration = 0;
        for (Schedule schedule : selectedLegs) {
            totalFare += schedule.getFare();
            // Calculate duration from departure and arrival times
            int duration = com.siyam.travelschedulemanager.util.DateUtils.calculateDuration(
                schedule.getDepartureTime(), 
                schedule.getArrivalTime()
            );
            totalDuration += duration;
        }
        
        textTotalFare.setText(String.format("Total Fare: ৳%.2f", totalFare));
        textTotalDuration.setText("Total Duration: " + 
            com.siyam.travelschedulemanager.util.DateUtils.formatDuration(totalDuration));
    }

    private void savePlan() {
        String planName = editPlanName.getText().toString().trim();
        
        if (planName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter plan name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLegs.isEmpty()) {
            Toast.makeText(requireContext(), "Please add at least one route to your journey", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if editing existing plan
        String existingPlanId = null;
        if (getArguments() != null) {
            existingPlanId = getArguments().getString("planId");
        }
        
        // Create or update Plan with legs
        Plan plan;
        if (existingPlanId != null) {
            // Update existing plan
            plan = new Plan();
            plan.setId(existingPlanId);
            plan.setUserId(userId);
            plan.setName(planName);
            plan.setNotes("Journey with " + selectedLegs.size() + " legs");
        } else {
            // Create new plan
            plan = new Plan(userId, planName, "Journey with " + selectedLegs.size() + " legs");
        }
        
        plan.setTotalFare(totalFare);
        plan.setTotalDuration(totalDuration);
        
        // Convert schedules to plan legs
        List<Plan.PlanLeg> legs = new ArrayList<>();
        int legNumber = 1;
        for (Schedule schedule : selectedLegs) {
            Plan.PlanLeg leg = new Plan.PlanLeg(
                    schedule.getId(),
                    schedule.getTransportType(),
                    schedule.getOrigin(),
                    schedule.getDestination(),
                    schedule.getDepartureTime(),
                    schedule.getArrivalTime(),
                    schedule.getFare(),
                    schedule.getOperatorName(),
                    legNumber++
            );
            legs.add(leg);
        }
        plan.setLegs(legs);

        // Save plan
        planViewModel.createPlan(plan);
        
        planViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                
                // Navigate back to saved plans
                NavController navController = Navigation.findNavController(requireView());
                navController.navigateUp();
            }
        });
        
        planViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadExistingPlan(String planId) {
        planViewModel.loadPlan(planId);
        planViewModel.getCurrentPlan().observe(getViewLifecycleOwner(), plan -> {
            if (plan != null) {
                // Populate plan name
                editPlanName.setText(plan.getName());
                
                // Populate date
                if (plan.getCreatedDate() != null) {
                    selectedDate.setTime(plan.getCreatedDate().toDate());
                    editTextDate.setText(dateFormat.format(selectedDate.getTime()));
                }
                
                // Convert plan legs to schedules and add to selected legs
                if (plan.getLegs() != null && !plan.getLegs().isEmpty()) {
                    selectedLegs.clear();
                    for (Plan.PlanLeg leg : plan.getLegs()) {
                        Schedule schedule = new Schedule();
                        schedule.setId(leg.getScheduleId());
                        schedule.setTransportType(leg.getTransportType());
                        schedule.setOrigin(leg.getOrigin());
                        schedule.setDestination(leg.getDestination());
                        schedule.setDepartureTime(leg.getDepartureTime());
                        schedule.setArrivalTime(leg.getArrivalTime());
                        schedule.setFare(leg.getFare());
                        schedule.setOperatorName(leg.getOperatorName());
                        selectedLegs.add(schedule);
                    }
                    
                    // Update UI
                    selectedLegAdapter.setSelectedLegs(selectedLegs);
                    calculateTotals();
                    textSelectedLegs.setVisibility(View.VISIBLE);
                    recyclerSelectedLegs.setVisibility(View.VISIBLE);
                    cardSummary.setVisibility(View.VISIBLE);
                }
                
                Toast.makeText(requireContext(), "Plan loaded for editing", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
