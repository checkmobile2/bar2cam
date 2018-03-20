package com.lampa.bar2cam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by User on 19.03.2018.
 */

public class BarBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION_OPEN = "android.intent.action.OPEN_BARCODE_RFID";
    public static final String ACTION_CLOSE = "android.intent.action.CLOSE_BARCODE_RFID";
    public static final String ACTION_SCAN = "ACTION_KE_START";
    private static Thread scanThread;

    private static final String TAG = "KeyBroadcastReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "action: " + intent.getAction());

        String action = intent.getAction();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d(TAG, String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
            }
        }

        if("com.rscja.android.KEY_DOWN".equals(action)) {
            Intent intentBar = new Intent(ACTION_OPEN);
            context.sendBroadcast(intentBar);

            intentBar = new Intent(ACTION_SCAN);
            context.sendBroadcast(intentBar);

            if(scanThread != null) {
                scanThread.interrupt();
            }

            scanThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        Intent intentBar = new Intent(ACTION_CLOSE);
                        context.sendBroadcast(intentBar);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            scanThread.start();
        }
        else if("com.chengwei.scanled.statuschanged".equals(action)) {
            if(intent.hasExtra("value")) {
                int value = intent.getIntExtra("value", 0);

                if(value == 255) {
                    Intent intentBar = new Intent(ACTION_CLOSE);
                    context.sendBroadcast(intentBar);

                    if(scanThread != null) {
                        scanThread.interrupt();
                    }
                }
            }
        }
    }
}
