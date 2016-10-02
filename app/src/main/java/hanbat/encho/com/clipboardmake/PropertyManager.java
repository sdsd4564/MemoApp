package hanbat.encho.com.clipboardmake;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Encho on 2016-10-02.
 */

public class PropertyManager {
    private static PropertyManager f = null;

    public static PropertyManager getInstance() {
        if (f == null) {
            f = new PropertyManager();
        }
        return f;
    }
    SharedPreferences mPref;
    SharedPreferences.Editor mEdit;

    private PropertyManager() {
        mPref = PreferenceManager.getDefaultSharedPreferences(Application.getMyContext());
        mEdit = mPref.edit();
    }

    private static final String NOTIFICATION_SETTING = "notification";

    public void setNotificationSetting(boolean isNotificationOn) {
        mEdit.putBoolean(NOTIFICATION_SETTING, isNotificationOn);
        mEdit.commit();
    }
    public boolean getNotificationSetting(){
        return mPref.getBoolean(NOTIFICATION_SETTING, true);
    }
}
