package com.ubaydullayev.dailyoutcoming

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.ubaydullayev.dailyoutcoming.adapter.ExpensesAdapter
import com.ubaydullayev.dailyoutcoming.adapter.InComingAdapter
import com.ubaydullayev.dailyoutcoming.avtivity.ExpensesActivity
import com.ubaydullayev.dailyoutcoming.avtivity.IncomingActivity
import com.ubaydullayev.dailyoutcoming.avtivity.TypeOfExpensesActivity
import com.ubaydullayev.dailyoutcoming.avtivity.ValutaActivity
import com.ubaydullayev.dailyoutcoming.databinding.ActivityMainBinding
import java.util.Locale
import com.ubaydullayev.dailyoutcoming.database.MyDatabaseHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: MyDatabaseHelper
    private lateinit var incomeAdapter: InComingAdapter
    private lateinit var expensesAdapter: ExpensesAdapter
    private lateinit var incomeCursor: Cursor
    private lateinit var expensesCursor: Cursor
    private var isFabOpen = false
    private lateinit var rotateOpen: Animation
    private lateinit var rotateClose: Animation
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var tabLayout: TabLayout
    private lateinit var inComingSumTxt: TextView
    private lateinit var expensesSumTxt: TextView

    //  Activity so'rov kodlari
    companion object {
        private const val REQUEST_ADD_EXPENSE = 1003
        private const val REQUEST_ADD_INCOME = 1004
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //  Status bar ko‘rinishi
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            false

        //  Ma'lumotlar bazasi va viewlarni tayyorlash
        dbHelper = MyDatabaseHelper(this)
        inComingSumTxt = findViewById(R.id.income_summa)
        expensesSumTxt = findViewById(R.id.expenses_summa)

        val recyclerIncome = findViewById<RecyclerView>(R.id.recyclerIncome)
        val recyclerExpenses = findViewById<RecyclerView>(R.id.recyclerExpenses)
        tabLayout = findViewById(R.id.tab_layout)

        recyclerIncome.layoutManager = LinearLayoutManager(this)
        recyclerExpenses.layoutManager = LinearLayoutManager(this)

        incomeCursor = dbHelper.getAllIncomingAddDataCursor()
        expensesCursor = dbHelper.getAllExpenseDataCursor()

        //  Adapterlarni yaratamiz
        incomeAdapter = InComingAdapter(this, incomeCursor) { id ->
            dbHelper.deleteIncome(id)
            refreshData()
        }
        expensesAdapter = ExpensesAdapter(this, expensesCursor) { id ->
            dbHelper.deleteExpense(id)
            refreshData()
        }

        binding.filterBtn.setOnClickListener {
            showFilterDialog()
        }
        recyclerIncome.adapter = incomeAdapter
        recyclerExpenses.adapter = expensesAdapter

        //  TabLayout sozlash
        tabLayout.addTab(tabLayout.newTab().setText("Incoming"))
        tabLayout.addTab(tabLayout.newTab().setText("Expenses"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        recyclerIncome.visibility = View.VISIBLE
                        recyclerExpenses.visibility = View.GONE
                    }

                    1 -> {
                        recyclerIncome.visibility = View.GONE
                        recyclerExpenses.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        //  Drawer menyu
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navSide
        val headerView = navView.getHeaderView(0)
        headerView.findViewById<TextView>(R.id.name_text).text = "Ubaydullayev Jasurbek"

        binding.navigationTopBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        //  FAB animatsiyalar
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open)
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close)
        fabOpen = AnimationUtils.loadAnimation(this, R.anim.translate_open)
        fabClose = AnimationUtils.loadAnimation(this, R.anim.translate_close)

        binding.addBtn.setOnClickListener {
            if (isFabOpen) closeFabMenu() else openFabMenu()
        }

        binding.incomingBtn.setOnClickListener {
            closeFabMenu(true)
            val intent = Intent(this, IncomingActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_INCOME)
        }

        binding.outcomingBtn.setOnClickListener {
            closeFabMenu(true)
            val intent = Intent(this, ExpensesActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_EXPENSE)
        }
        isFabOpen = savedInstanceState?.getBoolean("FAB_STATE", false) ?: false
        if (isFabOpen) {
            binding.incomingBtn.visibility = View.VISIBLE
            binding.outcomingBtn.visibility = View.VISIBLE
            binding.intcomingTxt.visibility = View.VISIBLE
            binding.outcomingTtx.visibility = View.VISIBLE
            binding.incomingBtn.isClickable = true
            binding.outcomingBtn.isClickable = true
        }

        //  Drawer menyu elementlarini sozlash
        setupDrawerMenu(navView)
    }

    fun showFilterDialog() {

        val dialogView = layoutInflater
            .inflate(R.layout.filter_dialog_bg, null)

        val fromDateEdit = dialogView.findViewById<TextInputEditText>(R.id.fromDataEdit)
        val toDateEdit = dialogView.findViewById<TextInputEditText>(R.id.toDataEdit)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Filtrlash")
            .setView(dialogView)
            .setPositiveButton("Filtrash") { _, _ ->

                val from = fromDateEdit.text.toString()
                val to = toDateEdit.text.toString()

                if (from.isEmpty() || to.isEmpty()) {
                    return@setPositiveButton
                }


            }
            .setNegativeButton("Bekor qilish", null)
            .create()
        dialog.show()

    }


    //  Drawer ichidagi menyularni sozlash
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private fun setupDrawerMenu(navView: NavigationView) {

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.xarajatBtn -> {
                    val intent = Intent(this, TypeOfExpensesActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.languageBtn -> {
                    val languages = arrayOf("Uzbek", "English", "Russian")
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Choose language:")
                    builder.setSingleChoiceItems(languages, -1) { dialog, which ->
                        when (which) {
                            0 -> setLocale("uz")
                            1 -> setLocale("en")
                            2 -> setLocale("ru")
                        }
                        recreate()
                        dialog.dismiss()
                    }
                    builder.show()
                    true
                }

                R.id.valutaCourse -> {
                    val intent = Intent(this, ValutaActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    //  Activity natijalarini qayta ishlash
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ADD_EXPENSE -> {
                if (resultCode == RESULT_OK) {
                    refreshData()
                    Toast.makeText(this, "Xarajat muvaffaqiyatli qo'shildi", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            REQUEST_ADD_INCOME -> {
                if (resultCode == RESULT_OK) {
                    refreshData()
                    Toast.makeText(this, "Daromad muvaffaqiyatli qo'shildi", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun setLocale(localeToSet: String) {
        val locale = Locale(localeToSet)
        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)
        val config = resources.configuration
        config.setLocales(localeList)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        getSharedPreferences("Settings", Context.MODE_PRIVATE).edit().apply {
            putString("My_Lang", localeToSet)
            apply()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateTotals() {
        val incomeTotal = dbHelper.getTotalIncomeAdd()
        val expensesTotal = dbHelper.getTotalExpenseAdd()
        inComingSumTxt.text = "${incomeTotal.toInt()} "
        expensesSumTxt.text = "${expensesTotal.toInt()} "
    }

    override fun onResume() {
        super.onResume()
        refreshData()
        // Faqat FAB ochiq bo'lsa yopamiz
        if (isFabOpen) {
            closeFabMenu(animated = false)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFabOpen) {
            closeFabMenu(animated = false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!incomeCursor.isClosed) incomeCursor.close()
        if (!expensesCursor.isClosed) expensesCursor.close()
        dbHelper.close()
    }

    private fun openFabMenu() {
        if (isFabOpen) return

        binding.addBtn.startAnimation(rotateOpen)

        val viewsToOpen = listOf(
            binding.incomingBtn,
            binding.outcomingBtn,
            binding.intcomingTxt,
            binding.outcomingTtx
        )

        viewsToOpen.forEach { view ->
            view.visibility = View.VISIBLE
            view.startAnimation(fabOpen)
        }

        binding.incomingBtn.isClickable = true
        binding.outcomingBtn.isClickable = true

        isFabOpen = true
    }

    private fun closeFabMenu(animated: Boolean = true) {
        if (!isFabOpen) return

        if (animated) {
            binding.addBtn.startAnimation(rotateClose)

            // Barcha elementlar uchun animatsiya
            val viewsToClose = listOf(
                binding.incomingBtn,
                binding.outcomingBtn,
                binding.intcomingTxt,
                binding.outcomingTtx
            )

            viewsToClose.forEach { view ->
                view.startAnimation(fabClose)
            }

            // Clickable holatini o'zgartirish
            binding.incomingBtn.isClickable = false
            binding.outcomingBtn.isClickable = false

            // Animatsiya tugaganda visibility ni o'zgartirish
            fabClose.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    viewsToClose.forEach { view ->
                        view.visibility = View.GONE
                    }
                    isFabOpen = false
                }

                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
            })
        } else {
            // Animatsiyasiz yopish
            binding.incomingBtn.visibility = View.GONE
            binding.outcomingBtn.visibility = View.GONE
            binding.intcomingTxt.visibility = View.GONE
            binding.outcomingTtx.visibility = View.GONE
            binding.incomingBtn.isClickable = false
            binding.outcomingBtn.isClickable = false
            isFabOpen = false
        }
    }

    private fun refreshData() {
        val newIncomeCursor = dbHelper.getAllIncomingAddDataCursor()
        val newExpensesCursor = dbHelper.getAllExpenseDataCursor()

        incomeAdapter.swapCursor(newIncomeCursor)
        expensesAdapter.swapCursor(newExpensesCursor)

        calculateTotals()
    }


}
