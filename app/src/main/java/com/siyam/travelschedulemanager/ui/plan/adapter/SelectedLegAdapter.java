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

public class SelectedLegAdapter extends RecyclerView.Adapter<SelectedLegAdapter.ViewHolder> {
    private List<Schedule> selectedLegs = new ArrayList<>();
    private OnLegRemoveListener listener;

    public interface OnLegRemoveListener {
        void onLegRemoved(int position);
    }

    public SelectedLegAdapter(OnLegRemoveListener listener) {
        this.listener = listener;
    }

    public void setSelectedLegs(List<Schedule> legs) {
        this.selectedLegs = legs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_leg, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = selectedLegs.get(position);
        holder.bind(schedule, position + 1);
    }

    @Override
    public int getItemCount() {
        return selectedLegs.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textLegNumber, textTransportType, textServiceName;
        private TextView textFare, textRoute, textTime;
        private MaterialButton buttonRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textLegNumber = itemView.findViewById(R.id.text_leg_number);
            textTransportType = itemView.findViewById(R.id.text_transport_type);
            textServiceName = itemView.findViewById(R.id.text_service_name);
            textFare = itemView.findViewById(R.id.text_fare);
            textRoute = itemView.findViewById(R.id.text_route);
            textTime = itemView.findViewById(R.id.text_time);
            buttonRemove = itemView.findViewById(R.id.button_remove);
        }

        public void bind(Schedule schedule, int legNumber) {
            textLegNumber.setText(String.valueOf(legNumber));
            String transportIcon = "BUS".equals(schedule.getTransportType()) ? "🚌" : "🚆";
            textTransportType.setText(transportIcon);
            textServiceName.setText(schedule.getOperatorName());
            textFare.setText(String.format("৳%.0f", schedule.getFare()));
            textRoute.setText(schedule.getOrigin() + " → " + schedule.getDestination());
            
            int hours = schedule.getDuration() / 60;
            int mins = schedule.getDuration() % 60;
            String durationText = hours > 0 ? "(" + hours + "h " + mins + "m)" : "(" + mins + "m)";
            textTime.setText("⏰ " + schedule.getDepartureTime() + " " + durationText);

            buttonRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLegRemoved(getAdapterPosition());
                }
            });
        }
    }
}
