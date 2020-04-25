package com.whiskey.notes

import android.annotation.SuppressLint
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item.view.checkBoxItem
import kotlinx.android.synthetic.main.group_item.view.dateText
import kotlinx.android.synthetic.main.group_item.view.itemTitle
import kotlinx.android.synthetic.main.group_item_note.view.*

class GroupItemsListAdapter(
    private val notes: ArrayList<NoteModel>,
    private val notesDB: NotesDB,
    private val recyclerView: RecyclerView,
    private val deleteAll: CheckBox,
    private val constraintLayout: ConstraintLayout,
    private val btnDelete: Button,
    private val groupTitle: String,
    private val textView: TextView

) :
    RecyclerView.Adapter<GroupItemsListAdapter.CustomViewHolder>() {

    private var checkedItems = ArrayList<Int>()
    var noteDB = notesDB
    private val noteList = noteDB.getAllNote()
    private var checkedVisible = false
    private var isAllChecked = false
    private var mCheckItems = SparseBooleanArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val rowView =
            LayoutInflater.from(parent.context).inflate(R.layout.group_item_note, parent, false)
        return CustomViewHolder(rowView)
    }

    override fun getItemCount(): Int {

        return notes.count()
    }

    fun hideItems() {
        checkedVisible = false
        checkedItems.clear()
        mCheckItems.clear()
        notifyDataSetChanged()

    }

    override fun onViewRecycled(holder: CustomViewHolder) {
        holder.rowView.checkBoxItem.setOnCheckedChangeListener(null)

    }
    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = notes[position]
        holder.rowView.itemTitle.text = item.title
        holder.rowView.itemNote2.text = item.note
        holder.rowView.dateText.text = item.date
        if (notes.size == checkedItems.size) {


            holder.rowView.checkBoxItem.isChecked = isAllChecked

            mCheckItems.clear()

        } else {
            holder.bind(position)
        }
        if (checkedItems.contains(0) && notes.size == 1) {
            holder.rowView.checkBoxItem.isChecked = true

        }
        if (notes.size != 0) {

            //Delete all items
            deleteAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedItems.clear()
                    for (i in 0 until notes.size) {

                        if (checkedItems.contains(i))
                        else {
                            checkedItems.add(i)
                            mCheckItems.put(i, true)
                        }
                    }
                    selectAll()
                } else if (!isChecked && notes.size == checkedItems.size &&
                    !holder.rowView.checkBoxItem.isSelected
                ) {
                    unSelectAll()
                    checkedItems.clear()
                    mCheckItems.clear()
                }

            }
        }

        // Add checked check boxes to array to delete checked items
        // and save checked states while scrolling
        holder.rowView.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (checkedItems.contains(position)) {
                    } else {
                        checkedItems.add(position)
                        mCheckItems.put(position, true)
                    }
                } else if (!isChecked && deleteAll.isChecked) {
                    checkedItems.clear()
                    mCheckItems.clear()
                    for (i in 0 until notes.size) {

                        checkedItems.add(i)
                        mCheckItems.put(i, true)
                    }
                    holder.rowView.checkBoxItem.isChecked = false
                    mCheckItems.put(position, false)
                    checkedItems.remove(position)

                } else if (!isChecked && !deleteAll.isChecked) {

                    mCheckItems.put(position, false)
                    checkedItems.remove(position)
                }
            }

        btnDelete.setOnClickListener {
                deleteItems(holder.rowView, deleteAll, btnDelete)
                notifyDataSetChanged()
        }
        fun hideOrShow() {
            if (checkedVisible) {

                holder.rowView.checkBoxItem.visibility = View.VISIBLE
                //holder.customView.checkBoxItem.isChecked = true
                btnDelete.visibility = View.VISIBLE
                holder.rowView.button2.visibility = View.VISIBLE
                deleteAll.visibility = View.VISIBLE
                constraintLayout.visibility = View.VISIBLE
            } else if (!checkedVisible) {
                holder.rowView.checkBoxItem.visibility = View.GONE
                btnDelete.visibility = View.GONE
                holder.rowView.button2.visibility = View.GONE
                deleteAll.visibility = View.GONE
                checkedItems.clear()
                mCheckItems.clear()
            }

        }
        holder.rowView.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {

                if (checkedItems.contains(position)) {
                } else {

                    checkedItems.add(position)
                    mCheckItems.put(position, true)


                }
            } else if (!isChecked && deleteAll.isChecked) {
                checkedItems.clear()
                mCheckItems.clear()
                for (i in notes.indices) {

                    checkedItems.add(i)
                    mCheckItems.put(i, true)
                }
                holder.rowView.checkBoxItem.isChecked = false
                mCheckItems.put(position, false)
                checkedItems.remove(position)
            } else if (!isChecked && !deleteAll.isChecked) {

                mCheckItems.put(position, false)
                checkedItems.remove(position)

            }
        }
        if (notes.isEmpty()) {
            checkedVisible = false
        }

        hideOrShow()

        holder.rowView.setOnClickListener {

            if (checkedVisible) {
                holder.rowView.checkBoxItem.isChecked = !holder.rowView.checkBoxItem.isChecked
                if (!checkedItems.contains(position) && holder.rowView.checkBoxItem.isChecked) {

                    checkedItems.add(position)
                    mCheckItems.put(position, true)

                } else if (!holder.rowView.checkBoxItem.isChecked) {
                    if (position < checkedItems.size) {
                        mCheckItems.put(position, false)
                        deleteAll.isChecked = false
                        checkedItems.removeAt(position)

                    }
                }
            } else {
                //TODO: Open Note in ViewNoteActivity
            }
        }

    }

    private fun clearAllItems() {
        checkedItems.clear()
        mCheckItems.clear()
    }

    private fun selectAll() {
        isAllChecked = true
        notifyDataSetChanged()
    }

    private fun unSelectAll() {
        isAllChecked = false
        notifyDataSetChanged()
    }

    private fun deleteItems(view: View, delete: CheckBox, btn: Button) {
        val dialogBuilder =
            AlertDialog.Builder(view.context, R.style.MyDialogTheme)
        Log.d("deletePOS", checkedItems.toString())

        val allNotes = notesDB.getGroup(groupTitle)
        Log.d("deletePOS", allNotes.toString())
        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to delete the selected notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                checkedItems.sort()


                for (i in 0 until checkedItems.size) {
                    if (checkedItems.isEmpty()) {
                        break
                    } else {
                        notesDB.updateGroup(
                            "",
                            noteList.indexOf(notes[checkedItems[0] - i]) + 1
                        )

                        notes.removeAt(checkedItems[0] - i)
                        checkedItems.removeAt(0)
                    }

                }
                checkedVisible = false
                clearAllItems()
                hideItems()
                delete.visibility = View.GONE
                btn.visibility = View.GONE
                constraintLayout.visibility = View.GONE
                deleteAll.isSelected = false
                unSelectAll()
                deleteAll.isChecked = false
                if (notes.isNotEmpty()) {
                    textView.visibility = View.GONE

                } else
                    textView.visibility = View.VISIBLE
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()

    }
    inner class CustomViewHolder(val rowView: View) :
        RecyclerView.ViewHolder(rowView), View.OnLongClickListener {
        private var checkbox = rowView.checkBoxItem

        init {
            rowView.isLongClickable = true
            rowView.setOnLongClickListener(this)


        }

        fun bind(position: Int) { // use the sparse boolean array to check
            rowView.checkBoxItem.isChecked = mCheckItems.get(position, false)
        }

        @SuppressLint("ClickableViewAccessibility")

        override fun onLongClick(v: View?): Boolean {
            //Changed the state of check box visibility
            //Toast.makeText(context, "Long click", Toast.LENGTH_SHORT).show()
            checkedVisible = true
            notifyDataSetChanged()
            constraintLayout.visibility = View.VISIBLE
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