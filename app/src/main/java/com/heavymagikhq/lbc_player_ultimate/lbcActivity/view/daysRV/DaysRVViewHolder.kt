package com.heavymagikhq.lbc_player_ultimate

import android.support.v7.widget.RecyclerView
import android.view.View
import java.util.*

class DaysRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    lateinit var date: Date

    init {
        itemView.setOnClickListener {
            (itemView.context as DaySelectedOnClickListener).onDateSelected(date, adapterPosition)
        }
    }
}