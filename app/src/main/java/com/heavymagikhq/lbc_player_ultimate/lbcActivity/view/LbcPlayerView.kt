package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view

import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.beans.LbcShow
import java.util.*

interface LbcPlayerView {
    fun onSetDatesTo(dates: List<Date>, selectedDateIndex: Int)
    fun onHandleFailureToGetShows(errorMessage: String)
    fun listThese(shows: List<LbcShow>)
}