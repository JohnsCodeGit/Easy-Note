package com.whiskey.notes

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item.view.*

class GroupItemsAdapter(
    private val notes: List<NoteModel>,
    private val notesDB: NotesDbHelper,
    private val context: Context
) :
    RecyclerView.Adapter<GroupItemsAdapter.CustomViewHolder>() {


    var noteDB = notesDB

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val rowView =
            LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
        return CustomViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        Log.d("notesSIZE", notes.count().toString())
        return notes.count()
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = notes[position]

        holder.rowView.itemTitle.text = item.title

        holder.item = item
    }

    class CustomViewHolder(val rowView: View, var item: NoteModel? = null) :
        RecyclerView.ViewHolder(rowView)
}