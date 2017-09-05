package com.nisoft.photogallery;

import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/2.
 */

public class NotificationReceiver extends BroadcastReceiver {
    public static final String TAG = "NotificationBroadcast";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received result: "+getResultCode());
        if(getResultCode()!= Activity.RESULT_OK){
            return;
        }
        int requestCode = intent.getIntExtra(PollService.REQUEST_CODE,0);
        Notification notification = intent.getParcelableExtra(PollService.NOTIFICATION);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode,notification);
    }

}
