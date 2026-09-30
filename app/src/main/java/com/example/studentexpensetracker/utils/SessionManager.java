package com.example.studentexpensetracker.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "StudentExpenseTracker";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {

        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE);

        editor = preferences.edit();
    }

    public void createLoginSession(int id,
                                   String name,
                                   String email) {

        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        editor.putInt(KEY_USER_ID, id);

        editor.putString(KEY_USER_NAME, name);

        editor.putString(KEY_USER_EMAIL, email);

        editor.apply();
    }

    public boolean isLoggedIn() {

        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {

        return preferences.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {

        return preferences.getString(KEY_USER_NAME, "");
    }

    public String getUserEmail() {

        return preferences.getString(KEY_USER_EMAIL, "");
    }

    public void logout() {

        editor.clear();
        editor.apply();
    }

}