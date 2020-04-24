package com.whiskey.notes

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NoteModel(var note: String, var title: String, var date: String, var group: String) :
    Parcelable