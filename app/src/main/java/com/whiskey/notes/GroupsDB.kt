package com.whiskey.notes

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class GroupsDB(context: Context?,
               factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME,
        factory, DATABASE_VERSION
    ) {
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = (
                "CREATE TABLE " + TABLE_NAME
                        + " ("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COLUMN_INDEX + " INT, "
                        + COLUMN_NAME + " TEXT, "
                        + COLUMN_NOTE + " TEXT, "
                        + COLUMN_TITLE + " TEXT, "
                        + COLUMN_DATE + " TEXT "
                        + ")"
                )
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addGroup(name: String, index: Int) {
        val values = ContentValues()
        values.put(COLUMN_NAME, name)


        val db = this.writableDatabase
        values.put(COLUMN_INDEX, index)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllGroups(): ArrayList<String> {
        val notes = ArrayList<String>()

        val cursor = this.readableDatabase.rawQuery(SELECT_NOTE, null)
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast){
                if (cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) != null) {
                    val note: String = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))


                    notes.add(note)

                }
                cursor.moveToNext()
            }
        }
        cursor.close()
        return notes
    }

    //    fun getAllGroupItems(position: Int): ArrayList<NoteModel> {
//        val notes = ArrayList<NoteModel>()
//
//        val cursor = this.readableDatabase.rawQuery(SELECT_NOTE, null)
//        if (cursor.moveToFirst()) {
//            while (!cursor.isAfterLast) {
//                if (cursor.getString(cursor.getColumnIndex(COLUMN_NAME)) != null) {
//                    val note: String = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
//                    val title: String = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
//                    val date: String = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
//                    val noteModel = NoteModel(note, title, date)
//                    notes.add(noteModel)
//
//                }
//                cursor.moveToNext()
//            }
//        }
//        cursor.close()
//        return notes
//    }
    fun deleteAll(){
        val db = this.writableDatabase
        db.execSQL("delete from $TABLE_NAME")
    }

    fun updateGroup(noteItem: NoteModel, position: Int) {
        val newValues = ContentValues()
        val note = noteItem.note
        val title = noteItem.title
        val date = noteItem.date


        newValues.put(COLUMN_NOTE, note)
        newValues.put(COLUMN_TITLE, title)
        newValues.put(COLUMN_DATE, date)

        val db = this.writableDatabase
        db.update(TABLE_NAME, newValues, "$COLUMN_INDEX=$position", null)
        db.close()
    }

    fun deleteItem(item: Int){
        val db = this.writableDatabase
        val items = item.toString()
        db.delete(TABLE_NAME, "item = $items", null)

        db.execSQL(
            "UPDATE $TABLE_NAME  SET $COLUMN_INDEX  = $COLUMN_INDEX -1   WHERE  + $COLUMN_INDEX >  $items "

        )
        db.close()
        Log.d("itemDeletedDataBase", item.toString())
    }



    fun getGroupSize(): Long{
        val db = this.readableDatabase
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME)
    }
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "GroupDatabase3.db"
        const val TABLE_NAME = "GroupsTable"
        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "nameCol"
        const val COLUMN_NOTE = "noteCol"
        const val COLUMN_TITLE = "titleCol"
        const val COLUMN_DATE = "dateCol"
        const val SELECT_NOTE = "SELECT * FROM $TABLE_NAME"
        const val COLUMN_INDEX = "item"

    }
}