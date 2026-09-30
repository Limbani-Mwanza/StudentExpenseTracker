package com.example.studentexpensetracker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentexpensetracker.models.User;

public class UserDao {

    private final DatabaseHelper databaseHelper;

    public UserDao(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    // Register a new user
    public boolean registerUser(User user) {

        if (emailExists(user.getEmail())) {
            return false;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.USER_NAME, user.getFullName());
        values.put(DatabaseHelper.USER_EMAIL, user.getEmail());
        values.put(DatabaseHelper.USER_PASSWORD, user.getPassword());

        long result = db.insert(DatabaseHelper.TABLE_USERS, null, values);

        db.close();

        return result != -1;
    }

    // Check if email already exists
    public boolean emailExists(String email) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.USER_EMAIL + "=?",
                new String[]{email},
                null,
                null,
                null
        );

        boolean exists = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return exists;
    }

    // Login user
    public User loginUser(String email, String password) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.USER_EMAIL + "=? AND "
                        + DatabaseHelper.USER_PASSWORD + "=?",
                new String[]{email, password},
                null,
                null,
                null
        );

        User user = null;

        if (cursor.moveToFirst()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_NAME));
            String userEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_EMAIL));

            user = new User(id, name, userEmail, password);
        }

        cursor.close();
        db.close();

        return user;
    }

    // Get user by email
    public User getUserByEmail(String email) {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_USERS,
                null,
                DatabaseHelper.USER_EMAIL + "=?",
                new String[]{email},
                null,
                null,
                null
        );

        User user = null;

        if (cursor.moveToFirst()) {

            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_NAME));
            String password = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.USER_PASSWORD));

            user = new User(id, name, email, password);
        }

        cursor.close();
        db.close();

        return user;
    }
}