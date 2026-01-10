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
    private ScheduleViewModel scheduleViewModel;
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
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
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
        observeSchedules();
        
        scheduleViewModel.loadAllSchedules();
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                Constants.BANGLADESH_CITIES
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
    }

    private void observeSchedules() {
        scheduleViewModel.getSchedules().observe(getViewLifecycleOwner(), schedules -> {
            // Schedules are loaded, waiting for search
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
        List<Schedule> allSchedules = scheduleViewModel.getSchedules().getValue();
        List<Schedule> filteredSchedules = new ArrayList<>();
        
        if (allSchedules != null) {
            for (Schedule schedule : allSchedules) {
                boolean matchesRoute = schedule.getOrigin().equalsIgnoreCase(origin) && 
                                      schedule.getDestination().equalsIgnoreCase(destination);
                boolean matchesType = transportType == null || schedule.getTransportType().equals(transportType);
                
                if (matchesRoute && matchesType) {
                    filteredSchedules.add(schedule);
                }
            }
        }

        if (filteredSchedules.isEmpty()) {
            Toast.makeText(requireContext(), "No routes found for this search", Toast.LENGTH_SHORT).show();
            textSearchResults.setVisibility(View.GONE);
            recyclerSearchResults.setVisibility(View.GONE);
        } else {
            searchAdapter.setSchedules(filteredSchedules);
            textSearchResults.setVisibility(View.VISIBLE);
            recyclerSearchResults.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), "Found " + filteredSchedules.size() + " routes", Toast.LENGTH_SHORT).show();
        }
    }

    private void onScheduleSelected(Schedule schedule) {
        selectedLegs.add(schedule);
        updateSelectedLegs();
        Toast.makeText(requireContext(), "Added to journey", Toast.LENGTH_SHORT).show();
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
            totalDuration += schedule.getDuration();
        }
        
        textTotalFare.setText(String.format("Total Fare: ৳%.2f", totalFare));
        
        int hours = totalDuration / 60;
        int mins = totalDuration % 60;
        String durationText = hours > 0 ? hours + "h " + mins + "m" : mins + "m";
        textTotalDuration.setText("Total Duration: " + durationText);
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

        // Create Plan with legs
        Plan plan = new Plan(userId, planName, "Journey with " + selectedLegs.size() + " legs");
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
}
