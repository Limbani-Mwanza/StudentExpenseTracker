package com.example.studentexpensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentexpensetracker.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionDao {

    private final DatabaseHelper databaseHelper;

    public TransactionDao(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public boolean insertTransaction(Transaction transaction) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.TRANSACTION_USER_ID,
                transaction.getUserId());

        values.put(DatabaseHelper.TRANSACTION_TITLE,
                transaction.getDescription());

        values.put(DatabaseHelper.TRANSACTION_AMOUNT,
                transaction.getAmount());

        values.put(DatabaseHelper.TRANSACTION_CATEGORY,
                transaction.getCategory());

        values.put(DatabaseHelper.TRANSACTION_TYPE,
                transaction.getType());

        values.put(DatabaseHelper.TRANSACTION_DATE,
                transaction.getDate());

        long result = db.insert(
                DatabaseHelper.TABLE_TRANSACTIONS,
                null,
                values);

        db.close();

        return result != -1;
    }

    public List<Transaction> getAllTransactions(int userId) {

        List<Transaction> transactionList = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TRANSACTIONS,
                null,
                DatabaseHelper.TRANSACTION_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null,
                null,
                DatabaseHelper.TRANSACTION_DATE + " DESC");

        if (cursor.moveToFirst()) {

            do {

                Transaction transaction = new Transaction();

                transaction.setId(cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_ID)));

                transaction.setUserId(cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_USER_ID)));

                transaction.setDescription(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TITLE)));

                transaction.setAmount(cursor.getDouble(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_AMOUNT)));

                transaction.setCategory(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_CATEGORY)));

                transaction.setType(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TYPE)));

                transaction.setDate(cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DATE)));

                transactionList.add(transaction);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return transactionList;
    }

    public double getTotalIncome(int userId) {

        double total = 0;

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(

                "SELECT SUM(" + DatabaseHelper.TRANSACTION_AMOUNT + ") FROM "
                        + DatabaseHelper.TABLE_TRANSACTIONS
                        + " WHERE "
                        + DatabaseHelper.TRANSACTION_USER_ID + "=? AND "
                        + DatabaseHelper.TRANSACTION_TYPE + "=?",

                new String[]{
                        String.valueOf(userId),
                        "Income"
                });

        if (cursor.moveToFirst()) {

            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return total;
    }

    public double getTotalExpense(int userId) {

        double total = 0;

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(

                "SELECT SUM(" + DatabaseHelper.TRANSACTION_AMOUNT + ") FROM "
                        + DatabaseHelper.TABLE_TRANSACTIONS
                        + " WHERE "
                        + DatabaseHelper.TRANSACTION_USER_ID + "=? AND "
                        + DatabaseHelper.TRANSACTION_TYPE + "=?",

                new String[]{
                        String.valueOf(userId),
                        "Expense"
                });

        if (cursor.moveToFirst()) {

            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return total;
    }

    public double getCurrentBalance(int userId) {

        return getTotalIncome(userId) - getTotalExpense(userId);
    }

    public Transaction getTransactionById(int id) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TRANSACTIONS,
                null,
                DatabaseHelper.TRANSACTION_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        Transaction transaction = null;

        if (cursor.moveToFirst()) {

            transaction = new Transaction();

            transaction.setId(cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_ID)));

            transaction.setUserId(cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_USER_ID)));

            transaction.setDescription(cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TITLE)));

            transaction.setAmount(cursor.getDouble(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_AMOUNT)));

            transaction.setCategory(cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_CATEGORY)));

            transaction.setType(cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TYPE)));

            transaction.setDate(cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DATE)));
        }

        cursor.close();
        db.close();

        return transaction;
    }

    public boolean updateTransaction(Transaction transaction) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.TRANSACTION_TITLE,
                transaction.getDescription());

        values.put(DatabaseHelper.TRANSACTION_AMOUNT,
                transaction.getAmount());

        values.put(DatabaseHelper.TRANSACTION_CATEGORY,
                transaction.getCategory());

        values.put(DatabaseHelper.TRANSACTION_TYPE,
                transaction.getType());

        values.put(DatabaseHelper.TRANSACTION_DATE,
                transaction.getDate());

        int rows = db.update(
                DatabaseHelper.TABLE_TRANSACTIONS,
                values,
                DatabaseHelper.TRANSACTION_ID + "=?",
                new String[]{String.valueOf(transaction.getId())});

        db.close();

        return rows > 0;
    }

    public boolean deleteTransaction(int id) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        int rows = db.delete(
                DatabaseHelper.TABLE_TRANSACTIONS,
                DatabaseHelper.TRANSACTION_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();

        return rows > 0;
    }

    public double getCurrentMonthIncome(int userId) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String currentMonth = new java.text.SimpleDateFormat(
                "yyyy-MM",
                java.util.Locale.getDefault())
                .format(new java.util.Date());

        android.database.Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.TRANSACTION_AMOUNT + ") " +
                        "FROM " + DatabaseHelper.TABLE_TRANSACTIONS +
                        " WHERE " + DatabaseHelper.TRANSACTION_USER_ID + "=? " +
                        "AND " + DatabaseHelper.TRANSACTION_TYPE + "=? " +
                        "AND substr(" + DatabaseHelper.TRANSACTION_DATE + ",1,7)=?",
                new String[]{
                        String.valueOf(userId),
                        "Income",
                        currentMonth
                });

        double total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return total;
    }

    public double getCurrentMonthExpense(int userId) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String currentMonth = new java.text.SimpleDateFormat(
                "yyyy-MM",
                java.util.Locale.getDefault())
                .format(new java.util.Date());

        android.database.Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.TRANSACTION_AMOUNT + ") " +
                        "FROM " + DatabaseHelper.TABLE_TRANSACTIONS +
                        " WHERE " + DatabaseHelper.TRANSACTION_USER_ID + "=? " +
                        "AND " + DatabaseHelper.TRANSACTION_TYPE + "=? " +
                        "AND substr(" + DatabaseHelper.TRANSACTION_DATE + ",1,7)=?",
                new String[]{
                        String.valueOf(userId),
                        "Expense",
                        currentMonth
                });

        double total = 0;

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return total;
    }

    public java.util.List<Transaction> searchTransactions(int userId, String keyword) {

        java.util.List<Transaction> list = new java.util.ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        android.database.Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_TRANSACTIONS +
                        " WHERE " + DatabaseHelper.TRANSACTION_USER_ID + "=? " +
                        "AND (" +
                        DatabaseHelper.TRANSACTION_TITLE + " LIKE ? OR " +
                        DatabaseHelper.TRANSACTION_CATEGORY + " LIKE ? OR " +
                        DatabaseHelper.TRANSACTION_TYPE + " LIKE ?" +
                        ") ORDER BY " + DatabaseHelper.TRANSACTION_DATE + " DESC",
                new String[]{
                        String.valueOf(userId),
                        "%" + keyword + "%",
                        "%" + keyword + "%",
                        "%" + keyword + "%"
                });

        while (cursor.moveToNext()) {

            Transaction transaction = new Transaction();

            transaction.setId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_ID)));

            transaction.setUserId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_USER_ID)));

            transaction.setDescription(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TITLE)));

            transaction.setAmount(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_AMOUNT)));

            transaction.setCategory(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_CATEGORY)));

            transaction.setType(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_TYPE)));

            transaction.setDate(
                    cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.TRANSACTION_DATE)));

            list.add(transaction);
        }

        cursor.close();
        db.close();

        return list;
    }

    public Cursor getExpenseBreakdown(int userId) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String query =
                "SELECT " +
                        DatabaseHelper.TRANSACTION_CATEGORY +
                        ", SUM(" + DatabaseHelper.TRANSACTION_AMOUNT + ") AS total " +
                        "FROM " + DatabaseHelper.TABLE_TRANSACTIONS +
                        " WHERE " + DatabaseHelper.TRANSACTION_USER_ID + "=? " +
                        "AND " + DatabaseHelper.TRANSACTION_TYPE + "='Expense' " +
                        "GROUP BY " + DatabaseHelper.TRANSACTION_CATEGORY;

        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }
}