package com.whiskey.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item_layout.*

class GroupItems : AppCompatActivity() {

    var notes = ArrayList<NoteModel>()
    var notesAll = ArrayList<NoteModel>()
    private var groups = ArrayList<String>()
    private val notesDB = NotesDbHelper(this, null)
    private val groupsDB = GroupsDB(this, null)
    private val layoutM = LinearLayoutManager(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteadapter: GroupItemsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_item_layout)
        val groupPosition = intent.getIntExtra("groupPos", -1)
        groups = groupsDB.getAllGroups()
        toolbar.title = groups[groupPosition]
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (groupsDB.getGroupSize() != 0.toLong())
            notes = notesDB.getGroup(groups[groupPosition])

//        notesAll = notesDB.getAllNote()
//        Log.d("notesDBGROUP", notesAll.toString())

        recyclerView = findViewById(R.id.groupItemRecyclerView)
        recyclerView.apply {
            noteadapter = GroupItemsAdapter(notes, notesDB, this.context)
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM
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

    override fun onBackPressed() {

        val mainIntent = Intent(this, MainActivity::class.java)

        startActivity(mainIntent)
    }


}