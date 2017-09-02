package com.nisoft.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/2.
 */

public class NotificationReceiver extends BroadcastReceiver {
    public static final String TAG = "NotificationBroadcast";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received result: "+getResultCode());
    }
}
