package com.example.studentexpensetracker.activities;

import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputEditText;

import com.example.studentexpensetracker.adapter.TransactionAdapter;
import com.example.studentexpensetracker.models.Transaction;

import java.util.List;

import com.example.studentexpensetracker.database.TransactionDao;
import com.example.studentexpensetracker.databinding.ActivityDashboardBinding;
import com.example.studentexpensetracker.utils.SessionManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private SessionManager sessionManager;
    private TransactionDao transactionDao;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private TextInputEditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        transactionDao = new TransactionDao(this);

        etSearch = binding.etSearch;

        binding.txtGreeting.setText(
                getGreeting() + ", " +
                        sessionManager.getUserName() +
                        " 👋");

        String today = new SimpleDateFormat(
                "EEEE, dd MMMM yyyy",
                Locale.getDefault()).format(new Date());

        binding.txtDate.setText(today);

        loadDashboardData();

        binding.btnAddTransaction.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    AddTransactionActivity.class);

            startActivity(intent);

        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                searchTransactions(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnStatistics.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    StatisticsActivity.class);

            startActivity(intent);

        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadDashboardData();
    }

    private void loadDashboardData() {

        int userId = sessionManager.getUserId();

        double income = transactionDao.getTotalIncome(userId);

        double expense = transactionDao.getTotalExpense(userId);

        double balance = transactionDao.getCurrentBalance(userId);

        DecimalFormat format = new DecimalFormat("$#,##0.00");

        binding.txtIncome.setText(format.format(income));

        binding.txtExpense.setText(format.format(expense));

        binding.txtBalance.setText(format.format(balance));

        transactionList = transactionDao.getAllTransactions(userId);

        if (transactionList.isEmpty()) {

            binding.txtEmpty.setVisibility(android.view.View.VISIBLE);
            binding.rvTransactions.setVisibility(android.view.View.GONE);

        } else {

            binding.txtEmpty.setVisibility(android.view.View.GONE);
            binding.rvTransactions.setVisibility(android.view.View.VISIBLE);

            transactionAdapter = new TransactionAdapter(
                    transactionList,
                    transaction -> {

                        Intent intent = new Intent(
                                DashboardActivity.this,
                                EditTransactionActivity.class);

                        intent.putExtra("transaction_id", transaction.getId());

                        startActivity(intent);
                    });

            binding.rvTransactions.setLayoutManager(
                    new LinearLayoutManager(this));

            binding.rvTransactions.setAdapter(transactionAdapter);
        }
    }

    private void searchTransactions(String keyword) {

        int userId = sessionManager.getUserId();

        if (keyword.trim().isEmpty()) {

            loadDashboardData();
            return;
        }

        transactionList = transactionDao.searchTransactions(userId, keyword);

        if (transactionList.isEmpty()) {

            binding.txtEmpty.setVisibility(android.view.View.VISIBLE);
            binding.rvTransactions.setVisibility(android.view.View.GONE);

        } else {

            binding.txtEmpty.setVisibility(android.view.View.GONE);
            binding.rvTransactions.setVisibility(android.view.View.VISIBLE);

            transactionAdapter = new TransactionAdapter(
                    transactionList,
                    transaction -> {

                        Intent intent = new Intent(
                                DashboardActivity.this,
                                EditTransactionActivity.class);

                        intent.putExtra("transaction_id", transaction.getId());

                        startActivity(intent);

                    });

            binding.rvTransactions.setLayoutManager(
                    new LinearLayoutManager(this));

            binding.rvTransactions.setAdapter(transactionAdapter);
        }
    }

    private String getGreeting() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {
            return "Good Morning";
        } else if (hour < 18) {
            return "Good Afternoon";
        } else {
            return "Good Evening";
        }
    }
}