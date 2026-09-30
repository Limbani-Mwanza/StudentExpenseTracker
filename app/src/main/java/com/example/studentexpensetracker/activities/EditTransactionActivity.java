package com.example.studentexpensetracker.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.example.studentexpensetracker.R;
import com.example.studentexpensetracker.database.TransactionDao;
import com.example.studentexpensetracker.models.Transaction;
import com.google.android.material.textfield.TextInputEditText;

public class EditTransactionActivity extends AppCompatActivity {

    private RadioButton rbIncome, rbExpense;

    private TextInputEditText etAmount;
    private TextInputEditText etDescription;

    private AutoCompleteTextView actCategory;

    private Button btnSave;
    private Button btnDelete;

    private TransactionDao transactionDao;

    private Transaction transaction;

    private int transactionId;

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
        setContentView(R.layout.activity_edit_transaction);

        initializeViews();

        transactionDao = new TransactionDao(this);

        transactionId = getIntent().getIntExtra("transaction_id", -1);

        if (transactionId != -1) {

            loadTransaction();
        }

        btnSave.setOnClickListener(v -> updateTransaction());

        btnDelete.setOnClickListener(v -> deleteTransaction());
    }

    private void updateTransaction() {

        String amountText = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = actCategory.getText().toString().trim();

        if (TextUtils.isEmpty(amountText)
                || TextUtils.isEmpty(description)
                || TextUtils.isEmpty(category)) {

            Toast.makeText(
                    this,
                    "Please fill in all fields.",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        double amount = Double.parseDouble(amountText);

        String type = rbIncome.isChecked() ? "Income" : "Expense";

        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setCategory(category);
        transaction.setType(type);

        transaction.setDate(
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault())
                        .format(new Date()));

        boolean success = transactionDao.updateTransaction(transaction);

        if (success) {

            Toast.makeText(
                    this,
                    "Transaction Updated!",
                    Toast.LENGTH_SHORT).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Update Failed!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTransaction() {

        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> {

                    boolean success =
                            transactionDao.deleteTransaction(transaction.getId());

                    if (success) {

                        Toast.makeText(
                                this,
                                "Transaction Deleted!",
                                Toast.LENGTH_SHORT).show();

                        finish();

                    } else {

                        Toast.makeText(
                                this,
                                "Delete Failed!",
                                Toast.LENGTH_SHORT).show();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void initializeViews() {

        rbIncome = findViewById(R.id.rbIncome);
        rbExpense = findViewById(R.id.rbExpense);

        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);

        actCategory = findViewById(R.id.actCategory);

        btnSave = findViewById(R.id.btnSave);

        btnDelete = findViewById(R.id.btnDelete);
    }

    private void loadTransaction() {

        transaction = transactionDao.getTransactionById(transactionId);

        if (transaction == null) {
            finish();
            return;
        }

        etAmount.setText(String.valueOf(transaction.getAmount()));

        etDescription.setText(transaction.getDescription());

        if (transaction.getType().equals("Income")) {

            rbIncome.setChecked(true);

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            incomeCategories);

            actCategory.setAdapter(adapter);

        } else {

            rbExpense.setChecked(true);

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_dropdown_item_1line,
                            expenseCategories);

            actCategory.setAdapter(adapter);
        }

        actCategory.setText(transaction.getCategory(), false);
    }
}