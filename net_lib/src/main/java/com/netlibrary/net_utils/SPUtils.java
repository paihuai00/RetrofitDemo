package com.netlibrary.net_utils;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Map;

/**
 * 方便操作的工具类 直接调用
 *
 * 使用：需要在Application中调用 SPUtils.initialize(Context context, String PREF_NAME)
 */

public class SPUtils {
    private static SharedPreferences pref;

    private static SharedPreferences.Editor editor;

    /**
     *
     * @param context
     * @param PREF_NAME name
     */
    public static void initialize(Context context, String PREF_NAME) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static SharedPreferences getSP() {
        return pref;
    }

    public static boolean contains(String key) {
        return pref.contains(key);
    }

    public static Map<String, ?> getAll() {
        return pref.getAll();
    }

    public static boolean get(String key, boolean defValue) {
        return pref.getBoolean(key, defValue);
    }

    public static float get(String key, float defValue) {
        return pref.getFloat(key, defValue);
    }

    public static int get(String key, int defValue) {
        return pref.getInt(key, defValue);
    }

    public static long get(String key, long defValue) {
        return pref.getLong(key, defValue);
    }

    public static String get(String key, String defValue) {
        return pref.getString(key, defValue);
    }

    public static String get(String key) {
        return pref.getString(key, "");
    }

    public static void put(String key, boolean value) {
        editor.putBoolean(key, value);
        commit();
    }

    public static void put(String key, float value) {
        editor.putFloat(key, value);
        commit();
    }

    public static void put(String key, int value) {
        editor.putInt(key, value);
        commit();
    }

    public static void put(String key, long value) {
        editor.putLong(key, value);
        commit();
    }

    public static void put(String key, String value) {
        editor.putString(key, value);
        commit();
    }

    public static void commit() {
        editor.commit();
    }

    public static void clear() {
        editor.clear();
        editor.commit();
    }

    public static void remove(String key) {
        editor.remove(key);
    }

    public static void save(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void save(String key, float value) {
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void save(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
    }

    public static void save(String key, long value) {
        editor.putLong(key, value);
        editor.commit();
    }

    public static void save(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }
}
