package com.ubaydullayev.dailyoutcoming.avtivity

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.ubaydullayev.dailyoutcoming.R
import com.ubaydullayev.dailyoutcoming.database.MyDatabaseHelper
import com.ubaydullayev.dailyoutcoming.databinding.ActivityTypeOfExpensesBinding

class TypeOfExpensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTypeOfExpensesBinding
    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var editTypeTitle: TextInputEditText
    private lateinit var editTypeDesc: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Status bar rangini o'rnatish
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false

        binding = ActivityTypeOfExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MyDatabaseHelper(this)

        editTypeTitle = binding.editTypeTxt
        editTypeDesc = binding.editTypeTextTxt

        // Toolbar orqaga qaytish
        binding.toolbar3.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Qo‘shish tugmasi bosilganda
        binding.addTypeButton.setOnClickListener {
            saveExpenseType()
        }
    }

    private fun saveExpenseType() {
        val title = editTypeTitle.text?.toString()?.trim()
        val desc = editTypeDesc.text?.toString()?.trim()

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Xarajat nomini kiriting!", Toast.LENGTH_SHORT).show()
            return
        }
        // Ma'lumotni DB ga saqlash
        val success = dbHelper.insertExpenseType(title!!, desc ?: "")

        if (success) {
            // Qo‘shilganini bildirish
            MaterialAlertDialogBuilder(this)
                .setTitle("Muvaffaqiyatli!")
                .setMessage("Yangi xarajat turi qo‘shildi.")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                    editTypeTitle.setText("")
                    editTypeDesc.setText("")
                }
                .show()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("Xatolik!")
                .setMessage("Bu turdagi xarajat allaqachon mavjud.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dbHelper.close()
    }
}
