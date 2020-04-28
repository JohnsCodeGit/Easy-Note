package com.whiskey.notes

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        supportActionBar?.title = "Add to Group:"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        groupList = groupDB.getAllGroups()

        recyclerView = findViewById(R.id.groupListRecyclerView)

        val groupList: ArrayList<String> = intent.getStringArrayListExtra("groupList")
        val itemPosition: Int = intent.getIntExtra("itemPosition", -1)
        val checkedGroupItems: ArrayList<Int> = intent.getIntegerArrayListExtra("itemPositionList")

        recyclerView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM

            noteAdapter = AddToGroupAdapter(
                groupList, this.context, itemPosition, checkedGroupItems
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