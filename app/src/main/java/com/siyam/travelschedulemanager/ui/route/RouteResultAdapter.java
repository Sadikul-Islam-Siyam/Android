package com.siyam.travelschedulemanager.ui.route;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.data.remote.dto.ScheduleDTO;

import java.util.List;

public class RouteResultAdapter extends RecyclerView.Adapter<RouteResultAdapter.RouteViewHolder> {
    private List<ScheduleDTO> schedules;

    public RouteResultAdapter(List<ScheduleDTO> schedules) {
        this.schedules = schedules;
    }

    public void updateSchedules(List<ScheduleDTO> newSchedules) {
        this.schedules = newSchedules;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_result, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        ScheduleDTO schedule = schedules.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        private final TextView typeText;
        private final TextView routeText;
        private final TextView timeText;
        private final TextView fareText;
        private final TextView detailsText;
        private final TextView seatsText;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            typeText = itemView.findViewById(R.id.text_type);
            routeText = itemView.findViewById(R.id.text_route);
            timeText = itemView.findViewById(R.id.text_time);
            fareText = itemView.findViewById(R.id.text_fare);
            detailsText = itemView.findViewById(R.id.text_details);
            seatsText = itemView.findViewById(R.id.text_seats);
        }

        public void bind(ScheduleDTO schedule) {
            typeText.setText(schedule.getType());
            routeText.setText(schedule.getOrigin() + " → " + schedule.getDestination());
            timeText.setText(schedule.getDepartureTime() + " - " + schedule.getArrivalTime());
            fareText.setText("৳" + schedule.getFare());
            seatsText.setText(schedule.getAvailableSeats() + " seats");

            // Set details based on type
            if (schedule.isBus()) {
                detailsText.setText(schedule.getCompanyName() + " | " + schedule.getBusType());
            } else if (schedule.isTrain()) {
                detailsText.setText(schedule.getTrainName() + " | " + schedule.getTrainNumber() + " | " + schedule.getSeatClass());
            }
        }
    }
}
