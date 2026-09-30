package com.example.studentexpensetracker.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;

import android.widget.TextView;
import java.text.DecimalFormat;

import com.example.studentexpensetracker.database.TransactionDao;
import com.example.studentexpensetracker.utils.SessionManager;

import com.example.studentexpensetracker.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private TransactionDao transactionDao;
    private SessionManager sessionManager;
    private TextView txtStatsBalance;
    private TextView txtStatsIncome;
    private TextView txtStatsExpense;

    private TextView txtMonthIncome;
    private TextView txtMonthExpense;
    private TextView txtMonthSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pieChart = findViewById(R.id.pieChart);

        txtStatsBalance = findViewById(R.id.txtStatsBalance);
        txtStatsIncome = findViewById(R.id.txtStatsIncome);
        txtStatsExpense = findViewById(R.id.txtStatsExpense);

        txtMonthIncome = findViewById(R.id.txtMonthIncome);
        txtMonthExpense = findViewById(R.id.txtMonthExpense);
        txtMonthSaved = findViewById(R.id.txtMonthSaved);

        transactionDao = new TransactionDao(this);
        sessionManager = new SessionManager(this);

        loadStatistics();

        loadPieChart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadStatistics();
        loadPieChart();
    }

    private void loadStatistics() {

        int userId = sessionManager.getUserId();

        double balance = transactionDao.getCurrentBalance(userId);
        double income = transactionDao.getTotalIncome(userId);
        double expense = transactionDao.getTotalExpense(userId);

        double monthIncome = transactionDao.getCurrentMonthIncome(userId);
        double monthExpense = transactionDao.getCurrentMonthExpense(userId);
        double monthSaved = monthIncome - monthExpense;

        DecimalFormat format = new DecimalFormat("$#,##0.00");

        txtStatsBalance.setText(format.format(balance));
        txtStatsIncome.setText(format.format(income));
        txtStatsExpense.setText(format.format(expense));

        txtMonthIncome.setText("Income: " + format.format(monthIncome));
        txtMonthExpense.setText("Expense: " + format.format(monthExpense));
        txtMonthSaved.setText("Saved: " + format.format(monthSaved));
    }

    private void loadPieChart() {

        ArrayList<PieEntry> entries = new ArrayList<>();

        int userId = sessionManager.getUserId();

        Cursor cursor = transactionDao.getExpenseBreakdown(userId);

        while (cursor.moveToNext()) {

            String category = cursor.getString(0);

            float total = cursor.getFloat(1);

            entries.add(new PieEntry(total, category));
        }

        cursor.close();

        if (entries.isEmpty()) {

            pieChart.clear();
            pieChart.setNoDataText("No expense data available");
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses");

        dataSet.setColors(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#F44336"),
                Color.parseColor("#9C27B0"),
                Color.parseColor("#009688"),
                Color.parseColor("#795548"),
                Color.parseColor("#E91E63")
        );

        dataSet.setValueTextSize(14f);

        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.setCenterText("Expense Breakdown");
        pieChart.setCenterTextSize(18f);
        pieChart.getDescription().setEnabled(false);
        pieChart.animateY(1000);

        pieChart.invalidate();
    }
}