package com.whiskey.notes

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.note_row_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class GroupAdapter(
    var bDelete: Button,
    var deleteAll: CheckBox,
    var buttonLayout: ConstraintLayout,

    var context: Context,

    var recyclerviewMain: RecyclerView,
    var noteList: ArrayList<String>,
    var searchItems: ArrayList<String>,
    var textView5: TextView,
    var fab: FloatingActionButton
)  : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>(), Filterable {
    private var checkedItems = ArrayList<Int>()
    var deleteList = ArrayList<String>()
    private var checkedVisible = false
    private var isAllChecked = false
    private var mCheckItems = SparseBooleanArray()
    var trashDB: TrashDB = TrashDB(this.context, null)
    private val groupsDB = GroupsDB(this.context, null)

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
        holder.customView.checkBox.setOnCheckedChangeListener(null)

    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {

        searchItems = groupsDB.getAllGroups()
        holder.customView.itemTitle.text = searchItems[position]


        //DO NOT TOUCH
        if(searchItems.size == checkedItems.size){


            holder.customView.checkBox.isChecked = isAllChecked

            mCheckItems.clear()

        }
        else {
            holder.bind(position)
        }
        Log.d("Bound", mCheckItems[position].toString())

        if (searchItems.size != 0) {

            //Delete all items
            deleteAll.setOnCheckedChangeListener{_, isChecked ->
                if (isChecked){
                    checkedItems.clear()

                    for(i in 0 until noteList.size){

                        if (checkedItems.contains(i))
                        else {
                            checkedItems.add(i)
                            mCheckItems.put(i, true)
                            Log.d("itemAdded",
                                mCheckItems[i].toString()
                                        + ", "
                                        + position.toString())
                        }
                    }
                    Log.d("itemAddedAll", checkedItems.size.toString() +", "+ searchItems.size.toString())
                    SelectAll()

                }
                else if(!isChecked && searchItems.size == checkedItems.size && !holder.customView.checkBox.isSelected){
                    Log.d("itemsCleared", checkedItems.size.toString())

                    unSelectAll()
                    checkedItems.clear()
                    mCheckItems.clear()
                    Log.d("itemsCleared", checkedItems.size.toString())
                }

            }

            // Add checked check boxes to array to delete checked items
            // and save checked states while scrolling
            holder.customView.checkBox.setOnCheckedChangeListener { _, isChecked ->

                Log.d("itemChecked", isChecked.toString())
                Log.d("itemNotesSize", searchItems.size.toString())


                if (isChecked) {

                    if(checkedItems.contains(position)){}
                    else {

                        checkedItems.add(position)
                        mCheckItems.put(position, true)
                        Log.d("itemAdded",
                            mCheckItems[position].toString()
                                    + ", "
                                    + position.toString())

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
                    Log.d("itemRemoved", checkedItems.size.toString())
                }
                else if(!isChecked && !deleteAll.isChecked){

                    mCheckItems.put(position, false)
                    checkedItems.remove(position)
                    Log.d("itemRemoved", checkedItems.size.toString())

                }
                Log.d("itemsChecked", mCheckItems[position].toString())
                Log.d("itemDeleteCheckState", deleteAll.isChecked.toString())

            }


            holder.customView.checkBox.setOnClickListener {
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
                holder.customView.checkBox.isChecked = true
                Log.d("forceCheck", true.toString())
            }

            fun Hide(){
                if(checkedVisible) {

                    holder.customView.checkBox.visibility = View.VISIBLE
                    //holder.customView.checkBox.isChecked = true
                    bDelete.visibility = View.VISIBLE
//                    holder.customView.button.visibility = View.VISIBLE
                    deleteAll.visibility = View.VISIBLE
                    buttonLayout.visibility = View.VISIBLE
                }
                else if(!checkedVisible) {
                    holder.customView.checkBox.visibility = View.GONE
                    bDelete.visibility = View.GONE
//                    holder.customView.button.visibility = View.GONE
                    deleteAll.visibility = View.GONE
                    checkedItems.clear()
                    mCheckItems.clear()
                }

            }

            if(noteList.size == 0){
                checkedVisible = false
            }

            //Show or hide check boxes
            Hide()
            Log.d("checkedVisible", checkedVisible.toString())


            holder.customView.setOnClickListener {

                if (checkedVisible){
                    holder.customView.checkBox.isChecked = !holder.customView.checkBox.isChecked
                    if(!checkedItems.contains(position) && holder.customView.checkBox.isChecked){

                        checkedItems.add(position)
                        mCheckItems.put(position, true)
                        Log.d("itemAdded",
                            mCheckItems[position].toString()
                                    + ", "
                                    + position.toString())
                    }
                    else if(!holder.customView.checkBox.isChecked){
                        if(position < checkedItems.size) {
                            mCheckItems.put(position, false)
                            deleteAll.isChecked = false
                            checkedItems.removeAt(position)
                            Log.d("itemRemoved", position.toString())
                        }
                    }
                }
                else {

                    holder.customView.checkBox.visibility = View.GONE
//                    holder.customView.button.visibility = View.GONE
                    bDelete.visibility = View.GONE
                    checkedItems.clear()
                    mCheckItems.clear()

                    
                }
            }
        }


    }
    fun clearAllItems(){
        checkedItems.clear()
        mCheckItems.clear()

    }

    fun SelectAll(){
        isAllChecked = true
        notifyDataSetChanged()


    }
    fun unSelectAll(){
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
                for (i in 0 until checkedItems.size) {
                    if(checkedItems.size == 0) {
                        break
                    } else {
//                        deleteList = trashDB.getAllNote()
//                        val noteModel = NoteModel(
//                            noteList[checkedItems[0] - i].note,
//                            noteList[checkedItems[0] - i].title,
//                            noteList[checkedItems[0] - i].date
//                        )
//                        deleteList.add(noteModel)
//                        trashDB.addNote(
//                            noteList[checkedItems[0] - i].note,
//                            noteList[checkedItems[0] - i].title,
//                            noteList[checkedItems[0] - i].date,
//                            deleteList.size
//                        )
                        groupsDB.deleteItem(checkedItems[0] + 1 - i)
                        noteList.removeAt(checkedItems[0] - i)
                        searchItems.removeAt(checkedItems[0] - i)

//                        notifyItemRemoved(checkedItems[0]-i)
                        checkedItems.removeAt(0)
                        Log.d("itemDeleted List", (checkedItems).toString())


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
                if (noteList.size != 0) {
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
                Log.d("itemDeleted List1", (checkedItems).toString())

                for (i in 0 until checkedItems.size){
                    if(checkedItems.size == 0) {
                        break
                    }
                    else{
//                        deleteList = trashDB.getAllNote()
//                        val noteModel = NoteModel(
//                            noteList[checkedItems[0] - i].note,
//                            noteList[checkedItems[0] - i].title,
//                            noteList[checkedItems[0] - i].date
//                        )
//                        deleteList.add(noteModel)
//                        trashDB.addNote(
//                            noteList[checkedItems[0]-i].note,
//                            noteList[checkedItems[0]-i].title,
//                            noteList[checkedItems[0]-i].date,
//                            deleteList.size
//                        )
                        groupsDB.deleteItem(checkedItems[0] + 1 - i)
                        noteList.removeAt(checkedItems[0]-i)
                        searchItems.removeAt(checkedItems[0]-i)

//                        notifyItemRemoved(checkedItems[0]-i)
                        checkedItems.removeAt(0)
                        Log.d("itemDeleted List", (checkedItems).toString())


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
                if (noteList.size != 0) {
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
        private var checkbox = customView.checkBox

        init {
            customView.isLongClickable = true
            customView.setOnLongClickListener(this)
            searchItems = groupsDB.getAllGroups()

        }

        fun bind(position: Int) { // use the sparse boolean array to check
            customView.checkBox.isChecked = mCheckItems.get(position, false)
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

//        override fun onClick(v: View?) {
//            //Changed the state of check box visibility
//
//            if(checkedVisible){
//                checkedVisible = false
//                notifyDataSetChanged()
//            }
//
//        }


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
                        Log.d("noteItemText", noteItem)

                        if (item.toLowerCase(Locale.getDefault()).trim().contains(filterPattern)
                            || item.toLowerCase(Locale.getDefault()).trim().contains(
                                filterPattern
                            )
                            && (item.isNotBlank())
                        ) {
                            searchItems.add(item)

                            Log.d("addedSearch", searchItems.toString())
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