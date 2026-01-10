package com.siyam.travelschedulemanager.dashboard.user;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.data.firebase.AuthRepository;
import com.siyam.travelschedulemanager.ui.auth.LoginActivity;
import com.siyam.travelschedulemanager.viewmodel.AuthViewModel;

public class UserDashboardActivity extends AppCompatActivity {
    private AuthViewModel authViewModel;
    private AuthRepository authRepository;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        authRepository = new AuthRepository();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup navigation
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.user_nav_host_fragment);
        if (navHostFragment != null) {
            navController = ((NavHostFragment) navHostFragment).getNavController();
            
            // Enable back button
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(destination.getId() != R.id.userHomeFragment);
                }
            });
        }

        // Load current user
        authViewModel.loadCurrentUser();
        
        // Observe user data for toolbar title
        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null && toolbar != null) {
                toolbar.setTitle("Welcome, " + user.getUsername());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!authRepository.isUserLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
