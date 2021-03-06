package com.heavymagikhq.lbc_player_ultimate.lbcStreamingService;

import android.os.Binder;

public class RadioStreamingServiceBinder extends Binder {

    public RadioStreamingService mRadioStreamingService;

    public RadioStreamingServiceBinder(RadioStreamingService radioStreamingService){

        this.mRadioStreamingService = radioStreamingService;
    }

    /**
     * returns the mRadioStreamingService
     * @return - mRadioStreamingService
     */
    public RadioStreamingService getRadioStreamingService(){

        return mRadioStreamingService;
    }
}
