package com.example.studentexpensetracker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "student_expense_tracker.db";
    public static final int DATABASE_VERSION = 1;

    // User Table
    public static final String TABLE_USERS = "users";
    public static final String USER_ID = "id";
    public static final String USER_NAME = "full_name";
    public static final String USER_EMAIL = "email";
    public static final String USER_PASSWORD = "password";

    // Transaction Table
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TRANSACTION_ID = "id";
    public static final String TRANSACTION_USER_ID = "user_id";
    public static final String TRANSACTION_TITLE = "title";
    public static final String TRANSACTION_AMOUNT = "amount";
    public static final String TRANSACTION_CATEGORY = "category";
    public static final String TRANSACTION_TYPE = "type";
    public static final String TRANSACTION_DATE = "date";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        USER_NAME + " TEXT NOT NULL, " +
                        USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                        USER_PASSWORD + " TEXT NOT NULL)";

        String createTransactionsTable =
                "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                        TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TRANSACTION_USER_ID + " INTEGER NOT NULL, " +
                        TRANSACTION_TITLE + " TEXT NOT NULL, " +
                        TRANSACTION_AMOUNT + " REAL NOT NULL, " +
                        TRANSACTION_CATEGORY + " TEXT NOT NULL, " +
                        TRANSACTION_TYPE + " TEXT NOT NULL, " +
                        TRANSACTION_DATE + " TEXT NOT NULL, " +
                        "FOREIGN KEY(" + TRANSACTION_USER_ID + ") REFERENCES "
                        + TABLE_USERS + "(" + USER_ID + "))";

        db.execSQL(createUsersTable);
        db.execSQL(createTransactionsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        onCreate(db);
    }
}