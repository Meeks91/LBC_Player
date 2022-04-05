package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.showsRV

import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcShow

interface ShowSelectedOnClickListener {
    fun onShowSelected(show: LbcShow, selectedPosition: Int)
    fun openDownloadBrowserFor(show: LbcShow, selectedPosition: Int)
}