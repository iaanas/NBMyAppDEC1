package com.example.xavier.aesstor;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class PreferencesSettings {
    private static final String PREF_FILE = "settings_pref";

    static void saveToPref(Context context, String str) {
        Editor editor = context.getSharedPreferences(PREF_FILE, 0).edit();
        editor.putString("code", str);
        editor.commit();
    }

    static String getCode(Context context) {
        return context.getSharedPreferences(PREF_FILE, 0).getString("code", "");
    }
}
