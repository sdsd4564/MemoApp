package hanbat.encho.com.clipboardmake;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class MemoBootStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent mIntent = new Intent(context, MemoService.class);
            context.startService(mIntent);

            PendingIntent mPendingIntent = PendingIntent.getActivity(context,
                    0, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
            Notification.Builder mBuilder = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(context.getString(R.string.notification_head))
                    .setContentText(context.getString(R.string.notification_body))
                    .setAutoCancel(false)
                    .setOngoing(true);

            mBuilder.setContentIntent(mPendingIntent);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (PropertyManager.getInstance().getNotificationSetting()) {
                notificationManager.notify(MainActivity.NOTIFICATION_ID, mBuilder.build());
            }
        }
    }
}
