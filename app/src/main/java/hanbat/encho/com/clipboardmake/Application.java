package hanbat.encho.com.clipboardmake;

import android.content.Context;

/**
 * Created by Encho on 2016-09-11.
 */
public class Application extends android.app.Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getMyContext(){
        return mContext;
    }
}
