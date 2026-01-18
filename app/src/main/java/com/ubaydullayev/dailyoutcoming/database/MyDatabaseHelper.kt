package com.ubaydullayev.dailyoutcoming.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ubaydullayev.dailyoutcoming.model.ValutaModel

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "EXPENSE-INCOME-DB"
        const val DB_VERSION = 2

        // Main table (Expense + Income)
        const val EXPENSE_INCOME_TABLE = "expense_income_table"
        const val EXP_ADD_ID = "id"
        const val EXP_ADD_NAME = "name"
        const val EXP_ADD_DATE = "date"
        const val EXP_ADD_AMOUNT = "amount"
        const val EXP_ADD_CURRENCY = "currency"
        const val EXPENSE_INCOME_TYPE = "expense_income_type" // either EXPENSE or INCOME

        // Category table
        const val CATEGORY = "category"
        const val EXP_TYPE_ID = "id"
        const val EXP_TYPE_NAME = "name"
        const val EXP_TYPE_DEC = "description"

        const val CURRENCY_TABLE = "currency_table"
        const val CUR_ID = "id"

        const val CUR_CURRENCY = "currency"
        const val CUR_AMOUNT = "amount"
        const val CUR_FLAG = "flag"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Main table
        db!!.execSQL(
            """
            CREATE TABLE $EXPENSE_INCOME_TABLE(
                $EXP_ADD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $EXP_ADD_NAME TEXT,
                $EXP_ADD_DATE TEXT,
                $EXP_ADD_AMOUNT REAL,
                $EXP_ADD_CURRENCY TEXT,
                $EXPENSE_INCOME_TYPE TEXT
            )
            """.trimIndent()
        )

        // Category table
        db.execSQL(
            """
            CREATE TABLE $CATEGORY(
                $EXP_TYPE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $EXP_TYPE_NAME TEXT,
                $EXP_TYPE_DEC TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE $CURRENCY_TABLE(
            $CUR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $CUR_CURRENCY TEXT,
            $CUR_AMOUNT REAL DEFAULT 1.00,
            $CUR_FLAG INTEGER
            )
        """.trimIndent()
        )

    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        if (oldVersion < 2){
            db?.execSQL(
                """
            CREATE TABLE IF NOT EXISTS $CURRENCY_TABLE(
            $CUR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $CUR_CURRENCY TEXT,
            $CUR_AMOUNT REAL,
            $CUR_FLAG INTEGER
            )
        """.trimIndent()
            )
        }
    }
    // CURRENCY FUNCTION

    fun insertCurrency(currency: String, amount: Double, flag: Int): Boolean {

        val db = writableDatabase
        val values = ContentValues().apply {
            put(CUR_CURRENCY, currency)
            put(CUR_AMOUNT, amount)
            put(CUR_FLAG, flag)
        }
        val result = db.insert(CURRENCY_TABLE, null, values)
        db.close()
        return result != -1L
    }

    //  CATEGORY FUNCTIONS
    fun insertExpenseType(title: String, description: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(EXP_TYPE_NAME, title)
            put(EXP_TYPE_DEC, description)
        }
        val id = db.insert(CATEGORY, null, cv)
        return id != -1L
    }

    fun getAllCategoryType(): List<CategoryType> {
        val list = mutableListOf<CategoryType>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $CATEGORY", null)
        if (cursor.moveToFirst()) {
            do {
                list.add(
                    CategoryType(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(EXP_TYPE_ID)),
                        title = cursor.getString(cursor.getColumnIndexOrThrow(EXP_TYPE_NAME)),
                        description = cursor.getString(cursor.getColumnIndexOrThrow(EXP_TYPE_DEC))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
    //  INCOME FUNCTIONS

    fun insertIncomingAdd(title: String, date: String, amount: String, currency: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(EXP_ADD_NAME, title)
            put(EXP_ADD_DATE, date)
            put(EXP_ADD_AMOUNT, amount)
            put(EXP_ADD_CURRENCY, currency)
            put(EXPENSE_INCOME_TYPE, "INCOME")
        }
        val id = db.insert(EXPENSE_INCOME_TABLE, null, cv)
        return id != -1L
    }

    fun getAllIncomingAddDataCursor(): Cursor {
        val db = readableDatabase
        return db.rawQuery(
            "SELECT * FROM $EXPENSE_INCOME_TABLE WHERE $EXPENSE_INCOME_TYPE = 'INCOME' ORDER BY $EXP_ADD_ID DESC",
            null
        )
    }
    //  EXPENSE FUNCTIONS

    fun insertExpenseAdd(title: String, date: String, amount: String, currency: String): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(EXP_ADD_NAME, title)
            put(EXP_ADD_DATE, date)
            put(EXP_ADD_AMOUNT, amount)
            put(EXP_ADD_CURRENCY, currency)
            put(EXPENSE_INCOME_TYPE, "EXPENSE")
        }
        val id = db.insert(EXPENSE_INCOME_TABLE, null, cv)
        return id != -1L
    }
    fun getAllExpenseDataCursor(): Cursor {
        val db = readableDatabase
        return db.rawQuery(
            "SELECT * FROM $EXPENSE_INCOME_TABLE WHERE $EXPENSE_INCOME_TYPE = 'EXPENSE' ORDER BY $EXP_ADD_ID DESC",
            null
        )
    }
    //  DELETE FUNCTIONS

    fun deleteIncome(id: Int): Int {
        val db = writableDatabase
        return db.delete(EXPENSE_INCOME_TABLE, "$EXP_ADD_ID=?", arrayOf(id.toString()))
    }

    fun deleteExpense(id: Int): Int {
        val db = writableDatabase
        return db.delete(EXPENSE_INCOME_TABLE, "$EXP_ADD_ID=?", arrayOf(id.toString()))
    }


    //  TOTALS

    fun getTotalIncomeAdd(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($EXP_ADD_AMOUNT) FROM $EXPENSE_INCOME_TABLE WHERE $EXPENSE_INCOME_TYPE = 'INCOME'",
            null
        )
        var total = 0.0
        if (cursor.moveToFirst()) total = cursor.getDouble(0)
        cursor.close()
        return total
    }

    fun getTotalExpenseAdd(): Double {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT SUM($EXP_ADD_AMOUNT) FROM $EXPENSE_INCOME_TABLE WHERE $EXPENSE_INCOME_TYPE = 'EXPENSE'",
            null
        )
        var total = 0.0
        if (cursor.moveToFirst()) total = cursor.getDouble(0)
        cursor.close()
        return total
    }

    fun getAllCurrencies(): List<ValutaModel> {
        val list = mutableListOf<ValutaModel>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $CURRENCY_TABLE", null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val currency = cursor.getString(cursor.getColumnIndexOrThrow("currency"))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
                val flag = cursor.getInt(cursor.getColumnIndexOrThrow("flag"))
                list.add(ValutaModel(id, currency, flag, amount))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    fun updateContent(tableName: String, contentValues: ContentValues, condition: String, stringArray: Array<String>): Int{
       val db = writableDatabase
       return db.update(tableName,contentValues,condition,stringArray)
    }
}
