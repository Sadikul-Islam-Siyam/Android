package com.siyam.travelschedulemanager.ui.plan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.Plan;
import com.siyam.travelschedulemanager.util.DateUtils;
import com.siyam.travelschedulemanager.viewmodel.PlanViewModel;

public class PlanDetailFragment extends Fragment {
    private PlanViewModel viewModel;
    private String planId;
    
    private TextView tvPlanName, tvPlanDate, tvTotalFare, tvTotalDuration;
    private LinearLayout legsContainer;
    private MaterialToolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plan_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(PlanViewModel.class);
        
        // Initialize views
        toolbar = view.findViewById(R.id.toolbar);
        tvPlanName = view.findViewById(R.id.tv_plan_name);
        tvPlanDate = view.findViewById(R.id.tv_plan_date);
        tvTotalFare = view.findViewById(R.id.tv_total_fare);
        tvTotalDuration = view.findViewById(R.id.tv_total_duration);
        legsContainer = view.findViewById(R.id.legs_container);
        
        // Get planId from arguments
        if (getArguments() != null) {
            planId = getArguments().getString("planId");
        }
        
        setupToolbar();
        loadPlanDetails();
    }
    
    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> 
            Navigation.findNavController(v).navigateUp()
        );
    }
    
    private void loadPlanDetails() {
        if (planId == null) {
            Toast.makeText(getContext(), "Error: Plan ID not found", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Load plan and observe changes
        viewModel.loadPlan(planId);
        viewModel.getCurrentPlan().observe(getViewLifecycleOwner(), plan -> {
            if (plan != null) {
                tvPlanName.setText(plan.getName());
                tvPlanDate.setText(DateUtils.formatDate(plan.getCreatedDate().toDate()));
                tvTotalFare.setText(String.format("৳%.2f", plan.getTotalFare()));
                tvTotalDuration.setText(DateUtils.formatDuration(plan.getTotalDuration()));
                
                // Display legs
                legsContainer.removeAllViews();
                if (plan.getLegs() != null) {
                    for (int i = 0; i < plan.getLegs().size(); i++) {
                        Plan.PlanLeg leg = plan.getLegs().get(i);
                        addLegView(leg, i + 1);
                    }
                }
            } else {
                Toast.makeText(getContext(), "Failed to load plan details", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void addLegView(Plan.PlanLeg leg, int legNumber) {
        View legView = LayoutInflater.from(getContext()).inflate(R.layout.item_plan_leg_detail, legsContainer, false);
        
        TextView tvLegNumber = legView.findViewById(R.id.tv_leg_number);
        TextView tvRoute = legView.findViewById(R.id.tv_route);
        TextView tvCompany = legView.findViewById(R.id.tv_company);
        TextView tvTiming = legView.findViewById(R.id.tv_timing);
        TextView tvFare = legView.findViewById(R.id.tv_fare);
        TextView tvVehicle = legView.findViewById(R.id.tv_vehicle);
        
        tvLegNumber.setText("Leg " + legNumber);
        tvRoute.setText(leg.getOrigin() + " → " + leg.getDestination());
        tvCompany.setText(leg.getOperatorName());
        tvTiming.setText(leg.getDepartureTime() + " - " + leg.getArrivalTime());
        tvFare.setText(String.format("৳%.2f", leg.getFare()));
        tvVehicle.setText(leg.getTransportType());
        
        legsContainer.addView(legView);
    }
}
