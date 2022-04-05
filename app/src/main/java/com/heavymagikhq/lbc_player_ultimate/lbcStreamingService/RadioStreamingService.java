package com.heavymagikhq.lbc_player_ultimate.lbcStreamingService;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.heavymagikhq.lbc_player_ultimate.lbcExoPlayer.LbcExoPlayer;
import com.heavymagikhq.lbc_player_ultimate.radioControlsBR.PlayerControlsBroadcastReceiver;
import com.heavymagikhq.lbc_player_ultimate.radioControlsBR.PlayerNotification;

/**
 * Created by Micah on 13/08/2017.
 */

public class RadioStreamingService extends Service {

    private RadioStreamingServiceBinder mBinder;
    private final String TAG = RadioStreamingService.class.getSimpleName();
    private LbcExoPlayer mLbcExoPlayer;
    private PlayerControlsBroadcastReceiver playerControlsBR;

    // MARK: ---------- LIFECYCLE

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initPlayerControlsBroadcastReceiver();

        mBinder = new RadioStreamingServiceBinder(this);

        startForegroundLBCPlayer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLbcExoPlayer.pauseLBC();
        mLbcExoPlayer.release();
        mLbcExoPlayer = null;
        mBinder = null;

        unregisterReceiver(playerControlsBR);
        stopSelf();
    }

    // MARK: ---------- LIFECYCLE

    // MARK: ---------- INITIALISATION

    /**
     * creates a PlayerControlsBroadcastReceiver,
     * registers the pause and play actions
     * and then registers it
     */
    private void initPlayerControlsBroadcastReceiver() {
        playerControlsBR = new PlayerControlsBroadcastReceiver(this);
        IntentFilter playerControlsBRIntentFilter = playerControlsBR.generatePlayerControlsBroadcastReceiverIntentFilter();
        registerReceiver(playerControlsBR, playerControlsBRIntentFilter);
    }

    /**
     * makes the the service a foreground service and assigns it a
     * PlayerNotification to give it the radio player controls
     */
    public void startForegroundLBCPlayer() {
        Notification playerNotification = PlayerNotification.build(this);
        startForeground(1, playerNotification);
    }

    // MARK: ---------- INITIALISATION

    // MARK: ---------- CONTROLS

    public void pauseLBC() {
        getLbcPlayer().pauseLBC();
    }

    public void resumeLastStartedPlayer() {
            getLbcPlayer().resumeLBC();
    }

    // MARK: ---------- CONTROLS

    // MARK: ---------- GETTERS & SETTERS

    public LbcExoPlayer getLbcPlayer() {
        return mLbcExoPlayer;
    }

    public void setLbcPlayer(LbcExoPlayer player) {
         this.mLbcExoPlayer = player;
    }

    // MARK: ---------- GETTERS & SETTERS
}



