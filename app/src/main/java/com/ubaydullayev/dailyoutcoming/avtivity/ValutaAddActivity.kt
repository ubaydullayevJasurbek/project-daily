package com.ubaydullayev.dailyoutcoming.avtivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.ubaydullayev.dailyoutcoming.R
import com.ubaydullayev.dailyoutcoming.database.MyDatabaseHelper
import com.ubaydullayev.dailyoutcoming.databinding.ActivityValutaAddBinding
import com.ubaydullayev.dailyoutcoming.model.ValutaModel

class ValutaAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityValutaAddBinding
    private var selectedCurrency: String = ""
    private var selectedFlag: Int = R.drawable.usa_flag

    private val currencyFlags = mapOf(
        "USD" to R.drawable.usa_flag,
        "EUR" to R.drawable.eurflag,
        "GBP" to R.drawable.uk_flag,
        "JPY" to R.drawable.jpg_flag,
        "CHF" to R.drawable.swiss_flag,
        "CNY" to R.drawable.chine_flag,
        "RUB" to R.drawable.rus_flag,
        "KZT" to R.drawable.kz_flag,
        "TRY" to R.drawable.turk_flag,
        "AED" to R.drawable.uea_flag
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityValutaAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarExp.setNavigationOnClickListener {
            onBackPressed()
        }


        binding.spnCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCurrency = parent?.getItemAtPosition(position).toString().trim()
                selectedFlag = currencyFlags[selectedCurrency] ?: R.drawable.usa_flag
                binding.spnFlag.setImageResource(selectedFlag)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        binding.btnSave.setOnClickListener {
            val summaText = binding.summaVal.text.toString().trim()

            if (summaText.isEmpty()) {
                binding.tilSumma.error = "Iltimos, summa kiriting!"
            } else {
                val summa = summaText.toDoubleOrNull()

                if (summa == null || summa <= 0) {
                    binding.tilSumma.error = "Noto‘g‘ri summa kiritildi!"
                } else {
                    binding.tilSumma.error = null

                    val db = MyDatabaseHelper(this)
                    val isSaved = db.insertCurrency(selectedCurrency, summa, selectedFlag)
                    if (isSaved) {
                        val newValuta = ValutaModel(0, selectedCurrency, selectedFlag, summa)
                        val bundle = Bundle()
                        bundle.putSerializable("new-valuta", newValuta)
                        val intent = Intent().apply {
                            putExtra("bundle", bundle)
                        }
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        binding.tilSumma.error = "Xatolik yuz berdi!"
                    }
                }
            }
        }


        binding.toolbarExp.setNavigationOnClickListener {
            finish()
        }

    }

}