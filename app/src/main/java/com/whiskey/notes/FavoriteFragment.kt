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
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whiskey.notes.NewNoteActivity
import com.whiskey.notes.NoteAdapter
import com.whiskey.notes.R
import com.whiskey.notes.VerticalSpacing
import kotlinx.android.synthetic.main.fragment_fav.*

class FavoriteFragment : Fragment() {
    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()
    private lateinit var drawer: DrawerLayout
    private lateinit  var noteadapter: NoteAdapter
    private val notedbHandler = NotesDbHelper(this.context, null)
    private lateinit var searchView: SearchView
    private val layoutM = LinearLayoutManager(activity)
    private lateinit var fabs: FloatingActionButton
    private lateinit var noteItem: NoteModel
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fav, container, false)

        view.setOnTouchListener { v, event ->
            true
        }
//        fabButton.setOnClickListener {
//            val intent = Intent(activity, NewNoteActivity::class.java)
//
//            intent.putParcelableArrayListExtra("noteList", noteList)
//            startActivity(intent) }
//
//
//        fabs = fabButton
//
//        searchItems.clear()
//
//        if(notedbHandler.getNoteSize() != 0.toLong() ) {
//
//            noteList = notedbHandler.getAllNote()
//
//            searchItems = notedbHandler.getAllNote()
//
//
//
//        }
//
//        val noteText = activity?.intent?.getStringExtra("note")
//        val titleText = activity?.intent?.getStringExtra("title")
//        val dateText = activity?.intent?.getStringExtra("date")
//
//        if((noteText != null || titleText != null) && dateText != null) {
//            Log.d("NOTETEXT", noteText)
//            noteItem = NoteModel(noteText.toString(), titleText.toString(), dateText)
//
//            val position: Int = activity?.intent!!.getIntExtra("position", -1)
//
//            Log.d("notePosition", position.toString())
//            if(position == -1 && (noteText!!.isNotEmpty() || titleText!!.isNotEmpty())) {
//                //
//
//
//                noteList.add(noteItem)
//                searchItems.add(noteItem)
//
//                notedbHandler.addNote(noteText, titleText.toString(), dateText, noteList.size)
//
//                Log.d("itemAddedNoteItem", noteItem.toString())
//
//
//            }
//            else if(position != -1 && (noteText!!.isNotEmpty() || titleText!!.isNotEmpty())){
//
//                searchItems.removeAt(position)
//                noteList.removeAt(position)
//                notedbHandler.deleteItem(position+1)
//
//
//                noteList.add(noteItem)
//                searchItems.add(noteItem)
//                notedbHandler.addNote(noteItem.note, noteItem.title, noteItem.date, noteList.size)
//
//
//            }
//
//        }
//        val delete = btnDelete
//        val deleteAll = radioButton
//        recyclerView_main.apply {
//            setBackgroundColor(Color.TRANSPARENT)
//            layoutM.stackFromEnd = true
//            layoutM.reverseLayout = true
//            layoutManager = layoutM
//            val buttonLayout: ConstraintLayout = constrain
//            adapter  = NoteAdapter(delete, deleteAll, buttonLayout, fabButton, this.context,
//                notedbHandler, recyclerView_main, noteList, searchItems)
//            noteadapter = adapter as NoteAdapter
//            addItemDecoration(VerticalSpacing(50))
//
//        }
        return view
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val manager = activity?.getSystemService(SEARCH_SERVICE) as SearchManager
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search in Notes..."
        searchView.setSearchableInfo(manager.getSearchableInfo(activity!!.componentName))
        searchView.setOnCloseListener {
            searchItems.clear()
            searchItems = notedbHandler.getAllNote()
            //fabButton.visibility = View.VISIBLE

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


    }

}