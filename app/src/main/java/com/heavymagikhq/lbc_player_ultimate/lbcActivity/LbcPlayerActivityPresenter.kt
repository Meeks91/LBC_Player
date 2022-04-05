package com.heavymagikhq.lbc_player_ultimate.lbcActivity

import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcPresenter
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcShow
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcShowRetrieverService
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.LbcPlayerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LbcPlayerActivityPresenter(private val view: LbcPlayerView) {

    private val showCache: MutableMap<LbcPresenter, List<LbcShow>> = mutableMapOf()

    init {
        loadPresenters()
    }

    // MARK: --------- DATA RETRIEVAL PROCESS

    fun loadPresenters() = GlobalScope.launch {
        try {
            val presenters = LbcShowRetrieverService.getAllPresenters()
            view.onPresentersSet(presenters)
        } catch (error: Throwable) {
            view.onHandleFailureToGetShows(
                error.localizedMessage ?: "Error getting presenters"
            )
        }
    }


    fun loadShows(presenter: LbcPresenter, clearCache: Boolean = false) = GlobalScope.launch {
        try {
            if (clearCache) {
                showCache.remove(presenter)
            }

            var shows = showCache[presenter]

            if (shows == null) {
                shows = LbcShowRetrieverService.getShows(presenter.id)
                showCache[presenter] = shows
            }

            view.listPresenterShows(shows)
        } catch (error: Throwable) {
            view.onHandleFailureToGetShows(
                error.localizedMessage ?: "Error getting presenter shows"
            )
            view.listPresenterShows(listOf())
        }
    }

    // MARK: --------- DATA RETRIEVAL PROCESS
}