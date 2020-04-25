package com.whiskey.notes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.group_item.view.*
import kotlinx.android.synthetic.main.note_row_item.view.*
import kotlinx.android.synthetic.main.note_row_item.view.itemTitle
import java.util.*
import kotlin.collections.ArrayList

class GroupsAdapter(
    private var bDelete: Button,
    var deleteAll: CheckBox,
    private var buttonLayout: ConstraintLayout,
    var context: Context,

    var recyclerviewMain: RecyclerView,
    var noteList: ArrayList<String>,
    var searchItems: ArrayList<String>,
    private var textView5: TextView,
    var fab: FloatingActionButton
) : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>(), Filterable {
    private var checkedItems = ArrayList<Int>()
    private var checkedVisible = false
    private var isAllChecked = false
    private var mCheckItems = SparseBooleanArray()
    private val groupsDB = GroupsDB(this.context, null)
    private val notesDB = NotesDB(this.context, null)
    private val notes = notesDB.getAllNote()
    private lateinit var groupName: String

    override fun getItemCount(): Int {

        searchItems = groupsDB.getAllGroups()
        return searchItems.size
    }

    fun hideItems(){
        checkedVisible = false
        checkedItems.clear()
        mCheckItems.clear()
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val customView = layoutInflater.inflate(R.layout.group_item, parent, false)
        searchItems = groupsDB.getAllGroups()

        return GroupViewHolder(customView)

    }

    override fun onViewRecycled(holder: GroupViewHolder) {
        holder.customView.checkBoxItem.setOnCheckedChangeListener(null)

    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {

        searchItems = groupsDB.getAllGroups()
        holder.customView.itemTitle.text = searchItems[position]
        groupName = searchItems[position]

        //DO NOT TOUCH
        if(searchItems.size == checkedItems.size){


            holder.customView.checkBoxItem.isChecked = isAllChecked

            mCheckItems.clear()

        }
        else {
            holder.bind(position)
        }


        if (searchItems.isNotEmpty()) {

            //Delete all items
            deleteAll.setOnCheckedChangeListener{_, isChecked ->
                if (isChecked){
                    checkedItems.clear()

                    for(i in 0 until noteList.size){

                        if (checkedItems.contains(i))
                        else {
                            checkedItems.add(i)
                            mCheckItems.put(i, true)


                        }
                    }

                    selectAll()

                } else if (!isChecked && searchItems.size == checkedItems.size && !holder.customView.checkBoxItem.isSelected) {


                    unSelectAll()
                    checkedItems.clear()
                    mCheckItems.clear()

                }

            }

            // Add checked check boxes to array to delete checked items
            // and save checked states while scrolling
            holder.customView.checkBoxItem.setOnCheckedChangeListener { _, isChecked ->


                if (isChecked) {

                    if(checkedItems.contains(position)){}
                    else {

                        checkedItems.add(position)
                        mCheckItems.put(position, true)


                    }
                }
                else if(!isChecked && deleteAll.isChecked){
                    checkedItems.clear()
                    mCheckItems.clear()
                    for(i in 0 until searchItems.size){

                        checkedItems.add(i)
                        mCheckItems.put(i, true)
                    }
                    holder.customView.checkBox.isChecked = false
                    mCheckItems.put(position, false)
                    checkedItems.remove(position)

                }
                else if(!isChecked && !deleteAll.isChecked){

                    mCheckItems.put(position, false)
                    checkedItems.remove(position)


                }


            }


            holder.customView.checkBoxItem.setOnClickListener {
                deleteAll.isSelected = false

                deleteAll.isChecked = false

            }

            // Delete items
            bDelete.setOnClickListener {

                if (deleteAll.isChecked){

                    deleteAll(holder.customView, deleteAll, bDelete)
                    notifyDataSetChanged()

                }else {

                    deleteItems(holder.customView, deleteAll, bDelete)
                    notifyDataSetChanged()

                }


            }
            if (checkedItems.contains(0) && noteList.size == 1) {
                holder.customView.checkBoxItem.isChecked = true

            }

            fun hideOrShow() {
                if(checkedVisible) {

                    holder.customView.checkBoxItem.visibility = View.VISIBLE
                    //holder.customView.checkBoxItem.isChecked = true
                    bDelete.visibility = View.VISIBLE
//                    holder.customView.button.visibility = View.VISIBLE
                    deleteAll.visibility = View.VISIBLE
                    buttonLayout.visibility = View.VISIBLE
                }
                else if(!checkedVisible) {
                    holder.customView.checkBoxItem.visibility = View.GONE
                    bDelete.visibility = View.GONE
//                    holder.customView.button.visibility = View.GONE
                    deleteAll.visibility = View.GONE
                    checkedItems.clear()
                    mCheckItems.clear()
                }

            }

            if (noteList.isEmpty()) {
                checkedVisible = false
            }

            //Show or hide check boxes
            hideOrShow()




            holder.customView.setOnClickListener {

                if (checkedVisible){
                    holder.customView.checkBoxItem.isChecked =
                        !holder.customView.checkBoxItem.isChecked
                    if (!checkedItems.contains(position) && holder.customView.checkBoxItem.isChecked) {

                        checkedItems.add(position)
                        mCheckItems.put(position, true)
                    } else if (!holder.customView.checkBoxItem.isChecked) {
                        if(position < checkedItems.size) {
                            mCheckItems.put(position, false)
                            deleteAll.isChecked = false
                            checkedItems.removeAt(position)

                        }
                    }
                }
                else {

                    val intent = Intent(holder.customView.context, GroupItemsList::class.java)
                    intent.putExtra("groupPos", position)

                    holder.customView.checkBoxItem.visibility = View.GONE
                    //holder.customView.button.visibility = View.GONE
                    bDelete.visibility = View.GONE
                    checkedItems.clear()
                    mCheckItems.clear()

                    startActivity(holder.customView.context, intent, null)

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
    private fun deleteAll(view: View, delete: CheckBox, btn: Button){
        val dialogBuilder =
            AlertDialog.Builder(view.context, R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to delete all notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") {
                    dialog, _-> dialog.dismiss()
                for (i in checkedItems.indices) {
                    if (checkedItems.isEmpty()) {
                        break
                    } else {
//                        val groupPosition = notesDB.getGroupPositions(groupName)
//
//                        for (j in groupPosition.indices)
//                            notesDB.updateGroup("", groupPosition[i])

                        groupsDB.deleteItem(checkedItems[0] + 1 - i)
                        noteList.removeAt(checkedItems[0] - i)
                        searchItems.removeAt(checkedItems[0] - i)
                        checkedItems.removeAt(0)
                    }

                }

                searchItems.clear()
                noteList.clear()
                groupsDB.deleteAll()

                checkedVisible = false
                hideItems()
                clearAllItems()

                delete.visibility = View.GONE
                btn.visibility = View.GONE
                buttonLayout.visibility = View.GONE
                deleteAll.isSelected = false
                unSelectAll()
                deleteAll.isChecked = false
                if (noteList.isNotEmpty()) {
                    textView5.visibility = View.GONE
                } else
                    textView5.visibility = View.VISIBLE
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun deleteItems(view: View, delete: CheckBox, btn: Button){
        val dialogBuilder =
            AlertDialog.Builder(view.context, R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to delete the selected notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") {
                    dialog, _-> dialog.dismiss()
                checkedItems.sort()


                for (i in checkedItems.indices) {
                    if (checkedItems.isEmpty()) {
                        break
                    }
                    else{
//                        val groupPositions = notesDB.getGroupPositions(groupName)
//                        Log.d("groupSize", groupPositions.toString())
//                        for (j in groupPositions.indices)
//                            notesDB.updateGroup("", groupPositions[i])

                        groupsDB.deleteItem(checkedItems[0] + 1 - i)
                        noteList.removeAt(checkedItems[0]-i)
                        searchItems.removeAt(checkedItems[0]-i)
                        checkedItems.removeAt(0)
                    }

                }
                checkedVisible = false
                clearAllItems()
                hideItems()
                delete.visibility = View.GONE
                btn.visibility = View.GONE
                buttonLayout.visibility = View.GONE
                deleteAll.isSelected = false
                unSelectAll()
                deleteAll.isChecked = false
                if (noteList.isNotEmpty()) {
                    textView5.visibility = View.GONE

                } else
                    textView5.visibility = View.VISIBLE
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()

    }

    inner class GroupViewHolder(val customView: View) : RecyclerView.ViewHolder(customView),
        View.OnLongClickListener {
        private var checkbox = customView.checkBoxItem

        init {
            customView.isLongClickable = true
            customView.setOnLongClickListener(this)
            searchItems = groupsDB.getAllGroups()

        }

        fun bind(position: Int) { // use the sparse boolean array to check
            checkbox.isChecked = mCheckItems.get(position, false)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onLongClick(v: View?): Boolean {
            //Changed the state of check box visibility
            checkedVisible = true
            notifyDataSetChanged()

            checkbox.isChecked = true
            recyclerviewMain.isLayoutFrozen = true
            recyclerviewMain.setOnTouchListener { _, event ->

                if(event.action == MotionEvent.ACTION_UP){
                    recyclerviewMain.isLayoutFrozen = false

                }
                false
            }
            return true
        }
    }

    override fun getFilter(): Filter {
        searchItems.clear()
        return object: Filter(){

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                if(constraint == null || constraint.isEmpty()){

                    searchItems.addAll(noteList)
                }
                else{

                    val filterPattern =
                        constraint.toString().toLowerCase(Locale.getDefault()).trim()
                    for (item: String in noteList) {
                        val noteItem = item.replace("\n", " ")
                        noteItem.replace("\t", " ")


                        if (item.toLowerCase(Locale.getDefault()).trim().contains(filterPattern)
                            || item.toLowerCase(Locale.getDefault()).trim().contains(
                                filterPattern
                            )
                            && (item.isNotBlank())
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