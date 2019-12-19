package com.whiskey.notes.com.whiskey.notes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class dateDbHelper (context: Context,
                     factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME
                + " TEXT" + ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }
    fun addDate(note: String) {
        val values = ContentValues()
        values.put(COLUMN_NAME, note)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getAllDate(): ArrayList<String> {
        var dates = ArrayList<String>()

        val cursor = this.readableDatabase.rawQuery(SELECT_DATE, null)
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                val date: String = cursor.getString(cursor.getColumnIndex("date"))
                dates.add(date)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return dates
    }
    fun deleteAll(){
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_NAME")
    }
    fun DeleteItem(item: String){
        writableDatabase.delete(TABLE_NAME, "date = $item", null)

    }
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "DatesDB.db"
        val TABLE_NAME = "dates"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "date"
        val SELECT_DATE = "SELECT * FROM dates"

    }
}