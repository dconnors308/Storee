package com.example.stohre.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.stohre.BuildConfig;

import static android.content.Context.MODE_PRIVATE;

public class Utilities {

    private Context context;

    public Utilities(Context context) {
        this.context = context;
    }

    public boolean checkFirstRun() {
        final String PREFS_NAME = "PREFERENCES";
        final String PREF_VERSION_CODE_KEY = "VERSION";
        final int DOESNT_EXIST = -1;
        boolean isNormalRun, isNewInstall, isNewUpgrade;
        int currentVersionCode = BuildConfig.VERSION_CODE;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        isNormalRun = false;
        isNewInstall = false;
        isNewUpgrade = false;
        // check run history
        if (currentVersionCode == savedVersionCode) { //normal
            isNormalRun = true;
        }
        else if (savedVersionCode == DOESNT_EXIST) { // new install
            isNewInstall = true;
        }
        else if (currentVersionCode > savedVersionCode) { //upgrade
            isNewUpgrade = true;
        }
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return isNewInstall;
    }
}
