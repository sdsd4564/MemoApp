package hanbat.encho.com.clipboardmake;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Encho on 2016-09-11.
 */
public class MemoService extends Service {

    //private ClipboardManager manager = null;
    private ClipData data = null;
    private static final String TAG = "메모 서비스";
    private DbOpenner mOpenner;
    private String mPrevius = "";
    private Cursor mCursor;
    private Entity mEntity;

    @Override
    public void onCreate() {
        super.onCreate();
        mOpenner = DbOpenner.getInstance(Application.getMyContext());

        Log.d(TAG, "온크리에이트");
        final ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        manager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                data = manager.getPrimaryClip();

                String Contents = data.getItemAt(0).coerceToText(Application.getMyContext()).toString();

                if (mPrevius.equals(Contents)) return;
                else if (!Contents.equals("")){
                    mOpenner.open();
                    mPrevius = Contents;
                    mOpenner.insertColumn(Contents);
                    Log.d(TAG, Contents);
                    mOpenner.close();
                    Toast mToast = Toast.makeText(Application.getMyContext(), R.string.message_when_add, Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    mToast.show();
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }
}
