package com.whiskey.notes

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_home.*

class TrashFragment : Fragment() {

    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()
    private lateinit  var noteadapter: TrashAdapter
    private var trashDB = TrashDB(this.context, null)
    private lateinit var searchView: SearchView
    private val layoutM = LinearLayoutManager(activity)
    private lateinit var deleteButton: Button
    private lateinit var restoreButton: ImageButton
    private lateinit var mView: View
    private lateinit var checkBox: CheckBox
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var recyclerView: RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val deleteAll = activity?.findViewById<CheckBox>(R.id.radioButton)!!
        val constraint = activity?.findViewById<ConstraintLayout>(R.id.constrain)!!
        val textView = activity?.findViewById<TextView>(R.id.textView7)!!

        mView = view
        deleteButton = mView.findViewById(R.id.btnDelete)
        checkBox = mView.findViewById(R.id.radioButton)
        constraintLayout = mView.findViewById(R.id.constrain)
        restoreButton = mView.findViewById(R.id.btnRestore)
        searchItems.clear()

        if(trashDB.getNoteSize() != 0.toLong() ) {
            textView.visibility = View.GONE

            noteList = trashDB.getAllNote()

            searchItems = trashDB.getAllNote()

        } else
            textView.visibility = View.VISIBLE

        Log.d("favList", noteList.toString())

        recyclerView = view.findViewById(R.id.recyclerView_trash)

        recyclerView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM

            noteadapter  = TrashAdapter(deleteButton, deleteAll, constraint, this.context,
                trashDB, recyclerView, noteList, searchItems, restoreButton, textView
            )
            adapter= noteadapter
            addItemDecoration(VerticalSpacing(25))

        }
    }

    override fun onDetach() {
        mView.invalidate()
        super.onDetach()
    }
    @SuppressLint("RestrictedApi")
    fun HideDeleteMenu(view: View){


        deleteButton = view.findViewById(R.id.btnDelete)
        checkBox = view.findViewById(R.id.radioButton)
        constraintLayout = view.findViewById(R.id.constrain)
        recyclerView = view.findViewById(R.id.recyclerView_trash)

        noteadapter = recyclerView.adapter as TrashAdapter

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

    override fun onAttach(context: Context) {
        trashDB = TrashDB(activity,null)
        super.onAttach(context)
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(false)
        mView = inflater.inflate(R.layout.fragment_trash, container, false)

        return mView
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val manager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search in Notes..."
        searchView.setSearchableInfo(manager.getSearchableInfo(activity!!.componentName))
        searchView.isIconified = true
        searchView.setOnCloseListener {
            searchItems.clear()
            searchItems = trashDB.getAllNote()
            fabButton.visibility = View.VISIBLE

            true
        }

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {

                return false

            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onQueryTextChange(newText: String?): Boolean {
                val text: String = newText.toString().trim()

                noteadapter.filter.filter(text)



                return true
            }

        })

        super.onCreateOptionsMenu(menu, menuInflater)
    }

}