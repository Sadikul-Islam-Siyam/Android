package com.siyam.travelschedulemanager.ui.routes;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.model.UnifiedRoute;
import com.siyam.travelschedulemanager.ui.adapter.RouteAdapter;
import com.siyam.travelschedulemanager.viewmodel.RouteManagementViewModel;

import java.util.ArrayList;
import java.util.List;

public class RouteManagementFragment extends Fragment implements RouteAdapter.OnRouteActionListener {

    private RouteManagementViewModel viewModel;
    private RouteAdapter adapter;

    // UI Components
    private MaterialToolbar toolbar;
    private AutoCompleteTextView searchAutoComplete;
    private ChipGroup filterChipGroup;
    private Chip chipAll, chipBus, chipTrain;
    private TextView routesCountText;
    private ProgressBar loadingProgress;
    private View emptyStateLayout;
    private RecyclerView routesRecyclerView;
    private ExtendedFloatingActionButton fabAddRoute;

    // Debounce handler for search
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    // Role-based (passed from navigation args)
    private String userRole = "master"; // default to master permissions

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get user role from arguments
        if (getArguments() != null) {
            userRole = getArguments().getString("userRole", "master");
        }

        initViews(view);
        setupToolbar();
        setupRecyclerView();
        setupSearch();
        setupFilters();
        setupFab();
        setupViewModel();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        searchAutoComplete = view.findViewById(R.id.searchAutoComplete);
        filterChipGroup = view.findViewById(R.id.filterChipGroup);
        chipAll = view.findViewById(R.id.chipAll);
        chipBus = view.findViewById(R.id.chipBus);
        chipTrain = view.findViewById(R.id.chipTrain);
        routesCountText = view.findViewById(R.id.routesCountText);
        loadingProgress = view.findViewById(R.id.loadingProgress);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        routesRecyclerView = view.findViewById(R.id.routesRecyclerView);
        fabAddRoute = view.findViewById(R.id.fabAddRoute);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    private void setupRecyclerView() {
        adapter = new RouteAdapter(this);
        routesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        routesRecyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        // Setup autocomplete adapter
        ArrayAdapter<String> autocompleteAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        );
        searchAutoComplete.setAdapter(autocompleteAdapter);

        // Setup text change listener with debounce
        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Cancel previous search
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Debounce search (300ms delay)
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    viewModel.setSearchQuery(query);
                    
                    // Load autocomplete suggestions
                    if (query.length() >= 2) {
                        viewModel.loadAutocompleteSuggestions(query);
                    }
                };
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Handle autocomplete item selection
        searchAutoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            searchAutoComplete.setText(selected);
            searchAutoComplete.setSelection(selected.length());
            viewModel.setSearchQuery(selected);
        });
    }

    private void setupFilters() {
        filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.contains(R.id.chipAll)) {
                viewModel.setFilter("ALL");
            } else if (checkedIds.contains(R.id.chipBus)) {
                viewModel.setFilter("BUS");
            } else if (checkedIds.contains(R.id.chipTrain)) {
                viewModel.setFilter("TRAIN");
            }
        });
    }

    private void setupFab() {
        fabAddRoute.setOnClickListener(v -> {
            // Navigate to add route selection
            try {
                Bundle args = new Bundle();
                args.putString("userRole", userRole);
                Navigation.findNavController(v).navigate(R.id.action_routeManagement_to_addRouteSelection, args);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Navigation error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RouteManagementViewModel.class);

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loadingProgress.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Observe filtered routes
        viewModel.getFilteredRoutes().observe(getViewLifecycleOwner(), routes -> {
            if (routes != null) {
                adapter.submitList(routes);
                updateRoutesCount(routes.size());
                updateEmptyState(routes.isEmpty());
            }
        });

        // Observe autocomplete suggestions
        viewModel.getAutocompleteSuggestions().observe(getViewLifecycleOwner(), suggestions -> {
            if (suggestions != null && !suggestions.isEmpty()) {
                ArrayAdapter<String> newAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_dropdown_item_1line,
                        suggestions
                );
                searchAutoComplete.setAdapter(newAdapter);
                newAdapter.notifyDataSetChanged();
            }
        });

        // Observe errors
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                viewModel.clearError();
            }
        });

        // Observe success messages
        viewModel.getSuccessMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.clearSuccessMessage();
            }
        });

        // Load routes
        viewModel.loadAllRoutes();
    }

    private void updateRoutesCount(int count) {
        String text;
        if (count == 0) {
            text = "No routes found";
        } else if (count == 1) {
            text = "1 route found";
        } else {
            text = count + " routes found";
        }
        routesCountText.setText(text);
    }

    private void updateEmptyState(boolean isEmpty) {
        emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        routesRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    // ==================== RouteAdapter.OnRouteActionListener ====================

    @Override
    public void onRouteClick(UnifiedRoute route) {
        // Navigate to edit route with route data
        navigateToEditRoute(route);
    }

    @Override
    public void onEditClick(UnifiedRoute route) {
        navigateToEditRoute(route);
    }

    @Override
    public void onDeleteClick(UnifiedRoute route) {
        showDeleteConfirmationDialog(route);
    }

    private void navigateToEditRoute(UnifiedRoute route) {
        try {
            Bundle args = new Bundle();
            args.putString("routeId", route.getId());
            args.putString("routeType", route.getRouteType());
            args.putString("userRole", userRole);
            
            if (route.isBus()) {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_routeManagement_to_editBusRoute, args);
            } else {
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_routeManagement_to_editTrainRoute, args);
            }
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Cannot navigate to edit screen", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(UnifiedRoute route) {
        String routeName = route.getDisplayName() != null ? route.getDisplayName() : "this route";
        
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Route")
                .setMessage("Are you sure you want to delete \"" + routeName + "\"?\n\nThis action cannot be undone.")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    if ("master".equals(userRole)) {
                        // Master can delete directly
                        deleteRouteDirect(route);
                    } else {
                        // Developer needs to submit for approval
                        showDeleteRequestDialog(route);
                    }
                })
                .show();
    }

    private void deleteRouteDirect(UnifiedRoute route) {
        if (route.isBus()) {
            viewModel.deleteBusRouteDirect(route.getId());
        } else {
            viewModel.deleteTrainRouteDirect(route.getId());
        }
    }

    private void showDeleteRequestDialog(UnifiedRoute route) {
        // For developers - show a dialog to add message to master
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_request, null);
        com.google.android.material.textfield.TextInputEditText messageInput = 
                dialogView.findViewById(R.id.messageToMasterInput);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Request Deletion")
                .setMessage("Your delete request will be sent to a Master for approval.")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Submit Request", (dialog, which) -> {
                    String message = messageInput != null && messageInput.getText() != null ?
                            messageInput.getText().toString().trim() : "";
                    viewModel.submitDeleteRequest(route.getId(), route.getRouteType(), message);
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up handler
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
