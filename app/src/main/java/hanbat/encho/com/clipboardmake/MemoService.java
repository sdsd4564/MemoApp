package hanbat.encho.com.clipboardmake;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Encho on 2016-09-11.
 */
public class MemoService extends Service {

    private ClipboardManager manager = null;
    private ClipData data = null;
    private static final String TAG = "메모 서비스";
    private DbOpener mOpener;
    private String mPrevious = "";


    ClipboardManager.OnPrimaryClipChangedListener mListener = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mOpener = DbOpener.getInstance(Application.getMyContext());

        manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);


        mListener = new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                data = manager.getPrimaryClip();

                if (data.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML) ||
                        data.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    String Contents = data.getItemAt(0).coerceToText(Application.getMyContext()).toString();

                    if (mPrevious.equals(Contents)) return;
                    else if (!Contents.equals("")) {
                        mOpener.open();
                        mPrevious = Contents;
                        mOpener.insertColumn(Contents, 0);
                        mOpener.close();
                        Toast mToast = new Toast(Application.getMyContext());
                        mToast.setDuration(Toast.LENGTH_LONG);
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                        LayoutInflater mInflater = (LayoutInflater) Application.getMyContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = mInflater.inflate(R.layout.toast_view, null);
                        mToast.setView(view);
                        mToast.show();
                    }
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