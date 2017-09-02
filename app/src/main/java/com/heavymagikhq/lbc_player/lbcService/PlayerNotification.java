package com.heavymagikhq.lbc_player.lbcService;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.heavymagikhq.lbc_player.R;

public class PlayerNotification{

    public static Notification build(Context context){

        Intent notificationIntent = new Intent(context, RadioStreamingService.class);

        NotificationCompat.Action pauseAction = createPauseAction(context);

        NotificationCompat.Action playAction = createPlayAction(context);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.lbc_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.lbc_icon))
                .setContentText("The people's LBC player")
                .addAction(pauseAction)
                .addAction(playAction)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setContentIntent(pendingIntent)
                .build();
    }

    private static NotificationCompat.Action createPlayAction(Context context) {

        Intent playLBCIntent = new Intent(PlayerControlsBroadcastReceiver.PLAY_LBC_ACTION);

        playLBCIntent.putExtra(PlayerControlsBroadcastReceiver.CONTROL_TYPE_KEY, PlayerControlsBroadcastReceiver.PLAY_LBC_KEY);

        return new NotificationCompat.Action(R.drawable.play_icon,
                                                "More LBC", PendingIntent.getBroadcast(context,
                                                     0, playLBCIntent,
                                                         PendingIntent.FLAG_UPDATE_CURRENT));
    }

    private static NotificationCompat.Action createPauseAction(Context context) {

        Intent pauseLBCIntent = new Intent(PlayerControlsBroadcastReceiver.PAUSE_LBC_ACTION);

        pauseLBCIntent.putExtra(PlayerControlsBroadcastReceiver.CONTROL_TYPE_KEY, PlayerControlsBroadcastReceiver.PAUSE_LBC_KEY);

        return new NotificationCompat.Action(R.mipmap.ic_launcher,
                                        "No More LBC", PendingIntent.getBroadcast(context,
                                             0, pauseLBCIntent,
                                                 PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
