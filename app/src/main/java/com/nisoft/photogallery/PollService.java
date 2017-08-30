package com.nisoft.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class PollService extends IntentService {
    private static final String TAG = "PollService";
    private static final int POLL_INTERAL = 1000*60;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PollService() {
        super(TAG);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!isNetWorkAvailableAndConnected()) {
            return;
        }
        String query = QueryPreference.getStoredQuery(this);
        String lastResultId = QueryPreference.getLastResultId(this);
        List<GalleryItem> items;
        if (query == null) {
            items = new ArrayList<>();
        } else {
            items = new FlickrFetchr().fetchSearchItems(query);
        }
        if (items.size() == 0) {
            return;
        }
        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got an old result: " + resultId);
        } else {
            Log.i(TAG, "Got a new result: " + resultId);
        }
        QueryPreference.setLastResultId(this, resultId);

    }

    private boolean isNetWorkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetWorkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetWorkConnected = isNetWorkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetWorkConnected;
    }
    public static void setServiceAlarm(Context context,boolean isOn){
        Intent i = PollService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context,0,i,0);
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn){
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(),POLL_INTERAL,pi);
        }else{
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }
}
