package com.whiskey.notes

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item_layout.*

class GroupItemsList : AppCompatActivity() {
    private lateinit var mView: View
    var notes = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()
    private var groups = ArrayList<String>()
    private lateinit var searchView: SearchView

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
        searchItems.clear()

        val textView = findViewById<TextView>(R.id.textView10)

        if (notes.isNotEmpty()) {
            textView.visibility = View.GONE
            notes = notesDB.getAllNote()

            searchItems = notesDB.getAllNote()
        } else
            textView.visibility = View.VISIBLE

        constraintLayout.visibility = View.VISIBLE
        recyclerView.apply {
            noteAdapter = GroupItemsListAdapter(
                notes, notesDB, recyclerView,
                checkBox, constraintLayout, deleteAll,
                groups[groupPosition], textView, searchItems
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

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search in Notes..."
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.isIconified = true
        searchView.setOnCloseListener {
            searchItems.clear()
            searchItems = notesDB.getAllNote()
            //fabButton.visibility = View.VISIBLE

            true
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                return false

            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextChange(newText: String?): Boolean {
                val text: String = newText.toString().trim()

                noteAdapter.filter.filter(text)



                return true
            }

        })

        super.onCreateOptionsMenu(menu)
        return true
    }


    override fun onBackPressed() {

        val mainIntent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragment", "Groups")
        startActivity(mainIntent)
    }


}