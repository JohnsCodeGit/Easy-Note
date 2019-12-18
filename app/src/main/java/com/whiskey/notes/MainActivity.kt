package com.whiskey.notes

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.btnDelete


class MainActivity : AppCompatActivity() {
    var notes = ArrayList<String>()
    var titles = ArrayList<String>()
    lateinit  var noteadapter: NoteAdapter

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putStringArrayList("savedNotes", notes)
        outState?.putStringArrayList("savedTitles", titles)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        notes = savedInstanceState?.getStringArrayList("savedNotes") as ArrayList<String>
        titles = savedInstanceState?.getStringArrayList("savedTitles") as ArrayList<String>

    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(Color.WHITE)

        fab.setOnClickListener { val intent = Intent(this, NewNoteActivity::class.java)
            intent.putStringArrayListExtra("notes", notes)
            intent.putStringArrayListExtra("titles", titles)
            startActivity(intent)
        }

        val noteText = intent.getStringExtra("note")
        val titleText = intent.getStringExtra("title")


        if(noteText != null || titleText != null) {
            Log.d("NOTETEXT", noteText)
            notes = intent.getStringArrayListExtra("notes")
            titles = intent.getStringArrayListExtra("titles")
            val position: Int = intent.getIntExtra("position", -1)

            Log.d("notePosition", position.toString())
            if(position == -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())) {
                notes.add(noteText)
                titles.add(titleText)
            }
            else if(position != -1 && (noteText.isNotEmpty() || titleText.isNotEmpty())){
                notes[position] = noteText
                titles[position] = titleText
            }

        }

        val delete = btnDelete
        val deleteAll = radioButton
        recyclerView_main.apply {
            setBackgroundColor(Color.TRANSPARENT)
            val layoutM = LinearLayoutManager(this.context)
            layoutM.stackFromEnd = true
            layoutM.reverseLayout = true
            layoutManager = layoutM
            val buttonLayout: ConstraintLayout = constrain
            adapter  = NoteAdapter(notes, titles, delete, deleteAll, buttonLayout, fab)
            noteadapter = adapter as NoteAdapter
            addItemDecoration(VerticalSpacing(50))
            (adapter as NoteAdapter).notifyDataSetChanged()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu_delete, menu)

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val btn: MenuItem? = menu?.findItem(R.id.delete)
        btn?.isVisible = false

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()
           if (id == R.id.delete){
                Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show()
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


