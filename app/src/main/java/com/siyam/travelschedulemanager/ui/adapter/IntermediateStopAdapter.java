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
import android.widget.PopupMenu;

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
 * Adapter for intermediate train stops (between start and destination).
 */
public class IntermediateStopAdapter extends RecyclerView.Adapter<IntermediateStopAdapter.StopViewHolder> {

    private final List<RouteStop> stops;
    private final Context context;
    private final OnStopActionListener listener;
    private List<String> stationSuggestions = new ArrayList<>();

    public interface OnStopActionListener {
        void onStopChanged(int position, RouteStop stop);
        void onStopDeleted(int position);
    }

    public IntermediateStopAdapter(Context context, List<RouteStop> stops, OnStopActionListener listener) {
        this.context = context;
        this.stops = stops != null ? stops : new ArrayList<>();
        this.listener = listener;
    }

    public void setStationSuggestions(List<String> suggestions) {
        this.stationSuggestions = suggestions != null ? suggestions : new ArrayList<>();
    }

    public void addStop(RouteStop stop) {
        stops.add(stop);
        notifyItemInserted(stops.size() - 1);
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

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_intermediate_stop, parent, false);
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
        private final AutoCompleteTextView stationAutoComplete;
        private final TextInputEditText arrivalTimeInput;
        private final TextInputEditText departureTimeInput;
        private final TextInputEditText fareInput;
        private final ImageButton menuButton;
        private final View connectingLine;

        private boolean isBinding = false;
        private TextWatcher stationWatcher;
        private TextWatcher fareWatcher;

        StopViewHolder(@NonNull View itemView) {
            super(itemView);
            stationAutoComplete = itemView.findViewById(R.id.stationAutoComplete);
            arrivalTimeInput = itemView.findViewById(R.id.arrivalTimeInput);
            departureTimeInput = itemView.findViewById(R.id.departureTimeInput);
            fareInput = itemView.findViewById(R.id.fareInput);
            menuButton = itemView.findViewById(R.id.menuButton);
            connectingLine = itemView.findViewById(R.id.connectingLine);
        }

        void bind(RouteStop stop, int position) {
            isBinding = true;

            // Remove old watchers
            if (stationWatcher != null) {
                stationAutoComplete.removeTextChangedListener(stationWatcher);
            }
            if (fareWatcher != null) {
                fareInput.removeTextChangedListener(fareWatcher);
            }

            // Set station with autocomplete
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_dropdown_item_1line, stationSuggestions);
            stationAutoComplete.setAdapter(adapter);
            stationAutoComplete.setText(stop.getStation() != null ? stop.getStation() : "", false);

            // Set times
            arrivalTimeInput.setText(stop.getArrivalTime() != null ? stop.getArrivalTime() : "");
            departureTimeInput.setText(stop.getDepartureTime() != null ? stop.getDepartureTime() : "");

            // Set fare
            double fare = stop.getCumulativeFare();
            fareInput.setText(fare > 0 ? String.valueOf((int) fare) : "");

            // Show/hide connecting line (hide for last item)
            connectingLine.setVisibility(position < stops.size() - 1 ? View.VISIBLE : View.INVISIBLE);

            isBinding = false;

            // Setup listeners
            setupListeners(stop, position);
        }

        private void setupListeners(RouteStop stop, int position) {
            // Station text change
            stationWatcher = new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!isBinding) {
                        stop.setStation(s.toString().trim());
                        notifyStopChanged(position, stop);
                    }
                }
            };
            stationAutoComplete.addTextChangedListener(stationWatcher);

            // Arrival time picker
            arrivalTimeInput.setOnClickListener(v -> {
                showTimePicker(arrivalTimeInput, time -> {
                    stop.setArrivalTime(time);
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
            fareWatcher = new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (!isBinding) {
                        try {
                            double fare = s.toString().isEmpty() ? 0 : Double.parseDouble(s.toString());
                            stop.setCumulativeFare(fare);
                            notifyStopChanged(position, stop);
                        } catch (NumberFormatException e) {
                            // Ignore
                        }
                    }
                }
            };
            fareInput.addTextChangedListener(fareWatcher);

            // Menu button - show popup menu
            menuButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenu().add("Delete");
                popup.setOnMenuItemClickListener(item -> {
                    if ("Delete".equals(item.getTitle())) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            removeStop(pos);
                        }
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        private void showTimePicker(TextInputEditText input, TimeCallback callback) {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            String existingTime = input.getText() != null ? input.getText().toString() : "";
            if (!existingTime.isEmpty() && existingTime.contains(":")) {
                try {
                    String[] parts = existingTime.split(":");
                    hour = Integer.parseInt(parts[0]);
                    minute = Integer.parseInt(parts[1]);
                } catch (Exception e) {
                    // Use current time
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

    private interface TimeCallback {
        void onTimeSelected(String time);
    }

    private abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
