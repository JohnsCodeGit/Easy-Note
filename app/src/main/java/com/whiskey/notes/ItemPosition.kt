package com.whiskey.notes

object ItemPosition {

    private var position: Int = 0
    private var editMode = false

    fun setPosition(positions: Int){

        position = positions
    }
    fun getPosition(): Int{

        return position

    }
    fun setEditMode(mode: Boolean){

        editMode = mode
    }
    fun getEditMode(): Boolean{

        return editMode

    }



}