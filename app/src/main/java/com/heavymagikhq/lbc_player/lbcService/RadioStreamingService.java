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
    private LBCExoPlayer mLbcExoPlayer;
    private PlayerControlsBroadcastReceiver playerControlsBR;

    //MARK: ---------- LIFECYCLE & GETTERS

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopSelf();

        mLbcExoPlayer.pauseLBC();

        mLbcExoPlayer = null;

        mBinder = null;
    }

    //MARK: ---------- LIFECYCLE & GETTERS

    //MARK: ---------- INITIALISATION

    /**
     * inits the mLbcExoPlayer
     */
    private void initLbcExoplayer() {

        mLbcExoPlayer = new LBCExoPlayer(this);
    }

    /**
     * creates a PlayerControlsBroadcastReceiver,
     * registers the pause and play actions
     * and then registers it
     */
    private void initPlayerControlsBroadcastReceiver() {

        playerControlsBR = new PlayerControlsBroadcastReceiver(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerControlsBroadcastReceiver.PAUSE_LBC_ACTION);
        intentFilter.addAction(PlayerControlsBroadcastReceiver.PLAY_LBC_ACTION);

        registerReceiver(playerControlsBR, intentFilter);
    }

    /**
     * makes the the service a foreground service and assigns it a
     * PlayerNotification to give it the radio player controls
     */
    private void startForegroundLBCPlayer() {

        Notification playerNotification = PlayerNotification.build(this);

        startForeground(1, playerNotification);
    }

    //MARK: ---------- INITIALISATION

    //MARK: ---------- GETTERS

    public LBCExoPlayer getLbcExoPlayer() {
        return mLbcExoPlayer;
    }

    //MARK: ---------- GETTERS
}



