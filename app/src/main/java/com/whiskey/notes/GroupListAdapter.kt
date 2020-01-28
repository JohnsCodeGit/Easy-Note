package com.whiskey.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.note_row_item.view.*

class GroupListAdapter(groupList: ArrayList<String>) :
    RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>() {

    var list = groupList

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val customView = layoutInflater.inflate(R.layout.group_item, parent, false)

        return GroupViewHolder(customView)

    }

    override fun onViewRecycled(holder: GroupViewHolder) {
        holder.customView.checkBox.setOnCheckedChangeListener(null)

    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {

        holder.customView.itemTitle.text = list[position]
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