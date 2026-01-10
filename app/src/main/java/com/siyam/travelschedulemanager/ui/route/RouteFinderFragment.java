package com.siyam.travelschedulemanager.ui.route;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.util.Constants;
import com.siyam.travelschedulemanager.viewmodel.ScheduleViewModel;

public class RouteFinderFragment extends Fragment {
    private ScheduleViewModel scheduleViewModel;
    private Spinner spinnerOrigin;
    private Spinner spinnerDestination;
    private Button buttonFindRoutes;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_finder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerOrigin = view.findViewById(R.id.spinner_origin);
        spinnerDestination = view.findViewById(R.id.spinner_destination);
        buttonFindRoutes = view.findViewById(R.id.button_find_routes);

        // Setup spinners with Bangladesh cities
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, Constants.BANGLADESH_CITIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOrigin.setAdapter(adapter);
        spinnerDestination.setAdapter(adapter);

        buttonFindRoutes.setOnClickListener(v -> findRoutes());

        scheduleViewModel.loadAllSchedules();
    }

    private void findRoutes() {
        String origin = spinnerOrigin.getSelectedItem().toString();
        String destination = spinnerDestination.getSelectedItem().toString();

        if (origin.equals(destination)) {
            Toast.makeText(requireContext(), "Origin and destination cannot be the same", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use current date and max 3 legs
        java.util.Date travelDate = new java.util.Date();
        int maxLegs = 3;

        scheduleViewModel.findOptimalRoutes(origin, destination, travelDate, maxLegs);
        Toast.makeText(requireContext(), "Finding routes from " + origin + " to " + destination, Toast.LENGTH_SHORT).show();
    }
}
