package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.headersRV

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcPresenter

class PresenterRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var presenter: LbcPresenter

    init {
        itemView.setOnClickListener {
            (itemView.context as PresenterSelectedOnClickListener).onPresenterSelected(
                presenter,
                bindingAdapterPosition
            )
        }
    }
}