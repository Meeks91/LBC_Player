package com.heavymagikhq.lbc_player_ultimate.lbcActivity.view.daysRV

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import asDayMonthYear
import com.heavymagikhq.lbc_player_ultimate.R
import kotlinx.android.synthetic.main.item_day.view.*
import toDayOfWeek
import java.util.*

class DayRVAdapter(var dates: List<Date> = listOf()) : RecyclerView.Adapter<DaysRVViewHolder>() {

    var selectedDateIndex: Int = RecyclerView.NO_POSITION
    val selectedDate: Date? get() = dates.getOrNull(selectedDateIndex)

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
        holder.itemView.dateTV.text = "${date.toDayOfWeek()}\n${date.asDayMonthYear()}"
        holder.itemView.dateTV.setTextColor(if (isSelected) Color.WHITE else Color.DKGRAY)

        holder.itemView.isSelected = isSelected
    
        initEllyMode(holder)
    }
    
    private fun initEllyMode(holder: DaysRVViewHolder) {
        holder.itemView.isLongClickable = true
        holder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                Toast.makeText(holder.itemView.context, "... Venture Cannon Ball Fired", Toast.LENGTH_SHORT).show()
                return true
            }
        })
    }
}