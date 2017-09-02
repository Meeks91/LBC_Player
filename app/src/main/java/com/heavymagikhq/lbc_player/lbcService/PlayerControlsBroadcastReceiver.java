package com.heavymagikhq.lbc_player.lbcService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.heavymagikhq.lbc_player.lbcService.RadioStreamingService;

/**
 * Created by Micah on 13/08/2017.
 */

public class PlayerControlsBroadcastReceiver extends BroadcastReceiver {

    public static final String PAUSE_LBC_ACTION = "PAUSE_LBC_ACTION";
    public static final String PLAY_LBC_ACTION = "PLAY_LBC_ACTION";
    public static final String CONTROL_TYPE_KEY = "CONTROL_TYPE_KEY";
    public static final String PAUSE_LBC_KEY = "PAUSE_LBC_KEY";
    public static final String PLAY_LBC_KEY = "PLAY_LBC_KEY";
    private RadioStreamingService radioStreamingService;

    public PlayerControlsBroadcastReceiver(RadioStreamingService radioStreamingService) {

        this.radioStreamingService = radioStreamingService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getStringExtra(CONTROL_TYPE_KEY).equals(PAUSE_LBC_KEY)){

            radioStreamingService.getLbcExoPlayer().pauseLBC();
        }

        else if (intent.getStringExtra(CONTROL_TYPE_KEY).equals(PLAY_LBC_KEY)){

            radioStreamingService.getLbcExoPlayer().resumeLBC();
        }
    }
}
