package com.whiskey.notes

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item.view.*
import kotlinx.android.synthetic.main.group_item.view.itemTitle
import kotlinx.android.synthetic.main.note_row_item.view.*

class GroupItemsAdapter(
    private val notes: List<NoteModel>,
    private val notesDB: NotesDbHelper,
    private val context: Context,
    private val recyclerView: RecyclerView,
    private val deleteAll: CheckBox,
    private val constraintLayout: ConstraintLayout,
    private val btnDelete: Button
) :
    RecyclerView.Adapter<GroupItemsAdapter.CustomViewHolder>() {

    private var checkedItems = ArrayList<Int>()
    var noteDB = notesDB
    private var checkedVisible = false
    private var isAllChecked = false
    private var mCheckItems = SparseBooleanArray()

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
        holder.rowView.setOnClickListener {

            if (checkedVisible) {
                holder.rowView.checkBox.isChecked = !holder.rowView.checkBox.isChecked
                if (!checkedItems.contains(position) && holder.rowView.checkBox.isChecked) {

                    checkedItems.add(position)
                    mCheckItems.put(position, true)
                    Log.d(
                        "itemAdded",
                        mCheckItems[position].toString()
                                + ", "
                                + position.toString()
                    )
                } else if (!holder.rowView.checkBox.isChecked) {
                    if (position < checkedItems.size) {
                        mCheckItems.put(position, false)
                        deleteAll.isChecked = false
                        checkedItems.removeAt(position)
                        Log.d("itemRemoved", position.toString())
                    }
                }
            }
        }

    }

    inner class CustomViewHolder(val rowView: View) :
        RecyclerView.ViewHolder(rowView), View.OnLongClickListener {
        private var checkbox = rowView.checkBoxItem

        init {
            rowView.isLongClickable = true
            rowView.setOnLongClickListener(this)


        }

        fun bind(position: Int) { // use the sparse boolean array to check
            rowView.checkBox.isChecked = mCheckItems.get(position, false)
        }

        @SuppressLint("ClickableViewAccessibility")

        override fun onLongClick(v: View?): Boolean {
            //Changed the state of check box visibility
            checkedVisible = true
            notifyDataSetChanged()

            checkbox.isChecked = true
            recyclerView.isLayoutFrozen = true
            recyclerView.setOnTouchListener { _, event ->

                if (event.action == MotionEvent.ACTION_UP) {
                    recyclerView.isLayoutFrozen = false

                }
                false
            }
            return true
        }
    }
}