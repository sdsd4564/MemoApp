package hanbat.encho.com.clipboardmake;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
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
    private DbOpenner mOpenner;
    private String mPrevius = "";


    ClipboardManager.OnPrimaryClipChangedListener mListener = null;

    @Override
    public void onCreate() {
        super.onCreate();
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
                    Toast mToast = new Toast(Application.getMyContext());
                    mToast.setDuration(Toast.LENGTH_SHORT);
                    mToast.setGravity(Gravity.CENTER, 0, 0);
                    LayoutInflater mInflater = (LayoutInflater) Application.getMyContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = mInflater.inflate(R.layout.toast_view, null);
                    mToast.setView(view);
                    mToast.show();
                }
            }
        };
        manager.addPrimaryClipChangedListener(mListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            PendingIntent mPendingIntent = PendingIntent.getActivity(Application.getMyContext(),
                    0, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
            Notification.Builder mBuilder = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.notification_head))
                    .setContentText(getString(R.string.notification_body))
                    .setAutoCancel(false)
                    .setOngoing(true);

            mBuilder.setContentIntent(mPendingIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, mBuilder.build());

        }
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