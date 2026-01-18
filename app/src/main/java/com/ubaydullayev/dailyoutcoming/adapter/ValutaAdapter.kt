package com.ubaydullayev.dailyoutcoming.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.ubaydullayev.dailyoutcoming.R
import com.ubaydullayev.dailyoutcoming.databinding.ItemCurrencyBgBinding
import com.ubaydullayev.dailyoutcoming.model.ValutaModel

class ValutaAdapter(
    private var valutaList: MutableList<ValutaModel>
) : RecyclerView.Adapter<ValutaAdapter.ValutaViewHolder>() {

    var editOnClick: ((ValutaModel) -> Unit)? = null

    inner class ValutaViewHolder(val binding: ItemCurrencyBgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = valutaList[position]
            binding.apply {
                flagImageItem.setImageResource(item.flag)
                itemCrr.text = item.currency
                textView3.text = "1.00"
                summaItem.text = String.format("%.2f", item.amount)


                editItemDialog.setOnClickListener {
                    editOnClick?.invoke(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ValutaViewHolder {
        val binding =
            ItemCurrencyBgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ValutaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ValutaViewHolder,
        position: Int
    ) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = valutaList.size

    fun updateList(newList: List<ValutaModel>) {
        valutaList.clear()
        valutaList.addAll(newList)
        notifyDataSetChanged()
    }

    fun itemUpdate(updateValutaModel: ValutaModel) {
        val findItem = valutaList.find {
            it.id == updateValutaModel.id
        }
        if (findItem != null) {
            val index = valutaList.indexOf(findItem)
            valutaList[index].amount = updateValutaModel.amount
            notifyItemChanged(index)
        }
    }

    fun addItem(newValutaModel: ValutaModel) {
        valutaList.add(newValutaModel)
        notifyItemInserted(valutaList.size - 1)
    }

}