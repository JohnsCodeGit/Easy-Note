package com.whiskey.notes.com.whiskey.notes

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class NotesDbHelper (context: Context,
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
    fun addNote(note: String) {
        val values = ContentValues()
        values.put(COLUMN_NAME, note)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
        db.close()
    }
    fun getAllNote(): ArrayList<String> {
        var notes = ArrayList<String>()

        val cursor = this.readableDatabase.rawQuery(SELECT_NOTE, null)
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                val note: String = cursor.getString(cursor.getColumnIndex("note"))
                notes.add(note)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return notes
    }
    fun deleteAll(){
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_NAME")
    }
    fun DeleteItem(item: String){
        this.writableDatabase.delete(TABLE_NAME, "note = $item", null)

    }
    fun getNoteSize(): Long{
        val db = this.readableDatabase
        var count: Long = DatabaseUtils.queryNumEntries(db, TABLE_NAME)
        return count
    }
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "NotesDB.db"
        val TABLE_NAME = "notes"
        val COLUMN_ID = "_id"
        val COLUMN_NAME = "note"
        val SELECT_NOTE = "SELECT * FROM notes"
    }
}