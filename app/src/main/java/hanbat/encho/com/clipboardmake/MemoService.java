package hanbat.encho.com.clipboardmake;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Encho on 2016-09-11.
 */
public class MemoService extends Service {

    private ClipboardManager manager = null;
    private ClipData data = null;
    private static final String TAG = "메모 서비스";
    private DbOpenner mOpenner;
    private String mPrevius = "";


    ClipboardManager.OnPrimaryClipChangedListener mListener = null;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "tasdf");
        mOpenner = DbOpenner.getInstance(Application.getMyContext());

        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        mListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                data = manager.getPrimaryClip();

                String Contents = data.getItemAt(0).coerceToText(Application.getMyContext()).toString();

                if (mPrevius.equals(Contents)) return;
                else if (!Contents.equals("")) {
                    mOpenner.open();
                    mPrevius = Contents;
                    mOpenner.insertColumn(Contents);
                    mOpenner.close();
                    Toast mToast = Toast.makeText(Application.getMyContext(), "메모에 추가되었습니다", Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    mToast.show();
                }
            }
        };
        manager.addPrimaryClipChangedListener(mListener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.removePrimaryClipChangedListener(mListener);
    }
}