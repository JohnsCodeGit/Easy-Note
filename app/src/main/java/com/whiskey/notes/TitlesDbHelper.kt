package com.whiskey.notes.com.whiskey.notes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TitlesDbHelper (context: Context,
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
    fun addTitle(title: String) {
        val values = ContentValues()
        values.put(COLUMN_NAME, title)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getAllTitle(): ArrayList<String> {
        var titles = ArrayList<String>()

        val cursor = this.readableDatabase.rawQuery(SELECT_Title, null)
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                val title: String = cursor.getString(cursor.getColumnIndex("title"))
                titles.add(title)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return titles
    }fun deleteAll(){
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_NAME")
    }
    fun DeleteItem(item: String){
        writableDatabase.delete(TABLE_NAME, "title = $item", null)

    }
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "TitlesDB.db"
        val TABLE_NAME = "titles"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "title"
        val SELECT_Title = "SELECT * FROM titles"

    }
}