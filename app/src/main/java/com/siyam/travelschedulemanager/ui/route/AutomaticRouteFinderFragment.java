package com.siyam.travelschedulemanager.ui.route;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.algorithm.RouteGraph;
import com.siyam.travelschedulemanager.data.firebase.AuthRepository;
import com.siyam.travelschedulemanager.data.remote.ApiService;
import com.siyam.travelschedulemanager.data.remote.RetrofitClient;
import com.siyam.travelschedulemanager.data.remote.dto.UnifiedScheduleDTO;
import com.siyam.travelschedulemanager.model.Plan;
import com.siyam.travelschedulemanager.util.DateUtils;
import com.siyam.travelschedulemanager.viewmodel.PlanViewModel;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutomaticRouteFinderFragment extends Fragment {
    private ApiService apiService;
    private PlanViewModel planViewModel;
    private AuthRepository authRepository;
    
    private AutoCompleteTextView acFrom, acTo;
    private MaterialButton btnFindRoutes;
    private ProgressBar progressBar;
    private LinearLayout routesContainer;
    private TextView emptyStateText;
    
    private List<UnifiedScheduleDTO> allSchedules = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_automatic_route_finder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        apiService = RetrofitClient.getInstance(requireContext()).getApiService();
        planViewModel = new ViewModelProvider(this).get(PlanViewModel.class);
        authRepository = new AuthRepository();
        
        initViews(view);
        setupAutoComplete();
        loadSchedules();
        
        btnFindRoutes.setOnClickListener(v -> findRoutes());
    }
    
    private void initViews(View view) {
        acFrom = view.findViewById(R.id.ac_from);
        acTo = view.findViewById(R.id.ac_to);
        btnFindRoutes = view.findViewById(R.id.btn_find_routes);
        progressBar = view.findViewById(R.id.progress_bar);
        routesContainer = view.findViewById(R.id.routes_container);
        emptyStateText = view.findViewById(R.id.empty_state_text);
    }
    
    private void setupAutoComplete() {
        String[] districts = getResources().getStringArray(R.array.bangladesh_districts);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, districts);
        acFrom.setAdapter(adapter);
        acTo.setAdapter(adapter);
    }
    
    private void loadSchedules() {
        progressBar.setVisibility(View.VISIBLE);
        emptyStateText.setText("Loading schedules from desktop app...");
        emptyStateText.setVisibility(View.VISIBLE);
        
        android.util.Log.d("RouteFinderAPI", "Calling API: " + apiService.getAllSchedules().request().url());
        
        apiService.getAllSchedules().enqueue(new Callback<List<UnifiedScheduleDTO>>() {
            @Override
            public void onResponse(Call<List<UnifiedScheduleDTO>> call, Response<List<UnifiedScheduleDTO>> response) {
                progressBar.setVisibility(View.GONE);
                android.util.Log.d("RouteFinderAPI", "Response code: " + response.code());
                android.util.Log.d("RouteFinderAPI", "Response body: " + (response.body() != null ? response.body().size() + " items" : "null"));
                
                if (response.isSuccessful() && response.body() != null) {
                    allSchedules = response.body();
                    emptyStateText.setVisibility(View.GONE);
                    
                    // Log first schedule for debugging
                    if (!allSchedules.isEmpty()) {
                        UnifiedScheduleDTO first = allSchedules.get(0);
                        android.util.Log.d("RouteFinderAPI", "Sample: " + first.getStart() + " -> " + first.getDestination());
                    }
                    
                    Toast.makeText(requireContext(), "✓ Loaded " + allSchedules.size() + " schedules from desktop", Toast.LENGTH_LONG).show();
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            android.util.Log.e("RouteFinderAPI", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("RouteFinderAPI", "Error reading error body", e);
                    }
                    emptyStateText.setText("Failed to load. Code: " + response.code() + "\n" + errorBody);
                    Toast.makeText(requireContext(), "Failed. Code: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<UnifiedScheduleDTO>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                android.util.Log.e("RouteFinderAPI", "Connection failed", t);
                emptyStateText.setText("Connection Error: " + t.getMessage() + "\n\nMake sure:\n• Desktop app is running\n• Both devices on same WiFi\n• URL: http://192.168.0.144:8080");
                Toast.makeText(requireContext(), "Connection failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private void findRoutes() {
        String from = acFrom.getText().toString().trim();
        String to = acTo.getText().toString().trim();
        
        if (from.isEmpty() || to.isEmpty()) {
            Toast.makeText(requireContext(), "Please select both origin and destination", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (from.equalsIgnoreCase(to)) {
            Toast.makeText(requireContext(), "Origin and destination cannot be the same", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (allSchedules.isEmpty()) {
            Toast.makeText(requireContext(), "No schedules loaded. Please wait for data to load from desktop app.", Toast.LENGTH_LONG).show();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        routesContainer.removeAllViews();
        
        // Normalize input to lowercase to match API data
        String fromNormalized = from.toLowerCase();
        String toNormalized = to.toLowerCase();
        
        android.util.Log.d("RouteFinderAPI", "Finding routes from '" + fromNormalized + "' to '" + toNormalized + "'");
        android.util.Log.d("RouteFinderAPI", "Total schedules available: " + allSchedules.size());
        
        // Use Dijkstra-based route finding
        RouteGraph graph = new RouteGraph(allSchedules);
        List<List<UnifiedScheduleDTO>> routes = graph.findRoutes(fromNormalized, toNormalized, 3); // Max 3 legs
        
        android.util.Log.d("RouteFinderAPI", "Found " + routes.size() + " route(s)");
        
        progressBar.setVisibility(View.GONE);
        
        if (routes.isEmpty()) {
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("No routes found between " + from + " and " + to + ".\n\nTry:\n• Check spelling\n• Use exact district names\n• Example: Dhaka, Chattogram, Jamalpur");
            routesContainer.setVisibility(View.GONE);
        } else {
            emptyStateText.setVisibility(View.GONE);
            routesContainer.setVisibility(View.VISIBLE);
            displayRoutes(routes);
        }
    }
    
    private void displayRoutes(List<List<UnifiedScheduleDTO>> routes) {
        for (int i = 0; i < routes.size(); i++) {
            List<UnifiedScheduleDTO> route = routes.get(i);
            addRouteCard(route, i + 1);
        }
    }
    
    private void addRouteCard(List<UnifiedScheduleDTO> route, int routeNumber) {
        View cardView = LayoutInflater.from(requireContext()).inflate(R.layout.item_route_option, routesContainer, false);
        
        TextView tvRouteNumber = cardView.findViewById(R.id.tv_route_number);
        TextView tvLegsCount = cardView.findViewById(R.id.tv_legs_count);
        TextView tvTotalFare = cardView.findViewById(R.id.tv_total_fare);
        TextView tvTotalDuration = cardView.findViewById(R.id.tv_total_duration);
        LinearLayout legsContainer = cardView.findViewById(R.id.legs_summary_container);
        MaterialButton btnSavePlan = cardView.findViewById(R.id.btn_save_plan);
        
        // Calculate totals
        double totalFare = route.stream().mapToDouble(UnifiedScheduleDTO::getFare).sum();
        int totalDuration = route.stream()
                .mapToInt(s -> DateUtils.calculateDuration(s.getStartTime(), s.getArrivalTime()))
                .sum();
        
        tvRouteNumber.setText("Route Option " + routeNumber);
        tvLegsCount.setText(route.size() + (route.size() == 1 ? " leg" : " legs"));
        tvTotalFare.setText(String.format("৳%.2f", totalFare));
        tvTotalDuration.setText(DateUtils.formatDuration(totalDuration));
        
        // Display leg summaries
        for (UnifiedScheduleDTO schedule : route) {
            TextView legSummary = new TextView(requireContext());
            legSummary.setText(String.format("• %s → %s (%s - %s)",
                    schedule.getStart(), schedule.getDestination(),
                    schedule.getStartTime(), schedule.getArrivalTime()));
            legSummary.setTextSize(12);
            legSummary.setPadding(8, 4, 8, 4);
            legsContainer.addView(legSummary);
        }
        
        btnSavePlan.setOnClickListener(v -> showSavePlanDialog(route));
        
        routesContainer.addView(cardView);
    }
    
    private void showSavePlanDialog(List<UnifiedScheduleDTO> route) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_save_plan, null);
        TextInputEditText etPlanName = dialogView.findViewById(R.id.et_plan_name);
        
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Save Travel Plan")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String planName = etPlanName.getText().toString().trim();
                    if (planName.isEmpty()) {
                        Toast.makeText(requireContext(), "Please enter a plan name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    savePlan(planName, route);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    
    private void savePlan(String planName, List<UnifiedScheduleDTO> route) {
        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(requireContext(), "Please login to save plans", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Convert route to PlanLegs
        List<Plan.PlanLeg> legs = new ArrayList<>();
        double totalFare = 0;
        int totalDuration = 0;
        
        for (int i = 0; i < route.size(); i++) {
            UnifiedScheduleDTO schedule = route.get(i);
            Plan.PlanLeg leg = new Plan.PlanLeg(
                    "", // scheduleId - not available in UnifiedScheduleDTO
                    schedule.getType(),
                    schedule.getStart(),
                    schedule.getDestination(),
                    schedule.getStartTime(),
                    schedule.getArrivalTime(),
                    schedule.getFare(),
                    schedule.getName(),
                    i + 1
            );
            legs.add(leg);
            totalFare += schedule.getFare();
            totalDuration += DateUtils.calculateDuration(schedule.getStartTime(), schedule.getArrivalTime());
        }
        
        // Create Plan object
        Plan plan = new Plan();
        plan.setUserId(userId);
        plan.setName(planName);
        plan.setLegs(legs);
        plan.setTotalFare(totalFare);
        plan.setTotalDuration(totalDuration);
        plan.setCreatedDate(Timestamp.now());
        
        // Save plan
        planViewModel.createPlan(plan);
        Toast.makeText(requireContext(), "Plan saved successfully!", Toast.LENGTH_SHORT).show();
    }
}
