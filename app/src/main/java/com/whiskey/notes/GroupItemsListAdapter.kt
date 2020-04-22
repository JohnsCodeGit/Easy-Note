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
import kotlinx.android.synthetic.main.group_item.view.*

class GroupItemsListAdapter(
    private val notes: ArrayList<NoteModel>,
    private val notesDB: NotesDbHelper,
    private val recyclerView: RecyclerView,
    private val deleteAll: CheckBox,
    private val constraintLayout: ConstraintLayout,
    private val btnDelete: Button,
    private val textView10: TextView,
    private val groupTitle: String
) :
    RecyclerView.Adapter<GroupItemsListAdapter.CustomViewHolder>() {

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
        if (notes.size == checkedItems.size) {


            holder.rowView.checkBoxItem.isChecked = isAllChecked

            mCheckItems.clear()

        } else {
            holder.bind(position)
        }
        if (notes.isNotEmpty()) {

            //Delete all items
            deleteAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedItems.clear()

                    for (i in notes.indices) {

                        if (checkedItems.contains(i))
                        else {
                            checkedItems.add(i)
                            mCheckItems.put(i, true)
                            Log.d(
                                "itemAdded",
                                mCheckItems[i].toString()
                                        + ", "
                                        + position.toString()
                            )
                        }
                    }
                    Log.d(
                        "itemAddedAll",
                        checkedItems.size.toString() + ", " + notes.size.toString()
                    )
                    selectAll()

                } else if (!isChecked && notes.size == checkedItems.size && !holder.rowView.checkBoxItem.isSelected) {
                    Log.d("itemsCleared", checkedItems.size.toString())

                    unSelectAll()
                    checkedItems.clear()
                    mCheckItems.clear()
                    Log.d("itemsCleared", checkedItems.size.toString())
                }

            }
        }
        if (notes.isNotEmpty()) {

            //Delete all items
            deleteAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedItems.clear()

                    for (i in notes.indices) {

                        if (checkedItems.contains(i))
                        else {
                            checkedItems.add(i)
                            mCheckItems.put(i, true)
                            Log.d(
                                "itemAdded",
                                mCheckItems[i].toString()
                                        + ", "
                                        + position.toString()
                            )
                        }
                    }
                    Log.d(
                        "itemAddedAll",
                        checkedItems.size.toString() + ", " + notes.size.toString()
                    )
                    selectAll()

                } else if (!isChecked && notes.size == checkedItems.size && !holder.rowView.checkBoxItem.isSelected) {
                    Log.d("itemsCleared", checkedItems.size.toString())

                    unSelectAll()
                    checkedItems.clear()
                    mCheckItems.clear()
                    Log.d("itemsCleared", checkedItems.size.toString())
                }

            }
        }
        btnDelete.setOnClickListener {

            if (deleteAll.isChecked) {

                deleteAll(holder.rowView, deleteAll, btnDelete)
                notifyDataSetChanged()

            } else {

                deleteItems(holder.rowView, deleteAll, btnDelete)
                notifyDataSetChanged()

            }


        }
        fun hideOrShow() {
            if (checkedVisible) {

                holder.rowView.checkBoxItem.visibility = View.VISIBLE
                //holder.customView.checkBoxItem.isChecked = true
                btnDelete.visibility = View.VISIBLE
//                    holder.customView.button.visibility = View.VISIBLE
                deleteAll.visibility = View.VISIBLE
                constraintLayout.visibility = View.VISIBLE
            } else if (!checkedVisible) {
                holder.rowView.checkBoxItem.visibility = View.GONE
                btnDelete.visibility = View.GONE
//                    holder.customView.button.visibility = View.GONE
                deleteAll.visibility = View.GONE
                checkedItems.clear()
                mCheckItems.clear()
            }

        }
        holder.rowView.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->

            Log.d("itemChecked", isChecked.toString())
            Log.d("itemNotesSize", notes.size.toString())


            if (isChecked) {

                if (checkedItems.contains(position)) {
                } else {

                    checkedItems.add(position)
                    mCheckItems.put(position, true)
                    Log.d(
                        "itemAdded",
                        mCheckItems[position].toString()
                                + ", "
                                + position.toString()
                    )

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
                Log.d("itemRemoved", checkedItems.size.toString())
            } else if (!isChecked && !deleteAll.isChecked) {

                mCheckItems.put(position, false)
                checkedItems.remove(position)
                Log.d("itemRemoved", checkedItems.size.toString())

            }
            Log.d("itemsChecked", mCheckItems[position].toString())
            Log.d("itemDeleteCheckState", deleteAll.isChecked.toString())

        }
        if (notes.isEmpty()) {
            checkedVisible = false
        }
        hideOrShow()
        holder.rowView.itemTitle.text = item.title
        holder.rowView.setOnClickListener {

            if (checkedVisible) {
                holder.rowView.checkBoxItem.isChecked = !holder.rowView.checkBoxItem.isChecked
                if (!checkedItems.contains(position) && holder.rowView.checkBoxItem.isChecked) {

                    checkedItems.add(position)
                    mCheckItems.put(position, true)
                    Log.d(
                        "itemAdded",
                        mCheckItems[position].toString()
                                + ", "
                                + position.toString()
                    )
                } else if (!holder.rowView.checkBoxItem.isChecked) {
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

    private fun deleteAll(view: View, delete: CheckBox, btn: Button) {
        val dialogBuilder =
            AlertDialog.Builder(view.context, R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to delete all notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                for (i in 0 until checkedItems.size) {
                    if (checkedItems.isEmpty()) {
                        break
                    } else {
                        notes.removeAt(checkedItems[0] - i)
                        notesDB.updateGroup(
                            "9MM(@{M_|^9rcR)K3[3-j.Qm",
                            checkedItems[0] - i + 1
                        )

                        notifyItemRemoved(checkedItems[0] - i)
                        checkedItems.removeAt(0)
                        Log.d("itemDeleted List", (checkedItems).toString())
                    }
                }
                notes.clear()
                checkedVisible = false
                hideItems()
                clearAllItems()

                delete.visibility = View.GONE
                btn.visibility = View.GONE
                constraintLayout.visibility = View.GONE
                deleteAll.isSelected = false
                unSelectAll()
                deleteAll.isChecked = false
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun deleteItems(view: View, delete: CheckBox, btn: Button) {
        val dialogBuilder =
            AlertDialog.Builder(view.context, R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to delete the selected notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                checkedItems.sort()
                Log.d("itemDeleted List1", (checkedItems).toString())

                for (i in 0 until checkedItems.size) {
                    if (checkedItems.isEmpty()) {
                        break
                    } else {
                        notes.removeAt(checkedItems[0] - i)
                        notesDB.updateGroup(
                            "9MM(@{M_|^9rcR)K3[3-j.Qm",
                            checkedItems[0] + 1
                        )
                        Log.d("groupItemsList", notesDB.getGroup(groupTitle).toString())
                        Log.d(
                            "itemDeletedList",
                            (checkedItems).toString() + (checkedItems[0] + 1).toString()
                        )

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