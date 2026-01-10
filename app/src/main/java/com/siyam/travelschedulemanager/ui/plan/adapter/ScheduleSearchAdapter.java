package com.siyam.travelschedulemanager.ui.plan.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.Schedule;
import java.util.ArrayList;
import java.util.List;

public class ScheduleSearchAdapter extends RecyclerView.Adapter<ScheduleSearchAdapter.ViewHolder> {
    private List<Schedule> schedules = new ArrayList<>();
    private OnScheduleSelectListener listener;

    public interface OnScheduleSelectListener {
        void onScheduleSelected(Schedule schedule);
    }

    public ScheduleSearchAdapter(OnScheduleSelectListener listener) {
        this.listener = listener;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = schedules.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textTransportType, textServiceName, textFare;
        private TextView textRoute, textDeparture, textDuration;
        private MaterialButton buttonAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTransportType = itemView.findViewById(R.id.text_transport_type);
            textServiceName = itemView.findViewById(R.id.text_service_name);
            textFare = itemView.findViewById(R.id.text_fare);
            textRoute = itemView.findViewById(R.id.text_route);
            textDeparture = itemView.findViewById(R.id.text_departure);
            textDuration = itemView.findViewById(R.id.text_duration);
            buttonAdd = itemView.findViewById(R.id.button_add_to_journey);
        }

        public void bind(Schedule schedule) {
            String transportIcon = "BUS".equals(schedule.getTransportType()) ? "🚌 BUS" : "🚆 TRAIN";
            textTransportType.setText(transportIcon);
            textServiceName.setText(schedule.getOperatorName());
            textFare.setText(String.format("৳%.0f", schedule.getFare()));
            textRoute.setText(schedule.getOrigin() + " → " + schedule.getDestination());
            textDeparture.setText("⏰ " + schedule.getDepartureTime());
            
            int hours = schedule.getDuration() / 60;
            int mins = schedule.getDuration() % 60;
            String durationText = hours > 0 ? hours + "h " + mins + "m" : mins + "m";
            textDuration.setText("⏱ " + durationText);

            buttonAdd.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onScheduleSelected(schedule);
                }
            });
        }
    }
}
