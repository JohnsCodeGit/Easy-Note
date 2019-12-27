package com.whiskey.notes


import android.annotation.SuppressLint
import android.annotation.TargetApi
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
    var notes = ArrayList<String>()
    private var titles = ArrayList<String>()
    private var dates = ArrayList<String>()
    private var searchList = ArrayList<String>()
    private var searchList2 = ArrayList<String>()
    private var searchList3 = ArrayList<String>()

    private lateinit  var noteadapter: NoteAdapter
    private val notedbHandler = NotesDbHelper(this, null)
    private val titleDbHandler = TitlesDbHelper(this, null)
    private val dateDbHandler = dateDbHelper(this, null)
    private val layoutM = LinearLayoutManager(this)
    private lateinit var fabs: FloatingActionButton
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)


        outState?.putStringArrayList("savedNotes", notes)
        outState?.putStringArrayList("savedTitles", titles)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {

        notes = savedInstanceState?.getStringArrayList("savedNotes") as ArrayList<String>
        titles = savedInstanceState.getStringArrayList("savedTitles") as ArrayList<String>

    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.setBackgroundColor(Color.parseColor("#111116"))
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        window.statusBarColor = Color.parseColor("#111116")
        fab.setOnClickListener { val intent = Intent(this, NewNoteActivity::class.java)
            intent.putStringArrayListExtra("notes", notes)
            intent.putStringArrayListExtra("titles", titles)
            intent.putStringArrayListExtra("dates", dates)
            startActivity(intent)
        }
        fabs = fab
        searchList.clear()
        searchList2.clear()
        searchList3.clear()
        if(notedbHandler.getNoteSize() != 0.toLong() ) {
            notes = notedbHandler.getAllNote()
            titles = titleDbHandler.getAllTitle()
            dates = dateDbHandler.getAllDate()
            searchList = notedbHandler.getAllNote()
            searchList2 = titleDbHandler.getAllTitle()
            searchList3 = dateDbHandler.getAllDate()


        }

        val noteText = intent.getStringExtra("note")
        val titleText = intent.getStringExtra("title")
        val dateText = intent.getStringExtra("date")

        if((noteText != null || titleText != null) && dateText != null) {
            Log.d("NOTETEXT", noteText)
            notes = intent.getStringArrayListExtra("notes")
            titles = intent.getStringArrayListExtra("titles")
            dates = intent.getStringArrayListExtra("dates")
            val position: Int = intent.getIntExtra("position", -1)

            Log.d("notePosition", position.toString())
            if(position == -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {
                notes.add(noteText)
                titles.add(titleText)
                dates.add(dateText)
                searchList.add(noteText)
                searchList2.add(titleText)
                searchList3.add(dateText)
                notedbHandler.addNote(noteText, notes.size)
                titleDbHandler.addTitle(titleText, titles.size)
                dateDbHandler.addDate(dateText,dates.size)

                Log.d("itemDeletedSize",notes.size.toString() )


            }
            else if(position != -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())){
                notes[position] = noteText
                titles[position] = titleText
                dates[position] = dateText
                searchList[position] = noteText
                searchList2[position] = titleText
                searchList3[position] = dateText
                notedbHandler.updateNote(noteText, position+1)
                dateDbHandler.updateNote(dateText, position+1)
                titleDbHandler.updateNote(titleText, position+1)
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
            adapter  = NoteAdapter(notes, titles, delete, deleteAll, buttonLayout, fab, dates, this.context,
                notedbHandler, titleDbHandler, dateDbHandler, recyclerView_main, searchList, searchList2, searchList3)
            noteadapter = adapter as NoteAdapter
            addItemDecoration(VerticalSpacing(50))
           // (adapter as NoteAdapter).notifyDataSetChanged()
            //recycledViewPool.setMaxRecycledViews(R.id.button, 0)
        }


    }


    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

       menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.search)
        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search in Notes..."
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnCloseListener {
            searchList.clear()
            searchList2.clear()
            searchList3.clear()
            searchList = notedbHandler.getAllNote()
            searchList2 = titleDbHandler.getAllTitle()
            searchList3 = dateDbHandler.getAllDate()
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
           if (id == R.id.search){
//               val intent = Intent(this, SearchActivity::class.java)
                fabs.visibility = View.GONE
//               startActivity(intent)
           }
        return super.onOptionsItemSelected(item)
    }
//    private var chkBox: CheckBox = findViewById(R.id.checkBox)
//    private var but: Button = findViewById(R.id.button)
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBackPressed() {
        btnDelete.visibility = View.GONE
        radioButton.visibility = View.GONE
        constrain.visibility = View.GONE
        radioButton.isChecked = false
        radioButton.isSelected = false

        noteadapter.HideItems()
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


