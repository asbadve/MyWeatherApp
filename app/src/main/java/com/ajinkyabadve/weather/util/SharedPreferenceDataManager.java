package com.ajinkyabadve.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/***
 * class to save and retrieve data in the preferences
 *
 * @author spoton
 */
public class SharedPreferenceDataManager {
    private static final String TAG = SharedPreferenceDataManager.class.getSimpleName();
    public static final String SF_KEY_DEFAULT_CITY_ID = "defaultCityId";
    private Context context;

    public static final String SWAP_SELECTION = "swapOnOff";
    private SharedPreferences preferences = null;
    public static SharedPreferenceDataManager sharedPreferenceDataManager = null;


    /***
     * this is constructor for saving and retrieving the values from shared_preferences.
     *
     * @param context
     */
    public SharedPreferenceDataManager(Context context) {
        this.context = context;
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }


    /***
     * this is static function to create singleton object.
     *
     * @param context
     * @return
     */
    public static SharedPreferenceDataManager getInstance(Context context) {
        if (sharedPreferenceDataManager == null) {
            sharedPreferenceDataManager = new SharedPreferenceDataManager(context);
        }
        return sharedPreferenceDataManager;
    }

    /***
     * this will return saved preference having value as string
     *
     * @param key
     * @return
     */
    public String getSavedStringPreference(String key) {
        String value = preferences.getString(key, null);
        return value;
    }

    /***
     * this will return saved preference having value as boolean
     *
     * @param key
     * @return
     */
    public boolean getSavedBooleanPreference(String key) {
        boolean value = preferences.getBoolean(key, false);
        return value;
    }


    /***
     * this will return saved preference having value as boolean
     *
     * @param key
     * @return
     */
    public int getSavedIntPreference(String key) {
        int value = preferences.getInt(key, 0);
        return value;
    }


    /***
     * this will return saved preference having value as boolean
     *
     * @param key
     * @return
     */
    public long getSavedLongPreference(String key) {
        long value = preferences.getLong(key, 0);
        return value;
    }

    public float getSavedFloatPreference(String key) {
        float value = preferences.getFloat(key, 0f);
        return value;
    }

    /***
     * @param key
     * @param value
     */
    public void savePreference(String key, String value) {
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }


    /***
     * @param key
     * @param value
     */
    public void savePreference(String key, int value) {
        Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /***
     * @param key
     * @param value
     */
    public void savePreference(String key, long value) {
        Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /***
     * @param key
     * @param value
     */
    public void savePreference(String key, float value) {
        Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /***
     * @param key
     * @param value
     */
    public void savePreference(String key, boolean value) {
        Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /***
     * this will return saved preference having value as boolean
     *
     * @param key
     * @return
     */
    public int getSavedDefaultCityIdPreference(String key) {
        return preferences.getInt(key, 0);
    }


}
