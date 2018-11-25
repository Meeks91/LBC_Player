package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.showsRV

import android.support.v7.widget.RecyclerView
import android.view.View
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.beans.LbcShow

class LbcShowVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var show: LbcShow

    init {
        itemView.setOnClickListener {
            (itemView.context as ShowSelectedOnClickListener).onShowSelected(show, adapterPosition)
        }
        itemView.setOnLongClickListener {
            (itemView.context as ShowSelectedOnClickListener).openDownloadBrowserFor(show, adapterPosition)
            true
        }
    }
}