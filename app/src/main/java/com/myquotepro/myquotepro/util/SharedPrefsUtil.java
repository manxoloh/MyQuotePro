package com.myquotepro.myquotepro.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created  on 7/4/2017.
 */

public class SharedPrefsUtil {
    private static final String SHARED_PREFER_FILE_NAME = "keys";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    /**
     * Retrieve data from preference:
     */

    public SharedPrefsUtil(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(SHARED_PREFER_FILE_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }

    public void saveFirebaseRegistrationID(String firebaseRegId) {
        editor.putString("regId", firebaseRegId);
        editor.commit();
    }

    public String getFirebaseRegistrationID() {
        return pref.getString("regId", null);
    }

    public void saveIsFirstTime(boolean isFirstTime) {
        editor.putBoolean("firstTime", isFirstTime);
        editor.commit();
    }

    public boolean getIsFirstTime() {
        return pref.getBoolean("firstTime", false);
    }
}
