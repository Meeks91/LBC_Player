package com.heavymagikhq.lbc_player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.heavymagikhq.lbc_player.lbcExoPlayer.LBCExoPlayer;
import com.heavymagikhq.lbc_player.lbcService.RadioStreamingService;
import com.heavymagikhq.lbc_player.lbcService.RadioStreamingServiceBinder;

public class MainActivity extends AppCompatActivity {

 private final String TAG  = MainActivity.class.getSimpleName();
 private ServiceConnection connection = new ServiceConnection() {
     @Override
     public void onServiceConnected(ComponentName name, IBinder service) {

         Log.d(TAG, "onCreate: service is: " + service.getClass().getSimpleName());

         if (service instanceof RadioStreamingServiceBinder){

             Log.d(TAG, "onCreate: service instanceof RadioStreamingService.RadioStreamingServiceBinder == true");

             bindExoPlayerToExoPlayerView(((RadioStreamingServiceBinder) service).getRadioStreamingService().getLbcExoPlayer());
         }
     }

     @Override
     public void onServiceDisconnected(ComponentName name) {

     }
 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       startService(new Intent(this, RadioStreamingService.class));

       bindService(new Intent(this, RadioStreamingService.class), connection, Context.BIND_AUTO_CREATE);
    }

   private void bindExoPlayerToExoPlayerView(LBCExoPlayer radioStreamingService){

        ((SimpleExoPlayerView) findViewById(R.id.exoPlayer)).setPlayer(radioStreamingService.getExoPlayer());
  }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindService(connection);
        stopService(new Intent(this, RadioStreamingService.class));
    }
}
























