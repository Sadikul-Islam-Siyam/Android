package com.siyam.travelschedulemanager.ui.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.RouteStop;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for displaying and editing route stops in a RecyclerView.
 * Supports drag-and-drop reordering and inline editing.
 */
public class RouteStopAdapter extends RecyclerView.Adapter<RouteStopAdapter.StopViewHolder> {

    private final List<RouteStop> stops;
    private final Context context;
    private final OnStopChangeListener listener;
    private final List<String> stationSuggestions;

    public interface OnStopChangeListener {
        void onStopChanged(int position, RouteStop stop);
        void onStopDeleted(int position);
        void onStopsReordered(List<RouteStop> stops);
    }

    public RouteStopAdapter(Context context, List<RouteStop> stops, OnStopChangeListener listener) {
        this.context = context;
        this.stops = stops != null ? stops : new ArrayList<>();
        this.listener = listener;
        this.stationSuggestions = new ArrayList<>();
    }

    public void setStationSuggestions(List<String> suggestions) {
        this.stationSuggestions.clear();
        if (suggestions != null) {
            this.stationSuggestions.addAll(suggestions);
        }
    }

    public void addStop(RouteStop stop) {
        stops.add(stop);
        notifyItemInserted(stops.size() - 1);
        // Update connecting lines
        if (stops.size() > 1) {
            notifyItemChanged(stops.size() - 2);
        }
    }

    public void removeStop(int position) {
        if (position >= 0 && position < stops.size()) {
            stops.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, stops.size() - position);
            if (listener != null) {
                listener.onStopDeleted(position);
            }
        }
    }

    public List<RouteStop> getStops() {
        return new ArrayList<>(stops);
    }

    public void moveStop(int fromPosition, int toPosition) {
        if (fromPosition < 0 || fromPosition >= stops.size() ||
            toPosition < 0 || toPosition >= stops.size()) {
            return;
        }
        
        RouteStop stop = stops.remove(fromPosition);
        stops.add(toPosition, stop);
        notifyItemMoved(fromPosition, toPosition);
        // Update stop numbers
        notifyItemRangeChanged(Math.min(fromPosition, toPosition), 
                              Math.abs(fromPosition - toPosition) + 1);
        
        if (listener != null) {
            listener.onStopsReordered(stops);
        }
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_stop, parent, false);
        return new StopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        RouteStop stop = stops.get(position);
        holder.bind(stop, position);
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    class StopViewHolder extends RecyclerView.ViewHolder {
        private final TextView stopNumber;
        private final View connectingLine;
        private final ImageView dragHandle;
        private final AutoCompleteTextView stationAutoComplete;
        private final TextView arrivalTimeLabel;
        private final TextInputEditText arrivalTimeInput;
        private final TextInputEditText departureTimeInput;
        private final TextInputEditText fareInput;
        private final ImageButton deleteButton;

        private boolean isBinding = false;

        StopViewHolder(@NonNull View itemView) {
            super(itemView);
            stopNumber = itemView.findViewById(R.id.stopNumber);
            connectingLine = itemView.findViewById(R.id.connectingLine);
            dragHandle = itemView.findViewById(R.id.dragHandle);
            stationAutoComplete = itemView.findViewById(R.id.stationAutoComplete);
            arrivalTimeLabel = itemView.findViewById(R.id.arrivalTimeLabel);
            arrivalTimeInput = itemView.findViewById(R.id.arrivalTimeInput);
            departureTimeInput = itemView.findViewById(R.id.departureTimeInput);
            fareInput = itemView.findViewById(R.id.fareInput);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        void bind(RouteStop stop, int position) {
            isBinding = true;

            // Set stop number
            stopNumber.setText(String.valueOf(position + 1));

            // Show/hide connecting line (hide for last item)
            connectingLine.setVisibility(position < stops.size() - 1 ? View.VISIBLE : View.INVISIBLE);

            // Set station with autocomplete
            if (stationSuggestions != null && !stationSuggestions.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                        android.R.layout.simple_dropdown_item_1line, stationSuggestions);
                stationAutoComplete.setAdapter(adapter);
            }
            stationAutoComplete.setText(stop.getStation() != null ? stop.getStation() : "");

            // Set times
            String arrivalTime = stop.getArrivalTime() != null ? stop.getArrivalTime() : "";
            String departureTime = stop.getDepartureTime() != null ? stop.getDepartureTime() : "";
            
            arrivalTimeInput.setText(arrivalTime);
            departureTimeInput.setText(departureTime);
            
            // Show arrival time label for non-first stops
            if (position == 0) {
                arrivalTimeLabel.setText("--.-");
                arrivalTimeInput.setHint("--:--");
            } else {
                arrivalTimeLabel.setText(arrivalTime.isEmpty() ? "--:--" : arrivalTime);
            }

            // Set fare
            double fare = stop.getCumulativeFare();
            fareInput.setText(fare > 0 ? String.valueOf((int) fare) : position == 0 ? "0" : "");

            isBinding = false;

            // Setup listeners
            setupListeners(stop, position);
        }

        private void setupListeners(RouteStop stop, int position) {
            // Station text change
            stationAutoComplete.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!isBinding) {
                        stop.setStation(s.toString());
                        notifyStopChanged(position, stop);
                    }
                }
            });

            // Arrival time picker
            arrivalTimeInput.setOnClickListener(v -> {
                showTimePicker(arrivalTimeInput, time -> {
                    stop.setArrivalTime(time);
                    arrivalTimeLabel.setText(time);
                    notifyStopChanged(position, stop);
                });
            });

            // Departure time picker
            departureTimeInput.setOnClickListener(v -> {
                showTimePicker(departureTimeInput, time -> {
                    stop.setDepartureTime(time);
                    notifyStopChanged(position, stop);
                });
            });

            // Fare change
            fareInput.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!isBinding) {
                        try {
                            double fare = s.toString().isEmpty() ? 0 : Double.parseDouble(s.toString());
                            stop.setCumulativeFare(fare);
                            notifyStopChanged(position, stop);
                        } catch (NumberFormatException e) {
                            // Ignore invalid input
                        }
                    }
                }
            });

            // Delete button
            deleteButton.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    removeStop(pos);
                }
            });
        }

        private void showTimePicker(TextInputEditText input, TimePickerCallback callback) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            // Try to parse existing time
            String existingTime = input.getText() != null ? input.getText().toString() : "";
            if (!existingTime.isEmpty() && existingTime.contains(":")) {
                try {
                    String[] parts = existingTime.split(":");
                    hour = Integer.parseInt(parts[0]);
                    minute = Integer.parseInt(parts[1]);
                } catch (Exception e) {
                    // Use current time as default
                }
            }

            TimePickerDialog dialog = new TimePickerDialog(context,
                    (view, hourOfDay, minuteOfHour) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                        input.setText(time);
                        callback.onTimeSelected(time);
                    }, hour, minute, true);
            dialog.show();
        }

        private void notifyStopChanged(int position, RouteStop stop) {
            if (listener != null) {
                listener.onStopChanged(position, stop);
            }
        }
    }

    private interface TimePickerCallback {
        void onTimeSelected(String time);
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
