package com.orbaic.miner.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.orbaic.miner.MyApp;

public class SpManager {

    public static final String PREFERENCES_NAME = "MyAppPreferences";
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    public static String KEY_LAST_QS_INDEX = "last_qs_index";
    public static String KEY_MCQ_STATE = "mcq_state";
    public static String KEY_MCQ_RANDOM_NUMBERS = "mcq_qs_random";
    public static String KEY_LAST_QUIZ_FINISH_TIME = "mcq_finish_time";
    public static String KEY_IS_TAP_TARGET_SHOW = "tap_showed";
    public static String KEY_CORRECT_ANS = "correct_ans";
    public static String KEY_WRONG_ANS = "wrong_ans";
    public static String KEY_IS_NOTIFICATION_ENABLED = "notification";
    public static String KEY_FCM_TOKEN = "fcm_token";
    public static String KEY_FCM_NEW_TOKEN = "fcm_new_token";
    public static String KEY_MY_REFER_CODE = "refer";
    public static String KEY_REFERRED_BY_UID = "referred_by_uid";
    public static String KEY_POINTS_EARNED = "points_earned";
    public static String KEY_POINTS_REFER_EARNED = "refer_points_earned";
    public static String KEY_SERVER_TIME = "server_time";
    public static String KEY_MINER_STATUS = "miner_status";
    public static String KEY_QUIZ_COUNT = "quiz_count";
    public static String KEY_POINT_SEPARATED = "point_sep";
    public static String KEY_POINTS_FROM_SERVER = "point_server";


    // Initialize SharedPreferences
    public static void init(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static void initIfNeeded() {
        if (sharedPreferences == null) {
            sharedPreferences = MyApp.context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    public static void saveInt(String key, int value) {
        initIfNeeded();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(String key, int defaultValue) {
        initIfNeeded();
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static void saveDouble(String key, double value) {
        initIfNeeded();
        editor.putString(key, String.valueOf(value));
        editor.apply();
    }

    public static double getDouble(String key, double defaultValue) {
        initIfNeeded();
        String valueStr = sharedPreferences.getString(key, null);
        Log.e("getDouble123", "valueStr: "+valueStr);
        if (valueStr != null) {
            try {
                return Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Log.e("getDouble123", "getDouble: "+e.getLocalizedMessage());
            }
        }
        return defaultValue;
    }

    public static void saveBoolean(String key, boolean value) {
        initIfNeeded();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        initIfNeeded();
        return sharedPreferences.getBoolean(key, defaultValue);
    }



    public static void saveLong(String key, long value) {
        initIfNeeded();
        editor.putLong(key, value);
        editor.apply();
    }

    public static long getLong(String key, long defaultValue) {
        initIfNeeded();
        return sharedPreferences.getLong(key, defaultValue);
    }

    // Save a String value to SharedPreferences
    public static void saveString(String key, String value) {
        initIfNeeded();
        editor.putString(key, value);
        editor.apply();
    }

    // Retrieve a String value from SharedPreferences
    public static String getString(String key, String defaultValue) {
        initIfNeeded();
        return sharedPreferences.getString(key, defaultValue);
    }

    public static boolean isDailyTaskDone() {
        initIfNeeded();
        String currentDay = DateUtils.getCurrentDay();
        return sharedPreferences.contains("refKey");
    }

    public static void makeDailyTaskDone() {
        initIfNeeded();
//        String currentDay = DateUtils.getCurrentDay();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("refKey", "DONE");
        editor.apply();
    }


    // Remove a value from SharedPreferences
    public static void removeValue(String key) {
        initIfNeeded();
        editor.remove(key);
        editor.apply();
    }

    // Clear all data in SharedPreferences
    public static void clearPreferences() {
        initIfNeeded();
        editor.clear();
        editor.apply();
    }
}
