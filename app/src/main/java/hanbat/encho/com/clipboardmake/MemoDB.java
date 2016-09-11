package hanbat.encho.com.clipboardmake;

import android.provider.BaseColumns;

/**
 * Created by USER on 2016-09-10.
 */
public class MemoDB implements BaseColumns {

    public static final String MEMO = "memo";
    public static final String _TABLENAME = "address";
    public static final String _CREATE = "create table " + _TABLENAME + "("
            + _ID + " integer primary key autoincrement, "
            + MEMO + " text not null );";
}
