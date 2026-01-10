package com.siyam.travelschedulemanager.dashboard.master;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.siyam.travelschedulemanager.R;
import com.siyam.travelschedulemanager.data.firebase.AuthRepository;
import com.siyam.travelschedulemanager.ui.auth.LoginActivity;

public class MasterDashboardActivity extends AppCompatActivity {
    private AuthRepository authRepository;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_dashboard);

        authRepository = new AuthRepository();

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Master Control Panel");
        }

        // Setup navigation
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.master_nav_host_fragment);
        if (navHostFragment != null) {
            navController = ((NavHostFragment) navHostFragment).getNavController();
        }
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
