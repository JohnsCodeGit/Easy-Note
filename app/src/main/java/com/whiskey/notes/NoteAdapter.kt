package com.whiskey.notes


import android.annotation.SuppressLint
import android.content.Context
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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.note_row_item.view.*
import java.util.*
import kotlin.collections.ArrayList


class NoteAdapter(

    private var bDelete: Button,
    private var deleteAll: CheckBox,
    private var buttonLayout: ConstraintLayout,
    private var fab: FloatingActionButton,
    private var context: Context,
    private var noteDB: NotesDB,
    private var recyclerviewMain: RecyclerView,
    private var noteList: ArrayList<NoteModel>,
    private var searchItems: ArrayList<NoteModel>,
    private var textView5: TextView,
    private var addToGroup: ImageButton

)
    : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(), Filterable {
    private var checkedItems = ArrayList<Int>()
    private var deleteList = ArrayList<NoteModel>()
    private var checkedVisible = false
    private var isAllChecked = false
    private var mCheckItems = SparseBooleanArray()
    private var trashDB: TrashDB = TrashDB(this.context, null)
    private lateinit var groupName: String

    override fun getItemCount() = searchItems.size

    fun hideItems(){
        checkedVisible = false
        checkedItems.clear()
        mCheckItems.clear()
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val customView = layoutInflater.inflate(R.layout.note_row_item, parent, false)

        return NoteViewHolder(customView)

    }

    override fun onViewRecycled(holder: NoteViewHolder) {
        holder.customView.checkBox.setOnCheckedChangeListener(null)

    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        holder.customView.dateText.text  = searchItems[position].date
        holder.customView.itemTitle.text = searchItems[position].title
        holder.customView.itemNote.text  = searchItems[position].note

        groupName = searchItems[position].group

        //DO NOT TOUCH
        if(searchItems.size == checkedItems.size){

            holder.customView.checkBox.isChecked = isAllChecked

            mCheckItems.clear()

        }
        else {
            holder.bind(position)
        }


        if (!searchItems.isNullOrEmpty()) {

            //Delete all items
            deleteAll.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedItems.clear()
                    for (i in noteList.indices) {

                        if (checkedItems.contains(i)) {
                        }
                        else {
                            checkedItems.add(i)
                            mCheckItems.put(i, true)
                        }
                    }
                    selectAll()
                } else if (!isChecked && searchItems.size == checkedItems.size && !holder.customView.checkBox.isSelected) {
                    unSelectAll()
                    checkedItems.clear()
                    mCheckItems.clear()
                }

            }

            // Add checked check boxes to array to delete checked items
            // and save checked states while scrolling
            holder.customView.checkBox.setOnCheckedChangeListener { _, isChecked ->
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

                    holder.customView.checkBox.isChecked = false
                    mCheckItems.put(position, false)
                    checkedItems.remove(position)

                } else if (!isChecked && !deleteAll.isChecked) {

                        mCheckItems.put(position, false)
                        checkedItems.remove(position)
                }
            }

            holder.customView.checkBox.setOnClickListener {
                deleteAll.isSelected = false
                deleteAll.isChecked = false
            }

            addToGroup.setOnClickListener {
                val groupDb = GroupsDB(this.context, null)
                val groupListArray: ArrayList<String> = groupDb.getAllGroups()
                val intent = Intent(holder.customView.context, AddToGroup::class.java)
                intent.putStringArrayListExtra("groupList", groupListArray)
                intent.putIntegerArrayListExtra("itemPositionList", checkedItems)

                startActivity(this.context, intent, null)

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

            }

            fun hide() {
                if(checkedVisible) {

                    holder.customView.checkBox.visibility = View.VISIBLE
                    bDelete.visibility = View.VISIBLE
                    holder.customView.button.visibility = View.VISIBLE
                    deleteAll.visibility = View.VISIBLE
                    buttonLayout.visibility = View.VISIBLE
                    fab.isVisible = false
                }
                else if(!checkedVisible) {
                    holder.customView.checkBox.visibility = View.GONE
                    bDelete.visibility = View.GONE
                    holder.customView.button.visibility = View.GONE
                    deleteAll.visibility = View.GONE
                    fab.isVisible = true
                    checkedItems.clear()
                    mCheckItems.clear()
                }

            }

            if (noteList.isEmpty()) {
                checkedVisible = false
            }

            //Show or hide check boxes
            hide()

            holder.customView.setOnClickListener {

                if (checkedVisible){
                    holder.customView.checkBox.isChecked = !holder.customView.checkBox.isChecked
                    if(!checkedItems.contains(position) && holder.customView.checkBox.isChecked){
                        checkedItems.add(position)
                        mCheckItems.put(position, true)
                    }
                    else if(!holder.customView.checkBox.isChecked){
                        if(position < checkedItems.size) {
                            mCheckItems.put(position, false)
                            deleteAll.isChecked = false
                            checkedItems.removeAt(position)

                        }
                    }
                }
                else {
                    val intent = Intent(holder.customView.context, ViewNoteActivity::class.java)

                    holder.customView.checkBox.visibility = View.GONE
                    holder.customView.button.visibility = View.GONE
                    bDelete.visibility = View.GONE

                    checkedItems.clear()
                    mCheckItems.clear()

                    intent.putExtra("title", searchItems[position].title)
                    intent.putExtra("note", searchItems[position].note)
                    intent.putExtra("date", searchItems[position].date)
                    intent.putExtra("group", searchItems[position].group)
                    intent.putExtra("frag", "Notes")
                    intent.putParcelableArrayListExtra("noteList", noteList)
                    intent.putParcelableArrayListExtra("searchItems", searchItems)

                    if(noteList == searchItems){
                        intent.putExtra("position", position)
                    }else {
                        intent.putExtra("position", noteList.indexOf(searchItems[position]))
                    }
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

    private fun deleteAll(view:View, delete: CheckBox, btn: Button){
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
                        deleteList = trashDB.getAllNote()
                        val noteModel = NoteModel(
                            noteList[checkedItems[0] - i].note,
                            noteList[checkedItems[0] - i].title,
                            noteList[checkedItems[0] - i].date,
                            noteList[checkedItems[0] - i].group
                        )
                        deleteList.add(noteModel)
                        trashDB.addNote(
                            noteList[checkedItems[0] - i].note,
                            noteList[checkedItems[0] - i].title,
                            noteList[checkedItems[0] - i].date,
                            deleteList.size
                        )
                        noteDB.deleteItem(checkedItems[0] + 1 - i)
                        noteList.removeAt(checkedItems[0] - i)
                        searchItems.removeAt(checkedItems[0] - i)
                        checkedItems.removeAt(0)
                    }

                }

                searchItems.clear()
                noteList.clear()
                noteDB.deleteAll()

                checkedVisible = false
                hideItems()
                clearAllItems()

                delete.visibility = View.GONE
                btn.visibility = View.GONE
                buttonLayout.visibility = View.GONE
                fab.isVisible = true
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
                        deleteList = trashDB.getAllNote()
                        val noteModel = NoteModel(
                            noteList[checkedItems[0] - i].note,
                            noteList[checkedItems[0] - i].title,
                            noteList[checkedItems[0] - i].date,
                            noteList[checkedItems[0] - i].group
                        )
                        deleteList.add(noteModel)
                        trashDB.addNote(
                            noteList[checkedItems[0]-i].note,
                            noteList[checkedItems[0]-i].title,
                            noteList[checkedItems[0]-i].date,
                            deleteList.size
                        )
                        noteDB.deleteItem(checkedItems[0] + 1 - i)
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
                fab.isVisible = true
                deleteAll.isSelected = false
                unSelectAll()
                deleteAll.isChecked = false
                if (!noteList.isNullOrEmpty()) {
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

    inner class NoteViewHolder(val customView: View) : RecyclerView.ViewHolder(customView),
        View.OnLongClickListener {
        private var checkbox = customView.checkBox

        init {
            customView.isLongClickable = true
            customView.setOnLongClickListener(this)


        }

        fun bind(position: Int) { // use the sparse boolean array to check
            customView.checkBox.isChecked = mCheckItems.get(position, false)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onLongClick(v: View?): Boolean {
            //Changed the state of check box visibility
            checkedVisible = true
            notifyDataSetChanged()
            Log.d(
                "groupPosition",
                noteDB.getGroupPositions(groupName).toString() + adapterPosition.toString()
            )
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
        return object:Filter(){

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                if(constraint == null || constraint.isEmpty()){

                    searchItems.addAll(noteList)
                }
                else{

                    val filterPattern =
                        constraint.toString().toLowerCase(Locale.getDefault()).trim()
                    for(item: NoteModel in noteList){
                        val noteItem = item.note.replace("\n", " ")
                        noteItem.replace("\t", " ")


                        if (item.note.toLowerCase(Locale.getDefault()).trim().contains(filterPattern)
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



