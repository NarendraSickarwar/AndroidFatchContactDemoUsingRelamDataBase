package com.chatserver.contactdemo.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ububtu on 26/7/17.
 * save the application data in shared preference
 */

public class PreferenceManager {
    public static void setFirstTime(Context context, boolean val) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PreferenceConstant.FILE_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(PreferenceConstant.APP_FIRST_TIME, val);
        editor.commit();
    }

    public static boolean getFirstTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PreferenceConstant.FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PreferenceConstant.APP_FIRST_TIME, false);
    }

    class PreferenceConstant {
        public static final String APP_FIRST_TIME = "first_time";
        public static final String FILE_NAME = "file_name";
    }
}
