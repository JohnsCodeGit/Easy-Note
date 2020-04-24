package com.whiskey.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note_row_item.view.*

class AddToGroupAdapter(
    var groupList: ArrayList<String>,
    var context: Context,
    var noteItem: NoteModel?,
    private var itemPosition: Int,
    private var checkedGroupItems: ArrayList<Int>
) :
    RecyclerView.Adapter<AddToGroupAdapter.GroupViewHolder>() {
    private val groupsDB = GroupsDB(context, null)
    private val notesDB = NotesDbHelper(context, null)
    private val notes = notesDB.getAllNote()
    override fun getItemCount() = groupList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val customView = layoutInflater.inflate(R.layout.group_list_item, parent, false)

        return GroupViewHolder(customView)

    }

    override fun onViewRecycled(holder: GroupViewHolder) {
        holder.customView.checkBox.setOnCheckedChangeListener(null)

    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {

        holder.customView.itemTitle.text = groupList[position]

        holder.customView.setOnClickListener {
            if (checkedGroupItems.size != 0) {
                for (i in 0 until checkedGroupItems.size) {
                    notesDB.updateGroup(groupList[position], checkedGroupItems[i] + 1)
                }
            } else {
                notesDB.updateGroup(groupList[position], itemPosition + 1)
            }

            val intent = Intent(holder.customView.context, MainActivity::class.java)
            startActivity(holder.customView.context, intent, null)
        }
    }


    inner class GroupViewHolder(val customView: View) : RecyclerView.ViewHolder(customView),
        View.OnClickListener {

        init {
            customView.isLongClickable = true
            customView.setOnClickListener(this)

        }


        override fun onClick(v: View?) {

        }


    }
}