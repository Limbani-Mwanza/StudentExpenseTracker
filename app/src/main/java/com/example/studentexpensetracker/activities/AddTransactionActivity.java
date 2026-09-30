package com.example.studentexpensetracker.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentexpensetracker.R;
import com.google.android.material.textfield.TextInputEditText;

import android.text.TextUtils;
import android.widget.Toast;

import com.example.studentexpensetracker.database.TransactionDao;
import com.example.studentexpensetracker.models.Transaction;
import com.example.studentexpensetracker.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private RadioButton rbIncome, rbExpense;

    private TextInputEditText etAmount;
    private TextInputEditText etDescription;

    private AutoCompleteTextView actCategory;

    private Button btnSave;

    private TransactionDao transactionDao;
    private SessionManager sessionManager;

    private final String[] incomeCategories = {
            "Salary",
            "Allowance",
            "Scholarship",
            "Gift",
            "Other"
    };

    private final String[] expenseCategories = {
            "Food",
            "Transport",
            "Shopping",
            "Rent",
            "Entertainment",
            "Bills",
            "Health",
            "Education",
            "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        initializeViews();

        transactionDao = new TransactionDao(this);

        sessionManager = new SessionManager(this);

        loadIncomeCategories();

        rbIncome.setOnClickListener(v -> loadIncomeCategories());

        rbExpense.setOnClickListener(v -> loadExpenseCategories());

        btnSave.setOnClickListener(v -> saveTransaction());

    }

    private void initializeViews() {

        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);

        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);

        actCategory = findViewById(R.id.actCategory);

        btnSave = findViewById(R.id.btnSave);
    }

    private void loadIncomeCategories() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        incomeCategories);

        actCategory.setAdapter(adapter);
    }

    private void loadExpenseCategories() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        expenseCategories);

        actCategory.setAdapter(adapter);
    }

    private void saveTransaction() {

        String amountText = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = actCategory.getText().toString().trim();

        if (TextUtils.isEmpty(amountText)) {
            etAmount.setError("Enter amount");
            return;
        }

        if (TextUtils.isEmpty(category)) {
            actCategory.setError("Select category");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            etDescription.setError("Enter description");
            return;
        }

        String type;

        if (rbIncome.isChecked()) {
            type = "Income";
        } else if (rbExpense.isChecked()) {
            type = "Expense";
        } else {
            Toast.makeText(this, "Select transaction type", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountText);

        String date = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        Transaction transaction = new Transaction();

        transaction.setUserId(sessionManager.getUserId());
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDescription(description);
        transaction.setDate(date);

        boolean success = transactionDao.insertTransaction(transaction);

        if (success) {
            Toast.makeText(this, "Transaction Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save transaction.", Toast.LENGTH_SHORT).show();
        }
    }
}