package com.siyam.travelschedulemanager.util;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME_MODE = "theme_mode";
    
    public static final int MODE_LIGHT = 0;
    public static final int MODE_DARK = 1;
    public static final int MODE_SYSTEM = 2;

    private final SharedPreferences prefs;

    public ThemeManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setThemeMode(int mode) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply();
        applyTheme(mode);
    }

    public int getThemeMode() {
        return prefs.getInt(KEY_THEME_MODE, MODE_SYSTEM);
    }

    public void applyTheme() {
        applyTheme(getThemeMode());
    }

    public static void applyTheme(int mode) {
        switch (mode) {
            case MODE_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case MODE_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case MODE_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    public boolean isDarkMode(Context context) {
        int mode = getThemeMode();
        if (mode == MODE_DARK) {
            return true;
        } else if (mode == MODE_LIGHT) {
            return false;
        } else {
            // System mode - check current configuration
            int nightModeFlags = context.getResources().getConfiguration().uiMode 
                    & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        }
    }

    public String getThemeName() {
        int mode = getThemeMode();
        switch (mode) {
            case MODE_LIGHT:
                return "Light";
            case MODE_DARK:
                return "Dark";
            default:
                return "System";
        }
    }
}
