package com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.beans.LbcShow
import java.util.*

data class LbcShowApiModel(
        @SerializedName("Id") val id: String,
        @SerializedName("Name") val nameOfPresenter: String,
        @SerializedName("Description") val description: String?,
        @SerializedName("AirStartDate") private val _dateAired: String,
        @SerializedName("AudioAsset") private val audioDetails: HashMap<String, Any>) {

    private val fileName: String?
        get() =
            (audioDetails["AssetFile"] as? LinkedTreeMap<*, *>)?.get("FileName") as String?

    private val dateAired: String?
        get() = _dateAired.substringBefore("T").split("-")
                .takeIf { it.size == 3 }
                ?.let { "${it[2]}-${it[1]}-${it[0]}" }

    @Throws(Throwable::class)
    fun toLbcShow(): LbcShow =
            when {
                dateAired == null -> throw Throwable("dateAired is null")
                fileName == null -> throw Throwable("fileName is null")
                else -> LbcShow(
                        id, nameOfPresenter,
                        description ?: "None provided",
                        dateAired!!, fileName!!
                )
            }
}