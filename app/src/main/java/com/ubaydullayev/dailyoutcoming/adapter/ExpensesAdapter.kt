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

class ExpensesAdapter(
    private val context: Context,
    private var cursor: Cursor,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ExpensesAdapter.ExpensesViewHolder>() {

    inner class ExpensesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.add_expenses)
        val date: TextView = itemView.findViewById(R.id.dayYearRcyExp)
        val amount: TextView = itemView.findViewById(R.id.expSumma)
        val currency: TextView = itemView.findViewById(R.id.valyutaTxtExp)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtnExp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.add_outcoming_item, parent, false)
        return ExpensesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpensesViewHolder, position: Int) {
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
