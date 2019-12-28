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
//    var notes = ArrayList<String>()
//    private var titles = ArrayList<String>()
//    private var dates = ArrayList<String>()
//    private var searchList = ArrayList<String>()
//    private var searchList2 = ArrayList<String>()
//    private var searchList3 = ArrayList<String>()
    private var noteList = ArrayList<NoteModel>()
    private var searchItems = ArrayList<NoteModel>()

    private lateinit  var noteadapter: NoteAdapter
    private val notedbHandler = NotesDbHelper(this, null)
    private val titleDbHandler = TitlesDbHelper(this, null)
    private val dateDbHandler = dateDbHelper(this, null)
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


            noteList = intent.getParcelableArrayListExtra("noteList")


            val position: Int = intent.getIntExtra("position", -1)

            Log.d("notePosition", position.toString())
            if(position == -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {
                //
                noteItem = NoteModel(noteText, titleText, dateText)


                noteList.add(noteItem)
                searchItems.add(noteItem)

                notedbHandler.addNote(noteText, titleText, dateText, noteList.size)

                Log.d("itemAddedNoteItem", noteItem.toString())


            }
            else if(position != -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())){
                //
                noteList[position].note = noteText
                noteList[position].title = titleText
                noteList[position].date = dateText

                searchItems[position].note = noteText
                searchItems[position].title = titleText
                searchItems[position].date = dateText
                //


                notedbHandler.updateNote(noteText, titleText, dateText, position+1)

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
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Search in Notes..."
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))
        searchView.setOnCloseListener {

            searchItems.clear()

            searchItems = notedbHandler.getAllNote()

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


