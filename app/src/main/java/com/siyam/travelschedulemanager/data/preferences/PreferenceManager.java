package com.siyam.travelschedulemanager.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import com.siyam.travelschedulemanager.util.Constants;

public class PreferenceManager {
    private final SharedPreferences prefs;

    public PreferenceManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setTheme(String theme) {
        prefs.edit().putString(Constants.PREF_THEME, theme).apply();
    }

    public String getTheme() {
        return prefs.getString(Constants.PREF_THEME, "light");
    }

    public void setLanguage(String language) {
        prefs.edit().putString(Constants.PREF_LANGUAGE, language).apply();
    }

    public String getLanguage() {
        return prefs.getString(Constants.PREF_LANGUAGE, "en");
    }

    public void setLastSync(long timestamp) {
        prefs.edit().putLong(Constants.PREF_LAST_SYNC, timestamp).apply();
    }

    public long getLastSync() {
        return prefs.getLong(Constants.PREF_LAST_SYNC, 0);
    }

    public void clear() {
        prefs.edit().clear().apply();
    }
}
