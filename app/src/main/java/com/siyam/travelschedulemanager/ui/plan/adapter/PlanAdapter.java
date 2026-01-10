package com.siyam.travelschedulemanager.ui.plan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.Plan;
import com.siyam.travelschedulemanager.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {
    private List<Plan> plans = new ArrayList<>();
    private OnPlanClickListener listener;

    public interface OnPlanClickListener {
        void onPlanClick(Plan plan);
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
        notifyDataSetChanged();
    }

    public void setOnPlanClickListener(OnPlanClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = plans.get(position);
        holder.bind(plan);
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder {
        private final TextView planName, planDate, totalFare, totalDuration, legs;
        private final MaterialCardView cardView;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            planName = itemView.findViewById(R.id.plan_name);
            planDate = itemView.findViewById(R.id.plan_date);
            totalFare = itemView.findViewById(R.id.total_fare);
            totalDuration = itemView.findViewById(R.id.total_duration);
            legs = itemView.findViewById(R.id.legs_count);
        }

        public void bind(Plan plan) {
            planName.setText(plan.getName());
            planDate.setText(DateUtils.formatDate(plan.getCreatedDate().toDate()));
            totalFare.setText(String.format("৳%.2f", plan.getTotalFare()));
            totalDuration.setText(DateUtils.formatDuration(plan.getTotalDuration()));
            legs.setText(String.format("%d legs", plan.getLegs() != null ? plan.getLegs().size() : 0));

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlanClick(plan);
                }
            });
        }
    }
}
