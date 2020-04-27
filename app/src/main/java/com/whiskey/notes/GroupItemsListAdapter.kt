package com.whiskey.notes

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item.view.checkBoxItem
import kotlinx.android.synthetic.main.group_item.view.dateText
import kotlinx.android.synthetic.main.group_item.view.itemTitle
import kotlinx.android.synthetic.main.group_item_note.view.*
import java.util.*
import kotlin.collections.ArrayList

class GroupItemsListAdapter(
    private val notes: ArrayList<NoteModel>,
    private val notesDB: NotesDB,
    private val recyclerView: RecyclerView,
    private val deleteAll: CheckBox,
    private val constraintLayout: ConstraintLayout,
    private val btnDelete: Button,
    private val groupTitle: String,
    private val textView: TextView,
    private var searchItems: ArrayList<NoteModel>

) :
    RecyclerView.Adapter<GroupItemsListAdapter.CustomViewHolder>(), Filterable {

    private var checkedItems = ArrayList<Int>()
    private var noteDB = notesDB
    private val noteList = noteDB.getAllNote()
    private var checkedVisible = false
    private var isAllChecked = false
    private var mCheckItems = SparseBooleanArray()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val rowView =
            LayoutInflater.from(parent.context).inflate(R.layout.group_item_note, parent, false)

        return CustomViewHolder(rowView)
    }

    override fun getItemCount() = searchItems.size

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
        val item = searchItems[position]
        holder.rowView.itemTitle.text = item.title
        holder.rowView.itemNote2.text = item.note
        holder.rowView.dateText.text = item.date
        if (searchItems.size == checkedItems.size) {


            holder.rowView.checkBoxItem.isChecked = isAllChecked

            mCheckItems.clear()

        } else {
            holder.bind(position)
        }
        if (checkedItems.contains(0) && searchItems.size == 1) {
            holder.rowView.checkBoxItem.isChecked = true

        }
        if (searchItems.size != 0) {

            //Delete all items
            deleteAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedItems.clear()
                    for (i in 0 until searchItems.size) {

                        if (checkedItems.contains(i))
                        else {
                            checkedItems.add(i)
                            mCheckItems.put(i, true)
                        }
                    }
                    selectAll()
                } else if (!isChecked && searchItems.size == checkedItems.size &&
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
                    for (i in searchItems.indices) {

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
                for (i in searchItems.indices) {

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
        if (searchItems.isEmpty()) {
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
                val intent = Intent(holder.rowView.context, ViewNoteActivity::class.java)

                holder.rowView.checkBoxItem.visibility = View.GONE
                holder.rowView.button2.visibility = View.GONE
                btnDelete.visibility = View.GONE

                checkedItems.clear()
                mCheckItems.clear()

                intent.putExtra("title", searchItems[position].title)
                intent.putExtra("note", searchItems[position].note)
                intent.putExtra("date", searchItems[position].date)
                intent.putExtra("group", searchItems[position].group)
                intent.putParcelableArrayListExtra("noteList", noteList)
                intent.putParcelableArrayListExtra("searchItems", searchItems)

                if (noteList == searchItems) {
                    intent.putExtra("position", position)
                } else {
                    intent.putExtra("position", noteList.indexOf(searchItems[position]))
                }
                startActivity(holder.rowView.context, intent, null)
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
                            noteList.indexOf(searchItems[checkedItems[0] - i]) + 1
                        )

                        searchItems.removeAt(checkedItems[0] - i)
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
                if (searchItems.isNotEmpty()) {
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

    override fun getFilter(): Filter {
        searchItems.clear()
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                if (constraint == null || constraint.isEmpty()) {

                    searchItems.addAll(noteList)
                } else {

                    val filterPattern =
                        constraint.toString().toLowerCase(Locale.getDefault()).trim()
                    for (item: NoteModel in noteList) {
                        val noteItem = item.note.replace("\n", " ")
                        noteItem.replace("\t", " ")


                        if (item.note.toLowerCase(Locale.getDefault()).trim()
                                .contains(filterPattern)
                            || item.title.toLowerCase(Locale.getDefault()).trim().contains(
                                filterPattern
                            )
                            && (item.title.isNotBlank() || item.note.isNotBlank())
                        ) {
                            searchItems.add(item)


                        }
                    }


                }
                val filterResult = FilterResults()
                filterResult.values = (searchItems)

                return filterResult
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {


                notifyDataSetChanged()
            }

        }
    }
}