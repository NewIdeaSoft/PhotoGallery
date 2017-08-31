package com.nisoft.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2017/8/31.
 */

public class StartupReceiver extends BroadcastReceiver {
    private static final String TAG = "StartupReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received broadcast intent: "+intent.getAction());
        boolean isOn = QueryPreference.isAlarmOn(context);
        PollService.setServiceAlarm(context,isOn);
    }
}
