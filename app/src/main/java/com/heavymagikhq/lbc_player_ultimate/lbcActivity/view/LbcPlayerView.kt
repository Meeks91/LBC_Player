package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view

import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcPresenter
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcShow

interface LbcPlayerView {
    fun onPresentersSet(presenters: List<LbcPresenter>)
    fun onHandleFailureToGetShows(errorMessage: String)
    fun listPresenterShows(shows: List<LbcShow>)
}