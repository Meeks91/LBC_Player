package com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.beans.LbcShow
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.beans.LbcShowPresenter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

object LbcShowRetrieverService {

    private val baseUrl = "http://ws.geronimo.thisisglobal.com"
    private var client = OkHttpClient()
    private val gson = Gson()
    private val showSearchResponseType: Type = object : TypeToken<ShowSearchResponse>() {}.type
    private val yearMonthDayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    //MARK: ------------ REQUESTS

    fun getShowFor(presenter: LbcShowPresenter, date: Date = Calendar.getInstance().time): List<LbcShow> {
        val request = Request.Builder()
                .url(generateShowUrlFor(presenter, date))
                .get()
                .build()
        return parseShowSearchResponseIn(
                client.newCall(request).execute()
        )
    }

    fun getAllShows(): List<LbcShow> {
        val request = Request.Builder()
                .url(generateFetchAllShowsUrl())
                .get()
                .build()
        return parseShowSearchResponseIn(
                client.newCall(request).execute()
        )
    }

    private fun generateShowUrlFor(showPresenter: LbcShowPresenter, date: Date): String {
        val dateString = asYearMonthDay(date)
        val startDate = "${dateString}T${showPresenter.startTimeofShow}"
        val endDate = "${dateString}T${showPresenter.endTimeOfShow}"
        return "$baseUrl/api/Episode/ListEpisodesByStationAndAirDate?stationId=97b707c5-f937-4148-9d1d-2abfa0e91168&startDate=$startDate&endDate=$endDate"
    }

    private fun generateFetchAllShowsUrl(): String {
        val startDate = "${asYearMonthDay(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }.time)}T00:00"
        val endDate = "${asYearMonthDay(Date())}T23:59"
        return "$baseUrl/api/Episode/ListEpisodesByStationAndAirDate?stationId=97b707c5-f937-4148-9d1d-2abfa0e91168&startDate=$startDate&endDate=$endDate"
    }

    private fun asYearMonthDay(date: Date): String =
            yearMonthDayFormatter.format(date)

    //MARK: ------------ REQUESTS

    //MARK: ------------ PARSING

    private fun parseShowSearchResponseIn(response: Response): List<LbcShow> {
        val string = response.body()?.string() ?: ""
        return gson
                .fromJson<ShowSearchResponse>(string, showSearchResponseType)
                .shows
                .map { it.toLbcShow()  }
    }

    //MARK: ------------ PARSING
}