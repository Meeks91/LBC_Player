package com.heavymagikhq.lbc_player.lbcExoPlayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

/**
 * Created by Micah on 13/08/2017.
 */

public class LBCExoPlayer {

    private final String userAgent = "yourApplicationName";
    private final Uri uri = Uri.parse("http://media-ice.musicradio.com/LBCLondonMP3");
    private final String TAG =LBCExoPlayer.class.getSimpleName();
    private SimpleExoPlayer mExoPlayer;

    public LBCExoPlayer(Context context){

        initExoPlayer(context);
    }

    //MARK: ---------------- INITIALISATION

    /**
     * inits the mExoPlayer by bringing together the components
     * required to create it
     *
     * @param context
     */
    private void initExoPlayer(Context context) {

        //create the player
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(context, generateTrackSelector());

        //get a dataSource which is able process the LBC online stream
        DefaultHttpDataSourceFactory httpDataSourceFactory = generateDataSourceFactory();

        //get a data source pointing to the LBC online stream and
        //assign the httpDataSourceFactory to process the stream
        MediaSource lbcMediaSource = generateMediaSource(httpDataSourceFactory);

        //assign the lbcMediaSource so the mExoPlayer knows to play the LBC stream
        mExoPlayer.prepare(lbcMediaSource);
    }

    /**
     * generates and returns a DefaultHttpDataSourceFactory for the mExoPlayer.
     * The DefaultHttpDataSourceFactory is controls how we consume the LBC online stream.
     *
     * @returnDefaultHttpDataSourceFactory
     */
    private DefaultHttpDataSourceFactory generateDataSourceFactory(){

       return new DefaultHttpDataSourceFactory(
                userAgent, null,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                1800000,
                true);
    }

    /**
     * creates and returns a ExtractorMediaSource pointing at the LBC online steam
     *
     * @param httpDataSourceFactory - a DefaultHttpDataSourceFactory required to make the ExtractorMediaSource
     * @return - A ExtractorMediaSource configured for the receing date from the LBC online steam
     */
    private MediaSource generateMediaSource(DefaultHttpDataSourceFactory httpDataSourceFactory){

        Handler handler = new Handler();

        return new ExtractorMediaSource(uri, httpDataSourceFactory, Mp3Extractor.FACTORY, handler, null);
    }

    /**
     * creates and returns a DefaultTrackSelector which is used to control
     * the mExoPlayer
     *
     * @return DefaultTrackSelector used to control the mExoPlayer
     */
    private DefaultTrackSelector generateTrackSelector(){

        //get bandwidth meter to .. measure bandwidth
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        //create contorl to be sued by the mExoPlayer
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        //create and return the DefaultTrackSelector
        return new DefaultTrackSelector(videoTrackSelectionFactory);
    }

    //MARK: ---------------- INITIALISATION

    //MARK: ---------------- mExoPlayer CONTROLS AND GETTING mExoPlayer

    /**
     * makes the mExoPlayer resume playing from its last position
     */
    public void resumeLBC(){

        mExoPlayer.setPlayWhenReady(true);
    }

    /**
     * pauses the mExoPlayer at its current position
     */
    public void pauseLBC(){

       mExoPlayer.setPlayWhenReady(false);
    }

    /**
     * returns the mExoPlayer
     *
     * @return - mExoPlayer
     */
    public SimpleExoPlayer getExoPlayer() {

        return mExoPlayer;
    }

    //MARK: ---------------- mExoPlayer CONTROLS AND GETTING mExoPlayer
}
