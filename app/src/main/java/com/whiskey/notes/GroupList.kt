package com.whiskey.notes

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*


class GroupList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val layoutM = LinearLayoutManager(this)
    private lateinit var noteadapter: GroupListAdapter
    private lateinit var groupList: ArrayList<String>
    private val groupDB = GroupsDB(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_list_layout)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.dark))
//        toolbar.title = "Add to group: "
        supportActionBar?.title = "Add to group: "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        groupList = groupDB.getAllGroups()
        recyclerView = findViewById(R.id.groupListRecyclerView)

        recyclerView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM

            noteadapter = GroupListAdapter(
                groupList, this.context
            )
            adapter = noteadapter
//            addItemDecoration(VerticalSpacing(25))
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    this.context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }


}