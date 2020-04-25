package com.whiskey.notes

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item_layout.*

class GroupItemsList : AppCompatActivity() {
    private lateinit var mView: View
    var notes = ArrayList<NoteModel>()
    var notesAll = ArrayList<NoteModel>()
    private var groups = ArrayList<String>()
    private val notesDB = NotesDB(this, null)
    private val groupsDB = GroupsDB(this, null)
    private val layoutM = LinearLayoutManager(this)
    private lateinit var checkBox: CheckBox
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var deleteAll: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var noteAdapter: GroupItemsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_item_layout)
        setSupportActionBar(toolbarItems)

        val groupPosition = intent.getIntExtra("groupPos", -1)
        groups = groupsDB.getAllGroups()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = groups[groupPosition]

        if (groupsDB.getGroupSize() != 0.toLong())
            notes = notesDB.getGroup(groups[groupPosition])

        deleteAll = findViewById(R.id.btnDeleteAll)
        checkBox = findViewById(R.id.chkDeleteAll)
        constraintLayout = findViewById(R.id.constrainGLIST)
        recyclerView = findViewById(R.id.groupItemsRecyclerView)

        val textView = findViewById<TextView>(R.id.textView10)

        if (notes.isNotEmpty()) {
            textView.visibility = View.GONE
        } else
            textView.visibility = View.VISIBLE

        constraintLayout.visibility = View.VISIBLE
        recyclerView.apply {
            noteAdapter = GroupItemsListAdapter(
                notes, notesDB, recyclerView,
                checkBox, constraintLayout, deleteAll,
                groups[groupPosition], textView
            )
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM
            adapter = noteAdapter
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
        intent.putExtra("fragment", "Groups")
        startActivity(mainIntent)
    }


}