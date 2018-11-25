package com.heavymagikhq.lbc_player_ultimate.lbcStreamingService;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.LBCPlayerActivity;
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
    private LbcExoPlayer podcastExoPlayer;

    //MARK: ---------- LIFECYCLE

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initLbcExoPlayerWith(
                intent.getStringExtra(LBCPlayerActivity.PODCAST_URI_KEY)
        );

        initPlayerControlsBroadcastReceiver();

        mBinder = new RadioStreamingServiceBinder(this);

        startForegroundLBCPlayer();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLbcExoPlayer.pauseLBC();
        if (podcastExoPlayer != null)
            podcastExoPlayer.pauseLBC();

        mLbcExoPlayer = null;
        podcastExoPlayer = null;
        mBinder = null;

        unregisterReceiver(playerControlsBR);
        stopSelf();
    }

    //MARK: ---------- LIFECYCLE

    //MARK: ---------- INITIALISATION

    /**
     * inits the mLbcExoPlayer
     */
    private void initLbcExoPlayerWith(String podcastUrl) {
        mLbcExoPlayer = new LbcExoPlayer(this, podcastUrl);
    }

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
    private void startForegroundLBCPlayer() {
        Notification playerNotification = PlayerNotification.build(this);
        startForeground(1, playerNotification);
    }

    //MARK: ---------- INITIALISATION

    //MARK: ---------- CONTROLS

    public void pauseLBC() {
        if (getPodcastExoPlayer() != null)
            getPodcastExoPlayer().pauseLBC();
        getLiveLbcExoPlayer().pauseLBC();
    }

    public void resumeLastStartedPlayer() {
        if (getPodcastExoPlayer() != null && getPodcastExoPlayer().getLastTimeStarted() > getLiveLbcExoPlayer().getLastTimeStarted())
            getPodcastExoPlayer().resumeLBC();
        else
            getLiveLbcExoPlayer().resumeLBC();
    }

    //MARK: ---------- CONTROLS

    //MARK: ---------- GETTERS & SETTERS

    @NonNull
    public LbcExoPlayer getLiveLbcExoPlayer() {
        return mLbcExoPlayer;
    }

    @Nullable
    public LbcExoPlayer getPodcastExoPlayer() {
        return podcastExoPlayer;
    }

    public void setPodcastPlayer(@NonNull LbcExoPlayer podcastExoPlayer) {
        this.podcastExoPlayer = podcastExoPlayer;
    }

    //MARK: ---------- GETTERS & SETTERS
}



