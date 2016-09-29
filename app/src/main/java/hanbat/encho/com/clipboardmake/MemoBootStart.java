package hanbat.encho.com.clipboardmake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by USER on 2016-09-29.
 */

public class MemoBootStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent mIntent = new Intent(context, MemoService.class);
            context.startService(mIntent);
        }
    }
}
