package com.heavymagikhq.lbc_player_ultimate.radioControlsBR;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.heavymagikhq.lbc_player_ultimate.lbcStreamingService.RadioStreamingService;

/**
 * Created by Micah on 13/08/2017.
 */

public class PlayerControlsBroadcastReceiver extends BroadcastReceiver {

    public static final String PAUSE_LBC_ACTION_KEY = "PAUSE_LBC_ACTION_KEY";
    public static final String PLAY_LBC_KEY = "PLAY_LBC_KEY";
    private RadioStreamingService radioStreamingService;

    public PlayerControlsBroadcastReceiver(RadioStreamingService radioStreamingService) {

        this.radioStreamingService = radioStreamingService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //get the type of control being requested
        final String actionType = intent.getAction();

        //check if we should pause the lbc player
        if (actionType.equals(PAUSE_LBC_ACTION_KEY))
            radioStreamingService.pauseLBC();


        //check if we should play the lbc player
        else if (actionType.equals(PLAY_LBC_KEY))
            radioStreamingService.resumeLastStartedPlayer();
    }

    /**
     * generates and returns an intentFilter with all of the acitons that
     * the PlayerControlsBroadcastReceiver will listen to and handle
     *
     * @return - an intentFilter with the actions this broadcaster will response to
     */
    public IntentFilter generatePlayerControlsBroadcastReceiverIntentFilter(){

        //create the intentFilter to intercept player controls
        IntentFilter playerControlsBroadcastReceiverIntentFilter = new IntentFilter();

        //create player control actions:
        playerControlsBroadcastReceiverIntentFilter.addAction(PAUSE_LBC_ACTION_KEY);
        playerControlsBroadcastReceiverIntentFilter.addAction(PLAY_LBC_KEY);

        return playerControlsBroadcastReceiverIntentFilter;
    }
}
