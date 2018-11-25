package com.heavymagikhq.lbc_player_ultimate.lbcActivity

import asDayMonthYear
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.beans.LbcShow
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcShowRetrieverService
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.LbcPlayerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class LbcPlayerActivityPresenter(private val view: LbcPlayerView) {

    private var shows: List<LbcShow>? = null
    var selectedShow: LbcShow? = null

    private val availableDates: List<Date> by lazy {
        (-6..0).map {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, it)
            calendar.time
        }
    }

    init {
        initDatesInView()
        retrieveShowsThenSelect(Date())
    }

    //MARK: --------- INITIALISATION

    private fun initDatesInView() =
            view.onSetDatesTo(availableDates, availableDates.lastIndex)


    //MARK: --------- INITIALISATION

    //MARK: --------- DATA RETRIEVAL PROCESS

    fun retrieveShowsThenSelect(dateToSelectOnCompletion: Date) = GlobalScope.launch {
        try {
            shows = LbcShowRetrieverService.getAllShows()
            onDateSelected(dateToSelectOnCompletion)
        } catch (error: Throwable) {
            view.onHandleFailureToGetShows(error.localizedMessage ?: "An error occurred")
        }
    }

    fun onDateSelected(selectedDate: Date) =
            shows?.filter { it.dateAired == selectedDate.asDayMonthYear() }
                    ?.let { view.listThese(it) }

    //MARK: --------- DATA RETRIEVAL PROCESS
}