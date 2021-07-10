package com.dev.food_colorie_counter.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static final String APP_PREFERENCES = "Food_Calorie_Counter";

    public static final String groupCalorie = "groupcalorie";
    public static final String timestatue = "timestatue";
    public static final String today_date = "today_date";
    public static final String user_ID = "user_ID";

    public static String getValue_String(Context context, String Key) {
        SharedPreferences settings = context.getSharedPreferences(
                APP_PREFERENCES, 0);
        return settings.getString(Key, "");
    }

    public static void setValue_String(Context context, String Key, String Value) {
        SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public static Float getValue_Float(Context context, String Key) {
        SharedPreferences settings = context.getSharedPreferences(
                APP_PREFERENCES, 0);
        return settings.getFloat(Key, 0);
    }

    public static void setValue_Float(Context context, String Key, Float Value) {
        SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(Key, Value);
        editor.apply();
    }

    public static int getValue_Int(Context context, String Key) {
        SharedPreferences settings = context.getSharedPreferences(
                APP_PREFERENCES, 0);
        return settings.getInt(Key, 0);
    }

    public static void setValue_Int(Context context, String Key, int Value) {
        SharedPreferences settings = context.getSharedPreferences(APP_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(Key, Value);
        editor.apply();
    }

    public static boolean getValue_Boolean(Context context, String Key, boolean Default) {
        SharedPreferences settings = context.getSharedPreferences(
                APP_PREFERENCES, 0);
        return settings.getBoolean(Key, Default);
    }

    public static void setValue_Boolean(Context context, String Key, boolean Value) {
        SharedPreferences settings = context.getSharedPreferences(
                APP_PREFERENCES, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(Key, Value);
        editor.apply();
    }
}
