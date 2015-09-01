package io.bqbl.utils;

import android.content.Context;
import android.content.SharedPreferences;

import io.bqbl.R;

public final class SharedPreferencesUtils {

  public static final int NULL_INT_VALUE = -1;

  private static final String PREF_FILE_NAME = "bqblio_prefs";

  private SharedPreferencesUtils() {
  }

  public static boolean putInt(Context context, int keyResource, int value) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putInt(context.getString(keyResource), value);
    return editor.commit();
  }

  public static int getInt(Context context, int keyResource, int defaultResource) {
    SharedPreferences prefs = getSharedPreferences(context);
    return prefs.getInt(context.getString(keyResource), NULL_INT_VALUE);
  }

  public static boolean putString(Context context, int keyResource, String value) {
    SharedPreferences.Editor editor = getSharedPreferences(context).edit();
    editor.putString(context.getString(keyResource), value);
    return editor.commit();
  }

  public static String getString(Context context, int keyResource, int defaultResource) {
    SharedPreferences prefs = getSharedPreferences(context);
    return prefs.getString(context.getString(keyResource), context.getString(defaultResource));
  }

  public static int getCurrentUserId(Context context) {
    return getInt(context, R.string.pref_current_user, R.string.pref_current_user_default);
  }
  private static SharedPreferences getSharedPreferences(Context context) {
    return context.getSharedPreferences(context.getString(R.string.prefs_file_name), Context.MODE_PRIVATE);
  }
}
