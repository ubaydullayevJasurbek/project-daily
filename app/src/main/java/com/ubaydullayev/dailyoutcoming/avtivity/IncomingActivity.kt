package com.ubaydullayev.dailyoutcoming.avtivity

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.material.datepicker.MaterialDatePicker
import com.ubaydullayev.dailyoutcoming.R
import com.ubaydullayev.dailyoutcoming.database.CategoryType
import com.ubaydullayev.dailyoutcoming.database.MyDatabaseHelper
import com.ubaydullayev.dailyoutcoming.databinding.ActivityIncomingBinding
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.map

class IncomingActivity : AppCompatActivity() {

    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var binding: ActivityIncomingBinding
    private lateinit var intSpinner: AutoCompleteTextView
    private lateinit var adapter: ArrayAdapter<String>
    private var expenseTypes: List<CategoryType> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Status bar dizayni
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        binding = ActivityIncomingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MyDatabaseHelper(this)
        intSpinner = binding.intSpinnerBtn

        // Toolbar orqaga qaytish
        binding.toolbar2.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        setupSpinner()

        // Sana tanlash
        binding.dayYearEdit.setOnClickListener { showMaterialDatePicker() }

        // Qo'shish tugmasi
        binding.addButton.setOnClickListener {
            saveIncomeData()
        }
    }
    //  Spinnerga kategoriya ro‘yxatini yuklaydi
    private fun setupSpinner() {
        expenseTypes = dbHelper.getAllCategoryType()
        val titles = expenseTypes.map { it.title }  // List<String>
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, titles)
        intSpinner.setAdapter(adapter)
    }
    // 🔹 Har safar qaytganda yangilab turadi
    override fun onResume() {
        super.onResume()
        expenseTypes = dbHelper.getAllCategoryType()
        val titles = expenseTypes.map { it.title }
        adapter.clear()
        adapter.addAll(titles)
        adapter.notifyDataSetChanged()
    }

    // 🔹 Sana tanlash oynasi
    private fun showMaterialDatePicker() {
        val builder = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Sanani tanlang")
            .setTheme(R.style.MyMaterialDatePickerTheme)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val formatted = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(calendar.time)
            binding.dayYearEdit.setText(formatted)
        }
        picker.show(supportFragmentManager, "DATE_PICKER")
    }

    // 🔹 Ma'lumotlarni saqlash
    private fun saveIncomeData() {
        val category = intSpinner.text?.toString()?.trim().orEmpty()
        val title = binding.editIntTxt.text?.toString()?.trim().orEmpty()
        val date = binding.dayYearEdit.text?.toString()?.trim().orEmpty()
        val amount = binding.summaEdit.text?.toString()?.trim().orEmpty()
        val currency = binding.valyutaBtn.selectedItem?.toString().orEmpty()

        if (TextUtils.isEmpty(category) || TextUtils.isEmpty(title) ||
            TextUtils.isEmpty(date) || TextUtils.isEmpty(amount)) {
            AlertDialog.Builder(this)
                .setTitle("Xatolik")
                .setMessage("Iltimos, barcha maydonlarni to'ldiring")
                .setPositiveButton("OK", null)
                .show()
            return
        }
        // To‘liq nom yasash
        val fullTitle = if (category.isNotEmpty()) "$category - $title" else title

        val success = dbHelper.insertIncomingAdd(fullTitle, date, amount, currency)

        if (success) {
            Toast.makeText(this, "Daromad saqlandi.", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Xatolik!")
               .setMessage("Ma'lumotni saqlashda xatolik yuz berdi!")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
