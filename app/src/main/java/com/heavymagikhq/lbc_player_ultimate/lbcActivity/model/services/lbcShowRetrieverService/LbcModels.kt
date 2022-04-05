package com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService

import com.google.gson.annotations.SerializedName

data class LbcPresenter(
    val id: String,
    @SerializedName("title") val nameOfPresenter: String,
    val imageUrl: String?
)

data class PresenterShows(val episodes: List<LbcShow>)

data class LbcShow(
    val id: String,
    val streamUrl: String,
    val startDate: String,
    val duration: String,
    val fileName: String
) {
    val playableUrl: String
        get() = streamUrl.substringBefore("?")
    val showDateLabel: String
        get() = startDate.substringBefore("+").replace("T", " ")
}