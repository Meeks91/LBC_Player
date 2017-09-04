package com.heavymagikhq.lbc_player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.heavymagikhq.lbc_player.lbcExoPlayer.LBCExoPlayer;
import com.heavymagikhq.lbc_player.lbcService.RadioStreamingService;
import com.heavymagikhq.lbc_player.lbcService.RadioStreamingServiceBinder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.exoPlayer) SimpleExoPlayerView mExoPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //start the RadioStreamingService
        startService(new Intent(this, RadioStreamingService.class));

        //bind to the RadioStreamingService so we can bind the mExoPlayerView to the exoPlayer in the service
        bindService(new Intent(this, RadioStreamingService.class), connectionToRadioStreamingService, Context.BIND_AUTO_CREATE);
    }

    /**
     * binds the the mExoPlayerView to the radioStreamingService
     *
     * @param radioStreamingService - the service which contains the exoPlayer which plays LBC
     */
    private void bindExoPlayerToExoPlayerView(LBCExoPlayer radioStreamingService) {

        mExoPlayerView.setPlayer(radioStreamingService.getExoPlayer());
    }

    private ServiceConnection connectionToRadioStreamingService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            //ensure the service is the RadioStreamingServiceBinder
            if (service instanceof RadioStreamingServiceBinder)

                //bind the exoPlayerView to the exoPlayer in the RadioStreamingService
                bindExoPlayerToExoPlayerView(((RadioStreamingServiceBinder) service).getRadioStreamingService().getLbcExoPlayer());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //free resources:
        unbindService(connectionToRadioStreamingService);
        stopService(new Intent(this, RadioStreamingService.class));
    }
}