package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.showsRV

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.heavymagikhq.lbc_player_ultimate.R
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcPresenter
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcShow
import kotlinx.android.synthetic.main.item_lbc_show.view.*

class ShowsRVAdapter(var lbcShows: List<LbcShow> = listOf(), var presenter: LbcPresenter? = null) : RecyclerView.Adapter<LbcShowVViewHolder>() {

    var selectedShow: LbcShow? = null
    val indexOfSelectedShow: Int
        get() = lbcShows.indexOf(selectedShow).takeIf { it != -1 } ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LbcShowVViewHolder =
            LbcShowVViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_lbc_show,
                        parent,
                        false
                    )
            )

    override fun getItemCount(): Int = lbcShows.size

    override fun onBindViewHolder(holder: LbcShowVViewHolder, position: Int) {
        val showToPopulate = lbcShows[position]
        holder.show = showToPopulate

        val isSelected = selectedShow == showToPopulate
        holder.itemView.presenterNameTV.text = lbcShows[position].showDateLabel
        holder.itemView.presenterNameTV.setTextColor(if (isSelected) Color.WHITE else Color.DKGRAY)

        holder.itemView.isSelected = isSelected
    }
}