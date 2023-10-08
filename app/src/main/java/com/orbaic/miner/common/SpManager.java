package com.orbaic.miner.common;

import android.content.Context;
import android.content.SharedPreferences;

public class SpManager {

    private static final String PREFERENCES_NAME = "MyAppPreferences";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    public static String KEY_LAST_QS_INDEX = "last_qs_index";
    public static String KEY_MCQ_STATE = "mcq_state";
    public static String KEY_MCQ_RANDOM_NUMBERS = "mcq_qs_random";
    public static String KEY_LAST_QUIZ_FINISH_TIME = "mcq_finish_time";


    // Initialize SharedPreferences
    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Save an integer value to SharedPreferences
    public static void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    // Retrieve an integer value from SharedPreferences
    public static int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }


    public static void saveLong(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }

    // Save a String value to SharedPreferences
    public static void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    // Retrieve a String value from SharedPreferences
    public static String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Remove a value from SharedPreferences
    public static void removeValue(String key) {
        editor.remove(key);
        editor.apply();
    }

    // Clear all data in SharedPreferences
    public static void clearPreferences() {
        editor.clear();
        editor.apply();
    }
}
