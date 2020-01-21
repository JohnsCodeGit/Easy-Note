package com.whiskey.notes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FragmentGroups : Fragment(){
    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()
    private lateinit  var noteadapter: GroupAdapter
    private var groupsDB = GroupsDB(null, null)
    private lateinit var searchView: SearchView
    private val layoutM = LinearLayoutManager(activity)
    private lateinit var deleteButton: Button
    private lateinit var mView: View
    private lateinit var checkBox: CheckBox
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var recyclerView: RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mView = view
        deleteButton = mView.findViewById(R.id.btnDelete)
        checkBox = mView.findViewById(R.id.radioButton)
        constraintLayout = mView.findViewById(R.id.constrain)
        val textView = mView.findViewById<TextView>(R.id.textView8)
        searchItems.clear()

        if(groupsDB.getGroupSize() != 0.toLong() ) {

            noteList = groupsDB.getAllGroups()

            searchItems = groupsDB.getAllGroups()

        }

        if (noteList.size != 0)
            textView.visibility = View.GONE
        else {
            textView.visibility = View.VISIBLE
            Log.d("visibility", true.toString())
        }
        Log.d("favList", noteList.toString())

        val deleteAll = activity?.findViewById<CheckBox>(R.id.radioButton)!!
        val constraint = activity?.findViewById<ConstraintLayout>(R.id.constrain)!!
        recyclerView = view.findViewById(R.id.recyclerView_group)

        recyclerView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM

            noteadapter  = GroupAdapter(deleteButton, deleteAll, constraint, this.context,
                recyclerView, noteList, searchItems, textView
            )
            adapter = noteadapter
            addItemDecoration(VerticalSpacing(25))

        }
    }
    override fun onAttach(context: Context) {
        groupsDB = GroupsDB(activity,null)
        super.onAttach(context)
    }
    @SuppressLint("RestrictedApi")
    fun HideDeleteMenu(view: View){


        deleteButton = view.findViewById(R.id.btnDelete)
        checkBox = view.findViewById(R.id.radioButton)
        constraintLayout = view.findViewById(R.id.constrain)
        recyclerView = view.findViewById(R.id.recyclerView_fav)

        noteadapter = recyclerView.adapter as GroupAdapter

        deleteButton.visibility = View.GONE
        deleteButton.isVisible = false
        checkBox.visibility = View.GONE
        checkBox.isVisible = false

        constraintLayout.visibility = View.GONE
        constraintLayout.isVisible = false
        checkBox.isChecked = false
        checkBox.isSelected = false
        noteadapter.hideItems()
        Log.d("visible?", checkBox.isVisible.toString())

    }
    @SuppressLint("RestrictedApi")
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.group_layout, container, false)

        return mView
    }
}