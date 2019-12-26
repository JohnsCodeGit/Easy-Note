package com.whiskey.notes


import android.annotation.TargetApi
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whiskey.notes.com.whiskey.notes.NotesDbHelper
import com.whiskey.notes.com.whiskey.notes.ThemeActivity
import com.whiskey.notes.com.whiskey.notes.TitlesDbHelper
import com.whiskey.notes.com.whiskey.notes.dateDbHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var notes = ArrayList<String>()
    var titles = ArrayList<String>()
    var dates = ArrayList<String>()
    private lateinit  var noteadapter: NoteAdapter
    private val notedbHandler = NotesDbHelper(this, null)
    private val titleDbHandler = TitlesDbHelper(this, null)
    private val dateDbHandler = dateDbHelper(this, null)
    private val layoutM = LinearLayoutManager(this)

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
        toolbar.setBackgroundColor(Color.parseColor("#222227"))
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)
        window.statusBarColor = Color.parseColor("#222227")
        fab.setOnClickListener { val intent = Intent(this, NewNoteActivity::class.java)
            intent.putStringArrayListExtra("notes", notes)
            intent.putStringArrayListExtra("titles", titles)
            intent.putStringArrayListExtra("dates", dates)
            startActivity(intent)
        }

        if(notedbHandler.getNoteSize() != 0.toLong() ) {
            notes = notedbHandler.getAllNote()
            titles = titleDbHandler.getAllTitle()
            dates = dateDbHandler.getAllDate()


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
                notedbHandler.addNote(noteText, notes.size)
                titleDbHandler.addTitle(titleText, titles.size)
                dateDbHandler.addDate(dateText,dates.size)

                Log.d("itemDeletedSize",notes.size.toString() )


            }
            else if(position != -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())){
                notes[position] = noteText
                titles[position] = titleText
                dates[position] = dateText
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
                notedbHandler, titleDbHandler, dateDbHandler, recyclerView_main)
            noteadapter = adapter as NoteAdapter
            addItemDecoration(VerticalSpacing(50))
           // (adapter as NoteAdapter).notifyDataSetChanged()
            //recycledViewPool.setMaxRecycledViews(R.id.button, 0)
        }


    }
    fun scroll(){
        recyclerView_main.setPadding(0, 56, 0, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

       // menuInflater.inflate(R.menu.main_menu_delete, menu)

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
           if (id == R.id.Delete){
               val intent = Intent(this, ThemeActivity::class.java)

               startActivity(intent)
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


