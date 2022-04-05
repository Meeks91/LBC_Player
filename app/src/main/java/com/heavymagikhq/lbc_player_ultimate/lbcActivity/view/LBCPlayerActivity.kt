package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view


import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.gms.cast.framework.CastButtonFactory
import com.heavymagikhq.lbc_player_ultimate.R
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.LbcPlayerActivityPresenter
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcPresenter
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcShow
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.headersRV.PresenterSelectedOnClickListener
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.headersRV.PresentersRVAdapter
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.showsRV.ShowSelectedOnClickListener
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.showsRV.ShowsRVAdapter
import com.heavymagikhq.lbc_player_ultimate.lbcExoPlayer.LbcExoPlayer
import kotlinx.android.synthetic.main.activity_main.*


class LBCPlayerActivity : AppCompatActivity(), PresenterSelectedOnClickListener,
    ShowSelectedOnClickListener, LbcPlayerView {
    private lateinit var showsRVAdapter: ShowsRVAdapter
    private lateinit var presenterRvAdapter: PresentersRVAdapter
    private lateinit var liveExoPlayer: LbcExoPlayer
    private var podcastExoPlayer: LbcExoPlayer? = null
    private lateinit var lbcPlayerActivityPresenter: LbcPlayerActivityPresenter

    companion object {
        const val liveLbcUrl = "http://media-ice.musicradio.com/LBCLondonMP3"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        liveExoPlayer = LbcExoPlayer(liveLbcUrl, this, true, liveLbcPlayer, podcastExoPlayer)

        initRVAdapters()
        initPresenter()
        initSwipeRL()
        initPresenterSearchBar()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        CastButtonFactory.setUpMediaRouteButton(this, menu, R.id.media_route_menu_item)
        return true
    }

    // MARK: ---------- INITIALISATION

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initPresenterSearchBar() {
        presenterSearchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenterRvAdapter.presenterFilter.filter(newText)
                return true
            }
        })

        presenterSearchBar.findViewById<EditText>(R.id.search_src_text).onFocusChangeListener =
            OnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    presenterSearchBar.isIconified = true
                    presenterRV.layoutManager?.scrollToPosition(
                        presenterRvAdapter.selectedPresenterIdx
                    )
                }
            }
    }

    private fun initPresenter() {
        lbcPlayerActivityPresenter = LbcPlayerActivityPresenter(this)
    }

    private fun initSwipeRL() {
        val green = resources.getColor(android.R.color.holo_green_dark)
        val blue = resources.getColor(R.color.colorPrimaryDark)
        swipeRL!!.setColorSchemeColors(green, blue)
        swipeRL!!.setOnRefreshListener { onRefreshShowsData() }
    }

    private fun initRVAdapters() {
        showsRVAdapter = ShowsRVAdapter()
        showSelectionRV!!.adapter = showsRVAdapter
    }

    // MARK: ---------- INITIALISATION

    // MARK: ---------- PRESENTER SELECTION

    override fun onPresentersSet(presenters: List<LbcPresenter>) {
        runOnUiThread {
            presenterRvAdapter = PresentersRVAdapter(presenters, presenterRV!!.layoutManager!!)
            presenterRV!!.adapter = presenterRvAdapter
            presenterRvAdapter.notifyDataSetChanged()
        }
    }

    override fun onPresenterSelected(presenter: LbcPresenter, selectedPosition: Int) {
        val prevSelectedIdx = presenterRvAdapter.selectedPresenterIdx
        presenterRvAdapter.selectedPresenter = presenter
        presenterRvAdapter.notifyItemChanged(prevSelectedIdx)
        presenterRvAdapter.notifyItemChanged(selectedPosition)
        lbcPlayerActivityPresenter.loadShows(presenter, false)
    }

    override fun listPresenterShows(shows: List<LbcShow>) {
        runOnUiThread {
            showsRVAdapter.lbcShows = shows
            showsRVAdapter.notifyDataSetChanged()
            showSelectionRV!!
                .layoutManager!!
                .scrollToPosition(
                    showsRVAdapter.indexOfSelectedShow
                )
            if (swipeRL!!.isRefreshing) swipeRL!!.isRefreshing = false
        }
    }

    // MARK: ---------- PRESENTER SELECTION

    // MARK: ---------- SHOW SELECTION

    override fun openDownloadBrowserFor(show: LbcShow, selectedPosition: Int) {
        val launchPodcastWebsiteIntent = Intent(Intent.ACTION_VIEW)
        val showUri = Uri.parse(show.streamUrl)
        launchPodcastWebsiteIntent.data = showUri
        startActivity(launchPodcastWebsiteIntent)
    }

    override fun onShowSelected(show: LbcShow, selectedPosition: Int) {
        liveExoPlayer.pauseLBC()

        val playShow = {
            podcastExoPlayer =
                LbcExoPlayer(show.playableUrl, this, false, podcastPlayer, liveExoPlayer)
            liveExoPlayer.otherPlayer = podcastExoPlayer
            showsRVAdapter.selectedShow = show
            showsRVAdapter.notifyDataSetChanged()
        }

        if (showsRVAdapter.selectedShow != null && showsRVAdapter.selectedShow != show) {
            val builder = AlertDialog.Builder(this)
            builder.apply {
                setPositiveButton(R.string.ok) { _, _ -> playShow() }
                setNegativeButton(R.string.cancel, null)
            }
            builder.create().show()
        } else {
            playShow()
        }
    }

    // MARK: ---------- SHOW SELECTION

    // MARK: ---------- SHOW REFRESHING & ERROR HANDLING

    private fun onRefreshShowsData() {
        if (showsRVAdapter.presenter != null)
            lbcPlayerActivityPresenter.loadShows(showsRVAdapter.presenter!!, true)
        else
            swipeRL.isRefreshing = false
    }

    override fun onHandleFailureToGetShows(errorMessage: String) {
        runOnUiThread {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            if (swipeRL.isRefreshing) swipeRL.isRefreshing = false
        }
    }

    // MARK: ---------- SHOW REFRESHING & ERROR HANDLING

    // MARK: ---------- LIFECYCLE

    override fun onDestroy() {
        super.onDestroy()
        liveExoPlayer.release()
        podcastExoPlayer?.release()
    }

    // MARK: ---------- LIFECYCLE
}