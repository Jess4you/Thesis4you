package com.jess.thesisforyou.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jess.thesisforyou.MainActivity;

/**
 * Created by USER on 2/9/2019.
 *
 * This receiver is used to start the background service for the application lock
 */

public class BootReceiver extends BroadcastReceiver {

    public static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"onReceive: START");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
        Log.d(TAG,"onReceive: FINISH");
    }
}
