package com.heavymagikhq.lbc_player.lbcExoPlayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * Created by Micah on 13/08/2017.
 */

public class LBCExoPlayer {

    private final String TAG =LBCExoPlayer.class.getSimpleName();
    private DefaultHttpDataSourceFactory dataSourceFactory;
    private ExtractorMediaSource mediaSource;
    private DefaultBandwidthMeter bandwidthMeter;
    private AdaptiveTrackSelection.Factory videoTrackSelectionFactory;
    private DefaultTrackSelector trackSelector;
    private DefaultLoadControl loadControl;
    private SimpleExoPlayer mExoPlayer;

    public LBCExoPlayer(Context context){

        initExoPlayer(context);
    }

    private void initExoPlayer(Context context) {

        Handler mHandler = new Handler();

        String userAgent = "yourApplicationName";

        Uri uri = Uri.parse("http://media-ice.musicradio.com/LBCLondonMP3");

        dataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                1800000,
                true);

        mediaSource = new ExtractorMediaSource(uri,dataSourceFactory, Mp3Extractor.FACTORY,
                mHandler, null);

        bandwidthMeter = new DefaultBandwidthMeter();


        videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        loadControl = new DefaultLoadControl();

        mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        mExoPlayer.getCurrentTimeline();

        mExoPlayer.prepare(mediaSource);
    }

    public void resumeLBC(){

        Log.d(TAG, "resumeLBC() called");

        mExoPlayer.setPlayWhenReady(true);
    }

    public void pauseLBC(){

        Log.d(TAG, "pauseLBC() called");

       mExoPlayer.setPlayWhenReady(false);
    }

    public SimpleExoPlayer getExoPlayer() {

        return mExoPlayer;
    }

}
