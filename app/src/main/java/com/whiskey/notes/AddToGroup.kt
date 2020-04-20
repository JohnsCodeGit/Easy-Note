package com.whiskey.notes

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item_layout.*
import kotlinx.android.synthetic.main.group_list_layout.*


@Suppress(
    "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class AddToGroup : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private val layoutM = LinearLayoutManager(this)
    private lateinit var noteAdapter: AddToGroupAdapter
    private lateinit var groupList: ArrayList<String>
    private val groupDB = GroupsDB(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.group_list_layout)
        setSupportActionBar(toolbarGLIST)

        toolbarGLIST.setTitleTextColor(Color.WHITE)
        toolbarGLIST.setBackgroundColor(ContextCompat.getColor(this, R.color.dark))
//        toolbar.title = "Add to group: "
        supportActionBar?.title = "Add to Group:"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        groupList = groupDB.getAllGroups()
        recyclerView = findViewById(R.id.groupListRecyclerView)

        val groupList = intent.getStringArrayListExtra("groupList")
        val noteItem = intent.getParcelableExtra<NoteModel>("noteItem")
        val itemPosition = intent.getIntExtra("itemPositionList", -1)
        val checkedGroupItems = intent.getIntegerArrayListExtra("itemPositionList")

        if (groupList.isEmpty())
            textView10.visibility = View.VISIBLE
        else
            textView10.visibility = View.GONE

        recyclerView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM

            noteAdapter = AddToGroupAdapter(
                groupList, this.context, noteItem, itemPosition, checkedGroupItems
            )
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


}