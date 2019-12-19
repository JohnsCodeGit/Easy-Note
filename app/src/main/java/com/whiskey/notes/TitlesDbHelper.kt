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
        val CREATE_PRODUCTS_TABLE = ("CREATE TABLE " + TABLE_NAME
                + " ("
                + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_INDEX
                + " INT, "
                + COLUMN_NAME
                + " TEXT"
                + ")")
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
    fun addTitle(title: String, index: Int) {
        val values = ContentValues()
        values.put(COLUMN_NAME, title)
        val db = this.writableDatabase
        values.put(COLUMN_INDEX, index)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getAllTitle(): ArrayList<String> {
        val titles = ArrayList<String>()

        val cursor = this.readableDatabase.rawQuery(SELECT_Title, null)
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast) {
                if (cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) != null) {
                    val title: String = cursor.getString(cursor.getColumnIndex("title"))
                    titles.add(title)

                }
                cursor.moveToNext()
            }
        }
        cursor.close()
        return titles
    }fun deleteAll(){
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_NAME")
    }

    fun deleteItem(item: Int){
        val db = writableDatabase
        val items = item.toString()
        db.delete(TABLE_NAME, "item = $items", null)

        db.execSQL(
            "UPDATE $TABLE_NAME  SET $COLUMN_INDEX  = $COLUMN_INDEX -1   WHERE  + $COLUMN_INDEX >  $items "

        )

    }

    companion object {
        private const val DATABASE_VERSION = 27
        private const val DATABASE_NAME = "titleDB.db"
        const val TABLE_NAME = "titles"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "title"
        const val SELECT_Title = "SELECT * FROM titles"
        const val COLUMN_INDEX = "item"

    }
}