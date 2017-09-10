package com.heavymagikhq.lbc_player.radioControlsBR;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.heavymagikhq.lbc_player.LBCPlayerActivity;
import com.heavymagikhq.lbc_player.R;

public class PlayerNotification{

    /**
     * creates and returns a Notification that's configured
     * with actions to pause and play the LBC player
     *
     * @param context - context used to create the actions
     * @return - Notification with the LBC controls
     */
    public static Notification build(Context context){

        //create intent of where the PendingIntent should send the intent to
        Intent notificationIntent = new Intent(context, LBCPlayerActivity.class);

        //create action to pause the LBC player
        NotificationCompat.Action pauseAction = createPauseAction(context);

        //create action to play the LBC player
        NotificationCompat.Action playAction = createPlayAction(context);

        //Make pending intent to pass the notificationIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //create the NotificationCompat to show the LBC controls and keep it in the forefront
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.lbc_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.colour_lbc_icon))
                .setContentText("LBC Player")
                .addAction(pauseAction)
                .addAction(playAction)
                .setPriority(Notification.PRIORITY_MAX)
                .setWhen(0)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * creates and returns an action to play the LBC player
     *
     * @param context - used to create the action
     * @return - an action to play the LBC player
     */
    private static NotificationCompat.Action createPlayAction(Context context) {

        //Create the action so it can be routed by the broadcast receiver
        Intent playLBCIntent = new Intent(PlayerControlsBroadcastReceiver.PLAY_LBC_ACTION_KEY);

        //create and return the NotificationCompat.Action
        return new NotificationCompat.Action(R.drawable.play_icon,
                                                "More LBC",
                                                  PendingIntent.getBroadcast(context,
                                                     0, playLBCIntent,
                                                         PendingIntent.FLAG_UPDATE_CURRENT));
    }

    /**
     * creates and returns an action to pause the LBC player
     *
     * @param context - used to create the action
     * @return - an action to pause the LBC player
     */
    private static NotificationCompat.Action createPauseAction(Context context) {

        //Create the action so it can be routed by the broadcast receiver
        Intent pauseLBCIntent = new Intent(PlayerControlsBroadcastReceiver.PAUSE_LBC_ACTION_KEY);

        //create and return the NotificationCompat.Action
        return new NotificationCompat.Action(R.drawable.pause_icon,
                                        "No More LBC",
                                           PendingIntent.getBroadcast(context,
                                             0, pauseLBCIntent,
                                                 PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
