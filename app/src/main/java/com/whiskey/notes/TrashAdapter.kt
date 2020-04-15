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
import kotlinx.android.synthetic.main.note_row_item.view.*
import java.util.*
import kotlin.collections.ArrayList


class TrashAdapter(

    var bDelete: Button,
    var deleteAll: CheckBox,
    var buttonLayout: ConstraintLayout,

    var context: Context,
    var trashDB: TrashDB,

    var recyclerviewMain: RecyclerView,

    var noteList: ArrayList<NoteModel>,
    var searchItems: ArrayList<NoteModel>,
    var restoreButton: ImageButton,
    var textView7: TextView

)
    : RecyclerView.Adapter<TrashAdapter.NoteViewHolder>(), Filterable {
    var checkedItems= ArrayList<Int>()
    var notes = ArrayList<NoteModel>()
    private var checkedVisible = false
    private var isAllChecked = false
    private var mCheckItems = SparseBooleanArray()
    private val noteDB = NotesDbHelper(this.context, null)
    private lateinit var title: String
    private lateinit var note: String
    private lateinit var date: String
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

        title = holder.customView.itemTitle.text.toString()
        note = holder.customView.itemNote.text.toString()
        date = holder.customView.dateText.text.toString()

        notes = noteDB.getAllNote()

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

                        if(checkedItems.contains(i)){}
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
            holder.customView.checkBox.setOnCheckedChangeListener { _, isChecked ->

                Log.d("itemChecked", isChecked.toString())
                Log.d("itemNotesSize", searchItems.size.toString())


                if (isChecked) {
                            if(checkedItems.contains(position)){}
                            else {
                                holder.customView.checkBox.isChecked = true


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

            restoreButton.setOnClickListener {
                val dialogBuilder =
                    AlertDialog.Builder(this.context, R.style.MyDialogTheme)

                // set message of alert dialog
                dialogBuilder.setMessage("Do you want to restore the selected notes?")

                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        dialog.dismiss()
                        checkedItems.sort()

                        for (i in 0 until checkedItems.size) {
                            if (checkedItems.size == 0) {
                                break
                            } else {
                                val noteItem = noteList[checkedItems[0] - i].note
                                val titleItem = noteList[checkedItems[0] - i].title
                                val dateItem = noteList[checkedItems[0] - i].date
                                val noteModel = NoteModel(noteItem, titleItem, dateItem)

                                notes.add(noteModel)
                                noteDB.addNote(noteItem, titleItem, dateItem, 0, notes.size)

                                trashDB.deleteItem(checkedItems[0] + 1 - i)
                                noteList.removeAt(checkedItems[0] - i)
                                searchItems.removeAt(checkedItems[0] - i)

//                        notifyItemRemoved(checkedItems[0]-i)
                                checkedItems.removeAt(0)


                            }

                        }
                        checkedVisible = false
                        clearAllItems()
                        hideItems()
                        deleteAll.visibility = View.GONE
                        bDelete.visibility = View.GONE
                        buttonLayout.visibility = View.GONE
                        deleteAll.isSelected = false
                        unSelectAll()
                        deleteAll.isChecked = false

                    }
                    .setNegativeButton("No") { dialog, id ->
                        dialog.cancel()
                    }
                val alert = dialogBuilder.create()
                alert.show()
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
                    bDelete.visibility = View.VISIBLE
                    holder.customView.button.visibility = View.VISIBLE
                    deleteAll.visibility = View.VISIBLE
                    buttonLayout.visibility = View.VISIBLE
                }
                else if(!checkedVisible) {
                    holder.customView.checkBox.visibility = View.GONE
                    bDelete.visibility = View.GONE
                    holder.customView.button.visibility = View.GONE
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
                } else {
//                    val intent = Intent(holder.customView.context, ViewNoteActivity::class.java)
//                    holder.customView.checkBox.visibility = View.GONE
//                    holder.customView.button.visibility = View.GONE
//                    bDelete.visibility = View.GONE
//                    checkedItems.clear()
//                    mCheckItems.clear()
//                    intent.putExtra("title", searchItems[position].title)
//                    intent.putExtra("note", searchItems[position].note)
//                    intent.putExtra("date", searchItems[position].date)
//                    intent.putParcelableArrayListExtra("noteList", noteList)
//                    intent.putParcelableArrayListExtra("searchItems", searchItems)
//
//                    if (noteList == searchItems) {
//                        intent.putExtra("position", -2)
//                        Log.d("same", position.toString())
//                    } else {
//                        intent.putExtra("position", -2)
//                        Log.d("Not Same", noteList.indexOf(searchItems[position]).toString())
//                    }
//
//
//
//                    startActivity(holder.customView.context, intent, null)

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
    private fun deleteAll(view:View, delete: CheckBox, btn: Button){
        val dialogBuilder =
            AlertDialog.Builder(view.context, R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to PERMANENTLY delete all notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") {
                    dialog, id-> dialog.dismiss()

                searchItems.clear()
                noteList.clear()

                trashDB.deleteAll()

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
                    textView7.visibility = View.GONE

                } else
                    textView7.visibility = View.VISIBLE
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    private fun deleteItems(view: View, delete: CheckBox, btn: Button) {
        val dialogBuilder =
            AlertDialog.Builder(view.context, R.style.MyDialogTheme)

        // set message of alert dialog
        dialogBuilder.setMessage("Do you want to PERMANENTLY delete the selected notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") {
                    dialog, id-> dialog.dismiss()
                checkedItems.sort()
                Log.d("itemDeleted List1", (checkedItems).toString())

                for (i in 0 until checkedItems.size){
                    if(checkedItems.size == 0) {
                        break
                    }
                    else{
                        Log.d(
                            "itemDeleted3",
                            (checkedItems[0] + 1 - i).toString() + ", " + noteList.size
                        )

                        trashDB.deleteItem(checkedItems[0]+1-i)
                        //noteDB.updateNote(note, title, date, 0, notes.indexOf(searchItems[position]))
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
                    textView7.visibility = View.GONE

                } else
                    textView7.visibility = View.VISIBLE
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()

    }

    inner class NoteViewHolder(val customView: View) : RecyclerView.ViewHolder(customView),
        View.OnLongClickListener, View.OnClickListener{
        private var checkbox = customView.checkBox

        init {
            customView.isLongClickable = true
            customView.setOnLongClickListener(this)
            customView.setOnClickListener(this)


        }

        fun bind(position: Int) { // use the sparse boolean array to check
            customView.checkBox.isChecked = mCheckItems.get(position, false)
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onLongClick(v: View?): Boolean {
            //Changed the state of check box visibility
            checkedVisible = true
            customView.checkBox.isChecked = true
            notifyDataSetChanged()
            recyclerviewMain.isLayoutFrozen = true
            recyclerviewMain.setOnTouchListener { _, event ->

                if(event.action == MotionEvent.ACTION_UP){
                    recyclerviewMain.isLayoutFrozen = false

                }
                false
            }
            return true
        }

        override fun onClick(v: View?) {
            //Changed the state of check box visibility

            if(checkedVisible){
                checkedVisible = false
                notifyDataSetChanged()
            }

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
                        Log.d("noteItemText", noteItem)

                        if (item.note.toLowerCase(Locale.getDefault()).trim().contains(filterPattern)
                            || item.title.toLowerCase(Locale.getDefault()).trim().contains(
                                filterPattern
                            )
                            && (item.title.isNotBlank() || item.note.isNotBlank())
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



