package com.ubaydullayev.dailyoutcoming.avtivity

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.datepicker.MaterialDatePicker
import com.ubaydullayev.dailyoutcoming.R
import com.ubaydullayev.dailyoutcoming.database.MyDatabaseHelper
import com.ubaydullayev.dailyoutcoming.databinding.ActivityOutcomingBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ExpensesActivity : AppCompatActivity() {

    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var editTitle: TextInputEditText
    private lateinit var editDate: TextInputEditText
    private lateinit var editAmount: TextInputEditText
    private lateinit var spinnerCurrency: Spinner
    private lateinit var addButton: MaterialButton
    private lateinit var expSpinner: AutoCompleteTextView
    private lateinit var binding: ActivityOutcomingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  Status bar rang
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        binding = ActivityOutcomingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MyDatabaseHelper(this)

        //  Elementlarni aniqlash
        expSpinner = findViewById(R.id.ExpSpinnerBtn)
        editTitle = findViewById(R.id.editExp_Txt)
        editDate = findViewById(R.id.dayYearEditExp)
        editAmount = findViewById(R.id.summaEditExp)
        spinnerCurrency = findViewById(R.id.valyutaBtnExp)
        addButton = findViewById(R.id.addButtonExp)


        //  Spinnerga DB dan kategoriyalarni yuklash
        setupCategorySpinner()

        //  Sana tanlash uchun MaterialDatePicker
        val dateEdit = binding.dayYearEditExp
        dateEdit.setOnClickListener { showMaterialDatePicker() }

        //  Saqlash tugmasi bosilganda
        addButton.setOnClickListener {
            saveExpenseData()
        }
        binding.toolbarExp.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupCategorySpinner() {
        val categories = dbHelper.getAllCategoryType()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
       expSpinner.setAdapter(adapter)
    }

    //  TypeOfExpensesActivity dan qaytganda ro'yxat yangilansin
    override fun onResume() {
        super.onResume()
        setupCategorySpinner()
    }

    //  Sana tanlash oynasi
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
            binding.dayYearEditExp.setText(formatted)
        }

        picker.show(supportFragmentManager, "DATE_PICKER")
    }

    //  Ma'lumotlarni saqlash (alohida metod)
    private fun saveExpenseData() {
        val category = expSpinner.text?.toString()?.trim().orEmpty()
        val title = editTitle.text?.toString()?.trim().orEmpty()
        val date = editDate.text?.toString()?.trim().orEmpty()
        val amount = editAmount.text?.toString()?.trim().orEmpty()
        val currency = spinnerCurrency.selectedItem?.toString().orEmpty()

        //  Validatsiya
        if (TextUtils.isEmpty(category) || TextUtils.isEmpty(title) ||
            TextUtils.isEmpty(date) || TextUtils.isEmpty(amount)) {
            AlertDialog.Builder(this)
                .setTitle("Xatolik")
                .setMessage("Iltimos, barcha maydonlarni to'ldiring")
                .setPositiveButton("Ok", null)
                .show()
            return
        }

        //  Kategoriya va sarlavhani birlashtirish (agar kerak bo'lsa)
        val fullTitle = if (category.isNotEmpty()) "$category - $title" else title

       val success = dbHelper.insertExpenseAdd(fullTitle, date, amount, currency)

        if (success) {
           Toast.makeText(this, "Xarajat saqlandi.", Toast.LENGTH_SHORT).show()
           finish()
        } else {
          AlertDialog.Builder(this)
                .setTitle("Xatolik")
                .setMessage("Ma'lumotni saqlashda xatolik yuz berdi!")
               .setPositiveButton("OK", null)
               .show()
       }
    }
}