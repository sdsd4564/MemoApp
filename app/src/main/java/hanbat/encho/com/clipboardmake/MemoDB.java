package hanbat.encho.com.clipboardmake;

import android.provider.BaseColumns;

/**
 * Created by USER on 2016-09-10.
 */
public class MemoDB implements BaseColumns {

    public static final String MEMO = "memo";
    public static final String _TABLENAME = "address";
    public static final String MARKED = "marked";
    public static final String _CREATE =
            "CREATE TABLE " + _TABLENAME + "("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MEMO + " TEXT NOT NULL, "
                    + MARKED + " INTEGER DEFAULT 0 );";
}
