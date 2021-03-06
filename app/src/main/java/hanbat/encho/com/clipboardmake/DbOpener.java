package hanbat.encho.com.clipboardmake;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by USER on 2016-09-10.
 */
public class DbOpener {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 2;
    public static SQLiteDatabase mDB;
    private DB_Helper mHelper;
    private Context mContext;

    private static DbOpener f = null;

    public static DbOpener getInstance(Context context) {
        /* ----- Use Singleton ----- */
        if (f == null) {
            f = new DbOpener(context);
        }
        return f;
    }

    private class DB_Helper extends SQLiteOpenHelper {

        /* --------- 생성자 ----------- */
        public DB_Helper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        /* --------- 최초 DB 만들때 한 번만 호출됨 ---------- */
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(MemoDB._CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion) {
                case 1 : {
                    try {
                        db.beginTransaction();
                        db.execSQL("ALTER TABLE " + MemoDB._TABLENAME + " ADD COLUMN " + MemoDB.MARKED + " INTEGER DEFAULT 0");
                        db.setTransactionSuccessful();
                    } catch (IllegalStateException e) {
                        Log.e("디비오프너", e.toString());
                    } finally {
                        db.endTransaction();
                    }
                    break;
                }
            }
//            db.execSQL("DROP TABLE IF EXITS " + MemoDB._TABLENAME);
//            onCreate(db);
        }
    }

    public DbOpener(Context mContext) {
        this.mContext = mContext;
    }

    public DbOpener open() throws SQLException {
        mHelper = new DB_Helper(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDB.close();
    }

    public long insertColumn(String memo, int marked){
        ContentValues values = new ContentValues();
        values.put(MemoDB.MEMO, memo);
        values.put(MemoDB.MARKED, marked);
        return mDB.insert(MemoDB._TABLENAME, null, values);
    }
    public boolean updateColumn(long id, String memo, int marked) {
        ContentValues values = new ContentValues();
        values.put(MemoDB.MEMO, memo);
        values.put(MemoDB.MARKED, marked);
        return mDB.update(MemoDB._TABLENAME, values, "_id="+id, null) > 0;
    }
    public boolean deleteColumn(long id){
        return mDB.delete(MemoDB._TABLENAME, "_id="+id, null) > 0;
    }
    public Cursor getAllColumn() {
        return mDB.query(MemoDB._TABLENAME, null, null, null, null, null, null);
    }
    public Cursor getSingleColumn(long id) {
        Cursor c = mDB.query(MemoDB._TABLENAME, null, "_id="+id, null, null, null, null);
        if (c != null && c.getCount() != 0) c.moveToFirst();
        return c;
    }
}
