package com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

object LbcShowRetrieverService {

    private const val CATCH_UPS_URL =
        "https://bff-web-guacamole.musicradio.com/globalplayer/catchups"
    private var CLIENT = OkHttpClient()
    private val GSON = Gson()
    private val LBC_PRESENTER_TYPE: Type = object : TypeToken<List<LbcPresenter>>() {}.type
    private val LBC_SHOW_TYPE: Type = object : TypeToken<PresenterShows>() {}.type
    private val DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    // MARK: ------------ REQUESTS

    fun getAllPresenters(): List<LbcPresenter> {
        val request = Request.Builder()
            .url("$CATCH_UPS_URL/lbc/uk")
            .get()
            .build()
        return parsePresenters(
            CLIENT.newCall(request).execute()
        )
    }

    fun getShows(presenterId: String): List<LbcShow> {
        val request = Request.Builder()
            .url("$CATCH_UPS_URL/$presenterId")
            .get()
            .build()
        return parsePresenterShows(
            CLIENT.newCall(request).execute()
        )
    }

    // MARK: ------------ REQUESTS

    // MARK: ------------ PARSING

    private fun parsePresenters(response: Response): List<LbcPresenter> {
        val string = response.body?.string() ?: ""
        return GSON
            .fromJson<List<LbcPresenter>>(string, LBC_PRESENTER_TYPE)
    }

    private fun parsePresenterShows(response: Response): List<LbcShow> {
        val respAsStr = response.body?.string() ?: ""
        return GSON
            .fromJson<PresenterShows>(respAsStr, LBC_SHOW_TYPE)
            .episodes
    }

    // MARK: ------------ PARSING
}