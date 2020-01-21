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
import android.widget.Button
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note_row_item.view.*
import java.util.*
import kotlin.collections.ArrayList


class DeleteAdapter(

    var bDelete: Button,
    var deleteAll: CheckBox,
    var buttonLayout: ConstraintLayout,

    var context: Context,
    var notedbHandler: NotesDbHelper,

    var recyclerviewMain: RecyclerView,

    var noteList: ArrayList<NoteModel>,
    var searchItems: ArrayList<NoteModel>

)
    : RecyclerView.Adapter<DeleteAdapter.DeleteViewHolder>(), Filterable {
    var checkedItems= ArrayList<Int>()
    private var checkedVisible = false
    private var isAllChecked = false
    private var mCheckItems = SparseBooleanArray()

    override fun getItemCount() = searchItems.size

    fun hideItems(){
        checkedVisible = false
        checkedItems.clear()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeleteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val customView = layoutInflater.inflate(R.layout.note_row_item, parent, false)

        return DeleteViewHolder(customView)

    }

    override fun onViewRecycled(holder: DeleteViewHolder) {
        holder.customView.checkBox.setOnCheckedChangeListener(null)

    }



    override fun onBindViewHolder(holder: DeleteViewHolder, position: Int) {

        holder.customView.dateText.text  = searchItems[position].date
        holder.customView.itemTitle.text = searchItems[position].title
        holder.customView.itemNote.text  = searchItems[position].note

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

            if(noteList.size == 0){
                checkedVisible = false
            }

            //Show or hide check boxes
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


            holder.customView.setOnClickListener {

                if (checkedVisible){
                    holder.customView.checkBox.isChecked = !holder.customView.checkBox.isChecked
                    when {
                        !checkedItems.contains(position) && holder.customView.checkBox.isChecked -> {

                            checkedItems.add(position)
                            mCheckItems.put(position, true)
                            Log.d("itemAdded",
                                mCheckItems[position].toString()
                                        + ", "
                                        + position.toString())
                        }
                        !holder.customView.checkBox.isChecked -> {
                            when {
                                position < checkedItems.size -> {
                                    mCheckItems.put(position, false)
                                    deleteAll.isChecked = false
                                    checkedItems.removeAt(position)
                                    Log.d("itemRemoved", position.toString())
                                }
                                else->{}
                            }
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
                    intent.putParcelableArrayListExtra("noteList", noteList)
                    intent.putParcelableArrayListExtra("searchItems", searchItems)

                    if(noteList == searchItems){
                        intent.putExtra("position", position)
                        Log.d("same", position.toString())
                    }else {
                        intent.putExtra("position", noteList.indexOf(searchItems[position]))
                        Log.d("Not Same", noteList.indexOf(searchItems[position]).toString())
                    }



                    startActivity(holder.customView.context, intent, null)

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
        dialogBuilder.setMessage("Do you want to delete all notes?")

            .setCancelable(false)
            .setPositiveButton("Yes") {
                    dialog, _-> dialog.dismiss()

                searchItems.clear()
                noteList.clear()

                notedbHandler.deleteAll()

                checkedVisible = false
                hideItems()
                clearAllItems()

                delete.visibility = View.GONE
                btn.visibility = View.GONE
                buttonLayout.visibility = View.GONE
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
                        notedbHandler.deleteItem(checkedItems[0]+1-i)

                        noteList.removeAt(checkedItems[0]-i)
                        searchItems.removeAt(checkedItems[0]-i)

                        Log.d("itemDeleted3", checkedItems[0].toString())
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

            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }
        val alert = dialogBuilder.create()
        alert.show()
    }

    inner class DeleteViewHolder(val customView: View) : RecyclerView.ViewHolder(customView),
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
                            && noteItem.isNotBlank()){
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



