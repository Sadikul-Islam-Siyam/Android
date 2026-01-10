package com.siyam.travelschedulemanager.ui.schedule.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.Schedule;
import com.siyam.travelschedulemanager.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private List<Schedule> schedules = new ArrayList<>();
    private OnScheduleClickListener listener;

    public interface OnScheduleClickListener {
        void onScheduleClick(Schedule schedule);
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
        notifyDataSetChanged();
    }

    public void setOnScheduleClickListener(OnScheduleClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        private final TextView origin, destination, time, duration, fare, operator;
        private final Chip transportType;
        private final MaterialCardView cardView;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            origin = itemView.findViewById(R.id.origin);
            destination = itemView.findViewById(R.id.destination);
            time = itemView.findViewById(R.id.time);
            duration = itemView.findViewById(R.id.duration);
            fare = itemView.findViewById(R.id.fare);
            operator = itemView.findViewById(R.id.operator);
            transportType = itemView.findViewById(R.id.transport_type);
        }

        public void bind(Schedule schedule) {
            origin.setText(schedule.getOrigin());
            destination.setText(schedule.getDestination());
            time.setText(String.format("%s - %s", schedule.getDepartureTime(), schedule.getArrivalTime()));
            duration.setText(DateUtils.formatDuration(schedule.getDuration()));
            fare.setText(String.format("৳%.2f", schedule.getFare()));
            operator.setText(schedule.getOperatorName());
            transportType.setText(schedule.getTransportType());

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onScheduleClick(schedule);
                }
            });
        }
    }
}
