package com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService

import com.google.gson.annotations.SerializedName

data class ShowSearchResponse(@SerializedName("Episodes") val shows: List<LbcShowApiModel>)