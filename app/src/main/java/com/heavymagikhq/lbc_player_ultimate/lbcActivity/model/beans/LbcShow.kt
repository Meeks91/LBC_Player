package com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.beans

data class LbcShow(
        val id: String,
        val nameOfPresenter: String,
        val description: String,
        val dateAired: String,
        val fileName: String) {

    val streamUrl: String by lazy { "http://fs.geronimo.thisisglobal.com/audio/$fileName" }
}