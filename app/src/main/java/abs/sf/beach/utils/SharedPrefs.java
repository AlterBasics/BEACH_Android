package abs.sf.beach.utils;

import android.content.Context;
import android.content.SharedPreferences;

import abs.ixi.client.util.StringUtils;
import abs.sf.beach.core.BasicApplication;

/**
 * As the name suggests, {@code SharedPrefs} is a proxy interface to
 * Android {@code SharedPreferences}. This is a singleton class.
 * <p>
 * All the preferences stored using this class are private.
 * </p>
 */
public final class SharedPrefs {
    private static final String BEACH_SHARED_PREF = "beach-user-pref";

    private Context context;
    private SharedPreferences systemPrefs;
    private SharedPreferences.Editor editor;

    public static SharedPrefs instance;


    /**
     * Restricting access to local
     */
    private SharedPrefs() {
        this.context = BasicApplication.getContext();
        this.systemPrefs = context.getSharedPreferences(BEACH_SHARED_PREF, context.MODE_PRIVATE);
        this.editor = systemPrefs.edit();

    }

    /**
     * Returns the singleton instance of {@code SharedPrefs}
     *
     * @return
     */
    public static SharedPrefs getInstance() {
        if (instance == null) {
            instance = new SharedPrefs();
        }

        return instance;
    }

    /**
     * Stores value into user shared preferences
     *
     * @param key
     * @param val
     */
    public void put(String key, String val) {
        this.editor.putString(key, val);
        this.editor.commit();
    }

    public String get(String key) {
        return systemPrefs.getString(key, StringUtils.EMPTY);
    }

    public void remove(String key) {
        this.editor.remove(key);
        this.editor.apply();
    }

    /**
     * Removes all the preferences from the object it is editing.
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * Removes all the preferences from the object it is editing except FCM token
     */
    public void clearPartially() {
        this.clear();
    }

    public void setUsername(String val) {
        this.editor.putString(ApplicationProps.USERNAME, val);
        this.editor.commit();
    }

    public String getUsername() {
        return systemPrefs.getString(ApplicationProps.USERNAME, StringUtils.EMPTY);
    }

    public void setPassword(String val) {
        this.editor.putString(ApplicationProps.PASSWORD, val);
        this.editor.commit();
    }

    public String getPassword() {
        return systemPrefs.getString(ApplicationProps.PASSWORD, StringUtils.EMPTY);
    }


    public void setLoginStatus(boolean status) {
        this.editor.putBoolean(ApplicationProps.LOGIN_STATUS, status);
        this.editor.commit();
    }

    public boolean getLoginStatus() {
        return systemPrefs.getBoolean(ApplicationProps.LOGIN_STATUS, false);
    }

}
