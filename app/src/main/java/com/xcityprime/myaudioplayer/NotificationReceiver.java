package com.xcityprime.myaudioplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.xcityprime.myaudioplayer.ApplicationClass.CLOSE_N;
import static com.xcityprime.myaudioplayer.ApplicationClass.Next;
import static com.xcityprime.myaudioplayer.ApplicationClass.PREV;
import static com.xcityprime.myaudioplayer.ApplicationClass.Play;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String actionName = intent.getAction();
        Intent serviceIntent = new Intent(context, MusicService.class);
        if (actionName != null){
            switch (actionName){
                case Play:
                    serviceIntent.putExtra("ActionName", "play");
                    context.startService(serviceIntent);
                    break;
                case Next:
                    serviceIntent.putExtra("ActionName", "Next");
                    context.startService(serviceIntent);
                    break;
                case PREV:
                    serviceIntent.putExtra("ActionName", "previous");
                    context.startService(serviceIntent);
                    break;
                case CLOSE_N:
                    serviceIntent.putExtra("ActionName", "close_n");
                    context.startService(serviceIntent);
                    break;
            }
        }
    }
}

