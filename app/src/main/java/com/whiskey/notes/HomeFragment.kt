package com.whiskey.notes.com.whiskey.notes

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.CheckBox
import androidx.activity.OnBackPressedCallback
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whiskey.notes.NewNoteActivity
import com.whiskey.notes.NoteAdapter
import com.whiskey.notes.R
import com.whiskey.notes.VerticalSpacing
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.note_row_item.*

class HomeFragment : Fragment(){
    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()
    private lateinit  var noteadapter: NoteAdapter
    private var notedbHandler = NotesDbHelper(null, null)
    private lateinit var searchView: SearchView
    private val layoutM = LinearLayoutManager(activity)
    private lateinit var fabs: FloatingActionButton
    private lateinit var noteItem: NoteModel
    private lateinit var deleteButton: Button
    private lateinit var mView: View
    private lateinit var checkBox: CheckBox
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var recyclerView: RecyclerView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        noteadapter  = NoteAdapter(btnDelete, radioButton, constrain, fabButton, this.context!!,
//            notedbHandler, recyclerView, noteList, searchItems)
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
        searchItems.clear()

        if(notedbHandler.getNoteSize() != 0.toLong() ) {

            noteList = notedbHandler.getAllNote()

            searchItems = notedbHandler.getAllNote()



        }

        val noteText = activity?.intent?.getStringExtra("note")
        val titleText = activity?.intent?.getStringExtra("title")
        val dateText = activity?.intent?.getStringExtra("date")

        if((noteText != null || titleText != null) && dateText != null) {
            Log.d("NOTETEXT", noteText)
            noteItem = NoteModel(noteText.toString(), titleText.toString(), dateText)

            val position: Int = activity?.intent!!.getIntExtra("position", -1)

            Log.d("notePosition", position.toString())
            if(position == -1 && (noteText!!.isNotEmpty() || titleText!!.isNotEmpty())) {
                //


                noteList.add(noteItem)
                searchItems.add(noteItem)

                notedbHandler.addNote(noteText, titleText.toString(), dateText, noteList.size)

                Log.d("itemAddedNoteItem", noteItem.toString())


            }
            else if(position != -1 && (noteText!!.isNotEmpty() || titleText!!.isNotEmpty())){

                searchItems.removeAt(position)
                noteList.removeAt(position)
                notedbHandler.deleteItem(position+1)


                noteList.add(noteItem)
                searchItems.add(noteItem)
                notedbHandler.addNote(noteItem.note, noteItem.title, noteItem.date, noteList.size)


            }

        }

        val deleteAll = activity?.findViewById<CheckBox>(R.id.radioButton)!!
        val constraint = activity?.findViewById<ConstraintLayout>(R.id.constrain)!!
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fabButton)!!
        recyclerView = view.findViewById(R.id.recyclerView_main)

        recyclerView.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM

            noteadapter  = NoteAdapter(deleteButton, deleteAll, constraint, fab, this.context,
                notedbHandler, recyclerView, noteList, searchItems)
            adapter= noteadapter
            addItemDecoration(VerticalSpacing(50))

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
            recyclerView = view.findViewById(R.id.recyclerView_main)

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
                var text: String = newText.toString().trim()

                noteadapter.filter.filter(text)



                return true
            }

        })

    super.onCreateOptionsMenu(menu, menuInflater)
    }



//    override fun handleOnBackPressed() {
//        (recyclerView_main.adapter as NoteAdapter).hideItems()
//    }

}