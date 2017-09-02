package com.heavymagikhq.lbc_player.lbcService;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.heavymagikhq.lbc_player.lbcExoPlayer.LBCExoPlayer;


/**
 * Created by Micah on 13/08/2017.
 */

public class RadioStreamingService extends Service {

    private RadioStreamingServiceBinder mBinder;
    private final String TAG = RadioStreamingService.class.getSimpleName();
    private LBCExoPlayer lbcExoPlayer;
    private PlayerControlsBroadcastReceiver playerControlsBR;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initLbcExoplayer();

        initPlayerControlsBroadcastReceiver();

        mBinder = new RadioStreamingServiceBinder(this);

        startForegroundLBCPlayer();

        return START_STICKY;
    }

    private void initPlayerControlsBroadcastReceiver() {

        playerControlsBR = new PlayerControlsBroadcastReceiver(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerControlsBroadcastReceiver.PAUSE_LBC_ACTION);
        intentFilter.addAction(PlayerControlsBroadcastReceiver.PLAY_LBC_ACTION);

        registerReceiver(playerControlsBR, intentFilter);
    }

    private void startForegroundLBCPlayer() {

        Notification playerNotification = PlayerNotification.build(this);

        startForeground(1, playerNotification);
    }


    private void initLbcExoplayer() {

        lbcExoPlayer = new LBCExoPlayer(this);
    }


    public LBCExoPlayer getLbcExoPlayer() {
        return lbcExoPlayer;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopSelf();

        lbcExoPlayer.pauseLBC();

        lbcExoPlayer = null;

        mBinder = null;
    }
}



