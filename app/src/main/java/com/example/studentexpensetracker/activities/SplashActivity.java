package com.example.studentexpensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentexpensetracker.databinding.ActivitySplashBinding;
import com.example.studentexpensetracker.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        new Handler().postDelayed(() -> {

            Intent intent;

            if (sessionManager.isLoggedIn()) {

                intent = new Intent(
                        SplashActivity.this,
                        DashboardActivity.class);

            } else {

                intent = new Intent(
                        SplashActivity.this,
                        LoginActivity.class);
            }

            startActivity(intent);
            finish();

        }, 2000);
    }
}