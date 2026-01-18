package com.ubaydullayev.dailyoutcoming.avtivity

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.ubaydullayev.dailyoutcoming.R
import com.ubaydullayev.dailyoutcoming.adapter.ValutaAdapter
import com.ubaydullayev.dailyoutcoming.database.MyDatabaseHelper
import com.ubaydullayev.dailyoutcoming.databinding.ActivityValutaBinding
import com.ubaydullayev.dailyoutcoming.model.ValutaModel

class ValutaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityValutaBinding
    private lateinit var adapter: ValutaAdapter

    private lateinit var db: MyDatabaseHelper
    private val valutaList = mutableListOf<ValutaModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityValutaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarExp.setNavigationOnClickListener {
            onBackPressed()
        }

        db = MyDatabaseHelper(this)

        binding.valAddBtn.setOnClickListener {
            val intent = Intent(this, ValutaAddActivity::class.java)
            startActivityForResult(intent, 1000)
        }

        adapter = ValutaAdapter(valutaList)
        binding.recValList.layoutManager = LinearLayoutManager(this)
        binding.recValList.adapter = adapter

        adapter.editOnClick = { valutaModel ->
            val dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_edit_item, null)

            val editSum = dialogView.findViewById<TextInputEditText>(R.id.sumEditVal)
            editSum.setText(valutaModel.amount.toString())

            val dialog = AlertDialog.Builder(this)
                .setTitle("${valutaModel.currency} summani o'zgartirish")
                .setView(dialogView)
                .setPositiveButton("Save") { d, _ ->

                    val newSum = editSum.text.toString().toDoubleOrNull()

                    if (newSum != null) {
                        val contentValue = ContentValues()
                        contentValue.put(MyDatabaseHelper.CUR_AMOUNT, newSum)

                        val condition = "${MyDatabaseHelper.CUR_ID} = ?"
                        val stringArray = arrayOf(valutaModel.id.toString())
                        val result = db.updateContent(
                            tableName = MyDatabaseHelper.CURRENCY_TABLE,
                            contentValues = contentValue,
                            condition = condition,
                            stringArray = stringArray
                        )

                        if (result > 0) {
                            valutaModel.amount = newSum
                            adapter.itemUpdate(valutaModel)
                        }

                    }
                    d.dismiss()
                }
                .setNegativeButton("Cancel") { d, _ ->
                    d.dismiss()
                }
                .create()
            dialog.show()
        }
        loadCurrencies()
    }

    private fun loadCurrencies() {
        val valutaList = db.getAllCurrencies()
        adapter.updateList(valutaList)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            val bundle = data?.getBundleExtra("bundle")
            val newValutaModel = bundle?.getSerializable("new-valuta") as ValutaModel?
            newValutaModel?.let {
                adapter.addItem(it)
            }
        }
    }
}