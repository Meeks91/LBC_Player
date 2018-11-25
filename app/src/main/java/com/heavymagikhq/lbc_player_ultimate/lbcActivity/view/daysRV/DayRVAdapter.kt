package com.heavymagikhq.lbc_player_ultimate

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import df
import kotlinx.android.synthetic.main.item_day.view.*
import java.util.*

class DayRVAdapter(var dates: List<Date> = listOf()) : RecyclerView.Adapter<DaysRVViewHolder>() {

    var selectedDateIndex: Int = RecyclerView.NO_POSITION
    var selectedDate: Date? = dates.getOrNull(selectedDateIndex)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): DaysRVViewHolder =
            DaysRVViewHolder(
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_day, parent, false)
            )

    override fun getItemCount(): Int =
            dates.size

    override fun onBindViewHolder(holder: DaysRVViewHolder, position: Int) {
        val date = dates[position]
        holder.date = date

        val isSelected = position == selectedDateIndex
        holder.itemView.dateTV.text = df.format(date)
        holder.itemView.dateTV.setTextColor(if (isSelected) Color.WHITE else Color.DKGRAY)

        holder.itemView.isSelected = isSelected
    }
}