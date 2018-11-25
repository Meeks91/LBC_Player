package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.LbcPlayerActivityPresenter;
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.beans.LbcShow;
import com.heavymagikhq.lbc_player_ultimate.R;
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.showsRV.ShowSelectedOnClickListener;
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.showsRV.ShowsRVAdapter;
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.daysRV.DayRVAdapter;
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.daysRV.DaySelectedOnClickListener;
import com.heavymagikhq.lbc_player_ultimate.lbcExoPlayer.LbcExoPlayer;
import com.heavymagikhq.lbc_player_ultimate.lbcStreamingService.RadioStreamingService;
import com.heavymagikhq.lbc_player_ultimate.lbcStreamingService.RadioStreamingServiceBinder;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LBCPlayerActivity extends AppCompatActivity implements DaySelectedOnClickListener, ShowSelectedOnClickListener, LbcPlayerView {

    public static final String PODCAST_URI_KEY = "KEY";
    public static final String liveLbcUrl = "http://media-ice.musicradio.com/LBCLondonMP3";
    private final String TAG = LBCPlayerActivity.class.getSimpleName();
    private LbcPlayerActivityPresenter lbcPlayerActivityPresenter;

    @BindView(R.id.liveLbcPlayer)
    SimpleExoPlayerView liveLbcExoPlayerView;
    @BindView(R.id.podtcastLbcShowPlayer)
    SimpleExoPlayerView podcastExoPlayerView;
    @BindView(R.id.daySelectionRV)
    RecyclerView dateSelectionRV;
    @BindView(R.id.showSelectionRV)
    RecyclerView showSelectionRV;
    @BindView(R.id.swipeRL)
    SwipeRefreshLayout swipeRL;

    private DayRVAdapter datesRVAdapter = null;
    private ShowsRVAdapter showsRVAdapter = null;
    private RadioStreamingService radioStreamingService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startExoPlayerService();
        bindService(new Intent(this, RadioStreamingService.class), connectionToRadioStreamingService, Context.BIND_AUTO_CREATE);
        initRVAdapters();
        initPresenter();
        initSwipeRL();
    }

    //MARK: ---------- INITIALISATION

    private void initPresenter() {
        lbcPlayerActivityPresenter = new LbcPlayerActivityPresenter(this);
    }

    private void initSwipeRL() {
        final int green = getResources().getColor(android.R.color.holo_green_dark);
        final int blue = getResources().getColor(R.color.colorPrimaryDark);
        swipeRL.setColorSchemeColors(green, blue);
        swipeRL.setOnRefreshListener(this::onRefreshShowsData);
    }

    private void initRVAdapters() {
        datesRVAdapter = new DayRVAdapter();
        showsRVAdapter = new ShowsRVAdapter();
        dateSelectionRV.setAdapter(datesRVAdapter);
        showSelectionRV.setAdapter(showsRVAdapter);
    }

    private void startExoPlayerService() {
        final Intent startExoPlayerServiceIntent = new Intent(this, RadioStreamingService.class);
        startExoPlayerServiceIntent.putExtra(PODCAST_URI_KEY, LBCPlayerActivity.liveLbcUrl);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(startExoPlayerServiceIntent);
        else
            startService(new Intent(startExoPlayerServiceIntent));
    }

    /**
     * binds the the liveLbcExoPlayerView to the radioStreamingService
     *
     * @param radioStreamingService - the service which contains the exoPlayer which plays LBC
     */
    private void bindExoPlayerToExoPlayerView(LbcExoPlayer radioStreamingService) {
        liveLbcExoPlayerView.setPlayer(radioStreamingService.getExoPlayer());
    }

    private ServiceConnection connectionToRadioStreamingService = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof RadioStreamingServiceBinder) {
                radioStreamingService = ((RadioStreamingServiceBinder) service).getRadioStreamingService();
                bindExoPlayerToExoPlayerView((radioStreamingService.getLiveLbcExoPlayer()));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onSetDatesTo(@NotNull List<? extends Date> dates, int selectedDateIndex) {
        datesRVAdapter.setDates(dates);
        datesRVAdapter.setSelectedDateIndex(selectedDateIndex);
        dateSelectionRV.getLayoutManager().scrollToPosition(selectedDateIndex);
        showsRVAdapter.notifyDataSetChanged();
    }

    //MARK: ---------- INITIALISATION

    //MARK: ---------- SHOW SELECTION

    @Override
    public void onDateSelected(Date selectedDate, int selectedPosition) {
        lbcPlayerActivityPresenter.onDateSelected(selectedDate);
        datesRVAdapter.setSelectedDateIndex(selectedPosition);
        datesRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShowSelected(@NotNull LbcShow show, int selectedPosition) {
        if (podcastExoPlayerView.getPlayer() != null)
            podcastExoPlayerView.getPlayer().release();

        final LbcExoPlayer podcastExoPlayer = new LbcExoPlayer(this, show.getStreamUrl());
        podcastExoPlayerView.setPlayer(podcastExoPlayer.getExoPlayer());
        radioStreamingService.setPodcastPlayer(podcastExoPlayer);

        showsRVAdapter.setSelectedShow(show);
        showsRVAdapter.notifyDataSetChanged();
    }

    @Override
    public void listThese(@NotNull List<LbcShow> shows) {
        runOnUiThread(() -> {
            showsRVAdapter.setLbcShows(shows);
            showsRVAdapter.notifyDataSetChanged();

            showSelectionRV
                    .getLayoutManager()
                    .scrollToPosition(
                            showsRVAdapter.getIndexOfShowToSelected()
                    );

            if (swipeRL.isRefreshing())
                swipeRL.setRefreshing(false);
        });
    }


    @Override
    public void openDownloadBrowserFor(@NotNull LbcShow show, int selectedPosition) {
        final Intent launchPodcastWebsiteIntent = new Intent(Intent.ACTION_VIEW);
        final Uri showUri = Uri.parse(show.getStreamUrl());
        launchPodcastWebsiteIntent.setData(showUri);
        startActivity(launchPodcastWebsiteIntent);
    }

    //MARK: ---------- SHOW SELECTION

    //MARK: ---------- SHOW REFRESHING & ERROR HANDLING

    private void onRefreshShowsData() {
        final Date selectedDate = datesRVAdapter.getSelectedDate();
        final Date currentDate = new Date();
        if (selectedDate != null)
            lbcPlayerActivityPresenter.retrieveShowsThenSelect(selectedDate);
        else
            lbcPlayerActivityPresenter.retrieveShowsThenSelect(currentDate);
    }

    @Override
    public void onHandleFailureToGetShows(@NotNull String errorMessage) {
        runOnUiThread(() -> {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    if (swipeRL.isRefreshing())
                        swipeRL.setRefreshing(false);
                }
        );
    }

    //MARK: ---------- SHOW REFRESHING & ERROR HANDLING

    //MARK: ---------- LIFECYCLE

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //free resources:
        unbindService(connectionToRadioStreamingService);
        stopService(new Intent(this, RadioStreamingService.class));
        if (podcastExoPlayerView.getPlayer() != null)
            podcastExoPlayerView.getPlayer().release();
    }

    //MARK: ---------- LIFECYCLE
}