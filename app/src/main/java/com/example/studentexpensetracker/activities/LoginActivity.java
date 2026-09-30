package com.example.studentexpensetracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentexpensetracker.utils.SessionManager;

import com.example.studentexpensetracker.database.UserDao;
import com.example.studentexpensetracker.databinding.ActivityLoginBinding;
import com.example.studentexpensetracker.models.User;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private UserDao userDao;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDao = new UserDao(this);

        sessionManager = new SessionManager(this);

        binding.txtRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        binding.btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Please enter your email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.setError("Invalid email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Please enter your password");
            return;
        }

        User user = userDao.loginUser(email, password);

        if (user != null) {

            sessionManager.createLoginSession(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail()
            );

            Toast.makeText(this,
                    "Welcome, " + user.getFullName() + "!",
                    Toast.LENGTH_SHORT).show();

            startActivity(new Intent(
                    LoginActivity.this,
                    DashboardActivity.class));

            finish();

        } else {

            Toast.makeText(this,
                    "Incorrect email or password",
                    Toast.LENGTH_SHORT).show();
        }
    }
}