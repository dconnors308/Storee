package com.example.stohre.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

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
            Log.i("NORMAL RUN","CALLED");
            isNormalRun = true;
        }
        else if (savedVersionCode == DOESNT_EXIST) { // new install
            Log.i("FIRST RUN","CALLED");
            isNewInstall = true;
        }
        else if (currentVersionCode > savedVersionCode) { //upgrade
            Log.i("UPGRADE RUN","CALLED");
            isNewUpgrade = true;
        }
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        return isNewInstall;
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
    }
}
