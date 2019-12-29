package com.whiskey.notes


import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.whiskey.notes.com.whiskey.notes.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()

    private lateinit  var noteadapter: NoteAdapter
    private val notedbHandler = NotesDbHelper(this, null)
    private lateinit var searchView: SearchView
    private val layoutM = LinearLayoutManager(this)
    private lateinit var fabs: FloatingActionButton
    private lateinit var noteItem: NoteModel

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putParcelableArrayList("noteList", noteList)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {

        noteList = savedInstanceState?.getParcelableArrayList<NoteModel>("noteList")  as ArrayList<NoteModel>


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        window.statusBarColor = Color.parseColor("#111116")
        fab.setOnClickListener { val intent = Intent(this, NewNoteActivity::class.java)

            intent.putParcelableArrayListExtra("noteList", noteList)
            startActivity(intent)
        }
        fabs = fab

        searchItems.clear()

        if(notedbHandler.getNoteSize() != 0.toLong() ) {

            noteList = notedbHandler.getAllNote()

            searchItems = notedbHandler.getAllNote()



        }

        val noteText = intent.getStringExtra("note")
        val titleText = intent.getStringExtra("title")
        val dateText = intent.getStringExtra("date")

        if((noteText != null || titleText != null) && dateText != null) {
            Log.d("NOTETEXT", noteText)
            noteItem = NoteModel(noteText, titleText, dateText)

            val position: Int = intent.getIntExtra("position", -1)

            Log.d("notePosition", position.toString())
            if(position == -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {
                //


                noteList.add(noteItem)
                searchItems.add(noteItem)

                notedbHandler.addNote(noteText, titleText, dateText, noteList.size)

                Log.d("itemAddedNoteItem", noteItem.toString())


            }
            else if(position != -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())){

                searchItems[position] = noteItem

                noteList[position]= noteItem


            }

        }
        actionBar?.elevation = 0.toFloat()
        val delete = btnDelete
        val deleteAll = radioButton
        recyclerView_main.apply {
            setBackgroundColor(Color.TRANSPARENT)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM
            val buttonLayout: ConstraintLayout = constrain
            adapter  = NoteAdapter(delete, deleteAll, buttonLayout, fab, this.context,
                notedbHandler, recyclerView_main, noteList, searchItems)
            noteadapter = adapter as NoteAdapter
            addItemDecoration(VerticalSpacing(50))

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
        searchView.setOnCloseListener {
            searchItems.clear()
            searchItems = notedbHandler.getAllNote()
            fab.visibility = View.VISIBLE

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

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        return true
    }


    @SuppressLint("RestrictedApi")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
           if (id == R.id.search) {
               fab.visibility = View.GONE
           }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        btnDelete.visibility = View.GONE
        radioButton.visibility = View.GONE
        constrain.visibility = View.GONE
        radioButton.isChecked = false
        radioButton.isSelected = false
        fab.visibility = View.VISIBLE

        noteadapter.hideItems()
        noteadapter.notifyDataSetChanged()


    }

}
class VerticalSpacing(height: Int) : RecyclerView.ItemDecoration(){

    private var spaceHeight: Int = height

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = spaceHeight

    }



}


