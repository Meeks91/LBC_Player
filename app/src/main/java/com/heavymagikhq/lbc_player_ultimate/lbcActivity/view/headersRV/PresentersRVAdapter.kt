package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.headersRV

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.heavymagikhq.lbc_player_ultimate.R
import com.heavymagikhq.lbc_player_ultimate.lbcActivity.model.services.lbcShowRetrieverService.LbcPresenter
import kotlinx.android.synthetic.main.item_header.view.*
import java.util.*
import kotlin.collections.HashMap

class PresentersRVAdapter(
    var presenters: List<LbcPresenter> = listOf(),
    val manager: RecyclerView.LayoutManager
) :
    RecyclerView.Adapter<PresenterRVViewHolder>() {

    private var idxMap = HashMap<LbcPresenter, Int>()
    private var filteredPresenters: List<LbcPresenter> = listOf()
        set(value) {
            field = value
            value.forEachIndexed { idx, presenter ->
                idxMap[presenter] = idx
            }
        }

    init {
        filteredPresenters = presenters
    }

    var selectedPresenter: LbcPresenter? = null
    val selectedPresenterIdx: Int
        get() = selectedPresenter?.let { idxMap[it] } ?: RecyclerView.NO_POSITION


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PresenterRVViewHolder =
        PresenterRVViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(
                    R.layout.item_header,
                    parent,
                    false
                )
        )

    override fun getItemCount(): Int =
        filteredPresenters.size

    override fun onBindViewHolder(holder: PresenterRVViewHolder, position: Int) {
        val presenter = filteredPresenters[position]
        holder.presenter = presenter

        val isSelected = presenter == selectedPresenter
        holder.itemView.headerTV.text = filteredPresenters[position].nameOfPresenter
        holder.itemView.headerTV.setTextColor(if (isSelected) Color.WHITE else Color.DKGRAY)

        holder.itemView.isSelected = isSelected
    }

    val presenterFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            if (constraint == null || constraint.isBlank()) {
                val results = FilterResults()
                results.values = presenters
                return results
            }

            val results = FilterResults()

            results.values = presenters.filter {
                it.nameOfPresenter.toLowerCase(Locale.UK)
                    .contains("$constraint".toLowerCase(Locale.UK))
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results == null) return

            filteredPresenters = results.values as List<LbcPresenter>

            notifyDataSetChanged()

            if (constraint?.isBlank() != false) {
                manager.scrollToPosition(selectedPresenterIdx)
            }
        }
    }
}