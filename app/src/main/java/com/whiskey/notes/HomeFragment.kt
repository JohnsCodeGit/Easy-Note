package com.whiskey.notes

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Context.SEARCH_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(){
    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()
    private lateinit  var noteadapter: NoteAdapter
    private var notedbHandler = NotesDbHelper(null, null)
    private lateinit var searchView: SearchView
    private val layoutM = LinearLayoutManager(activity)
    private lateinit var fabs: FloatingActionButton
    private lateinit var deleteButton: Button
    private lateinit var mView: View
    private lateinit var checkBox: CheckBox
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var addToGroup: ImageButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabButton.setOnClickListener {
            val intent = Intent(activity, NewNoteActivity::class.java)

            intent.putParcelableArrayListExtra("noteList", noteList)
            startActivity(intent)
        }

        mView = view
        deleteButton = mView.findViewById(R.id.btnDelete)
        checkBox = mView.findViewById(R.id.radioButton)
        fabs = mView.findViewById(R.id.fabButton)
        constraintLayout = mView.findViewById(R.id.constrain)
        addToGroup = mView.findViewById(R.id.addGroup)
        searchItems.clear()

        if(notedbHandler.getNoteSize() != 0.toLong() ) {
            textView5.visibility = View.GONE
            noteList = notedbHandler.getAllNote()

            searchItems = notedbHandler.getAllNote()

        } else
            textView5.visibility = View.VISIBLE



        val deleteAll = activity?.findViewById<CheckBox>(R.id.radioButton)!!
        val constraint = activity?.findViewById<ConstraintLayout>(R.id.constrain)!!
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fabButton)!!
        recyclerView = view.findViewById(R.id.recyclerView_home)

        recyclerView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM

            noteadapter = NoteAdapter(
                deleteButton, deleteAll, constraint, fab, this.context,
                notedbHandler, recyclerView, noteList, searchItems, textView5, addToGroup
            )
            adapter = noteadapter
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
            fabs = view.findViewById(R.id.fabButton)
            constraintLayout = view.findViewById(R.id.constrain)
        recyclerView = view.findViewById(R.id.recyclerView_home)

            noteadapter = recyclerView.adapter as NoteAdapter

            deleteButton.visibility = View.GONE
            deleteButton.isVisible = false
            checkBox.visibility = View.GONE
            checkBox.isVisible = false

            constraintLayout.visibility = View.GONE
            constraintLayout.isVisible = false
            checkBox.isChecked = false
            checkBox.isSelected = false
            fabs.visibility = View.VISIBLE
            fabs.isVisible = true
            noteadapter.hideItems()
        Log.d("visible?", checkBox.isVisible.toString())

    }

    override fun onAttach(context: Context) {
        notedbHandler = NotesDbHelper(activity,null)
        super.onAttach(context)
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mView = inflater.inflate(R.layout.fragment_home, container, false)

        return mView
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val manager = activity!!.getSystemService(SEARCH_SERVICE) as SearchManager
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search in Notes..."
        searchView.setSearchableInfo(manager.getSearchableInfo(activity!!.componentName))
        searchView.isIconified = true
        searchView.setOnCloseListener {
            searchItems.clear()
            searchItems = notedbHandler.getAllNote()
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