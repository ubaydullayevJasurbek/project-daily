package com.ubaydullayev.dailyoutcoming.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ubaydullayev.dailyoutcoming.R
import com.ubaydullayev.dailyoutcoming.database.MyDatabaseHelper

class InComingAdapter(
    private val context: Context,
    private var cursor: Cursor,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<InComingAdapter.IncomingViewHolder>() {

    inner class IncomingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.add_intcoming)
        val date: TextView = itemView.findViewById(R.id.dayYearRcy)
        val amount: TextView = itemView.findViewById(R.id.intSumma)
        val currency: TextView = itemView.findViewById(R.id.valyutaTxt)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.add_intcoming_item, parent, false)
        return IncomingViewHolder(view)
    }

    override fun onBindViewHolder(holder: IncomingViewHolder, position: Int) {
        if (cursor.moveToPosition(position)) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(MyDatabaseHelper.EXP_ADD_ID))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.EXP_ADD_NAME))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.EXP_ADD_DATE))
            val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(MyDatabaseHelper.EXP_ADD_AMOUNT))
            val currency = cursor.getString(cursor.getColumnIndexOrThrow(MyDatabaseHelper.EXP_ADD_CURRENCY))

            holder.title.text = title
            holder.date.text = date
            holder.amount.text = amount.toString()
            holder.currency.text = currency

            holder.deleteBtn.setOnClickListener {
                onDeleteClick(id)
            }
        }
    }

    override fun getItemCount(): Int = cursor.count

    @SuppressLint("NotifyDataSetChanged")
    fun swapCursor(newCursor: Cursor) {
        if (cursor != newCursor) {
            cursor.close()
            cursor = newCursor
            notifyDataSetChanged()
        }
    }

    fun closeCursor() {
        cursor.close()
    }
}
