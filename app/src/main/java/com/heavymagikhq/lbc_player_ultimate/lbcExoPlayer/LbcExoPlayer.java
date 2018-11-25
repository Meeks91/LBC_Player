package com.heavymagikhq.lbc_player_ultimate.lbcExoPlayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.beans.LbcShow;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Micah on 13/08/2017.
 */

public class LbcExoPlayer {

    private final String userAgent = "yourApplicationName";
    private final Uri uri;
    private final String TAG = LbcExoPlayer.class.getSimpleName();
    private final SimpleExoPlayer exoPlayer;
    private long lastTimeStarted = System.currentTimeMillis();

    public LbcExoPlayer(@NonNull Context context, @NonNull String streamUrl){
        this.uri = Uri.parse(streamUrl);
        this.exoPlayer = createExoPlayerUsing(context);
        initExoPlayerStateChangerListener();
    }

    //MARK: ---------------- INITIALISATION


    /**
     * inits the exoPlayer by bringing together the components
     * required to create it
     *
     * @param context -
     */
    @NotNull
    private SimpleExoPlayer createExoPlayerUsing(@NonNull Context context) {

        //create the player
       final SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(context, generateTrackSelector());

        //get a dataSource which is able process the LBC online stream
        DefaultHttpDataSourceFactory httpDataSourceFactory = generateDataSourceFactory();

        //get a data source pointing to the LBC online stream and
        //assign the httpDataSourceFactory to process the stream
        MediaSource lbcMediaSource = generateMediaSource(httpDataSourceFactory);

        //assign the lbcMediaSource so the exoPlayer knows to play the LBC stream
        exoPlayer.prepare(lbcMediaSource);

      return exoPlayer;
    }

    private void initExoPlayerStateChangerListener() {
        exoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady)
                   setLastTimeStarted(System.currentTimeMillis());
            }
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) { }
            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) { }
            @Override
            public void onLoadingChanged(boolean isLoading) { }
            @Override
            public void onPlayerError(ExoPlaybackException error) { }
            @Override
            public void onPositionDiscontinuity() { }
            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { }
        });
    }

    /**
     * generates and returns a DefaultHttpDataSourceFactory for the exoPlayer.
     * The DefaultHttpDataSourceFactory is controls how we consume the LBC online stream.
     *
     * @return DefaultHttpDataSourceFactory
     */
    @NonNull
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
    @NonNull
    private MediaSource generateMediaSource(@NonNull DefaultHttpDataSourceFactory httpDataSourceFactory){
        Handler handler = new Handler();
        String t = uri.toString();
        boolean isMp4 = t.contains("mp4");
        return new ExtractorMediaSource(uri, httpDataSourceFactory, isMp4 ? Mp4Extractor.FACTORY : Mp3Extractor.FACTORY , handler, null);
    }

    /**
     * creates and returns a DefaultTrackSelector which is used to control
     * the exoPlayer
     *
     * @return DefaultTrackSelector used to control the exoPlayer
     */
    @NonNull
    private DefaultTrackSelector generateTrackSelector(){

        //get bandwidth meter to .. measure bandwidth
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

        //create contorl to be sued by the exoPlayer
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        //create and return the DefaultTrackSelector
        return new DefaultTrackSelector(videoTrackSelectionFactory);
    }

    //MARK: ---------------- INITIALISATION

    //MARK: ---------------- exoPlayer CONTROLS

    /**
     * makes the exoPlayer resume playing from its last position
     */
    public void resumeLBC(){
        exoPlayer.setPlayWhenReady(true);
    }

    /**
     * pauses the exoPlayer at its current position
     */
    public void pauseLBC(){
       exoPlayer.setPlayWhenReady(false);
    }

    //MARK: ---------------- exoPlayer CONTROLS

    //MARK: ---------------- GETTERS & SETTERS

    public long getLastTimeStarted() {
        return lastTimeStarted;
    }

    private void setLastTimeStarted(long lastTimeStarted) {
        this.lastTimeStarted = lastTimeStarted;
    }

    @NonNull
    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    //MARK: ---------------- GETTERS & SETTERS
}
