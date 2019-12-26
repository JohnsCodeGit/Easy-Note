package com.whiskey.notes.com.whiskey.notes

object NoteModel {

    private lateinit var note: String
    private lateinit var title: String
    private lateinit var date: String

    fun setNote(positions: String){

        note = positions
    }
    fun getNote(): String{

        return note

    }
    fun setTitle(titles: String){

        title = titles
    }
    fun getTitle(): String{

        return title

    }
    fun setDate(dates: String){

        date = dates
    }
    fun getDate(): String{

        return date

    }

}